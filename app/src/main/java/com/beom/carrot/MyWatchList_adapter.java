package com.beom.carrot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class MyWatchList_adapter extends RecyclerView.Adapter<MyWatchList_adapter.ViewHolder> {

	Context context;
	LayoutInflater inflacter;
	
	String post_id;
	String viewer_id;
	String views;
	String views_count;
	String like_check;
	String up_down;
	String like_count;
	
	int layout;
	int likes_cal;
	
	// adapter에 들어갈 list
	public static ArrayList<MyWatchList_data> MyWatchList_info = new ArrayList<>();

	//MainActivity 에서 context, info 받아오기
	public MyWatchList_adapter(Context context, int layout){
		this.context = context;
		inflacter = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.layout = layout;
		
	}
	
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		//recycler_view_item.xml을 inflate 시킨다.
		View view = LayoutInflater.from(context).inflate(R.layout.activity_my_watch_list_item, parent, false);
		
		return new ViewHolder(view);
	}
	
	/*onbindviewholder 란 ListView / RecyclerView 는 inflate를 최소화 하기 위해서 뷰를 재활용 하는데,
	이 때 각 뷰의 내용을 업데이트 하기 위해 findViewById 를 매번 호출 해야합니다.
	이로 인해 성능저하가 일어남에 따라 ItemView의 각 요소를 바로 엑세스 할 수 있도록 저장해두고 사용하기 위한 객체입니다.*/
	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
		//list 에 아이템 하나하나 보여주는 메소드 입니다.
		MyWatchList_data item = MyWatchList_info.get(position);
		
		System.out.println("테스트"+item.getTitle());
		
		/*String으로 된 이미지 정보를 표시하기 위해 분해*/
		String array_uri = item.getImage();
		array_uri = array_uri.replace("[", "");
		array_uri = array_uri.replace("]", "");
		array_uri = array_uri.replace("\"", "");
		
		String [] stringArray= array_uri.split(",");
		for(int j=0; j < 1; j++){
			/*그래들 라이브러리를 이용해 홀더에 이미지 세팅*/
			Glide.with(holder.itemView.getContext())
				.load("http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/UsedTradePost_Image/"+stringArray[j])
				.fitCenter()
				.into(holder.iv_image);
		}
		
		holder.tv_title.setText(item.getTitle());
		holder.tv_town.setText(item.getTown_name());
		holder.tv_price.setText(item.getPrice());
		holder.tv_SalesStatus.setText(item.getSalesStatus());
		
		if(Objects.equals(item.getSalesStatus(), "판매중")){
			holder.tv_SalesStatus.setVisibility(View.GONE);
		}
		if(Objects.equals(item.getSalesStatus(), "예약중")){
			holder.tv_SalesStatus.setVisibility(View.VISIBLE);
			
			holder.tv_SalesStatus.setTextColor(Color.GREEN);
		}
		if(Objects.equals(item.getSalesStatus(), "거래완료")){
			holder.tv_SalesStatus.setVisibility(View.VISIBLE);
			holder.tv_SalesStatus.setTextColor(Color.GRAY);
		}
		
		if(Integer.parseInt(item.getHeart_count()) < 1){
			holder.iv_heart.setVisibility(View.GONE);
			holder.tv_heart_count.setVisibility(View.GONE);
		}
		if(Integer.parseInt(item.getHeart_count()) > 0){
			holder.iv_heart.setVisibility(View.VISIBLE);
			holder.tv_heart_count.setVisibility(View.VISIBLE);
			holder.tv_heart_count.setText(item.getHeart_count());
		}
		if(Integer.parseInt(item.getChatting_count()) < 1){
			holder.iv_chatting.setVisibility(View.GONE);
			holder.tv_chatting_count.setVisibility(View.GONE);
		}
		if(Integer.parseInt(item.getChatting_count()) > 0){
			holder.iv_chatting.setVisibility(View.VISIBLE);
			holder.iv_chatting.setVisibility(View.VISIBLE);
			holder.tv_chatting_count.setText(item.getChatting_count());
		}
		
	}
	
	//리스트의 아이템 갯수
	@Override
	public int getItemCount() {
		return MyWatchList_info.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder {
		ImageView iv_image;
		TextView tv_title;
		TextView tv_town;
		TextView tv_price;
		TextView tv_heart_count;
		TextView tv_chatting_count;
		TextView tv_SalesStatus;
		
		ImageView iv_chatting;
		ImageView iv_heart;
		ImageView iv_heart_icon;
		
		ConstraintLayout lo_heart_icon;
		
		@SuppressLint("ClickableViewAccessibility")
		ViewHolder(final View view) {
			super(view);
			
			tv_title = view.findViewById(R.id.tv_title);
			tv_town = view.findViewById(R.id.tv_town);
			tv_price = view.findViewById(R.id.tv_price);
			tv_heart_count = view.findViewById(R.id.tv_heart_count);
			tv_chatting_count = view.findViewById(R.id.tv_chatting_count);
			
			iv_image = view.findViewById(R.id.iv_image);
			iv_chatting = view.findViewById(R.id.iv_chatting);
			iv_heart = view.findViewById(R.id.iv_heart);
			iv_heart_icon = view.findViewById(R.id.iv_heart_icon);
			tv_SalesStatus = view.findViewById(R.id.tv_SalesStatus);
			
			lo_heart_icon = view.findViewById(R.id.lo_heart_icon);
			
			/*하트버튼 눌렀을 때 좋아요 기능 작동*/
			lo_heart_icon.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					
					switch (event.getAction()){
						case MotionEvent.ACTION_DOWN: // ACTION_DOWN : 터치를 누르는 순간
							Log.i("tag","터치를 누르는 순간");
							
							/*int pos = getAdapterPosition() ; // 선택 한 위치 저장
							Log.i("tag","heart_pos : "+pos);
							
							Log.i("taggg","heart_pos : "+pos);
							MyWatchList_data location_data;
							location_data = MyWatchList_info.get(pos);
							
							Log.i("taggg","location_data : "+location_data);
							
							// 선택된 게시글 id를 가져와 변수에 저장
							post_id = location_data.getPost_id();
							Log.i("tag","post_id : "+post_id);
							// 선택된 게시글을 보는 사용자의 조회수를 가져와 변수에 저장
							views = location_data.getViews();
							Log.i("tag","views : "+views);
							// 선택된 게시글을 보는 사용자의 id를 가져와 변수에 저장
							viewer_id = location_data.getViewer_id();
							Log.i("tag","phone_number : "+viewer_id);
							
							*//*서버 데이터베이스에서 게시물 좋아요 여부 가져와 아이콘에 표시*//*
							Thread like_check_thread = new Thread(new like_check_thread());
							like_check_thread.start();*/
							
							break;
							
						case MotionEvent.ACTION_UP: // ACTION_UP : 터치를 때는 순간
							Log.i("tag","터치를 떼는 순간");
							
							int pos = getAdapterPosition() ; // 선택 한 위치 저장
							Log.i("tag","heart_pos : "+pos);
							
							Log.i("taggg","heart_pos : "+pos);
							MyWatchList_data location_data;
							location_data = MyWatchList_info.get(pos);
							Log.i("taggg","location_data : "+location_data);
							
							// 선택된 게시글 id를 가져와 변수에 저장
							post_id = location_data.getPost_id();
							Log.i("tag","post_id : "+post_id);
							// 선택된 게시글을 보는 사용자의 조회수를 가져와 변수에 저장
							views = location_data.getViews();
							Log.i("tag","views : "+views);
							// 선택된 게시글을 보는 사용자의 id를 가져와 변수에 저장
							viewer_id = location_data.getViewer_id();
							Log.i("tag","phone_number : "+viewer_id);
							
							/*서버 데이터베이스에서 게시물 좋아요 여부 가져오기*/
							Thread like_check_thread = new Thread(new like_check_thread());
							like_check_thread.start();
							
							/*서버 데이터베이스에서 게시물 좋아요 개수 가져오기*/
							Thread like_count_check = new Thread(new like_count_check());
							like_count_check.start();
							
							new Handler().postDelayed(new Runnable()
							{
								@Override
								public void run()
								{
									//딜레이 후 시작할 코드 작성
									if(Objects.equals(like_check, "")){
										Log.i("tag","좋아요");
//								Log.i("tag",like_check);
										
										up_down = "up";
										
										Thread like_insert = new Thread(new like_insert());
										like_insert.start();
										
										/*서버 데이터베이스에서 게시물 좋아요 개수 가져오기*/
										Thread like_count_check = new Thread(new like_count_check());
										like_count_check.start();
										
										iv_heart_icon.setImageResource(R.drawable.heart_red_icon);
										
									}
									else{
										Log.i("tag","좋아요 취소");
//								Log.i("tag",like_check);
										
										System.out.println("tag"+like_check);
										
										up_down = "down";
										
										Thread like_delete = new Thread(new like_delete());
										like_delete.start();
										
										/*서버 데이터베이스에서 게시물 좋아요 개수 가져오기*/
										Thread like_count_check = new Thread(new like_count_check());
										like_count_check.start();
										
										iv_heart_icon.setImageResource(R.drawable.heart_icon);
										
									}
								}
							}, 500);// 0.5초 정도 딜레이를 준 후 시작
							
							/*if(Objects.equals(like_check, "")){
								Log.i("tag","좋아요");
//								Log.i("tag",like_check);
								
								up_down = "up";
								
								Thread like_insert = new Thread(new like_insert());
								like_insert.start();

								*//*서버 데이터베이스에서 게시물 좋아요 개수 가져오기*//*
								Thread like_count_check = new Thread(new like_count_check());
								like_count_check.start();
								
								iv_heart_icon.setImageResource(R.drawable.heart_red_icon);
								
							}
							else{
								Log.i("tag","좋아요 취소");
//								Log.i("tag",like_check);
								
								System.out.println("tag"+like_check);
								
								up_down = "down";
								
								Thread like_delete = new Thread(new like_delete());
								like_delete.start();

								*//*서버 데이터베이스에서 게시물 좋아요 개수 가져오기*//*
								Thread like_count_check = new Thread(new like_count_check());
								like_count_check.start();
								
								iv_heart_icon.setImageResource(R.drawable.heart_icon);
								
							}*/
							
							break;
						
						case MotionEvent.ACTION_MOVE: // ACTUIB_MOVE : 터치되고 있는 순간
							Log.i("tag","터치되고 있는 순간");
							break;
					}
					return true;
				}
			});
			
			//아이템을 클릭 했을 때
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					int pos = getAdapterPosition() ; // 선택 한 위치 저장
					Log.i("tag","pos : "+pos);
					
