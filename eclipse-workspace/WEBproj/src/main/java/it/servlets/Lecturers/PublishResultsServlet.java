package it.servlets.Lecturers;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.utils.DataBaseConnection;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@WebServlet("/PublishResults")
public class PublishResultsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		//json to string somehow
		StringBuilder sb = new StringBuilder();
    		try (BufferedReader r = req.getReader()) {
    			String line;
    			while ((line = r.readLine()) != null) 
    				sb.append(line);
    		}
    		String raw = sb.toString().trim();

    		String examId = raw.replaceAll(".*\"examId\"\\s*:\\s*\"(\\d+)\".*", "$1");
    		List<String> students = new ArrayList<>();
    		Matcher m = Pattern.compile("\"students\"\\s*:\\s*\\[(.*?)\\]").matcher(raw);
    		if (m.find()) {
    			for (String s : m.group(1).split(",")) {
    				students.add(s.replaceAll("[^0-9]", ""));
    			}
    		}

    		//update status to 'published' from 'added'
    		String sql = """
    				UPDATE results 
    				SET status='published' 
    				WHERE idExam=? AND idStudent=?
    				""";
    		try (Connection c = DataBaseConnection.getConnection();
    				PreparedStatement ps = c.prepareStatement(sql)) {
    			int ex = Integer.parseInt(examId);
    			for (String st : students) {
    				ps.setInt(1, ex);
    				ps.setString(2, st);
    				ps.addBatch();
    			}
    			ps.executeBatch();
    			res.setStatus(HttpServletResponse.SC_OK);
    		} catch (Exception e) {
    			e.printStackTrace();
    			res.sendError(500, "Dataobese error");
    		}
	}
}
