
import java.net.*;

import java.io.*;

public class LogServiceUptime implements Runnable {

	private static DatabaseHandler db = new DatabaseHandler();
	private Service service;
	private URL url = null;
	
	public LogServiceUptime(Service service) {
		System.out.println("Construct: " + service.getServiceURL());
		this.service = service;
		try {
			this.url = new URL(service.getServiceURL());
		} catch (MalformedURLException mue) {
			System.out.println("Error constructing URL for: " + service.getServiceName());
		}
		
	}
	
	public void run() {
		
		if(url != null) {
			try {
				HttpURLConnection connection = (HttpURLConnection)url.openConnection();
				connection.setRequestMethod("GET");
				connection.connect();
				ResponseCode response = new ResponseCode(-1, service.getServiceId(), connection.getResponseCode());
				db.insertResponseCode(response);
				System.out.println("[" + service.getServiceName() + "] Response code: " + response.getResponseCode());
			} catch(IOException ioe) {
				System.out.println("Error parsing url for: " + service.getServiceName());
			} 
		}
	}
}