//					Toast.makeText(v.getContext(), "클릭 됨 : "+pos, Toast.LENGTH_SHORT).show();
					
					// 아이템이 선택 됐을 때 실행
					if (pos != RecyclerView.NO_POSITION) {
						// 선택 된 아이템 정보를 가져오기위해 선언 후 선택 포지션 값 저장
						MyWatchList_data location_data;
						location_data = MyWatchList_info.get(pos);
						
						// 선택된 게시글 id를 가져와 변수에 저장
						post_id = location_data.getPost_id();
						Log.i("tag","post_id : "+post_id);
						// 선택된 게시글을 보는 사용자의 조회수를 가져와 변수에 저장
						views = location_data.getViews();
						Log.i("tag","views : "+views);
						// 선택된 게시글을 보는 사용자의 id를 가져와 변수에 저장
						viewer_id = location_data.getViewer_id();
						Log.i("tag","phone_number : "+viewer_id);
						
						
						//조회수를 업데이트 한 후 게시글 상세보기로 이동
						Thread search_HomePost_detail = new Thread(new search_HomePost_detail());
						search_HomePost_detail.start();
						
					}
					else{
						Log.i("tag","리사이클러뷰 포지션 선택 안 됨");
					}
					
				}
			});
		}
	}
	
	/*게시물 정보에 좋아요 갯수 업데이트*/
	class update_like implements Runnable {
		@Override
		public void run() {
			try {
				
				String page = "http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/update_UsedTradePost.php"; // 연결 할 URL 주소
				//String page = "https://webhook.site/ab420874-fd80-48a6-9b3b-1750ebfdb40f"; // 연결 할 URL 주소
				
				URL url = new URL(page); // URL 객체 생성
				
				HttpURLConnection conn = (HttpURLConnection)url.openConnection(); // 연결 객체 생성
				
				
				
				if(Objects.equals(up_down, "up")){
					likes_cal = Integer.parseInt(like_count) + 1;
				}
				else if(Objects.equals(up_down, "down")){
					likes_cal = Integer.parseInt(like_count) - 1;
				}
				else{
					Log.i("tag", "else 진입");
				}
				
				
				// 서버에 전달 할 파라미터
				String params = "phone_number=" + viewer_id;
				params += "&post_id=" + post_id;
				params += "&like_count=" + likes_cal;
				
				StringBuilder responseBody = new StringBuilder(); // 서버에서 받은 결과값을 저장 할 문자열
				
				// 연결되면 실행
				if(conn != null) {
					Log.i("tag", "conn 연결");
					
					conn.setRequestProperty("Accept", "application/json"); // 서버에서 받을 데이터 타입 설정 (json | xml)
					conn.setConnectTimeout(10000); // 서버 접속시 연결 타임아웃 설정 (ms 단위)
					conn.setReadTimeout(10000); // 서버 접속시 데이터 가져오기 타임아웃 설정 (ms 단위)
					conn.setRequestMethod("POST"); // 요청 방식 선택 (GET | POST)
					conn.setRequestProperty("request", "update_like"); // header 세팅 (key, velue)
					
					conn.getOutputStream().write(params.getBytes("utf-8")); // 서버에 파라미터 전달 (문자 인코딩 방식 선택)
					
					// URL에 접속 성공하면 (200)
					if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
						// 서버에서 받은 결과 값 읽어오는 부분
						BufferedReader br = new BufferedReader(new InputStreamReader(
							conn.getInputStream(), "utf-8"
						));
						String line;
						while ((line = br.readLine()) != null) {
							responseBody.append(line);
						}
						
						br.close(); // 버퍼리더 종료
						
						// 서버에서 받은 데이터가 json 타입일 경우
						//JSONArray jsonResponse = new JSONArray(responseBody);
						//Log.i("tag", "확인 jsonArray : " + jsonResponse);
						
						Log.i("tag", "결과 문자열 :" +responseBody);
						
					}
					else {
						
						// URL에 접속 실패했을 때 실행
						
						Log.i("tag", "error :" +conn.getResponseCode());
						
					}
					
					conn.disconnect(); // 연결 끊기
					
				}
				
				
				// URL에 접속 성공했을 때 실행
//				tv_likes.setText(String.valueOf(likes_cal));
			
			
			} catch (Exception e) {
				Log.i("tag", "error :" + e);
			}
		}
	}
	
	/*좋아요 누르기 동작*/
	class like_insert implements Runnable {
		@Override
		public void run() {
			try {
				
				String page = "http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/insert_LikedPosts.php"; // 연결 할 URL 주소
				//String page = "https://webhook.site/ab420874-fd80-48a6-9b3b-1750ebfdb40f"; // 연결 할 URL 주소
				
				URL url = new URL(page); // URL 객체 생성
				
				HttpURLConnection conn = (HttpURLConnection)url.openConnection(); // 연결 객체 생성
				
				// 서버에 전달 할 파라미터
				String params = "Account_pn=" + viewer_id;
				params += "&Post_id=" + post_id;
				
				Log.i("tag","Account_pn``"+viewer_id);
				Log.i("tag","Post_id``"+post_id);
				
				StringBuilder responseBody = new StringBuilder(); // 서버에서 받은 결과값을 저장 할 문자열
				
				// 연결되면 실행
				if(conn != null) {
					Log.i("tag", "conn 연결");
					
					conn.setRequestProperty("Accept", "application/json"); // 서버에서 받을 데이터 타입 설정 (json | xml)
					conn.setConnectTimeout(10000); // 서버 접속시 연결 타임아웃 설정 (ms 단위)
					conn.setReadTimeout(10000); // 서버 접속시 데이터 가져오기 타임아웃 설정 (ms 단위)
					conn.setRequestMethod("POST"); // 요청 방식 선택 (GET | POST)
					conn.setRequestProperty("request", "insert"); // header 세팅 (key, velue)
					
					conn.getOutputStream().write(params.getBytes("utf-8")); // 서버에 파라미터 전달 (문자 인코딩 방식 선택)
					
					// URL에 접속 성공하면 (200)
					if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
						// 서버에서 받은 결과 값 읽어오는 부분
						BufferedReader br = new BufferedReader(new InputStreamReader(
							conn.getInputStream(), "utf-8"
						));
						String line;
						while ((line = br.readLine()) != null) {
							responseBody.append(line);
						}
						
						br.close(); // 버퍼리더 종료
						
						// 서버에서 받은 데이터가 json 타입일 경우
						//JSONArray jsonResponse = new JSONArray(responseBody);
						//Log.i("tag", "확인 jsonArray : " + jsonResponse);
						
						Log.i("tag", "결과 문자열 :" +responseBody);
						
					}
					else {
						
						// URL에 접속 실패했을 때 실행
						
						Log.i("tag", "error :" +conn.getResponseCode());
						
					}
					
					conn.disconnect(); // 연결 끊기
					
				}
				
				// URL에 접속 성공했을 때 실행
				
				//iv_heart.setImageResource(R.drawable.heart_red_icon);
				
				/*서버 데이터베이스에서 게시물 좋아요 여부 가져와 아이콘에 표시*/
				Thread like_check_thread = new Thread(new like_check_thread());
				like_check_thread.start();
				
				
			} catch (Exception e) {
				Log.i("tag", "error :" + e);
			}
		}
	}
	
	/*좋아요 취소 동작*/
	class like_delete implements Runnable {
		@Override
		public void run() {
			try {
				
				String page = "http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/delete_LikedPosts.php"; // 연결 할 URL 주소
				//String page = "https://webhook.site/ab420874-fd80-48a6-9b3b-1750ebfdb40f"; // 연결 할 URL 주소
				
				URL url = new URL(page); // URL 객체 생성
				
				HttpURLConnection conn = (HttpURLConnection)url.openConnection(); // 연결 객체 생성
				
				// 서버에 전달 할 파라미터
				String params = "LikePosts_id=" + like_check;
				
				Log.i("tag","LikePosts_id``"+like_check);
				
				StringBuilder responseBody = new StringBuilder(); // 서버에서 받은 결과값을 저장 할 문자열
				
				// 연결되면 실행
				if(conn != null) {
					Log.i("tag", "conn 연결");
					
					conn.setRequestProperty("Accept", "application/json"); // 서버에서 받을 데이터 타입 설정 (json | xml)
					conn.setConnectTimeout(10000); // 서버 접속시 연결 타임아웃 설정 (ms 단위)
					conn.setReadTimeout(10000); // 서버 접속시 데이터 가져오기 타임아웃 설정 (ms 단위)
					conn.setRequestMethod("POST"); // 요청 방식 선택 (GET | POST)
					conn.setRequestProperty("request", "delete"); // header 세팅 (key, velue)
					
					conn.getOutputStream().write(params.getBytes("utf-8")); // 서버에 파라미터 전달 (문자 인코딩 방식 선택)
					
					// URL에 접속 성공하면 (200)
					if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
						// 서버에서 받은 결과 값 읽어오는 부분
						BufferedReader br = new BufferedReader(new InputStreamReader(
							conn.getInputStream(), "utf-8"
						));
						String line;
						while ((line = br.readLine()) != null) {
							responseBody.append(line);
						}
						
						br.close(); // 버퍼리더 종료
						
						// 서버에서 받은 데이터가 json 타입일 경우
						//JSONArray jsonResponse = new JSONArray(responseBody);
						//Log.i("tag", "확인 jsonArray : " + jsonResponse);
						
						Log.i("tag", "결과 문자열 :" +responseBody);
						
					}
					else {
						
						// URL에 접속 실패했을 때 실행
						Log.i("tag", "error :" +conn.getResponseCode());
						
					}
					
					conn.disconnect(); // 연결 끊기
					
				}
				
				// URL에 접속 성공했을 때 실행
//				iv_heart.setImageResource(R.drawable.heart_icon);
				
				/*서버 데이터베이스에서 게시물 좋아요 여부 가져와 아이콘에 표시*/
				Thread like_check_thread = new Thread(new like_check_thread());
				like_check_thread.start();
				
			} catch (Exception e) {
				Log.i("tag", "error :" + e);
			}
		}
	}
	
	
	/*DB에서 좋아요 여부 조회*/
	class like_check_thread implements Runnable {
		@Override
		public void run() {
			try {
				String page = "http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/search_LikedPosts.php"; // 연결 할 URL 주소
				//String page = "https://webhook.site/ab420874-fd80-48a6-9b3b-1750ebfdb40f"; // 연결 할 URL 주소
				
				URL url = new URL(page); // URL 객체 생성
				
				HttpURLConnection conn = (HttpURLConnection)url.openConnection(); // 연결 객체 생성
				
				// 서버에 전달 할 파라미터
				String params = "Account_pn=" + viewer_id;
				params += "&Post_id=" + post_id;
				
				StringBuilder responseBody = new StringBuilder(); // 서버에서 받은 결과값을 저장 할 문자열
				
				// 연결되면 실행
				if(conn != null) {
					Log.i("tag", "conn 연결");
					
					conn.setRequestProperty("Accept", "application/json"); // 서버에서 받을 데이터 타입 설정 (json | xml)
					conn.setConnectTimeout(10000); // 서버 접속시 연결 타임아웃 설정 (ms 단위)
					conn.setReadTimeout(10000); // 서버 접속시 데이터 가져오기 타임아웃 설정 (ms 단위)
					conn.setRequestMethod("POST"); // 요청 방식 선택 (GET | POST)
					conn.setRequestProperty("request", "select"); // header 세팅 (key, velue)
					
					conn.getOutputStream().write(params.getBytes("utf-8")); // 서버에 파라미터 전달 (문자 인코딩 방식 선택)
					
					// URL에 접속 성공하면 (200)
					if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
						// 서버에서 받은 결과 값 읽어오는 부분
						BufferedReader br = new BufferedReader(new InputStreamReader(
							conn.getInputStream(), "utf-8"
						));
						String line;
						while ((line = br.readLine()) != null) {
							responseBody.append(line);
						}
						
						br.close(); // 버퍼리더 종료
						
						// 서버에서 받은 데이터가 json 타입일 경우
						//JSONArray jsonResponse = new JSONArray(responseBody);
						//Log.i("tag", "확인 jsonArray : " + jsonResponse);
						
						Log.i("tag", "결과 문자열 :" +responseBody);
						
					}
					else {
						
						// URL에 접속 실패했을 때 실행
						Log.i("tag", "error :" +conn.getResponseCode());
						
					}
					
					conn.disconnect(); // 연결 끊기
					
				}
				
				// URL에 접속 성공했을 때 실행
				String like_chek_result;
				like_chek_result = String.valueOf(responseBody);
				Log.i("tag", "결과 문자열 :" +like_chek_result);
				
				if(!like_chek_result.equals("")){
					like_check = String.valueOf(responseBody);
					
					Log.i("tag", "좋아요 여부 : O");
					Log.i("tag", "결과 문자열-- : "+like_check);
					
				}
				else{
					like_check = String.valueOf(responseBody);
					
					Log.i("tag", "좋아요 여부 : X");
					Log.i("tag", "결과 문자열-- : "+like_check);
					
				}
				
			} catch (Exception e) {
				Log.i("tag", "error :" + e);
			}
		}
	}
	
	/*서버 데이터베이스에서 게시물 좋아요 개수 가져오기*/
	class like_count_check implements Runnable {
		@Override
		public void run() {
			try {
				String page = "http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/search_LikedPosts.php"; // 연결 할 URL 주소
				//String page = "https://webhook.site/ab420874-fd80-48a6-9b3b-1750ebfdb40f"; // 연결 할 URL 주소
				
				URL url = new URL(page); // URL 객체 생성
				
				HttpURLConnection conn = (HttpURLConnection)url.openConnection(); // 연결 객체 생성
				
				// 서버에 전달 할 파라미터
				String params = "Post_id=" + post_id;
				
				StringBuilder responseBody = new StringBuilder(); // 서버에서 받은 결과값을 저장 할 문자열
				
				// 연결되면 실행
				if(conn != null) {
					Log.i("tag", "conn 연결");
					
					conn.setRequestProperty("Accept", "application/json"); // 서버에서 받을 데이터 타입 설정 (json | xml)
					conn.setConnectTimeout(10000); // 서버 접속시 연결 타임아웃 설정 (ms 단위)
					conn.setReadTimeout(10000); // 서버 접속시 데이터 가져오기 타임아웃 설정 (ms 단위)
					conn.setRequestMethod("POST"); // 요청 방식 선택 (GET | POST)
					conn.setRequestProperty("request", "select_num"); // header 세팅 (key, velue)
					
					conn.getOutputStream().write(params.getBytes("utf-8")); // 서버에 파라미터 전달 (문자 인코딩 방식 선택)
					
					// URL에 접속 성공하면 (200)
					if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
						// 서버에서 받은 결과 값 읽어오는 부분
						BufferedReader br = new BufferedReader(new InputStreamReader(
							conn.getInputStream(), "utf-8"
						));
						String line;
						while ((line = br.readLine()) != null) {
							responseBody.append(line);
						}
						
						br.close(); // 버퍼리더 종료
						
						// 서버에서 받은 데이터가 json 타입일 경우
						//JSONArray jsonResponse = new JSONArray(responseBody);
						//Log.i("tag", "확인 jsonArray : " + jsonResponse);
						
						Log.i("tag", "결과 문자열 :" +responseBody);
						
					}
					else {
						// URL에 접속 실패했을 때 실행
						Log.i("tag", "error :" +conn.getResponseCode());
					}
					
					conn.disconnect(); // 연결 끊기
					
				}
				
				// URL에 접속 성공했을 때 실행
				like_count = String.valueOf(responseBody);
				
				if(Objects.equals(up_down, "up")){
					Thread update_like = new Thread(new update_like());
					update_like.start();
				}
				else if(Objects.equals(up_down, "down")){
					Thread update_like = new Thread(new update_like());
					update_like.start();
				}
				else{
					Log.i("tag", "else 진입");
				}
				
				
			} catch (Exception e) {
				Log.i("tag", "error :" + e);
			}
		}
	}
	
	class views_update implements Runnable {
		@Override
		public void run() {
			try {
				String page = "http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/update_UsedTradePost.php"; // 연결 할 URL 주소
				//String page = "https://webhook.site/ab420874-fd80-48a6-9b3b-1750ebfdb40f"; // 연결 할 URL 주소
				
				URL url = new URL(page); // URL 객체 생성
				
				HttpURLConnection conn = (HttpURLConnection)url.openConnection(); // 연결 객체 생성
				
				// 서버에 전달 할 파라미터
				String params = "phone_number=" + viewer_id; // 조회하는 사람의 아이디 값
				params += "&views=" + views_count; // 조회 전 게시글의 조회수
				params += "&post_id=" + post_id; // 조회 전 게시글의 조회수
				
				StringBuilder responseBody = new StringBuilder(); // 서버에서 받은 결과값을 저장 할 문자열
				
				// 연결되면 실행
				if(conn != null) {
					Log.i("tag", "conn 연결");
					
					conn.setRequestProperty("Accept", "application/json"); // 서버에서 받을 데이터 타입 설정 (json | xml)
					conn.setConnectTimeout(10000); // 서버 접속시 연결 타임아웃 설정 (ms 단위)
					conn.setReadTimeout(10000); // 서버 접속시 데이터 가져오기 타임아웃 설정 (ms 단위)
					conn.setRequestMethod("POST"); // 요청 방식 선택 (GET | POST)
					conn.setRequestProperty("request", "update_view"); // header 세팅 (key, velue)
					
					conn.getOutputStream().write(params.getBytes("utf-8")); // 서버에 파라미터 전달 (문자 인코딩 방식 선택)
					
					// URL에 접속 성공하면 (200)
					if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
						// 서버에서 받은 결과 값 읽어오는 부분
						BufferedReader br = new BufferedReader(new InputStreamReader(
							conn.getInputStream(), "utf-8"
						));
						String line;
						while ((line = br.readLine()) != null) {
							responseBody.append(line);
						}
						
						br.close(); // 버퍼리더 종료
						
						// 서버에서 받은 데이터가 json 타입일 경우
						//JSONArray jsonResponse = new JSONArray(responseBody);
						//Log.i("tag", "확인 jsonArray : " + jsonResponse);
						
						Log.i("tag", "결과 문자열 :" +responseBody);
						
						// 인텐트로 아이템 정보와 함께 화면 전환
						Intent intent = new Intent(context, ViewUsedTradePost_Activity.class);
						//intent.putExtra("phone_number", phone_number);
						
						intent.putExtra("previous_screen", "MyWatchList_Activity"); //다음 화면에서 뒤로가기 눌렀을 때 돌아갈 곳을 찾기위해 정보를 넘겨 줌
						intent.putExtra("post_id", post_id); //다음 화면 게시글 상세보기를 하기위해 게시글 고유아이디정보를 넘겨줌
						
						context.startActivity(intent);
						((Activity) context).finish();
						
						
					}
					else {
						// runOnUiThread 기본
						// URL에 접속 실패했을 때 실행
						Log.i("tag","연결 문제 발생");
					}
					
					conn.disconnect(); // 연결 끊기
					
				}
				
			} catch (Exception e) {
				Log.i("tag", "error :" + e);
			}
		}
	}
	
	/*게시글 내용에서 조회수만 조회*/
	class search_HomePost_detail implements Runnable {
		@Override
		public void run() {
			try {
				String page = "http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/search_HomePost_detail.php"; // 연결 할 URL 주소
				
				URL url = new URL(page); // URL 객체 생성
				
				HttpURLConnection conn = (HttpURLConnection)url.openConnection(); // 연결 객체 생성
				
				// 서버에 전달 할 파라미터
				String params = "post_id=" + post_id;
				
				StringBuilder responseBody = new StringBuilder(); // 서버에서 받은 결과값을 저장 할 문자열
				//String responseBody = null;
				
				// 연결되면 실행
				if(conn != null) {
					Log.i("tag", "conn 연결");
					
					conn.setRequestProperty("Accept", "application/json"); // 서버에서 받을 데이터 타입 설정 (json | xml)
					conn.setConnectTimeout(10000); // 서버 접속시 연결 타임아웃 설정 (ms 단위)
					conn.setReadTimeout(10000); // 서버 접속시 데이터 가져오기 타임아웃 설정 (ms 단위)
					conn.setRequestMethod("POST"); // 요청 방식 선택 (GET | POST)
					conn.setRequestProperty("request", "select"); // header 세팅 (key, velue)
					
					conn.getOutputStream().write(params.getBytes("utf-8")); // 서버에 파라미터 전달 (문자 인코딩 방식 선택)
					
					// URL에 접속 성공하면 (200)
					if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
						
						// 서버에서 받은 결과 값 읽어오는 부분
						BufferedReader br = new BufferedReader(new InputStreamReader(
							conn.getInputStream(), "utf-8"
						));
						String line;
						while ((line = br.readLine()) != null) {
							responseBody.append(line);
						}
						
						br.close(); // 버퍼리더 종료
						
						/*서버에서 받아온 결과값을 리사이클러뷰로 보여줌*/
						Log.i("tag", "결과 문자열 :" +responseBody);
						
						try {
							JSONObject object = new JSONObject(String.valueOf(responseBody));
							String message = object.getString("message");
							JSONArray contents = object.getJSONArray("contents");
							
							Log.i("tag", "message : " + message);
							Log.i("tag", "contents : " + contents);
							
							Log.i("tag", "message length : " + message.length());
							Log.i("tag", "contents length : " + contents.length());
							
							for (int i = 0; i < contents.length(); i++) {
								JSONObject obj = contents.getJSONObject(i);
								
								views_count = obj.getString("views");
								
								Log.i("tag", "views : " + views_count);
								
							}
							//조회수를 업데이트 한 후 게시글 상세보기로 이동
							Thread views_update = new Thread(new views_update());
							views_update.start();
							
						}
						catch (JSONException e) {
							Log.i("tag", "Home_Activity_error : " + e);
						}
						
					}
					else {
						// runOnUiThread 기본
						// URL에 접속 실패했을 때 실행
					}
					
					conn.disconnect(); // 연결 끊기
					
				}
				
				
				
				
				
			} catch (Exception e) {
				Log.i("tag", "error :" + e);
			}
			
		}
	}
	
	
	
	public void addPost(MyWatchList_data data){
		MyWatchList_info.add(data);
	}

	public void removePost(int pos) {
		MyWatchList_info.remove(pos);
	}

	public void modify(int pos, MyWatchList_data data){
		MyWatchList_info.set(pos, data);
	}
}