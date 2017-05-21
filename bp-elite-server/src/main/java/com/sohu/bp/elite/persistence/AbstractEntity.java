package com.sohu.bp.elite.persistence;

import java.io.Serializable;

/** 
 * @author: yuguangyuan
 * @date 创建时间：2015年11月6日 上午9:56:16   
 */
public abstract class AbstractEntity implements Persistable {

	public abstract Serializable getInternalId();
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!o.getClass().getName().equals(this.getClass().getName())) return false;
		return ((Persistable)o).getInternalId().equals(this.getInternalId());
	}
	
	@Override
	public int hashCode() {
		return this.getInternalId().hashCode();
	}

}
