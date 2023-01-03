import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


// url --> http://localhost:8081/BookStoreBackend/books

@WebServlet(name = "Books", urlPatterns = {"/books"}) 
public class Books extends HttpServlet {
	
	private String getBookId(String queryString) {
		if(queryString == null) return null;
		// split the string by key-value pairs
		String[] pairs = queryString.split("&");
		for(String pair : pairs) {
			// if we find a pair with "id" as the key
			if(pair.contains("id=")) {
				return pair.split("=")[1]; // return the value
			}
		}
		return null;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json;charset=UTF-8"); 
		
		try (PrintWriter out = response.getWriter()) {
			// make a request to the Google Books API
			
			StringBuilder responseBody = new StringBuilder();
			

			String urlString = "https://www.googleapis.com/books/v1/volumes?q=ui%20design&filter=paid-ebooks&maxResults=40";
			
			String bookId = getBookId(request.getQueryString());
			
			if(bookId != null) {
				urlString = "https://www.googleapis.com/books/v1/volumes/" + bookId; 
			}
		
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			// set up request headers
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/json");
			// read response data
			try(BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				for(String l; (l = buffer.readLine()) != null;) {
					responseBody.append(l);
				}
			} catch (Exception e) {
				String errorAsJSON = new String(connection.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
				out.println(errorAsJSON);
				
			}
			// send it back to our frontend
			out.println(responseBody);
		}
		
     }
}
