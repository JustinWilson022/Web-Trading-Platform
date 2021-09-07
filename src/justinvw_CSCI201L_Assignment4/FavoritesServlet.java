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

import org.apache.tomcat.util.http.parser.Cookie;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class FavoritesServlet
 */
@WebServlet("/FavoritesServlet")
public class FavoritesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String databaseURL = "jdbc:mysql://localhost:3306/Assignment4?user=root&password=Justinwilson02"
			+ "&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FavoritesServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		String ticker = request.getParameter("ticker"); 
		javax.servlet.http.Cookie[] cookies = request.getCookies(); 
		String username = cookies[0].getValue();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(databaseURL);
			String check = "SELECT * from Favorites where Username = ? and Ticker = ?"; 
			PreparedStatement st = conn.prepareStatement(check);
			st.setString(1, username); 
			st.setString(2, ticker);
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				String string = "{\"message\": \"This item is already in your favorites\"}";
				JSONObject json = new JSONObject(string);
				out.println(json); 
			}
			else {
				st = conn.prepareStatement("INSERT INTO Favorites(Username, Ticker) VALUES(?, ?)");
				st.setString(1, username); 
				st.setString(2, ticker);
				st.executeUpdate();
				String string = "{\"message\": \"Item successfully added to favorites!\"}";
				JSONObject json = new JSONObject(string);
				out.println(json); 
			}
		}
		catch(SQLException sqle){
			System.out.println(sqle.getMessage());
		} 
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
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
