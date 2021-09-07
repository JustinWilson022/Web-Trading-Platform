package justinvw_CSCI201L_Assignment4;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SignUpServlet
 */
@WebServlet("/SignUpServlet")
public class SignUpServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String databaseURL = "jdbc:mysql://localhost:3306/Assignment4?user=root&password=Justinwilson02"
			+ "&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SignUpServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		String email = request.getParameter("email"); 
		String username = request.getParameter("username"); 
		String password = request.getParameter("password"); 
		String confirm = request.getParameter("confirm"); 
		PrintWriter out = response.getWriter(); 
		if(!password.equals(confirm)) {
			out.println("The passwords entered must match"); 
		}
		else {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				Connection conn = DriverManager.getConnection(databaseURL);
				String emailCheck = "SELECT * from Users where Email = ?";  
				PreparedStatement st = conn.prepareStatement(emailCheck);
				st.setString(1, email); 
				ResultSet rs = st.executeQuery();
				if(rs.next()) {
					out.println("The email entered is already in use");
				}
				else {
					String usernameCheck = "SELECT * from Users where Username = ?"; 
					st = conn.prepareStatement(usernameCheck);
					st.setString(1, username); 
					ResultSet rs2 = st.executeQuery(); 
					if(rs2.next()) {
						out.println("The username entered is already in use"); 
					}
					else {
						st = conn.prepareStatement("INSERT INTO USERS(Email, Username, Password, Balance) VALUES(?, ?, ?, ?)");
						st.setString(1, email);
						st.setString(2, username);
						st.setString(3, password);
						st.setInt(4, 50000); 
						st.executeUpdate(); 
						Cookie cookie = new Cookie("user", username); 
						cookie.setPath(request.getContextPath());
						response.addCookie(cookie);
					}
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
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
