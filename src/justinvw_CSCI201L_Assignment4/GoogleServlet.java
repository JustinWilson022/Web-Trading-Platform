package justinvw_CSCI201L_Assignment4;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GoogleServlet
 */
@WebServlet("/GoogleServlet")
public class GoogleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String databaseURL = "jdbc:mysql://localhost:3306/Assignment4?user=root&password=Justinwilson02"
			+ "&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoogleServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		PrintWriter out = response.getWriter();
		String username = request.getParameter("username");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(databaseURL);
			String check = "SELECT * from Users where Username = ?"; 
			PreparedStatement st = conn.prepareStatement(check);
			st.setString(1, username);
			ResultSet rs = st.executeQuery(); 
			if(!rs.next()) {
				st = conn.prepareStatement("INSERT INTO USERS(Email, Username, Password, Balance) VALUES(?, ?, ?, ?)");
				st.setString(1, null);
				st.setString(2, username);
				st.setString(3, null);
				st.setInt(4, 50000);
				st.executeUpdate();
				out.println("success"); 
			}
			else {
				out.println("success");
			}
		}
		catch(SQLException sqle){
			System.out.println(sqle.getMessage());
		} 
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
