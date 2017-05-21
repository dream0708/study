package com.sohu.bp.elite.persistence;

import javax.persistence.*;

import java.io.Serializable;

/**
 * Created by nicholastang on 2017/3/15.
 */
@Entity
@Table(name = "elite_admin")
public class EliteAdmin extends AbstractEntity {
    private static final long serialVersionUID = -1L;

    private Long id;
    private Integer status;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "status")
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Transient
    public Serializable getInternalId() {
        return this.getId();
    }
}
