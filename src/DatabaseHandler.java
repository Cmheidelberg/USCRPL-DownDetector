import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DatabaseHandler {

	Connection conn = null;
	Statement st = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	
	public DatabaseHandler() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/down_detector?user=root&password=root");
			st = conn.createStatement();
			
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
				
				ResponseCode responseCode = new ResponseCode(pid, serviceId, rid);
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
