package it.beans;

import java.util.Optional;

public class ExamResult {
    private int examId;
    private String courseName;
    private String date;
    private String grade;
    private String status;
    private String nameStud;
    private String email;
    private int idStud;
    private String surnameStud;

    public ExamResult(int examId, String courseName, String date, String grade, String status, int idStud, String name, String surname, String email) {
        this.examId = examId;
        this.courseName = courseName;
        this.date = date;
        this.grade = grade;
        this.status = status;
        this.idStud = idStud;
        this.email = email;
        this.nameStud = name;
        this.surnameStud = surname;
    }

    public int getExamId() {return examId;}

    public void setExamId(int examId) {this.examId = examId;}

    public String getCourseName() {return courseName;}

    public void setCourseName(String courseName) {this.courseName = courseName;}

    public String getDate() {return date;}

    public void setDate(String date) {this.date = date;}

    public String getGrade() {return grade;}

    public void setGrade(String grade) {this.grade = grade;}

    public String getStatus() {return status;}

    public void setStatus(String status) {this.status = status;}

	public String getNameStud() {
		return nameStud;
	}

	public void setNameStud(String nameStud) {
		this.nameStud = nameStud;
	}

	public int getIdStud() {
		return idStud;
	}

	public void setIdStud(int idStud) {
		this.idStud = idStud;
	}

	public String getSurnameStud() {
		return surnameStud;
	}

	public void setSurnameStud(String surnameStud) {
		this.surnameStud = surnameStud;
	}
	
	public int toInt(String s) {
		return Optional.ofNullable(s)
		 .map(Integer::parseInt)
		 .orElse(0);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
