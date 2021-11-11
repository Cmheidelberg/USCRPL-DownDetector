public class Service {
	private int serviceId;
	private String serviceName;
	private String serviceURL;
	
	public Service(int serviceId, String serviceName, String serviceURL) {
		this.serviceId = serviceId;
		this.serviceName = serviceName;
		this.serviceURL = serviceURL;
	}
	
	public int getServiceId() {
		return serviceId;
	}
	
	public String getServiceName() {
		return serviceName;
	}
	
	public String getServiceURL() {
		return serviceURL;
	}
}
