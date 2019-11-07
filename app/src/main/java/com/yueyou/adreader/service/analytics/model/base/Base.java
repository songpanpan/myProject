package com.yueyou.adreader.service.analytics.model.base;

import java.util.Date;

public class Base {
	private int id;
	private String appId;
	private String channelId;
	private String deviceId;
	private int platId;
	private String version;
	private String ip;
	private String imei;
	private String androidId;
	private String mac;
	private String oaid;
	private Date insdt;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public int getPlatId() {
		return platId;
	}

	public void setPlatId(int platId) {
		this.platId = platId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getAndroidId() {
		return androidId;
	}

	public void setAndroidId(String androidId) {
		this.androidId = androidId;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getOaid() {
		return oaid;
	}

	public void setOaid(String oaid) {
		this.oaid = oaid;
	}

	public Date getInsdt() {
		return insdt;
	}

	public void setInsdt(Date insdt) {
		this.insdt = insdt;
	}

}
