package com.beom.carrot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager2.widget.ViewPager2;
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

public class ViewUsedTradePost_Activity extends AppCompatActivity {
	int likes_cal;
	
	Button bt_SalesStatus;
	
	ImageButton ib_back;
	ImageButton ib_trade_post_popup;
	ImageButton ib_go_back_home;
	
	ImageView iv_profile_image;
	ImageView iv_heart;
	
	ViewPager2 viewPager;
	
	TextView tv_nick_name;
	TextView tv_town_name;
	TextView tv_title_view;
	TextView tv_price_view;
	TextView tv_contents_view;
	TextView tv_views;
	TextView tv_likes;
	TextView tv_SalesStatus;
	
	String user_name;
	String town_name;
	String image_uri;
	String phone_number;
	String post_id;
	String title_view;
	String price_view;
	String contents_view;
	String profile_image;
	String nick_name;
	String views_view;
	String like_check;
	String like_count;
	String up_down;
	String previous_screen;
	String SalesStatus_view;
	
	ConstraintLayout lo_heart_touch;
	ConstraintLayout lo_profile_info;
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_used_trade_post);
		
		bt_SalesStatus = findViewById(R.id.bt_SalesStatus);
		
		tv_title_view = findViewById(R.id.tv_title_view);
		tv_price_view = findViewById(R.id.tv_price_view);
		tv_contents_view = findViewById(R.id.tv_contents_view);
		tv_nick_name = findViewById(R.id.tv_nick_name);
		tv_town_name = findViewById(R.id.TextView);
		tv_views = findViewById(R.id.tv_views);
		tv_likes = findViewById(R.id.tv_likes);
		tv_SalesStatus = findViewById(R.id.tv_SalesStatus);
		
		viewPager = findViewById(R.id.vp_image_view);
		
		ib_trade_post_popup = findViewById(R.id.ib_trade_post_popup);
		ib_back = findViewById(R.id.ib_back);
		ib_go_back_home = findViewById(R.id.ib_go_back_home);
		
		iv_heart = findViewById(R.id.iv_heart);
		
		iv_profile_image = findViewById(R.id.iv_profile_image);
		
		lo_heart_touch = findViewById(R.id.lo_heart_touch);
		lo_profile_info = findViewById(R.id.lo_profile_info);
		
		Intent intent = getIntent();
		previous_screen = intent.getStringExtra("previous_screen");
		post_id = intent.getStringExtra("post_id");
		
		/*저장된 로그인 정보를 불러오기 위해 같은 네임파일을 찾음.*/
		SharedPreferences sp = getSharedPreferences("login_data", Activity.MODE_PRIVATE);
		String result = sp.getString("phone_number","no");//test이름을 가진 키에 저장 된 값을 text에 저장. 값이 없을 시"no"이라는 값을 반환
		phone_number = result;
		
		/*서버에 데이터베이스에서 게시물의 내용을 가져오기*/
		Thread search_HomePost_detail = new Thread(new search_HomePost_detail());
		search_HomePost_detail.start();
		
		/*서버 데이터베이스에서 게시물 좋아요 여부 가져와 아이콘에 표시*/
		Thread like_check_thread = new Thread(new like_check_thread());
		like_check_thread.start();
		
		/*서버 데이터베이스에서 게시물 좋아요 개수 가져오기*/
		Thread like_count_check = new Thread(new like_count_check());
		like_count_check.start();
		
		/*팝업 버튼 클릭 했을 때 게시글 수정, 삭제 메뉴 나옴*/
		ib_trade_post_popup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				final PopupMenu popupMenu = new PopupMenu(getApplicationContext(),view);
				getMenuInflater().inflate(R.menu.trade_post_menu,popupMenu.getMenu());
				popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem menuItem) {
						if (menuItem.getItemId() == R.id.action_menu_modify){
							Log.i("tag","게시글 수정 클릭");
							
							Intent intent = new Intent(ViewUsedTradePost_Activity.this, WriteUsedTradePost_Activity.class);
							intent.putExtra("edit_post_check", "edit_start");
							intent.putExtra("post_id", post_id);
							intent.putExtra("town_name", town_name);
							
							startActivity(intent);
							
						}
						else if (menuItem.getItemId() == R.id.action_menu_delete){
							Log.i("tag","게시글 삭제 클릭");
							
							Show_Delete_Check_Dialog();
							
						}
						return false;
					}
				});
				popupMenu.show();
			}
		});
		
		/*홈으로가기 이미지버튼 눌렀을 때*/
		ib_go_back_home.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				//버튼이 클릭 됐을 때
				
				Intent intent = new Intent(ViewUsedTradePost_Activity.this, Home_Activity.class);
				startActivity(intent) ;
				finish();
				
			}
		});
		
		/*뒤로가기 이미지버튼 눌렀을 때*/
		ib_back.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				//버튼이 클릭 됐을 때
