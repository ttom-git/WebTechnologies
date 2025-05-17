package it.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.beans.Courses;
import it.utils.DataBaseConnection;

public class StudentDAO {

	public static List<Courses> findCoursesByStudentId(String idStudent) {
		List<Courses> courses = new ArrayList<>();
		String sql = """
	  		  SELECT c.idCourse, c.name
	  		  FROM Courses c JOIN StudentsInCourses sc ON sc.idCourse = c.idCourse
	  		  WHERE sc.idStudent = ?
	  		""";
		try ( Connection conn = DataBaseConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql) ) {

			ps.setString(1, idStudent);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Courses c = new Courses(rs.getInt("idCourse"), rs.getString("name"));
					courses.add(c);
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return courses;
	}
}
