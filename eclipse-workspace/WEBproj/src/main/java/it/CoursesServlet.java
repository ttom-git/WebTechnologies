package it;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import it.utils.DataBaseConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/api/courses")
public class CoursesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        // Get current session and ensure user is a lecturer
        HttpSession session = request.getSession(false);
        if (session == null || !"docente".equals(session.getAttribute("userType"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("[]");
            out.flush();
            return;
        }

        String email = (String) session.getAttribute("email");
        int lecturerId = -1;
        
        try (Connection conn = DataBaseConnection.getConnection()) {
            // First, find the lecturer's ID by email
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT idLecturer FROM Lecturers WHERE email = ?")) {
                stmt.setString(1, email);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        lecturerId = rs.getInt("idLecturer");
                    }
                }
            }

            if (lecturerId < 0) {
                // No matching lecturer
                out.print("[]");
                out.flush();
                return;
            }

            // Now query courses taught by this lecturer, ordered by name DESC
            List<String> coursesJson = new ArrayList<>();
            String sql = "SELECT idCourse, name FROM Courses WHERE idLecturer = ? ORDER BY name DESC";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, lecturerId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("idCourse");
                        String name = rs.getString("name");
                        coursesJson.add(String.format("{\"id\":%d,\"nome\":\"%s\"}", id, name));
                    }
                }
            }

            // Output JSON array
            out.print("[" + String.join(",", coursesJson) + "]");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("[]");
        } finally {
            out.flush();
        }
    }
}
