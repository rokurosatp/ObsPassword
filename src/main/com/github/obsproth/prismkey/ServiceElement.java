package com.github.obsproth.prismkey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ServiceElement {

	private final String serviceName;
	private final int length;
	private final String baseHash;
	private final int version;
	public final List<String> config;

	public ServiceElement(String serviceName, int length, String baseHash, int version, List<String> config) {
		this.serviceName = serviceName;
		this.length = length;
		this.baseHash = baseHash.substring(0, HashUtil.BASEHASH_LENGTH);
		this.version = version;
		this.config = config;
	}

	@Deprecated
	public ServiceElement(String serviceName, int length, String baseHash) {
		this(serviceName, length, baseHash, 1, null);
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

	public String asCSV() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.serviceName);
		sb.append(',');
		sb.append(this.length);
		sb.append(',');
		sb.append(this.baseHash);
		sb.append(',');
		sb.append(this.version);
		if (this.config != null) {
			for (String element : this.config) {
				sb.append(',');
				sb.append(element);
			}
		}
		return sb.toString();
	}

	public static ServiceElement buildFromCSV(String str) {
		String[] strArr = str.split(",");
		Iterator<String> iter = Arrays.asList(strArr).iterator();
		String serviceName = iter.next();
		int length = Integer.parseInt(iter.next());
		String baseHash = iter.next();
		int version = Integer.parseInt(iter.next());
		//
		List<String> config = new ArrayList<>();
		while (iter.hasNext()) {
			config.add(iter.next());
		}
		return new ServiceElement(serviceName, length, baseHash, version, config);
	}

}
