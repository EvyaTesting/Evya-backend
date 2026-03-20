package com.ewe.pojo;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "white_lable_orgs", uniqueConstraints = @UniqueConstraint(columnNames = "orgName"))
@JsonIgnoreProperties(ignoreUnknown = true, value = {"hibernateLazyInitializer", "handler"})
public class WhiteLabelOrgs extends BaseEntity {

	private static final long serialVersionUID = 1L;
	private String orgName;
	private boolean flag;

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}
}
