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
 * Servlet implementation class SellServlet
 */
@WebServlet("/SellServlet")
public class SellServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String databaseURL = "jdbc:mysql://localhost:3306/Assignment4?user=root&password=Justinwilson02"
			+ "&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SellServlet() {
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
		double sellPrice = Double.valueOf(request.getParameter("sellPrice")); 
		int total = 0; 
		int quantity = Integer.valueOf(request.getParameter("quantity")); 
		int originalQuantity = quantity; 
		javax.servlet.http.Cookie[] cookies = request.getCookies(); 
		String username = cookies[0].getValue();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(databaseURL);
			String statement = "SELECT SUM(Quantity) AS Total from Portfolio "
					+ "where Username = ? and Ticker = ?"; 
			PreparedStatement st = conn.prepareStatement(statement);
			st.setString(1, username);
			st.setString(2, ticker);
			ResultSet rs = st.executeQuery(); 
			while(rs.next()) {
				total = rs.getInt("Total"); 
			}
			if(total < quantity) {
				out.println("You cannot sell more shares than you currently have"); 
			}
			else {
				String sell = "SELECT * from Portfolio where Username = ? and Ticker = ?"; 
				st = conn.prepareStatement(sell, ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_UPDATABLE); 
				st.setString(1, username);
				st.setString(2, ticker);
				ResultSet rs2 = st.executeQuery(); 
				double addToBalance = 0.0; 
				while(quantity > 0) {
					rs2.next(); 
					int tableQuantity = rs2.getInt("Quantity"); 
					if(tableQuantity > quantity) {
						double formerPrice = rs2.getDouble("Price"); 
						addToBalance += (sellPrice * quantity); 
						rs2.updateInt("Quantity", tableQuantity - quantity);
						rs2.updateRow(); 
						quantity = 0; 
					}
					else if(tableQuantity == quantity) {
						double formerPrice = rs2.getDouble("Price");
						addToBalance += (sellPrice * quantity);
						rs2.deleteRow();
						quantity = 0; 
					}
					else {
						double formerPrice = rs2.getDouble("Price");
						addToBalance += (sellPrice * tableQuantity);
						rs2.deleteRow();
						quantity -= tableQuantity; 
					}
				}
				String getBalance = "SELECT * from Users where Username = ?"; 
				st = conn.prepareStatement(getBalance);
				st.setString(1, username);
				ResultSet rs3 = st.executeQuery();
				double oldBalance = 0; 
				while(rs3.next()) {
					oldBalance = rs3.getDouble("Balance");
				}
				double newBalance = oldBalance + addToBalance; 
				String updateBalance = "UPDATE Users SET Balance = ? WHERE Username = ?";
				st = conn.prepareStatement(updateBalance);
				st.setDouble(1, newBalance); 
				st.setString(2, username);
				st.executeUpdate(); 
				out.println("SUCCESS: Executed sale of " + originalQuantity + " share(s) of " + ticker); 
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