//				SharedPreferences sp = getSharedPreferences("login_data", Activity.MODE_PRIVATE);
//				String result = sp.getString("phone_number","no");//test이름을 가진 키에 저장 된 값을 text에 저장. 값이 없을 시"no"이라는 값을 반환
//				Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
				
				if(Objects.equals(previous_screen, "Home_Activity")){
					Log.i("tag","Home_Activity 로 이동");
					
					Intent intent = new Intent(ViewUsedTradePost_Activity.this, Home_Activity.class);
					intent.putExtra("phone_number", phone_number);
					startActivity(intent) ;
				}
				else if(Objects.equals(previous_screen, "MyWatchList_Activity")){
					Log.i("tag","MyWatchList_Activity 로 이동");
					
					Intent intent = new Intent(ViewUsedTradePost_Activity.this, MyWatchList_Activity.class);
					intent.putExtra("phone_number", phone_number);
					startActivity(intent) ;
				}
				else if(Objects.equals(previous_screen, "SearchPost_Activity")){
					Log.i("tag","SearchPost_Activity 로 이동");
					
//					Intent intent = new Intent(ViewUsedTradePost_Activity.this, SearchPost_Activity.class);
//					intent.putExtra("phone_number", phone_number);
//					startActivity(intent) ;
				}
				
				finish();
			}
		});
		
		/*프로필정보 부분 눌렀을 때 프로필 정보 보는 페이지로 이동*/
		lo_profile_info.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()){
					case MotionEvent.ACTION_DOWN: // ACTION_DOWN : 터치를 누르는 순간
						Log.i("tag","터치를 누르는 순간");
						break;
					case MotionEvent.ACTION_UP: // ACTION_UP : 터치를 때는 순간
						Log.i("tag","터치를 떼는 순간");
						
						if(!Objects.equals(user_name, phone_number)){
							Intent intent = new Intent(ViewUsedTradePost_Activity.this, ViewProfile_Activity.class);
							intent.putExtra("before_activity", "ViewUsedTradePost_Activity");
							intent.putExtra("phone_number", user_name);
							startActivity(intent);
						}
						else{
							Toast.makeText(getApplicationContext(), "내 프로필 정보 보는 액티비티로 이동", Toast.LENGTH_SHORT).show();
							
							Intent intent = new Intent(ViewUsedTradePost_Activity.this, ViewProfile_Activity.class);
							intent.putExtra("before_activity", "ViewUsedTradePost_Activity");
							intent.putExtra("phone_number", user_name);
							startActivity(intent);
						}
						
						
						break;
					
					case MotionEvent.ACTION_MOVE: // ACTUIB_MOVE : 터치되고 있는 순간
						Log.i("tag","터치되고 있는 순간");
						break;
				}
				return true;
			}
		});
		
		/*판매상태 버튼 눌렀을 때*/
		bt_SalesStatus.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				//버튼이 클릭 됐을 때
				Log.i("tag","판매상태 버튼 눌림");
				
				CharSequence info[] = new CharSequence[] {"판매중", "예약중", "거래완료"};
				
				AlertDialog.Builder builder = new AlertDialog.Builder(ViewUsedTradePost_Activity.this);
