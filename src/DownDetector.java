import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
		System.out.println("\n\\DownDetector called");
		PrintWriter out = response.getWriter();
		DatabaseHandler db = new DatabaseHandler();
		long timeDiffInput = new Date().getTime();
			
		try {
			timeDiffInput = Long.parseLong(request.getParameter("timeDiff"));
		} catch (NumberFormatException nfe) {
			out.print("Error: parsing time diff!");
			db.close();
			return;
		}
		String serviceName;
		int serviceIndex = 0;
		try {
			serviceName = request.getParameter("serviceName");
			if (!serviceName.equalsIgnoreCase("null") && !serviceName.equalsIgnoreCase("all")) {
				String[] split = serviceName.split("-");
				serviceIndex = Integer.parseInt(split[1]) -1;
			} else {
				serviceName = null;
			}
		} catch (NumberFormatException nfe) {
			serviceName = null;
		}
				
		// Initialize jobs if they havent been started yet
		BackgroundJobManager bjm = new BackgroundJobManager();
		if(bjm.initiatedJobs == 0) {
			System.out.println("Initializing Service checkers"); 
			bjm.contextInitialized(null);
		} else {
			System.out.println("Ignoring Service checker init"); 
		}
		
		String outp = "";
		
		if (serviceName == null) {
			int numOfServices = db.getLastServiceIndex();
			for(int i = 0; i< numOfServices; i++) {
				String resp = getResponse(i, timeDiffInput);
				if (resp.length() > 0) {
					outp += "<div class=\"status-div\" id=\"service-" + (i+1) +"\">";
					outp += resp;
					outp += "</div><br>";
				}
			}
		} else {
			outp += getResponse(serviceIndex, timeDiffInput);
			outp += "<br>";
		}
		
		out.print(outp);
		db.close();
	}
    
    private String getResponse(int i, long timeDiffInput) {
    	String outp = "";
    	DatabaseHandler db = new DatabaseHandler();
    	Service s = db.getService(i+1);
		
    	if (s == null) {
    		return "";
    	}
    	
		long timeDiff = timeDiffInput;
		//SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Long endTime = new Date().getTime();
		Long startTime = endTime - timeDiff;
		
		if(timeDiffInput == -1) {
			try {
			ResponseCode firstResponse = db.getServiceResponseCodes(i+1).get(0);
			startTime = firstResponse.getResponseDate().getTime();
			} catch (ParseException pe) {
				System.out.println("Error getting time diff");
				timeDiff = 10; //time near epoch
			}
		}		
//		System.out.println("Start time: " + dateSdf.format(startTime));
//		System.out.println("End time: " + dateSdf.format(endTime));
		
		ArrayList<ResponseCode> responseCodes = db.getResponsesBetweenTime(startTime, endTime, i+1);
		
		int onlineCalls = 0;
		for(int t = 0; t < responseCodes.size(); t++) {
			if(responseCodes.get(t).getResponseCode() < 400) {
				onlineCalls++;
			}
		}
				
		// Top bar information (name and online status
		outp += "<div class=\"status-box-info\" id=\"" + s.getServiceId() + "-info-box\">";
		outp += "<span style=\"float:left\">" + s.getServiceName() + "</span>";
		
		
		ResponseCode latestResponse;
		
		if (responseCodes.size() > 0) {
			latestResponse = responseCodes.get(responseCodes.size()-1);
		}else {
			latestResponse = new ResponseCode(-1, -1, -1, "0");
		}
		
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
		for(int j = 0; j < numOfBoxes; j++) {
			String boxColor = "grey";
			boolean hasOffline = false;
			boolean hasOnline = false;
			ArrayList<ResponseCode> codes = getResponseCodesForStatusBodIndex(startTime,j,numOfBoxes, db,i+1);
			if (codes.size() > 0) {
				for(int k = 0; k < codes.size(); k++) {
					if (codes.get(k).getResponseCode() < 400) {
						hasOnline = true;
					} else {
						hasOffline = true;
					}
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
		outp += "<select name=\"from\" id=\"from\" onChange=getStatus(this.value,'service-"+ s.getServiceId() +"')>";
		
		outp += constructSelectMenu("All Time",-1,timeDiffInput);
		outp += constructSelectMenu("Last 90 Days",7776000000L,timeDiffInput);
		outp += constructSelectMenu("Last 30 Days",2592000000L,timeDiffInput);
		outp += constructSelectMenu("Last 7 Days",604800000L,timeDiffInput);
		outp += constructSelectMenu("Last 24 Hours",86400000L,timeDiffInput);
		
		outp += "</select>";
		outp += "</div>";
		outp += "<div style=\"float: right\">";
		outp += "<span>Today</span></div>";
		outp += "<div style=\"text-align: center\">";
		outp += "<span>Uptime "+ df.format(onlinePercent)+"%</span>";
		outp += "</div></div>";
		
		db.close();
		return outp;
    }
    
    private ArrayList<ResponseCode> getResponseCodesForStatusBodIndex(Long statusBoxStartTime, 
    		int currIndex, int totalNumOfStatusBoxes, DatabaseHandler db, int servicerId) {
    		
    	Long timeNow = new Date().getTime();
    	Long timeDiff = timeNow - statusBoxStartTime;
		Long timeDelta = timeDiff/totalNumOfStatusBoxes;
		Long startTime = statusBoxStartTime + timeDelta*currIndex;
		Long endTime = statusBoxStartTime + timeDelta*(currIndex+1);
		
		return db.getResponsesBetweenTime(startTime, endTime, servicerId);
    }
    
    private String constructSelectMenu(String text, long value, long currTimeDiffInput) {
    	if(value == currTimeDiffInput) {
    		return "<option value=\""+ value +"\" selected=\"selected\">"+text+"</option>";
    	}else {
    		return "<option value=\""+ value +"\">"+text+"</option>";
    	}
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