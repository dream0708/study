package com.sohu.bp.elite.thrift.service.impl;

import com.sohu.bp.elite.service.EliteThriftService;
import com.sohu.bp.elite.util.SpringUtil;
import com.sohu.suc.thrift.regist.ThriftServiceRegister;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * 
 * @author nicholastang
 * 2016年6月6日
 */
public class EliteThriftServiceRegisterImpl {
	
	private static Logger logger = LoggerFactory.getLogger(EliteThriftServiceRegisterImpl.class);
	
	private String serviceName;
	private int servicePort;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public int getServicePort() {
		return servicePort;
	}

	public void setServicePort(int servicePort) {
		this.servicePort = servicePort;
	}

	public void init(){
		final CountDownLatch eliteServiceCDL = new CountDownLatch(1);
		
		new Thread(new Runnable() {
    		public void run() {
    			TNonblockingServerTransport serverTransport = null;
    			try {
    				serverTransport = new TNonblockingServerSocket(servicePort);
    			} catch (TTransportException e) {
    				logger.error("bp elite service start error.",e);
    			}
    			TBinaryProtocol.Factory proFactory = new TBinaryProtocol.Factory();
    			EliteThriftService.Iface eliteThriftService = SpringUtil.getBean("eliteThriftService", EliteThriftService.Iface.class);
    			TProcessor processor = new EliteThriftService.Processor<EliteThriftService.Iface>(eliteThriftService);
//    			TServer server = new TThreadedSelectorServer(new TThreadedSelectorServer.Args(serverTransport).processor(processor)
//    					.protocolFactory(proFactory).selectorThreads(10).workerThreads(10));
    			
    			TThreadedSelectorServer.Args tArgs = new TThreadedSelectorServer.Args(serverTransport).processor(processor)
                        .protocolFactory(proFactory).selectorThreads(10).workerThreads(100);
                tArgs.maxReadBufferBytes = 10 * 1024 * 1024L;
                TServer server = new TThreadedSelectorServer(tArgs);
                
    			logger.info("bp elite service is started at port "+servicePort);
    			System.setProperty("swift.serverName", serviceName);
    			System.setProperty("swift.port", String.valueOf(servicePort));
    			ThriftServiceRegister thriftServiceRegister = (ThriftServiceRegister) SpringUtil.getBean("thriftServiceRegister");
    			thriftServiceRegister.publish();
				eliteServiceCDL.countDown();
    			server.serve();
    		}
    	}).start();
		
		try {
			eliteServiceCDL.await();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("Thrift service regist end: {}:{}", new Object[]{serviceName, servicePort});
	}
}
