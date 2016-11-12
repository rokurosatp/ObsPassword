package com.github.obsproth.obspassword;

public class ServiceElement {

	private final String serviceName;
	private final int length;
	private final String baseHash;
	private final int version;

	public ServiceElement(String serviceName, int length, String baseHash) {
		this.serviceName = serviceName;
		this.length = length;
		this.baseHash = baseHash;
		this.version = ObsPassword.VERSION;
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

}
