package com.ewe.pojo;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users_in_whitelabels")
public class users_in_whitelabels extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "user_id")
    private Long user_id;

    @Column(name = "wl_org_id")
    private Long wl_org_id;

//    @Column(name = "owner_org_id")
//    private Long owner_org_id;

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getWl_org_id() {
        return wl_org_id;
    }

    public void setWl_org_id(Long wl_org_id) {
        this.wl_org_id = wl_org_id;
    }

//    public Long getOwner_org_id() {
//        return owner_org_id;
//    }
//
//    public void setOwner_org_id(Long owner_org_id) {
//        this.owner_org_id = owner_org_id;
//    }
}

