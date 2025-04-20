package it;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import it.utils.DataBaseConnection;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/api/iscritti")
public class IscrittiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String examId = req.getParameter("appelloId");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        if (examId == null) {
            out.print("[]");
            return;
        }

        // Debug
        System.out.println(">>> IscrittiServlet: fetching for examId=" + examId);

        String sql = 
          "SELECT s.idStudent, s.name, s.surname, s.email, " +
          "       r.result AS grade, r.status       " +
          "FROM Results r                         " +
          "  JOIN Students s ON s.idStudent = r.idStudent " +
          "WHERE r.idExam = ?                     " +
          "ORDER BY s.surname, s.name";

        List<String> rows = new ArrayList<>();

        try (Connection c = DataBaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(examId));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(String.format(
                      "{\"idStudent\":%d,\"name\":\"%s\",\"surname\":\"%s\",\"email\":\"%s\",\"grade\":\"%s\",\"status\":\"%s\"}",
                      rs.getInt("idStudent"),
                      rs.getString("name"),
                      rs.getString("surname"),
                      rs.getString("email"),
                      rs.getString("grade") != null ? rs.getString("grade") : "",
                      rs.getString("status")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        out.print("[" + String.join(",", rows) + "]");
        out.flush();
    }
}
