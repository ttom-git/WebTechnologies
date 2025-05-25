package it.beans;

import java.sql.Date;
import java.sql.Timestamp;

public class Record {
	
	private int idRecord;
	private int id;
	private Timestamp date;
	private String CourseName;
	private Date examDate;
	private String link;
	
	public Record(int idRecord, int id, Timestamp date, String CourseName, Date examDate, String link) {
		this.id = id;
		this.date = date;
		this.CourseName = CourseName;
		this.examDate = examDate;
		this.link = link;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Timestamp getDate() {
		return date;
	}
	public void setDate(Timestamp date) {
		this.date = date;
	}
	public String getCourseName() {
		return CourseName;
	}
	public void setCourseName(String courseName) {
		CourseName = courseName;
	}
	public Date getExamDate() {
		return examDate;
	}
	public void setExamDate(Date examDate) {
		this.examDate = examDate;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}

	public int getIdRecord() {
		return idRecord;
	}

	public void setIdRecord(int idRecord) {
		this.idRecord = idRecord;
	}
	
	

}
