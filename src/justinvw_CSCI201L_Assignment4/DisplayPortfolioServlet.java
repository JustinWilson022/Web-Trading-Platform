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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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
 * Servlet implementation class DisplayPortfolioServlet
 */
@WebServlet("/DisplayPortfolioServlet")
public class DisplayPortfolioServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String databaseURL = "jdbc:mysql://localhost:3306/Assignment4?user=root&password=Justinwilson02"
			+ "&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DisplayPortfolioServlet() {
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
		JSONArray portfolioArray = new JSONArray();
		Map<String, JSONObject> portfolio = new HashMap<>();
		String username = request.getParameter("username");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(databaseURL);
			String portfolioData = "SELECT * from Portfolio where Username = ?";
			PreparedStatement st = conn.prepareStatement(portfolioData);
			st.setString(1, username);
			ResultSet rs2 = st.executeQuery();
			while(rs2.next()) {
				String currentTicker = rs2.getString("Ticker"); 
				if(portfolio.containsKey(currentTicker)) {
					int old = portfolio.get(currentTicker).getInt("quantity"); 
					int newQuantity = old + rs2.getInt("Quantity"); 
					portfolio.get(currentTicker).remove("quantity"); 
					portfolio.get(currentTicker).put("quantity", newQuantity); 
					double oldTotal = portfolio.get(currentTicker).getDouble("totalCost"); 
					double newTotal = oldTotal + (double) rs2.getInt("Quantity")*rs2.getDouble("Price"); 
					portfolio.get(currentTicker).remove("totalCost"); 
					portfolio.get(currentTicker).put("totalCost", newTotal);
					double newAverage = newTotal / newQuantity; 
					portfolio.get(currentTicker).remove("averageCost"); 
					portfolio.get(currentTicker).put("averageCost", newAverage); 
					double currentPrice = portfolio.get(currentTicker).getDouble("currentPrice"); 
					double newChange = currentPrice - newAverage; 
					portfolio.get(currentTicker).remove("change"); 
					portfolio.get(currentTicker).put("change", newChange); 
					double newMarketVal = currentPrice * newQuantity; 
					portfolio.get(currentTicker).remove("marketValue");
					portfolio.get(currentTicker).put("marketValue", newMarketVal);
				}
				else {
					JSONObject current = new JSONObject(); 
					URL description = new URL("https://api.tiingo.com/tiingo/daily/" + currentTicker + 
							"?token=87afef4f31387c86c65345064907579a380822d7");
					URLConnection con = description.openConnection();
					BufferedReader in =  new BufferedReader(new InputStreamReader(con.getInputStream()));
					String inputLine = in.readLine();
					JSONObject descriptionObject = new JSONObject(inputLine);
					current.put("ticker", descriptionObject.get("ticker"));
					current.put("name", descriptionObject.get("name")); 
					current.put("quantity", rs2.getInt("Quantity")); 
					current.put("averageCost", rs2.getDouble("Price")); 
					current.put("totalCost", (double) rs2.getInt("Quantity")*rs2.getDouble("Price")); 
					URL iex = new URL("https://api.tiingo.com/iex/" + currentTicker + "?token=87afef4f31387c86c65345064907579a380822d7");
					URLConnection iexCon = iex.openConnection();
					BufferedReader iexIn = new BufferedReader(new InputStreamReader(iexCon.getInputStream()));
					String iexLine = iexIn.readLine();
					JSONArray iexArray = new JSONArray(iexLine);
					JSONObject iexObj = new JSONObject();
					iexObj = iexArray.getJSONObject(0); 
					double currentPrice = iexObj.getDouble("last"); 
					current.put("currentPrice", currentPrice); 
					current.put("change", currentPrice - rs2.getDouble("price")); 
					current.put("marketValue", currentPrice * rs2.getInt("Quantity")); 
					portfolio.put(currentTicker, current);
				}
			}
			double totalMarketVal = 0.0; 
			for(Map.Entry<String, JSONObject> entry : portfolio.entrySet()) {
				portfolioArray.put(entry.getValue()); 
				totalMarketVal += entry.getValue().getDouble("marketValue");  
			}
			String check = "SELECT * from Users where Username = ?";
			JSONObject balances = new JSONObject(); 
			st = conn.prepareStatement(check);
			st.setString(1, username); 
			ResultSet rs = st.executeQuery();
			double balance = 0.0;
			while(rs.next()) {
				balance = rs.getDouble("Balance"); 
			}
			balances.put("balance", balance);
			balances.put("accountVal", balance + totalMarketVal);
			portfolioArray.put(balances); 
			JSONArray sortedArray = new JSONArray(); 
			List<JSONObject> list = new ArrayList<JSONObject>(); 
			for(int i=0; i < portfolioArray.length(); i++) {
				list.add(portfolioArray.getJSONObject(i));
			}
			Collections.sort(list, new Comparator<JSONObject>() {
				private static final String KEY_NAME = "ticker";
				 @Override
			    public int compare(JSONObject a, JSONObject b) {
			       String str1 = new String();
			       String str2 = new String();
			       try {
			    	   str1 = (String)a.get(KEY_NAME);
			           str2 = (String)b.get(KEY_NAME);
			       } catch(JSONException e) {
			           System.out.println("in here");
			       }
			       return str1.compareTo(str2);
			         }
			});
			for (int i = 0; i < portfolioArray.length(); i++) {
		        sortedArray.put(list.get(i));
		    }
			out.println(sortedArray);
		}
		catch(SQLException sqle){
			System.out.println(sqle.getMessage());
		} 
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JSONException e) {
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
