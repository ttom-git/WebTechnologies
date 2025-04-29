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


        // update the status to rej
        public static boolean rejectExamGrade(String studentId, int examId) {
            /*String query = """
        			UPDATE results 
        			SET status = ? 
        			WHERE idStudent = ? AND idExam = ? AND status != 'verbalized'
        			""";*/
            String query = """
            			UPDATE results 
            			SET status = ? 
            			WHERE idStudent = ? AND idExam = ? AND status = 'published' AND result IN ('18','19','20','21','22','23','24','25','26','27','28','29','30', 'laude')
            		""";		// !! NON '==' fkweoinfw !!

            try (Connection conn = DataBaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, "rejected");  // status should now be 'rejected'
                stmt.setInt(2, Integer.valueOf(studentId));   //Integer.valueOf o parseInt? che differenza c'è lmao
                stmt.setInt(3, examId); 

                
                return stmt.executeUpdate() > 0;    //should be returning the number of line that has been changed, if > 0 then OK 
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    
}
