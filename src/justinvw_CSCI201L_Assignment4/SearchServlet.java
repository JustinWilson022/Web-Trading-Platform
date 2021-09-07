package justinvw_CSCI201L_Assignment4;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class SearchServlet
 */
@WebServlet("/SearchServlet")
public class SearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	JSONArray pricesArray;  
	PrintWriter out;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {	
			//response.getWriter().append("Served at: ").append(request.getContextPath());
			response.setContentType("application/json");
			out = response.getWriter();
			String ticker = request.getParameter("ticker"); 
			URL pricesUrl = new URL("https://api.tiingo.com/tiingo/daily/" + ticker + 
				"/prices?token=87afef4f31387c86c65345064907579a380822d7"); 
			URLConnection con = pricesUrl.openConnection();
			BufferedReader in =  new BufferedReader(new InputStreamReader(con.getInputStream()));  
			String inputLine = in.readLine();
			pricesArray = new JSONArray(inputLine);
			URL description = new URL("https://api.tiingo.com/tiingo/daily/" + ticker + 
					"?token=87afef4f31387c86c65345064907579a380822d7");
			URLConnection newCon = description.openConnection();
			BufferedReader newIn = new BufferedReader(new InputStreamReader(newCon.getInputStream()));
			String line = newIn.readLine(); 
			JSONObject descriptionObject = new JSONObject(line); 
			URL iex = new URL("https://api.tiingo.com/iex/" + ticker + "?token=87afef4f31387c86c65345064907579a380822d7");
			URLConnection iexCon = iex.openConnection();
			BufferedReader iexIn = new BufferedReader(new InputStreamReader(iexCon.getInputStream()));
			String iexLine = iexIn.readLine();
			JSONArray iexArray = new JSONArray(iexLine);
			pricesArray.put(descriptionObject);
			pricesArray.put(iexArray.get(0)); 
			System.out.println(pricesArray); 
			out.println(pricesArray);
			out.flush();
		}
		catch(FileNotFoundException fnfe) {
			pricesArray = new JSONArray();
			out.println(pricesArray);
		}
		catch(JSONException e) {
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
