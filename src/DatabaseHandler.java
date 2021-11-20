import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.io.File;  
import java.io.FileNotFoundException;
import java.util.Scanner; 
public class DatabaseHandler {

	Connection conn = null;
	Statement st = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	final String DB_FILE_NAME = "database_connection_line.txt";

	
	public DatabaseHandler() {
		try {
			String line = "jdbc:mysql://localhost/down_detector?user=root&password=root";
		    if (line != null) {
				Class.forName("com.mysql.cj.jdbc.Driver");
				conn = DriverManager.getConnection(line);
				st = conn.createStatement();
		    }			
		} catch(SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		} catch (ClassNotFoundException cnfe) {
			System.out.println ("ClassNotFoundException: " + cnfe.getMessage());
		}
	}
	
	
	public ResultSet getResultSet(String querery) {
		try {
			return st.executeQuery(querery);
		} catch (SQLException sqle) {
			System.out.println("sqle: " + sqle.getMessage());
		}
		return null;
	}
	
	
	public int getElementCount(String table) {
		String querey = "SELECT COUNT(*) FROM " + table;
		System.out.println(querey);
		
		int count = -1;
		try {
			ResultSet rs = getResultSet(querey);
			if(rs.next()) {
				count = rs.getInt("COUNT(*)");
			}else {
				System.out.println("Could not get count from "+table+"???");
			}
		} catch (SQLException sqle) {
			System.out.println("SQLE: " + sqle);
		}
		return count;
	}
	
	public int getLastServiceIndex() {
		String querey = "SELECT * FROM Service ORDER BY service_id DESC LIMIT 1";
		System.out.println(querey);
		
		int count = -1;
		try {
			ResultSet rs = getResultSet(querey);
			if(rs.next()) {
				count = rs.getInt("service_id");
			}else {
				System.out.println("Error: Could not get last element from service table.");
			}
		} catch (SQLException sqle) {
			System.out.println("SQLE: " + sqle);
		}
		return count;
	}
	
	
	public boolean initializeService(String serviceName, String serviceURL) {
		
		int count = getElementCount("service");
		String querey = String.format("INSERT INTO service (`service_id`, `service_name`, `service_url`) VALUES ('%s', '%s', '%s')",count+1,serviceName,serviceURL);
		System.out.println(querey);
		
		try {
			st.execute(querey);
			return true;
		} catch (SQLException sqle) {
			System.out.println("SQLE: " + sqle);
			return false;
		}
	}
	
	
	public ArrayList<ResponseCode> getResponsesBetweenTime(Long start, Long end, int serviceId) {
		SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String startTime = dateSdf.format(start);
		String endTime = dateSdf.format(end);
		
		String querey = String.format("SELECT * FROM uptime WHERE service_id=%s AND ping_date >= \"%s\" AND ping_date <= \"%s\"",serviceId, startTime, endTime);
		//System.out.println(querey);
		
		ArrayList<ResponseCode> responseCodes = new ArrayList<ResponseCode>();
		try {
			ResultSet rs = getResultSet(querey);
			
			while(rs.next()) {
				//System.out.println("ServiceID: " + rs.getInt("service_id") + " | Response_code: " + rs.getInt("response_code"));
					
				int pid = rs.getInt("service_id");
				int rid = rs.getInt("response_code");
				String  date = rs.getString("ping_date");
				ResponseCode responseCode = new ResponseCode(pid, serviceId, rid, date);
				responseCodes.add(responseCode);
			}
		} catch (SQLException sqle) {
			System.out.println("SQLE: " + sqle);
		} catch (NullPointerException npe) {
			//System.out.println("Returned null!");
			return responseCodes;
		}
		return responseCodes;
	}
	
	
	public boolean insertResponseCode(ResponseCode responseCode) {
		int count = getElementCount("uptime");
		
		SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Long timeNow = new Date().getTime();

		int code = responseCode.getResponseCode();
		int serviceId = responseCode.getServiceId();

		String querey = String.format("INSERT INTO uptime (`ping_id`, `service_id`, `ping_date` , `response_code`) VALUES ('%s', '%s', '%s',  '%s')",count+1,serviceId,dateSdf.format(timeNow),code);
		System.out.println(querey);
		
		try {
			st.execute(querey);
			return true;
		} catch (SQLException sqle) {
			System.out.println("SQLE: " + sqle);
			return false;
		}
	}
	
	
	public Service getService(int serviceId) {
		String querey = "SELECT * FROM service WHERE service_id=" + serviceId;
		System.out.println(querey);
		
		Service service = null;
		try {
			ResultSet rs = getResultSet(querey);
			if(rs.next()) {
				
				String name = rs.getString("service_name");
				String url = rs.getString("service_url");
				service = new Service(serviceId, name, url);
			}else {
				System.out.println("Could not get count???");
			}
		} catch (SQLException sqle) {
			System.out.println("SQLE: " + sqle);
		}
		return service;
	}
	
	
	public ArrayList<ResponseCode> getServiceResponseCodes(int serviceId) {
		String querey = "SELECT * FROM uptime WHERE service_id=" + serviceId;
		System.out.println(querey);
		
		ArrayList<ResponseCode> responseCodes = new ArrayList<ResponseCode>();
		try {
			ResultSet rs = getResultSet(querey);
			while(rs.next()) {
				//System.out.println("ServiceID: " + rs.getInt("service_id") + " | Response_code: " + rs.getInt("response_code"));
					
				int pid = rs.getInt("service_id");
				int rid = rs.getInt("response_code");
				String date = rs.getString("ping_date");
				
				ResponseCode responseCode = new ResponseCode(pid, serviceId, rid, date);
				responseCodes.add(responseCode);
			}
		} catch (SQLException sqle) {
			System.out.println("SQLE: " + sqle);
		}	
		return responseCodes;
	}
	
	
	public void close() {
		try {
			if (rs != null) {
				rs.close();
			}
			if (st != null) {
				st.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException sqle) {
			System.out.println("sqle: " + sqle.getMessage());
		}
	}
}
