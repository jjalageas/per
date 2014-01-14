package fr.per.bugextraction;


import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Access(AccessType.PROPERTY)
public class IssueEntity implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3282397399077346183L;
	
	private String issue_key;

	private String status;
	
	private Date date;
	
	private int id;

	public IssueEntity(String issue_k, String issue_s, Date issue_d)
	{
		this.issue_key = issue_k;
		this.status = issue_s;
		this.date = issue_d;
	}

	public IssueEntity(){
		this.issue_key = null;
		this.status = null;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	public void setId(int newId) {
		id = newId;
	}

	@Column(name="ISSUE_KEY")
	public String getIssue_key() {
		return issue_key;
	}

	public void setIssue_key(String issue_k) {
		this.issue_key = issue_k;
	}

	@Column(name="STATUS")
	public String getStatus() {
		return status;
	}

	public void setStatus(String issue_s) {
		this.status = issue_s;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name="DATE")
	public Date getDate(){
		return date;
	}
	
	public void setDate(Date s){
		this.date = s;
	}

}
