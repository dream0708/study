package com.sohu.bp.elite.zookeeper;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.zookeeper.client.ZookeeperClient;
import com.sohu.zookeeper.client.ZookeeperNode;
import com.sohu.zookeeper.util.InputStreamUtils;

/**
 * 
 * @author nicholastang
 * 2016-07-28 11:07:35
 * TODO
 */
public class ZookeeperContentService {
	private static final Logger log = LoggerFactory.getLogger(ZookeeperContentService.class);
	
	CuratorFramework client = null;
	
	public boolean flag = true;
	public String pageroot;
	private String node;
	
    public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}
	
	public void init(){	
		this.excute(node,false);
	}
	
	public ZookeeperContentService(){
    	
		client = CuratorFrameworkFactory.newClient(ZookeeperClient.zookeeperCon.con, 
				new ExponentialBackoffRetry(ZookeeperClient.zookeeperCon.baseSleepTimeMs, ZookeeperClient.zookeeperCon.maxRetries));
		
		client.start();
    }
    
    public void excute(final String node,final boolean f){}
    
    public void doWork(){
    	
    }
    
    public String getData(String node){
		try {
			byte[] content;
			content = client.getData().forPath(ZookeeperNode.zookeeperRoot.contents_path+"/"+node);
			String contents = InputStreamUtils.byteTOString(content);
			return contents;
		} catch (Exception e) {
			log.error("",e);
			return null;
		}
    }
    
    public String getNodeData(){
    	if(StringUtils.isEmpty(this.node))
    		return null;
    	try {
			byte[] content;
			content = client.getData().forPath(ZookeeperNode.zookeeperRoot.contents_path+"/"+ this.node);
			String contents = InputStreamUtils.byteTOString(content);
			return contents;
		} catch (Exception e) {
			log.error("",e);
			return null;
		}
    }
    
	public void close(){
		
		if(client != null){	
			client.close();
		}
	}
}
