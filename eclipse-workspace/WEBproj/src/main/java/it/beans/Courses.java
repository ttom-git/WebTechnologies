package it.beans;

public class Courses {
    private int idCourse;
    private String name;

    public Courses(int idCourse, String name) {
        this.idCourse = idCourse;
        this.name = name;
    }

    public int getIdCourse() { return idCourse; }
    
    public void setIdCourse(int idCourse) { this.idCourse = idCourse; }
    
    public String getName() { return name; }
    
    public void setName(String name) { this.name = name; }
    
}
