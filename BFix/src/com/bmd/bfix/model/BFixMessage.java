package com.bmd.bfix.model;

public class BFixMessage {
    private int key;
    private String value;
    private String mean;
    
    public BFixMessage(int key, String value,String mean) {
        this.key = key;
        this.value = value;
        this.mean=mean;
    }
    
	public int getKey() {
		return key;
	}
	
	public void setKey(int key) {
		this.key = key;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getMean() {
		return mean;
	}
	
	public void setMean(String mean) {
		this.mean = mean;
	}
}
