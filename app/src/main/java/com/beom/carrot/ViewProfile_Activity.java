package com.beom.carrot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class ViewProfile_Activity extends AppCompatActivity {
	String phone_number;
	String find_phone_number;
	String profile_image;
	String nick_name;
	String account_info_id;
	
	TextView tv_nick_name;
	TextView tv_account_info_id;
	TextView tv_product_for_sale;
	
	ImageView iv_profile_image;
	
	ImageButton ib_back;
	
	ConstraintLayout lo_product_for_sale;
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_profile);
		
		tv_nick_name = findViewById(R.id.tv_nick_name);
		tv_account_info_id = findViewById(R.id.tv_account_info_id);
		tv_product_for_sale = findViewById(R.id.tv_product_for_sale);
		
		iv_profile_image = findViewById(R.id.iv_profile_image);
		
		ib_back = findViewById(R.id.ib_back);
		
		lo_product_for_sale = findViewById(R.id.lo_product_for_sale);
		
		/*저장된 값을 불러오기 위해 같은 네임파일을 찾음.*/
		SharedPreferences sp = getSharedPreferences("login_data",MODE_PRIVATE);
		phone_number = sp.getString("phone_number","no");//phone_number라는 이름을 가진 키에 저장 된 값을 text에 저장. 값이 없을 시"no"이라는 값을 반환
		
		Intent intent = getIntent();
		find_phone_number = intent.getStringExtra("phone_number");
		
		Thread search_AccountInfo_All = new Thread(new search_AccountInfo_All());
		search_AccountInfo_All.start();
		
		Thread HomePost_count = new Thread(new HomePost_count());
		HomePost_count.start();
		
		/*뒤로가기 이미지버튼 눌렀을 때*/
		ib_back.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				//버튼이 클릭 됐을 때
				
				finish();
			}
		});
		
		/*전체상품 클릭 했을 때*/
		lo_product_for_sale.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()){
					case MotionEvent.ACTION_DOWN: // ACTION_DOWN : 터치를 누르는 순간
						Log.i("tag","터치를 누르는 순간");
						break;
					case MotionEvent.ACTION_UP: // ACTION_UP : 터치를 때는 순간
						Log.i("tag","터치를 떼는 순간");
						
						Intent intent = new Intent(ViewProfile_Activity.this, SalesHistoryList_Activity.class);
						intent.putExtra("before_activity", "ViewProfile_Activity");
						intent.putExtra("phone_number", find_phone_number);
						startActivity(intent);
						
						Toast.makeText(getApplicationContext(), "프로필 정보를 볼 수 있는 화면으로 이동", Toast.LENGTH_SHORT).show();
						
						break;
					case MotionEvent.ACTION_MOVE: // ACTUIB_MOVE : 터치되고 있는 순간
						Log.i("tag","터치되고 있는 순간");
						break;
				}
				return true;
			}
		});
		
	}
	
	/*프로필 정보에 특정 사용자가 작성한 전체 게시글 갯수 조회*/
	class HomePost_count implements Runnable {
		@Override
		public void run() {
			try {
				String page = "http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/search_HomePost.php"; // 연결 할 URL 주소
				//String page = "https://webhook.site/ab420874-fd80-48a6-9b3b-1750ebfdb40f"; // 연결 할 URL 주소
				
				URL url = new URL(page); // URL 객체 생성
				
				HttpURLConnection conn = (HttpURLConnection)url.openConnection(); // 연결 객체 생성
				
				// 서버에 전달 할 파라미터
				String params = "phone_number=" + find_phone_number;
				
				StringBuilder responseBody = new StringBuilder(); // 서버에서 받은 결과값을 저장 할 문자열
				//String responseBody = null;
				
				// 연결되면 실행
				if(conn != null) {
					Log.i("tag", "conn 연결");
					
					conn.setRequestProperty("Accept", "application/json"); // 서버에서 받을 데이터 타입 설정 (json | xml)
					conn.setConnectTimeout(10000); // 서버 접속시 연결 타임아웃 설정 (ms 단위)
					conn.setReadTimeout(10000); // 서버 접속시 데이터 가져오기 타임아웃 설정 (ms 단위)
					conn.setRequestMethod("POST"); // 요청 방식 선택 (GET | POST)
					conn.setRequestProperty("request", "HomePost_count"); // header 세팅 (key, velue)
					
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
						
						/*서버에서 받아온 결과값*/
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
						
						String result = String.valueOf(responseBody);
						tv_product_for_sale.setText("전체상품 "+result+"개");
					}
				});
				
				
			} catch (Exception e) {
				Log.i("tag", "error :" + e);
			}
			
		}
	}
	
	class search_AccountInfo_All implements Runnable {
		@Override
		public void run() {
			try {
				String page = "http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/search_AccountInfo_All.php"; // 연결 할 URL 주소
				
				URL url = new URL(page); // URL 객체 생성
				
				HttpURLConnection conn = (HttpURLConnection)url.openConnection(); // 연결 객체 생성
				
				// 서버에 전달 할 파라미터
				String params = "phone_number=" + find_phone_number;
				
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
								account_info_id = AccountInfo_id;
							}
							
							// runOnUiThread 기본
							// URL에 접속 성공했을 때 실행
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									tv_nick_name.setText(nick_name);
									tv_account_info_id.setText(account_info_id);
									
									Glide.with(ViewProfile_Activity.this)
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