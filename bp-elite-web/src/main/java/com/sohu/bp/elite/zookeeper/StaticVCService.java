package com.sohu.bp.elite.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.OverallData;
import com.sohu.zookeeper.client.ZookeeperNode;
import com.sohu.zookeeper.util.InputStreamUtils;

/**
 * 静态资源版本号zk监听响应服务
 * @author nicholastang
 *
 */
public class StaticVCService extends ZookeeperContentService
{
	private static final Logger log = LoggerFactory.getLogger(StaticVCService.class);
	@Override
	public void excute(final String node,final boolean f){

    	try {
    		byte[] content = client.getData().usingWatcher(new Watcher(){
				public void process(WatchedEvent event){

					excute(node,true);
				}
			}).forPath(ZookeeperNode.zookeeperRoot.contents_path+"/"+node);
    		String contents = InputStreamUtils.byteTOString(content);
    		
    		if(f){
	    		log.info("["+ZookeeperNode.zookeeperRoot.contents_path+"/"+node+"]:"+contents);
	    		OverallData.setStaticVerCode(contents);
    		}	
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
}