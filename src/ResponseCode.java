import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ResponseCode {

	private int ping_id;
	private int service_id;
	private int response_code;
	private String response_date_string;
	
	public ResponseCode(int print_id, int service_id, int response_code, String response_date_string) {
		this.ping_id = print_id;
		this.service_id = service_id;
		this.response_code = response_code;
		this.response_date_string = response_date_string;
	}
	
	public int getPingId() {
		return ping_id;
	}
	
	public int getServiceId() {
		return service_id;
	}
	
	public int getResponseCode() {
		return response_code;
	}
	
	public Date getResponseDate() throws ParseException {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dateCreated = format.parse(response_date_string);
		return dateCreated;
	}
	
}
