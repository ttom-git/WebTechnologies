package it.servlets.Students;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import it.beans.ExamResult;
import it.dao.ExamDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/StudentDataServlet")
public class StudentDataServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // gettin studentId from session or auth
        String studentId = (String) request.getSession().getAttribute("idStudent");
        if (studentId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not logged in");
            return;
        }

        List<ExamResult> results = ExamDAO.getExamResultsByStudent(studentId);

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        StringBuilder json = new StringBuilder();
        json.append("[");

        for (int i = 0; i < results.size(); i++) {
            ExamResult r = results.get(i);
            json.append("{")
                .append("\"idExam\":").append(r.getExamId()).append(",")
                .append("\"courseName\":\"").append(escapeJson(r.getCourseName())).append("\",")
                .append("\"date\":\"").append(r.getDate()).append("\",");
            if (r.getStatus().equals("pending") || r.getStatus().equals("added")) {		  //tengo separati i casi in cui sta added e pending perché non ha senso faccia vedere il grade
                json.append("\"grade\":\"\",")
                    .append("\"status\":\"pending\"");
            } else {
                json.append("\"grade\":\"").append(escapeJson(r.getGrade())).append("\",")
                    .append("\"status\":\"").append(escapeJson(r.getStatus())).append("\"");
            }
            json.append("}");
        
	        if (i < results.size() - 1) {
	                json.append(",");
	        }
        }
        json.append("]");
        out.print(json.toString());
    }

	// https://www.geeksforgeeks.org/how-to-escape-strings-in-json/
    private String escapeJson(String s) {
        return s == null ? "" : s.replace("\\", "\\\\")
                                 .replace("\"", "\\\"")
                                 .replace("\n", "\\n")
                                 .replace("\r", "\\r");
    }
}
