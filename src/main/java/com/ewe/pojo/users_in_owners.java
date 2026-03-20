package com.ewe.pojo;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "users_in_owners")
public class users_in_owners extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	private Long user_id;
	private Long owner_org_id;
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	public Long getOwner_org_id() {
		return owner_org_id;
	}
	public void setOwner_org_id(Long owner_org_id) {
		this.owner_org_id = owner_org_id;
	}

}
