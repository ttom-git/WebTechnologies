package it.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import it.utils.*;

import java.io.File; // Import the File class
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Servlet implementation class createVerbaleServlet
 */
@WebServlet("/createVerbale")
public class createVerbaleServlet extends ThymeleafServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public createVerbaleServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();

		String corso = request.getParameter("courseName");
		String idAppello = request.getParameter("idExam");

		String[] student = request.getParameterValues("status");
		
		JakartaServletWebApplication webApplication = JakartaServletWebApplication
				.buildApplication(getServletContext());
		WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
		
		try (Connection conn = DataBaseConnection.getConnection()) {

			String modifyReject = "UPDATE results SET result = 'retried' WHERE status = 'rejected' AND idExam = ?";

			PreparedStatement stmt = conn.prepareStatement(modifyReject);

			stmt.setString(1, idAppello);

			stmt.executeUpdate();

			String verbalizedGrade = "UPDATE results SET status = 'verbalized' WHERE idExam = ?";

			PreparedStatement stmt1 = conn.prepareStatement(verbalizedGrade);

			stmt1.setString(1, idAppello);

			stmt1.executeUpdate();

			/*-----------
			|	 .TXT	|
			-----------*/

			// String projectPath =request.getContextPath();

			String filesDir = getServletContext().getRealPath("/files");
			File dir = new File(filesDir);
			if (!dir.exists()) dir.mkdirs();

			String link = idAppello + ".txt";
			File outFile = new File(dir, link);
			System.out.println("Writing file to: " + outFile.getAbsolutePath());

			try (PrintWriter fw = new PrintWriter(
			        new OutputStreamWriter(new FileOutputStream(outFile), StandardCharsets.UTF_8))) {
			    
			    String sel = """
			        SELECT s.idStudent, s.name, s.surname, r.result
			        FROM Results r
			          JOIN Students s ON s.idStudent = r.idStudent
			        WHERE r.idExam = ? AND r.status = 'verbalized'
			        ORDER BY s.surname, s.name
			    """;

			    PreparedStatement ps = conn.prepareStatement(sel);
			    ps.setInt(1, Integer.parseInt(idAppello));
			    ResultSet rs = ps.executeQuery();

			    int count = 0;
			    while (rs.next()) {
			        fw.printf("%d %s %s %s\n",
			                  rs.getInt("idStudent"),
			                  rs.getString("name"),
			                  rs.getString("surname"),
			                  rs.getString("result"));
			        count++;
			    }
			    System.out.println("Numero di righe scritte: " + count);
			    
			} catch (Exception e) {
			    e.printStackTrace();
			}

			System.out.println(dir + "/" + link);


			String insertVerbale = "INSERT INTO records(Date,IdExam,link) VALUES (?, ? , ?)";

			PreparedStatement stmt2 = conn.prepareStatement(insertVerbale);

			stmt2.setTimestamp(1,new Timestamp(System.currentTimeMillis()));
			stmt2.setString(2, idAppello);
			stmt2.setString(3, link);

			stmt2.executeUpdate();

			ctx.setVariable("anyExams", corso != null ? true : false);
			ctx.setVariable("anyAppello", null);

			ctx.setVariable("nomeCorso", corso);
			templateEngine.process("lecturersArea/lecturer", ctx, response.getWriter());

		} catch (Exception e) {
			System.out.println("Exception catched @LoginServlet.java : " + e.getMessage());
			request.getRequestDispatcher("index").forward(request, response);
		}

	}

}
