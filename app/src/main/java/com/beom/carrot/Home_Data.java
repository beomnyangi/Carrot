package com.beom.carrot;

public class Home_Data {
	private static final String TAG= "log";

	String post_id;
	String account_id;
	String image;
	String title;
	String town_name;
	String uploaded_time;
	String upload_count;
	String price;
	String chatting_count;
	String heart_count;
	String price_offer_check;
	String views;
	String viewer_id;
	String SalesStatus;
	
	public String getPost_id() {
		return post_id;
	}
	public String getAccount_id() {
		return account_id;
	}
	public String getImage() {
		return image;
	}
	public String getTitle() {
		return title;
	}
	public String getTown_name() {
		return town_name;
	}
	public String getUploaded_time() {
		return uploaded_time;
	}
	public String getUpload_count() {
		return upload_count;
	}
	public String getPrice() {
		return price;
	}
	public String getChatting_count() {
		return chatting_count;
	}
	public String getHeart_count() {
		return heart_count;
	}
	public String getPrice_offer_check() {
		return price_offer_check;
	}
	public String getViews() {
		return views;
	}
	public String getViewer_id() {
		return viewer_id;
	}
	public String getSalesStatus() {
		return SalesStatus;
	}
	
	public Home_Data(String post_id, String account_id, String image, String title, String town_name, String uploaded_time, String upload_count, String price, String chatting_count, String heart_count, String price_offer_check, String views, String viewer_id, String SalesStatus){
		this.post_id = post_id;
		this.account_id = account_id;
		this.image = image;
		this.title = title;
		this.town_name = town_name;
		this.upload_count = upload_count;
		this.uploaded_time = uploaded_time;
		this.price = price;
		this.chatting_count = chatting_count;
		this.heart_count = heart_count;
		this.price_offer_check = price_offer_check;
		this.views = views;
		this.viewer_id = viewer_id;
		this.SalesStatus = SalesStatus;
	}
}