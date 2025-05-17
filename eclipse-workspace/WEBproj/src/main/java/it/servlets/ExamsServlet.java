package it.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import it.utils.DataBaseConnection;
//import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;


@WebServlet("/api/exams")
public class ExamsServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	    String corsoId = req.getParameter("corsoId");
	    resp.setContentType("application/json");
	    resp.setCharacterEncoding("UTF-8");
	    PrintWriter out = resp.getWriter();
	
	    if (corsoId == null) {
	      out.print("[]");
	      return;
	    }
	
	    List<String> arr = new ArrayList<>();
	    String sql = """
	    		SELECT idExam, date 
	    		FROM Exams 
	    		WHERE idCourse = ? 
	    		ORDER BY date DESC
	    		""";
	
	    try (Connection c = DataBaseConnection.getConnection(); 	PreparedStatement st = c.prepareStatement(sql)) {
	    	
	    	st.setInt(1, Integer.parseInt(corsoId));
	    	ResultSet rs = st.executeQuery();
	    	
	    	DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
	      	while (rs.next()) {
	      		int id = rs.getInt("idExam");
	      		Date d = rs.getDate("date");
	      		arr.add(String.format("{\"id\":\"%d\",\"date\":\"%s\"}", id, d.toLocalDate().format(fmt)));
	      	}
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	
	    out.print("[" + String.join(",", arr) + "]");
	    out.flush();
	}
}

/*

@WebServlet("/api/exams")
public class ExamsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session==null || session.getAttribute("idStudent")==null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String idCourse = req.getParameter("idCourse");
        if (idCourse==null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing idCourse");
            return;
        }

        List<String> datesJson = new ArrayList<>();
        String sql = """
            SELECT idExam, date
              FROM exams
             WHERE idCourse = ?
             ORDER BY date DESC
        """;
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idCourse);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idE    = rs.getInt("idExam");
                    String dt  = rs.getDate("date").toString();
                    datesJson.add(
                      String.format("{\"idExam\":%d,\"date\":\"%s\"}", idE, dt)
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.print("[" + String.join(",", datesJson) + "]");
        }
    }
}
*/
