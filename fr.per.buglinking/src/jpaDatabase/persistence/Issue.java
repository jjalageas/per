package jpaDatabase.persistence;


import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Access(AccessType.PROPERTY)
public class Issue implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String issue_key;

	private String status;
	
	private int id;

	public Issue(String issue_k, String issue_s)
	{
		this.issue_key = issue_k;
		this.status = issue_s;
	}

	public Issue(){
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

}
