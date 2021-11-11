import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Servlet implementation class DownDetector
 */
@WebServlet("/DownDetector")
public class DownDetector extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DownDetector() {
        super();
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("\\DownDetector called");
		String outp = "";
		PrintWriter out = response.getWriter();
		DatabaseHandler db = new DatabaseHandler();
		
		BackgroundJobManager bjm = new BackgroundJobManager();
		if(bjm.initiatedJobs == 0) {
			System.out.println("Initializing Service checkers"); 
			bjm.contextInitialized(null);
		} else {
			System.out.println("Ignoring Service checker init"); 
		}
		
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
