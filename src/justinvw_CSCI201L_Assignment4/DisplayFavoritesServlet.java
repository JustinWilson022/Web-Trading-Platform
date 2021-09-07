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
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class DisplayFavoritesServlet
 */
@WebServlet("/DisplayFavoritesServlet")
public class DisplayFavoritesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String databaseURL = "jdbc:mysql://localhost:3306/Assignment4?user=root&password=Justinwilson02"
			+ "&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DisplayFavoritesServlet() {
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
		Vector<String> tickers = new Vector<String>(); 
		JSONArray favoritesArray = new JSONArray();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(databaseURL);
			String check = "SELECT * from Favorites where Username = ?";
			PreparedStatement st = conn.prepareStatement(check);
			st.setString(1, username); 
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				String firstTicker = rs.getString("Ticker"); 
				tickers.add(firstTicker); 
				while(rs.next()) {
					String ticker = rs.getString("Ticker"); 
					tickers.add(ticker); 
				}
				for(int i=0; i < tickers.size(); i++) {
					JSONObject favorite = new JSONObject();
					URL description = new URL("https://api.tiingo.com/tiingo/daily/" + tickers.get(i) + 
							"?token=87afef4f31387c86c65345064907579a380822d7");
					URLConnection con = description.openConnection();
					BufferedReader in =  new BufferedReader(new InputStreamReader(con.getInputStream()));
					String inputLine = in.readLine();
					JSONObject descriptionObject = new JSONObject(inputLine);
					favorite.put("name", descriptionObject.get("name")); 
					favorite.put("ticker", descriptionObject.get("ticker")); 
					URL iex = new URL("https://api.tiingo.com/iex/" + tickers.get(i) + "?token=87afef4f31387c86c65345064907579a380822d7");
					URLConnection iexCon = iex.openConnection();
					BufferedReader iexIn = new BufferedReader(new InputStreamReader(iexCon.getInputStream()));
					String iexLine = iexIn.readLine();
					JSONArray iexArray = new JSONArray(iexLine);
					JSONObject iexObj = new JSONObject();
					iexObj = iexArray.getJSONObject(0); 
					favorite.put("last", iexObj.opt("last")); 
					favorite.put("prevClose", iexObj.opt("prevClose")); 
					favoritesArray.put(favorite); 
				}
				JSONArray sortedArray = new JSONArray(); 
				List<JSONObject> list = new ArrayList<JSONObject>(); 
				for(int i=0; i < favoritesArray.length(); i++) {
					list.add(favoritesArray.getJSONObject(i));
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
				for (int i = 0; i < favoritesArray.length(); i++) {
			        sortedArray.put(list.get(i));
			    }
				out.println(sortedArray); 
			}
			else {
				favoritesArray = null; 
				out.println(favoritesArray); 
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