//				builder.setTitle("제목");
				builder.setItems(info, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch(which)
						{
							case 0:
								// 판매중
								Log.i("tag","판매중");
								
								SalesStatus_view = "판매중";
								
								break;
							case 1:
								// 예약중
								Log.i("tag","예약중");
								
								SalesStatus_view = "예약중";
								
								break;
							case 2:
								// 거래완료
								Log.i("tag","거래완료");
								
								SalesStatus_view = "거래완료";
								
								break;
						}
						
						Thread edit = new Thread(new edit());
						edit.start();
						
						dialog.dismiss();
					}
				});
				builder.show();
				
			}
		});
		
		/*하트버튼 눌렀을 때 좋아요 기능 작동*/
		lo_heart_touch.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()){
					case MotionEvent.ACTION_DOWN: // ACTION_DOWN : 터치를 누르는 순간
						Log.i("tag","터치를 누르는 순간");
						break;
					case MotionEvent.ACTION_UP: // ACTION_UP : 터치를 때는 순간
						Log.i("tag","터치를 떼는 순간");
						
						if(Objects.equals(like_check, "")){
							Log.i("tag","좋아요");
							Log.i("tag",like_check);
							
							up_down = "up";
							
							Thread like_insert = new Thread(new like_insert());
							like_insert.start();
							
							/*서버 데이터베이스에서 게시물 좋아요 개수 가져오기*/
							Thread like_count_check = new Thread(new like_count_check());
							like_count_check.start();
							
						}
						else{
							Log.i("tag","좋아요 취소");
							Log.i("tag",like_check);
							
							up_down = "down";
							
							Thread like_delete = new Thread(new like_delete());
							like_delete.start();
							
							/*서버 데이터베이스에서 게시물 좋아요 개수 가져오기*/
							Thread like_count_check = new Thread(new like_count_check());
							like_count_check.start();
							
						}
						
						break;
						
					case MotionEvent.ACTION_MOVE: // ACTUIB_MOVE : 터치되고 있는 순간
						Log.i("tag","터치되고 있는 순간");
						break;
				}
				return true;
			}
		});
		
	}
	
	/*삭제 하기 전 확인하는 다이얼로그 보여주는 함수*/
	public void Show_Delete_Check_Dialog() {
		
		AlertDialog.Builder msgBuilder = new AlertDialog.Builder(this)
			.setTitle("게시글을 정말 삭제 하시겠어요?")
			//.setMessage("선택 하세요")
			
			.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int pos) {
					//finish();
					Log.i("tag", String.valueOf(dialog));
					Log.i("tag", String.valueOf(pos));
					Log.i("tag", "삭제");
					
					Thread delete = new Thread(new delete());
					delete.start();
					
				} })
			.setNegativeButton("취소", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int pos) {
					//finish();
					Log.i("tag", String.valueOf(dialog));
					Log.i("tag", String.valueOf(pos));
					Log.i("tag", "취소");
					
//					Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_SHORT).show();
				} });
		
		msgBuilder.create().show();
	}
	
	/*서버에서 가져온 이미지 정보를 표시하기 위해 재가공*/
	public ArrayList setImageList(String uri){
		ArrayList<String> itemList = new ArrayList();
		
		/*String으로 된 이미지 정보를 표시하기 위해 분해*/
		String array_uri = uri;
		array_uri = array_uri.replace("[", "");
		array_uri = array_uri.replace("]", "");
		array_uri = array_uri.replace("\"", "");
		
		String [] stringArray= array_uri.split(",");
		for(int j=0; j < stringArray.length; j++){
			Log.i("tag", "array_uri : " + stringArray[j]);
			itemList.add("http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/UsedTradePost_Image/"+stringArray[j]);
		}
		
		return itemList;
	}
	
	/*서버에 변경 된 상품상태를 업로드하는 부분*/
	class edit implements Runnable {
		@Override
		public void run() {
			try {
				String page = "http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/edit_SalesStatus.php"; // 연결 할 URL 주소
				
				URL url = new URL(page); // URL 객체 생성
				
				HttpURLConnection conn = (HttpURLConnection)url.openConnection(); // 연결 객체 생성
				
				// 서버에 전달 할 파라미터
				String params = "post_id=" + post_id;
				params += "&SalesStatus=" + SalesStatus_view;
				
				Log.i("tag", post_id);
				Log.i("tag", SalesStatus_view);
				Log.i("tag", params);
				
				StringBuilder responseBody = new StringBuilder(); // 서버에서 받은 결과값을 저장 할 문자열
				
				// 연결되면 실행
				if(conn != null) {
					Log.i("tag", "conn 연결");
					
					conn.setRequestProperty("Accept", "application/json"); // 서버에서 받을 데이터 타입 설정 (json | xml)
					conn.setConnectTimeout(10000); // 서버 접속시 연결 타임아웃 설정 (ms 단위)
					conn.setReadTimeout(10000); // 서버 접속시 데이터 가져오기 타임아웃 설정 (ms 단위)
					conn.setRequestMethod("POST"); // 요청 방식 선택 (GET | POST)
					conn.setRequestProperty("request", "update"); // header 세팅 (key, velue)
					
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
						
						/*여기에 실행시킬 내용 작성*/
						Log.i("tag", "responseBody :" +responseBody);
						
					}
					else {
						// runOnUiThread 기본
						// URL에 접속 실패했을 때 실행
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								//findViewById(R.id.Prod_cpb).setVisibility(View.GONE); // 프로그래스바 안보이게 처리
								Toast.makeText(getApplicationContext(), "연결 문제 발생", Toast.LENGTH_SHORT).show();
							}
						});
					}
					conn.disconnect(); // 연결 끊기
				}
				
				// 백그라운드 스레드에서는 메인화면을 변경 할 수 없음
				// runOnUiThread(메인 스레드영역)
				// URL에 접속 성공했을 때 실행
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						
						bt_SalesStatus.setText(SalesStatus_view);
						
					}
				});
				
			} catch (Exception e) {
				Log.e("tag", "error :" + e);
			}
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
				String params = "phone_number=" + phone_number;
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
						// runOnUiThread 기본
						// URL에 접속 실패했을 때 실행
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								//findViewById(R.id.Prod_cpb).setVisibility(View.GONE); // 프로그래스바 안보이게 처리
								try {
									Log.i("tag", "error :" +conn.getResponseCode());
								} catch (IOException e) {
									e.printStackTrace();
								}
								Toast.makeText(getApplicationContext(), "연결 문제 발생", Toast.LENGTH_SHORT).show();
							}
						});
					}
					
					conn.disconnect(); // 연결 끊기
					
				}
				
				// 백그라운드 스레드에서는 메인화면을 변경 할 수 없음
				// runOnUiThread(메인 스레드영역)
				// URL에 접속 성공했을 때 실행
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						
						tv_likes.setText(String.valueOf(likes_cal));
						
					}
				});
				
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
				String params = "Account_pn=" + phone_number;
				params += "&Post_id=" + post_id;
				
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
						// runOnUiThread 기본
						// URL에 접속 실패했을 때 실행
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								//findViewById(R.id.Prod_cpb).setVisibility(View.GONE); // 프로그래스바 안보이게 처리
								try {
									Log.i("tag", "error :" +conn.getResponseCode());
								} catch (IOException e) {
									e.printStackTrace();
								}
								Toast.makeText(getApplicationContext(), "연결 문제 발생", Toast.LENGTH_SHORT).show();
							}
						});
					}
					
					conn.disconnect(); // 연결 끊기
					
				}
				
				// 백그라운드 스레드에서는 메인화면을 변경 할 수 없음
				// runOnUiThread(메인 스레드영역)
				// URL에 접속 성공했을 때 실행
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						
						//iv_heart.setImageResource(R.drawable.heart_red_icon);
						
						/*서버 데이터베이스에서 게시물 좋아요 여부 가져와 아이콘에 표시*/
						Thread like_check_thread = new Thread(new like_check_thread());
						like_check_thread.start();
						
					}
				});
				
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
						// runOnUiThread 기본
						// URL에 접속 실패했을 때 실행
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								//findViewById(R.id.Prod_cpb).setVisibility(View.GONE); // 프로그래스바 안보이게 처리
								try {
									Log.i("tag", "error :" +conn.getResponseCode());
								} catch (IOException e) {
									e.printStackTrace();
								}
								Toast.makeText(getApplicationContext(), "연결 문제 발생", Toast.LENGTH_SHORT).show();
							}
						});
					}
					
					conn.disconnect(); // 연결 끊기
					
				}
				
				// 백그라운드 스레드에서는 메인화면을 변경 할 수 없음
				// runOnUiThread(메인 스레드영역)
				// URL에 접속 성공했을 때 실행
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						
						
						//like_check = "";
						//iv_heart.setImageResource(R.drawable.heart_icon);
						
						/*서버 데이터베이스에서 게시물 좋아요 여부 가져와 아이콘에 표시*/
						Thread like_check_thread = new Thread(new like_check_thread());
						like_check_thread.start();
					}
				});
				
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
				String params = "Account_pn=" + phone_number;
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
						// runOnUiThread 기본
						// URL에 접속 실패했을 때 실행
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								//findViewById(R.id.Prod_cpb).setVisibility(View.GONE); // 프로그래스바 안보이게 처리
								try {
									Log.i("tag", "error :" +conn.getResponseCode());
								} catch (IOException e) {
									e.printStackTrace();
								}
								Toast.makeText(getApplicationContext(), "연결 문제 발생", Toast.LENGTH_SHORT).show();
							}
						});
					}
					
					conn.disconnect(); // 연결 끊기
					
				}
				
				// 백그라운드 스레드에서는 메인화면을 변경 할 수 없음
				// runOnUiThread(메인 스레드영역)
				// URL에 접속 성공했을 때 실행
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						String like_chek_result;
						like_chek_result = String.valueOf(responseBody);
						Log.i("tag", "결과 문자열 :" +like_chek_result);
						
						if(!like_chek_result.equals("")){
							like_check = String.valueOf(responseBody);
							iv_heart.setImageResource(R.drawable.heart_red_icon);
							
							Log.i("tag", "좋아요 여부 : O");
							Log.i("tag", like_check);
						}
						else{
							like_check = String.valueOf(responseBody);
							iv_heart.setImageResource(R.drawable.heart_icon);
							
							Log.i("tag", "좋아요 여부 : X");
							Log.i("tag", like_check);
						}
						
					}
				});
				
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
						// runOnUiThread 기본
						// URL에 접속 실패했을 때 실행
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								//findViewById(R.id.Prod_cpb).setVisibility(View.GONE); // 프로그래스바 안보이게 처리
								try {
									Log.i("tag", "error :" +conn.getResponseCode());
								} catch (IOException e) {
									e.printStackTrace();
								}
								Toast.makeText(getApplicationContext(), "연결 문제 발생", Toast.LENGTH_SHORT).show();
							}
						});
					}
					
					conn.disconnect(); // 연결 끊기
					
				}
				
				// 백그라운드 스레드에서는 메인화면을 변경 할 수 없음
				// runOnUiThread(메인 스레드영역)
				// URL에 접속 성공했을 때 실행
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						
						like_count = String.valueOf(responseBody);
						tv_likes.setText(like_count);
						
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
						
					}
				});
				
			} catch (Exception e) {
				Log.i("tag", "error :" + e);
			}
		}
	}
	
	/*서버에 게시글 삭제 요청*/
	class delete implements Runnable {
		@Override
		public void run() {
			try {
				String page = "http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/delete_UsedTradePost.php"; // 연결 할 URL 주소
				//String page = "https://webhook.site/ab420874-fd80-48a6-9b3b-1750ebfdb40f"; // 연결 할 URL 주소
				
				URL url = new URL(page); // URL 객체 생성
				
				HttpURLConnection conn = (HttpURLConnection)url.openConnection(); // 연결 객체 생성
				
				// 서버에 전달 할 파라미터
				String params = "phone_number=" + phone_number;
				params += "&post_id=" + post_id;
				
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
						
						
						/** todo 이 공간에서 원하는 내용 작성*/
						
						
						// 서버에서 받은 데이터가 json 타입일 경우
						//JSONArray jsonResponse = new JSONArray(responseBody);
						//Log.i("tag", "확인 jsonArray : " + jsonResponse);
						
						Log.i("tag", "결과 문자열 :" +responseBody);
						
						
						Intent intent = new Intent(ViewUsedTradePost_Activity.this, Home_Activity.class);
						intent.putExtra("phone_number", phone_number);
						startActivity(intent);
						
						
						/**.......................*/
					}
					else {
						// runOnUiThread 기본
						// URL에 접속 실패했을 때 실행
						Toast.makeText(getApplicationContext(), "연결 문제 발생", Toast.LENGTH_SHORT).show();
					}
					
					conn.disconnect(); // 연결 끊기
					
				}
				
				
			} catch (Exception e) {
				Log.i("tag", "error :" + e);
			}
		}
	}
	
	/*게시글 내용 조회 후 출력*/
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
								
								String post_id = obj.getString("post_id");
								String PhoneNumber = obj.getString("PhoneNumber");
								String image = obj.getString("image");
								String title = obj.getString("title");
								String post_contents = obj.getString("contents");
								String TownName = obj.getString("TownName");
								String uploaded_time = obj.getString("uploaded_time");
								String upload_count = obj.getString("upload_count");
								String price = obj.getString("price");
								String chatting_count = obj.getString("chatting_count");
								String Like_count = obj.getString("Like_count");
								String PriceOffer_check = obj.getString("PriceOffer_check");
								String views = obj.getString("views");
								String SalesStatus = obj.getString("SalesStatus");
								
								
								Log.i("tag", "documents.length() : " + contents.length());
								Log.i("tag", "post_id : " + post_id);
								Log.i("tag", "PhoneNumber : " + PhoneNumber);
								Log.i("tag", "image : " + image);
								Log.i("tag", "title : " + title);
								Log.i("tag", "post_contents : " + post_contents);
								Log.i("tag", "TownName : " + TownName);
								Log.i("tag", "uploaded_time : " + uploaded_time);
								Log.i("tag", "upload_count : " + upload_count);
								Log.i("tag", "price : " + price);
								Log.i("tag", "chatting_count : " + chatting_count);
								Log.i("tag", "Like_count : " + Like_count);
								Log.i("tag", "PriceOffer_check : " + PriceOffer_check);
								Log.i("tag", "views : " + views);
								Log.i("tag", "SalesStatus : " + SalesStatus);
								
								user_name = PhoneNumber;
								town_name = TownName;
								image_uri = image;
								title_view = title;
								price_view = price;
								contents_view = post_contents;
								views_view = views;
								SalesStatus_view = SalesStatus;
							}
							
						}
						catch (JSONException e) {
							Log.i("tag", "Home_Activity_error : " + e);
						}
						
					}
					else {
						// runOnUiThread 기본
						// URL에 접속 실패했을 때 실행
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								//findViewById(R.id.Prod_cpb).setVisibility(View.GONE); // 프로그래스바 안보이게 처리
								Toast.makeText(getApplicationContext(), "연결 문제 발생", Toast.LENGTH_SHORT).show();
							}
						});
					}
					
					conn.disconnect(); // 연결 끊기
					
				}
				
				// 백그라운드 스레드에서는 메인화면을 변경 할 수 없음
				// runOnUiThread(메인 스레드영역)
				// URL에 접속 성공했을 때 실행
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Log.i("tag", "testtesttest : " + phone_number);
						Log.i("tag", "testtesttest : " + user_name);
						if(Objects.equals(phone_number, user_name)){
							ib_trade_post_popup.setVisibility(View.VISIBLE);
							bt_SalesStatus.setVisibility(View.VISIBLE);
							tv_SalesStatus.setVisibility(View.GONE);
						}
						else{
							ib_trade_post_popup.setVisibility(View.GONE);
							bt_SalesStatus.setVisibility(View.GONE);
							
//							Toast.makeText(getApplicationContext(), SalesStatus_view, Toast.LENGTH_SHORT).show();
							
							if(!Objects.equals(SalesStatus_view, "판매중")){
								tv_SalesStatus.setVisibility(View.VISIBLE);
								
								tv_SalesStatus.setText(SalesStatus_view);
								
								if(Objects.equals(SalesStatus_view, "예약중")){
									tv_SalesStatus.setTextColor(Color.GREEN);
								}
								else if(Objects.equals(SalesStatus_view, "거래완료")){
									tv_SalesStatus.setTextColor(Color.GRAY);
								}
								else{
									tv_SalesStatus.setText("");
								}
							}
							
							
						}
						
						if(Objects.equals(image_uri, "empty_HomePost_image.png")){
							viewPager.setVisibility(View.GONE);
						}
						else{
							viewPager.setVisibility(View.VISIBLE);
						}
						
						/*뷰페이저에 이미지 경로를 세팅*/
						ViewUsedTradePost_Adapter adapter = new ViewUsedTradePost_Adapter(setImageList(image_uri));
						viewPager.setAdapter(adapter);
						
						tv_town_name.setText(town_name);
						tv_title_view.setText(title_view);
						tv_price_view.setText(price_view);
						tv_contents_view.setText(contents_view);
						tv_views.setText(views_view);
						bt_SalesStatus.setText(SalesStatus_view);
						
						
						/*글 정보에 저장된 폰번호로 유저 정보를 조회 해 화면에 보여 줌*/
						Thread search_AccountInfo_All = new Thread(new search_AccountInfo_All());
						search_AccountInfo_All.start();
					}
				});
				
			} catch (Exception e) {
				Log.i("tag", "error :" + e);
			}
			
		}
	}
	
	/*게시글 작성자 정보 조회 후 출력*/
	class search_AccountInfo_All implements Runnable {
		@Override
		public void run() {
			try {
				String page = "http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/search_AccountInfo_All.php"; // 연결 할 URL 주소
				
				URL url = new URL(page); // URL 객체 생성
				
				HttpURLConnection conn = (HttpURLConnection)url.openConnection(); // 연결 객체 생성
				
				// 서버에 전달 할 파라미터
				String params = "phone_number=" + user_name;
				
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
								Log.i("tag", "documents.length() : " + contents.length());
								
								String AccountInfo_id = obj.getString("AccountInfo_id");
								Log.i("tag", "AccountInfo_id : " + AccountInfo_id);
								
								String PhoneNumber = obj.getString("PhoneNumber");
								Log.i("tag", "PhoneNumber : " + PhoneNumber);
								
								String FullTownName_1 = obj.getString("FullTownName_1");
								Log.i("tag", "FullTownName_1 : " + FullTownName_1);
								
								String TownName_1 = obj.getString("TownName_1");
								Log.i("tag", "TownName_1 : " + TownName_1);
								
								String X_1 = obj.getString("X_1");
								Log.i("tag", "X_1 : " + X_1);
								
								String Y_1 = obj.getString("Y_1");
								Log.i("tag", "Y_1 : " + Y_1);
								
								String FullTownName_2 = obj.getString("FullTownName_2");
								Log.i("tag", "FullTownName_2 : " + FullTownName_2);
								
								String TownName_2 = obj.getString("TownName_2");
								Log.i("tag", "TownName_2 : " + TownName_2);
								
								String X_2 = obj.getString("X_2");
								Log.i("tag", "X_2 : " + X_2);
								
								String Y_2 = obj.getString("Y_2");
								Log.i("tag", "Y_2 : " + Y_2);
								
								String ProfileImage = obj.getString("ProfileImage");
								Log.i("tag", "ProfileImage : " + ProfileImage);
								
								String NickName = obj.getString("NickName");
								Log.i("tag", "NickName : " + NickName);
								
								profile_image = ProfileImage;
								nick_name = NickName;
							}
							
							// runOnUiThread 기본
							// URL에 접속 성공했을 때 실행
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									tv_nick_name.setText(nick_name);
									
									
									Glide.with(ViewUsedTradePost_Activity.this)
										.load("http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/UsedTradePost_Image/"+profile_image)
										.fitCenter()
										.into(iv_profile_image);
								}
							});
							
							
						}
						catch (JSONException e) {
							Log.i("tag", "Home_Activity_error : " + e);
						}
						
					}
					
					else {
						// runOnUiThread 기본
						// URL에 접속 실패했을 때 실행
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								//findViewById(R.id.Prod_cpb).setVisibility(View.GONE); // 프로그래스바 안보이게 처리
								Toast.makeText(getApplicationContext(), "연결 문제 발생", Toast.LENGTH_SHORT).show();
							}
						});
					}
					
					conn.disconnect(); // 연결 끊기
					
				}
				
			} catch (Exception e) {
				Log.i("tag", "error :" + e);
			}
			
		}
	}
	
}