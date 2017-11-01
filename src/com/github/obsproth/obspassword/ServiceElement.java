package com.github.obsproth.obspassword;

public class ServiceElement {

	private final String serviceName;
	private final int length;
	private final String baseHash;
	private final int version;

	private ServiceElement(String serviceName, int length, String baseHash, int version) {
		this.serviceName = serviceName;
		this.length = length;
		this.baseHash = baseHash;
		this.version = version;
	}

	public ServiceElement(String serviceName, int length, String baseHash) {
		this(serviceName, length, baseHash, ObsPassword.ALGO_VERSION);
	}

	public String getServiceName() {
		return serviceName;
	}

	public int getLength() {
		return length;
	}

	public String getBaseHash() {
		return baseHash;
	}

	public int getVersion() {
		return version;
	}

	public String asCSV(){
		StringBuilder sb = new StringBuilder();
		sb.append(this.serviceName);
		sb.append(',');
		sb.append(this.length);
		sb.append(',');
		sb.append(this.baseHash);
		sb.append(',');
		sb.append(this.version);
		return sb.toString();
	}
	
	public static ServiceElement buildFromCSV(String str) {
		String[] strArr = str.split(",");
		return new ServiceElement(strArr[0], Integer.parseInt(strArr[1]), strArr[2], Integer.parseInt(strArr[3]));
	}

}
