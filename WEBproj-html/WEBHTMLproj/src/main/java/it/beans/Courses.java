package it.beans;

public class Courses {
	
	private String name;
	private int idCourse;
	private String year;
	private int idLecturer;
	
	public Courses(int idCourse, String name, String year, int idLecturer) {
		this.idCourse = idCourse;
		this.idLecturer = idLecturer;
		this.name = name;
		this.year = year;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIdCourse() {
		return idCourse;
	}

	public void setIdCourse(int idCourse) {
		this.idCourse = idCourse;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public int getIdLecturer() {
		return idLecturer;
	}

	public void setIdLecturer(int idLecturer) {
		this.idLecturer = idLecturer;
	}
	
	

}
