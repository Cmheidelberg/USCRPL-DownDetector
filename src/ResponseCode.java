
public class ResponseCode {

	private int ping_id;
	private int service_id;
	private int response_code;
	
	public ResponseCode(int print_id, int service_id, int response_code) {
		this.ping_id = print_id;
		this.service_id = service_id;
		this.response_code = response_code;
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
	
}
