package com.beom.carrot;

public class SearchLocation_Data {
	private static final String TAG= "log";

	String address_name;
	String town_name;
	String x;
	String y;

	public String getAddress_name() {
		return address_name;
	}
	public String getTown_name() {
		return town_name;
	}
	public String getX() {
		return x;
	}
	public String getY() {
		return y;
	}

	public SearchLocation_Data(String address_name, String town_name, String x, String y){
		this.address_name = address_name;
		this.town_name = town_name;
		this.x = x;
		this.y = y;
	}
}