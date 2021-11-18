import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.text.DecimalFormat;

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
		
		// Initialize jobs if they havent been started yet
		BackgroundJobManager bjm = new BackgroundJobManager();
		if(bjm.initiatedJobs == 0) {
			System.out.println("Initializing Service checkers"); 
			bjm.contextInitialized(null);
		} else {
			System.out.println("Ignoring Service checker init"); 
		}
		
		int numOfServices = db.getElementCount("service");
		for(int i = 0; i< numOfServices; i++) {
			Service s = db.getService(i+1);
			ArrayList<ResponseCode> responseCodes = db.getServiceResponseCodes(i+1);
			
			int onlineCalls = 0;
			for(int t = 0; t < responseCodes.size(); t++) {
				if(responseCodes.get(t).getResponseCode() < 400) {
					onlineCalls++;
				}
			}
			outp += "<div class=\"status-div\" id=\"" + s.getServiceName() +"\">";
			
			// Top bar information (name and online status
			outp += "<div class=\"status-box-info\" id=\"" + s.getServiceName() + "\">";
			outp += "<span style=\"float:left\">" + s.getServiceName() + "</span>";
			
			
			ResponseCode latestResponse = responseCodes.get(responseCodes.size()-1);
			
			String statusColor = "yellow";
			String OnlineStatus = "Unresolved!!";
			if(latestResponse.getResponseCode() < 400) {
				statusColor = "green";
				OnlineStatus = "Online"; 
			} else {
				statusColor = "red";
				OnlineStatus = "Offline"; 
			}

			outp += "<span style=\"float:right;color:"+statusColor+"\">" + OnlineStatus + "</span>";
			outp += "</div>";
			outp += "<hr>";
			
			// Status boxes over time 
			outp += "<div class='status-box-container'>";
			
			int numOfBoxes = 40;
			double responsesPerBox = (double)responseCodes.size()/(double)numOfBoxes;
			//System.out.println("NUM OF RESPONSES PER BOX!!!: " + responsesPerBox + " || Total responses: " + responseCodes.size());
			for(int j = 0; j < numOfBoxes; j++) {
				String boxColor = "purple";
				int startIndex = (int)(responsesPerBox * j);
				boolean hasOffline = false;
				boolean hasOnline = false;
				if (responsesPerBox > 0) {
					for(int k = 0; k < responsesPerBox; k++) {
						int index = startIndex + k;
						if (responseCodes.get(index).getResponseCode() < 400) {
							hasOnline = true;
						} else {
							hasOffline = true;
						}
					}
				} else {
					int index = startIndex;
					if (responseCodes.get(index).getResponseCode() < 400) {
						hasOnline = true;
					} else {
						hasOffline = true;
					}
				}
				if(hasOffline && hasOnline) {
					boxColor = "orange";
				} else if (hasOffline) {
					boxColor = "red";
				} else if (hasOnline) {
					boxColor = "green";
				}
				outp += "<div class='status-box-"+boxColor+"'></div>";	
			}
			outp += "</div>";
			
			// Footer
			double onlinePercent = (double)onlineCalls*100.0/(double)responseCodes.size();
			DecimalFormat df = new DecimalFormat("###.#");
			outp += "<div class='status-date-range'>";
			outp += "<div style=\"float: left\">";
			outp += "<select name=\"from\" id=\"from\">";
			outp += "<option value=\"all-time\">All Time</option>";
			outp += "<option value=\"90-days\">Last 90 Days</option>";
			outp += "<option value=\"30-days\" selected=\"selected\">Last 30 Days</option>";
			outp += "<option value=\"7-days\">Last 7 Days</option>";
			outp += "<option value=\"24-hours\">Last 24 Hours</option>";
			outp += "</select>";
			outp += "</div>";
			outp += "<div style=\"float: right\">";
			outp += "<span>Today</span></div>";
			outp += "<div style=\"text-align: center\">";
			outp += "<span>Uptime "+ df.format(onlinePercent)+"%</span>";
			outp += "</div></div>";

			outp += "</div><br>";	
		}
		
		out.print(outp);
	}	
}

/*
<div class="status-div">
<div class='status-box-info'>
	<span style="float:left">Name Of Item</span>
	<span style="float: right; color:green;">Status</span>
</div>
<hr>
<div class='status-box-container'>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
	<div class='status-box-green'></div>
</div>
<div class='status-date-range'>
	  <div style="float: left">
		  <select name="from" id="from">
		    <option value="all-time">All Time</option>
		    <option value="90-days">Last 90 Days</option>
		    <option value="30-days" selected="selected">Last 30 Days</option>
		    <option value="7-days">Last 7 Days</option>
		    <option value="24-hours">Last 24 Hours</option>
		  </select>
	  </div>
	  <div style="float: right">
	  	<span>Today</span></div>
	  <div style="text-align: center">
	  	<span>Uptime 100%</span>
	  </div>
</div>
</div>
*/