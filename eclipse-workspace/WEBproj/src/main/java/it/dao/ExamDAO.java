package it.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.beans.ExamResult;
import it.utils.DataBaseConnection;

public class ExamDAO {

    public static List<ExamResult> getExamResultsByStudent(String idStudent) {
        List<ExamResult> results = new ArrayList<>();

        String query = """
            SELECT er.idExam, c.name AS courseName, e.date, er.result, er.status
            FROM results er
            JOIN exams e ON er.idExam = e.idExam
            JOIN courses c ON e.idCourse = c.idCourse
            WHERE er.idStudent = ?
            ORDER BY e.date DESC
        """;

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, idStudent);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ExamResult r = new ExamResult();
                r.setExamId(rs.getInt("idExam"));
                r.setCourseName(rs.getString("courseName"));
                r.setDate(rs.getString("date"));
                r.setGrade(rs.getString("result"));
                r.setStatus(rs.getString("status"));
                results.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }


        // Reject an exam grade (update the status to 'rejected')
        public static boolean rejectExamGrade(String studentId, int examId) {
            //String query = "UPDATE results SET status = ? WHERE idStudent = ? AND idExam = ? AND status != 'verbalized'";
            String query = "UPDATE results SET status = ? WHERE idStudent = ? AND idExam = ? AND status = 'published'";		// NON == DIO

            try (Connection conn = DataBaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                // Set parameters for the query
                stmt.setString(1, "rejected");  // Set the status to 'rejected'
                stmt.setInt(2, Integer.valueOf(studentId));   // Set the student ID
                stmt.setInt(3, examId);         // Set the exam ID

                int rowsAffected = stmt.executeUpdate();

                return rowsAffected > 0;    // if one or more rows are affected, the rejection was successful

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    
}
