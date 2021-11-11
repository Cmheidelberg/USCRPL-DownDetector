import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AddService")
public class AddService extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static DatabaseHandler db = new DatabaseHandler();
	
    public AddService() {
        super();
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	System.out.println("\\AddService called");		
		db.initializeService(request.getParameter("name"), request.getParameter("url"));
		BackgroundJobManager bjm = new BackgroundJobManager();		
		bjm.addContext(db.getElementCount("service"));
		}
				

}