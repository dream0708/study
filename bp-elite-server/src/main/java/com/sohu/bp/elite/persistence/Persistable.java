package com.sohu.bp.elite.persistence;

import java.io.Serializable;

/** 
 * @author: yuguangyuan
 * @date 创建时间：2015年11月6日 上午9:55:49   
 */
public interface Persistable extends Serializable {
	Serializable getInternalId();
}
