package it.beans;

public class ExamResult {
    private int examId;
    private String courseName;
    private String date;
    private String grade;
    private String status;

    public ExamResult() {}

    public ExamResult(int examId, String courseName, String date, String grade, String status) {
        this.examId = examId;
        this.courseName = courseName;
        this.date = date;
        this.grade = grade;
        this.status = status;
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
}
