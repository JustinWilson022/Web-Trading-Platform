package justinvw_CSCI201L_Assignment4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class BuyServlet
 */
@WebServlet("/BuyServlet")
public class BuyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String databaseURL = "jdbc:mysql://localhost:3306/Assignment4?user=root&password=Justinwilson02"
			+ "&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BuyServlet() {
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
		String ticker = request.getParameter("ticker");
		double price = Double.valueOf(request.getParameter("buyPrice"));
		int quantity = Integer.valueOf(request.getParameter("quantity"));
		javax.servlet.http.Cookie[] cookies = request.getCookies(); 
		String username = cookies[0].getValue();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(databaseURL);
			String check = "SELECT * from Users where Username = ?";
			PreparedStatement st = conn.prepareStatement(check);
			st.setString(1, username); 
			ResultSet rs = st.executeQuery();
			double balance = 0.0;
			while(rs.next()) {
				balance = rs.getDouble("Balance"); 
			}
			if(balance - (price*quantity) >= 0) {
				String insert = "INSERT INTO Portfolio(Username, Ticker, Price, Quantity) VALUES(?, ?, ?, ?)";
				st = conn.prepareStatement(insert);
				st.setString(1, username); 
				st.setString(2, ticker);
				st.setDouble(3, price);
				st.setInt(4, quantity);
				st.executeUpdate(); 
				double newBalance = balance - (price*quantity); 
				String update = "UPDATE Users SET Balance = ? WHERE Username = ?"; 
				st = conn.prepareStatement(update);
				st.setDouble(1, newBalance);
				st.setString(2, username);
				st.executeUpdate(); 
				DecimalFormat df = new DecimalFormat("###.##");
				double total = price * quantity; 
				total = Double.valueOf(df.format(total)); 
				String message = "SUCCESS: Executed purchase of "+quantity+" share(s) of "+ticker+ " for $"+total; 
				out.println(message); 
			}
			else {
				out.println("FAILED: Sale not possible"); 
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
