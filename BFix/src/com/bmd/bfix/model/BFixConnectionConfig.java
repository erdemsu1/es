package com.bmd.bfix.model;

public class BFixConnectionConfig {
	private int id;
	private String key;
	private String value;
	private int terminalNo;
	private int type;
	private int status;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getTerminalNo() {
		return terminalNo;
	}
	public void setTerminalNo(int terminalNo) {
		this.terminalNo = terminalNo;
	}
}
