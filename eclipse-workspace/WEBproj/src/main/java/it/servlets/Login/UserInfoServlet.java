/*package it;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/getUserInfo")
public class UserInfoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);

        String name = (String) session.getAttribute("name");
        String surname = (String) session.getAttribute("surname");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.print("{\"nome\":\"" + name + "\", \"cognome\":\"" + surname + "\"}");
        out.flush();
    }
}
*/
package it.servlets.Login;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import it.utils.DataBaseConnection;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/getUserInfo")
public class UserInfoServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		HttpSession session = req.getSession(false);
	    if (session==null) {
	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	    	return;
	    }
	
	    String name      = (String) session.getAttribute("name");
	    String surname   = (String) session.getAttribute("surname");
	    String userType  = (String) session.getAttribute("userType");
	    String codePersona = null;
	
	    if ("studente".equals(userType)) {
	    	String email = (String) session.getAttribute("email");
	    	
	    	String sql = """
		        		SELECT idStudent 
		        		FROM Students 
		        		WHERE email=?
		        		""";
	    	try (Connection c = DataBaseConnection.getConnection()) {
	    	
	    		PreparedStatement st = c.prepareStatement(sql);
	    		
	    		st.setString(1, email);
	    		ResultSet rs = st.executeQuery();
	    		if (rs.next()) 
	    			codePersona = rs.getString("idStudent");                   
	        // out.print("Code Persona: " + codePersona); [2h perchè sta cribio di riga uccide per qualche arcano motivo il json povco povco	//future me speaking: sono ritardato
	
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    }
	
	    resp.setContentType("application/json");
	    resp.setCharacterEncoding("UTF-8");
	    PrintWriter out = resp.getWriter();
	    out.print("{"
	      + "\"name\":\"" + name +"\","
	      + "\"surname\":\"" + surname +"\","
	      + "\"codePersona\":\"" + codePersona +"\""
	      + "}"
	    );
	    out.flush();
	  }
	}
	
	
	
	
