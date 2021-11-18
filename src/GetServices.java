import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;


@WebServlet("/GetService")
public class GetServices extends HttpServlet {
	 
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			System.out.println("\\GetService");
			String outp = "";
			PrintWriter out = response.getWriter();
			DatabaseHandler db = new DatabaseHandler();
			
			
			for(int i = 0; i < db.getElementCount("service"); i++) {
				outp += "<h3>" + db.getService(i+1).getServiceName() + "</h3>";
				Service s = db.getService(i+1);
				ArrayList<ResponseCode> responseCodes = db.getServiceResponseCodes(i+1);
				
				outp += "<p>";
					for(int j = 0; j < responseCodes.size(); j++) {
						ResponseCode rs = responseCodes.get(j);
						outp += s.getServiceName() + ": [response code: " + rs.getResponseCode() + "]<br>"; 
				}
				outp += "</p><br><br>";
			}
			out.print(outp);
		}
}
