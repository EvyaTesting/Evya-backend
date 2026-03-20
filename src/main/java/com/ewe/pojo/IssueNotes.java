package com.ewe.pojo;

import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "issue_notes")
public class IssueNotes extends BaseEntity{

	private static final long serialVersionUID=1L;
    
	@Column(nullable = false, columnDefinition = "TEXT")
    private String notes;
	
    private String title;
    @Temporal(TemporalType.TIMESTAMP)
    
    @Column(name = "modified_date")
    private Date modifiedDate;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "last_modified_by")
    private String lastModifiedBy;
    
    
    public String getNotes() {
    	return notes;
    }
    public void setNotes(String notes) {
    	this.notes = notes;
    }
    public String getTitle() { 
    	return title;
    }
    public void setTitle(String title) {
    	this.title = title;
    }
    public Date getModifiedDate() {
    	return modifiedDate;
    }
    public void setModifiedDate(Date modifiedDate) { 
    	this.modifiedDate = modifiedDate; 
    }
    public String getCreatedBy() {
    	return createdBy;
    }
    public void setCreatedBy(String createdBy) {
    	this.createdBy = createdBy;
    }
    public String getLastModifiedBy() {
    	return lastModifiedBy; 
    }
    public void setLastModifiedBy(String lastModifiedBy) {
    	this.lastModifiedBy = lastModifiedBy;
    }
    
    @JsonIgnore
    private IssueReporting issueReporting;
    
    @ManyToOne(fetch=FetchType.LAZY,cascade= {CascadeType.REFRESH,CascadeType.MERGE})
    @JoinColumn(name="issue_reporting_id")
    public IssueReporting getIssueReporting() {
    	return issueReporting;
    }
    public void setIssueReporting(IssueReporting issueReporting) {
    	this.issueReporting = issueReporting;
    }

    @Override
    public String toString() {
        return "IssueNotes{" +
                "id=" + id +
                ", notes='" + notes + '\'' +
                ", title='" + title + '\'' +
                ", modifiedDate=" + modifiedDate +
                ", createdBy='" + createdBy + '\'' +
                ", lastModifiedBy='" + lastModifiedBy + '\'' +
                '}';
    }
}
