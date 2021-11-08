
import java.net.*;
import java.io.*;

public class LogServiceUptime implements Runnable {

	
	private URL url = null;
	private String ip;
	
	public LogServiceUptime(String ip) {
		System.out.println("Construct: " + ip);
		this.ip = ip;
		try {
			this.url = new URL(ip);
		} catch (MalformedURLException mue) {
			System.out.println("Error constructing URL for: " + ip);
		}
		
	}
	
	public void run() {
		
		if(url != null) {
			try {
				HttpURLConnection connection = (HttpURLConnection)url.openConnection();
				connection.setRequestMethod("GET");
				connection.connect();

				int code = connection.getResponseCode();
				System.out.println("[" + ip + "] Response code: " + code);
			} catch(IOException ioe) {
				System.out.println("Error parsing url for: " + ip);
			} 
		}
	}
}
