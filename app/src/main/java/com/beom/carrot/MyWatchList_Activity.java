package com.beom.carrot;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyWatchList_Activity extends AppCompatActivity {
	ProgressBar progressBar;
	
	int LIMIT = 10; // 페이징 할 때 한번에 불러올 게시글 갯수
	int OFFSET = 0; // 페이징 할 때 몇번째 열부터 보여줄지 설정하기 위함
	int post_count; // 전체 게시글 갯수
//	int post_count_limit = 0;
	
	String phone_number;
	
	ImageButton ib_back;
	
	MyWatchList_adapter adapter;
	RecyclerView recyclerView;
	
	private Handler handler_send = new Handler(Looper.myLooper());
	Runnable runnable_send = new Runnable() {
		
		@Override
		public void run() {
			Log.i("tag", "thread on : ");
			handler_send.post(runnable_send);
			handler_send.removeCallbacks(runnable_send);
			Log.i("tag", "thread off : ");
			
			setRecyclerView();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_watch_list);
		
		ib_back = findViewById(R.id.ib_back);
		
		progressBar = findViewById(R.id.progressBar);
		
		progressBar.setVisibility(View.INVISIBLE);
		
		/*저장된 로그인 정보를 불러오기 위해 같은 네임파일을 찾음.*/
		SharedPreferences sp = getSharedPreferences("login_data", Activity.MODE_PRIVATE);
		phone_number = sp.getString("phone_number","no");//test이름을 가진 키에 저장 된 값을 text에 저장. 값이 없을 시"no"이라는 값을 반환
		
		/*어댑터와 리사이클러뷰 세팅*/
		adapter.MyWatchList_info.clear(); //어댑터에서 데이터가 담기는 어레이리스트를 초기화
		recyclerView = (RecyclerView) findViewById(R.id.rv_watchlist);
		recyclerView.addItemDecoration(new SearchLocation_RecycleDiv_height(0));
		recyclerView.addItemDecoration(new SearchLocation_RecycleDiv_width(0));
		setRecyclerView();
		
		/*서버에서 홈화면의 개시글 가져와서 보여주기*/
//		Thread search_HomePost = new Thread(new search_HomePost());
//		search_HomePost.start();
		
		/*서버에서 홈화면의 개시글 가져와서 보여주기*/
		Thread search_HomePost_count = new Thread(new search_HomePost_count());
		search_HomePost_count.start();
		
		
		/*뒤로가기 이미지버튼 눌렀을 때*/
		ib_back.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				//버튼이 클릭 됐을 때
//				SharedPreferences sp = getSharedPreferences("login_data", Activity.MODE_PRIVATE);
//				String result = sp.getString("phone_number","no");//test이름을 가진 키에 저장 된 값을 text에 저장. 값이 없을 시"no"이라는 값을 반환
//				Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
				
				Intent intent = new Intent(MyWatchList_Activity.this, MyCarrot_Activity.class);
				intent.putExtra("phone_number", phone_number);
				startActivity(intent) ;
				finish();
			}
		});
		
		// 리프레시 했을 때(리사이클러뷰 맨 위애서 아래로 스와이프)
		SwipeRefreshLayout srl_MyWatchList = findViewById(R.id.srl_MyWatchList);
		srl_MyWatchList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				LIMIT = 10; // 페이징 할 때 한번에 불러올 게시글 갯수
				OFFSET = 0; // 페이징 할 때 몇번째 열부터 보여줄지 설정하기 위함
				
				/*서버에서 홈화면의 개시글 가져와서 보여주기*/
				Thread search_HomePost_count = new Thread(new search_HomePost_count());
				search_HomePost_count.start();
				
				srl_MyWatchList.setRefreshing(false);// 새로고침 완료(false를 하지 않으면 새로고침 아이콘 계속 동작함)
			}
		});
		
		// 리사이클러뷰 아이템의 현재 위치값을 알기 위한 리스너
		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
			}
			
			@Override
			public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				
				int lastPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
				lastPosition += 1;
				int totalCount = recyclerView.getAdapter().getItemCount();
				
				Log.i("tag","lastPosition : "+lastPosition);
				Log.i("tag","totalCount : "+totalCount);
				
				if(lastPosition == totalCount){
					
					Log.i("tag","새로운 페이지 불러오기");
					
					if(lastPosition >= post_count){
						// 마지막 게시물로 이동했을 때 실행
						Toast.makeText(getApplicationContext(), "마지막 게시글 입니다.", Toast.LENGTH_SHORT).show();
					}
					else{
						OFFSET = lastPosition;
						progressBar.setVisibility(View.VISIBLE);
						
						/*서버에서 홈화면의 게시글 가져와서 보여주기*/
						Thread search_HomePost = new Thread(new search_HomePost());
						search_HomePost.start();
						
					}
					
					
				}
			}
		});
		
	}
	
	//recyclerView 와 adapter 를 연결시켜주는 메소드
	void setRecyclerView(){
		LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.removeAllViewsInLayout();
		recyclerView.setLayoutManager(layoutManager);
		
		//액티비티에 context 와 item 데이터를 Adapter 에 넘겨준다.
		adapter = new MyWatchList_adapter(this, R.layout.activity_my_watch_list_item);
		recyclerView.setAdapter(adapter);
	}
	
	/*페이징을 위해 표시할 게시글 개수 구하기*/
	class search_HomePost_count implements Runnable {
		@Override
		public void run() {
			try {
				String page = "http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/search_LikedPosts.php"; // 연결 할 URL 주소
				//String page = "https://webhook.site/ab420874-fd80-48a6-9b3b-1750ebfdb40f"; // 연결 할 URL 주소
				
				URL url = new URL(page); // URL 객체 생성
				
				HttpURLConnection conn = (HttpURLConnection)url.openConnection(); // 연결 객체 생성
				
				// 서버에 전달 할 파라미터
				String params = "phone_number=" + phone_number;
				
				StringBuilder responseBody = new StringBuilder(); // 서버에서 받은 결과값을 저장 할 문자열
				//String responseBody = null;
				
				// 연결되면 실행
				if(conn != null) {
					Log.i("tag", "conn 연결");
					
					conn.setRequestProperty("Accept", "application/json"); // 서버에서 받을 데이터 타입 설정 (json | xml)
					conn.setConnectTimeout(10000); // 서버 접속시 연결 타임아웃 설정 (ms 단위)
					conn.setReadTimeout(10000); // 서버 접속시 데이터 가져오기 타임아웃 설정 (ms 단위)
					conn.setRequestMethod("POST"); // 요청 방식 선택 (GET | POST)
					conn.setRequestProperty("request", "select_WatchList_count"); // header 세팅 (key, velue)
					
					conn.getOutputStream().write(params.getBytes("utf-8")); // 서버에 파라미터 전달 (문자 인코딩 방식 선택)
					
					// URL에 접속 성공하면 (200)
					if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
						adapter.MyWatchList_info.clear(); //어댑터에서 데이터가 담기는 어레이리스트를 초기화
						
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
						String Stringmessage = String.valueOf(responseBody);
						post_count = Integer.parseInt(Stringmessage);
						
						/*서버에서 홈화면의 게시글 가져와서 보여주기*/
						Thread search_HomePost = new Thread(new search_HomePost());
						search_HomePost.start();
					}
				});
				
			} catch (Exception e) {
				Log.i("tag", "error :" + e);
			}
			
		}
	}
	
	/*중고거래 게시글 조회 후 리사이클러뷰에 표시하기 위함*/
	class search_HomePost implements Runnable {
		@Override
		public void run() {
			try {
				String page = "http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/search_LikedPosts.php"; // 연결 할 URL 주소
				//String page = "https://webhook.site/ab420874-fd80-48a6-9b3b-1750ebfdb40f"; // 연결 할 URL 주소
				
				URL url = new URL(page); // URL 객체 생성
				
				HttpURLConnection conn = (HttpURLConnection)url.openConnection(); // 연결 객체 생성
				
				// 서버에 전달 할 파라미터
				String params = "phone_number=" + phone_number;
				params += "&LIMIT=" + LIMIT;
				params += "&OFFSET=" + OFFSET;
				
				StringBuilder responseBody = new StringBuilder(); // 서버에서 받은 결과값을 저장 할 문자열
				//String responseBody = null;
				
				// 연결되면 실행
				if(conn != null) {
					Log.i("tag", "conn 연결");
					
					conn.setRequestProperty("Accept", "application/json"); // 서버에서 받을 데이터 타입 설정 (json | xml)
					conn.setConnectTimeout(10000); // 서버 접속시 연결 타임아웃 설정 (ms 단위)
					conn.setReadTimeout(10000); // 서버 접속시 데이터 가져오기 타임아웃 설정 (ms 단위)
					conn.setRequestMethod("POST"); // 요청 방식 선택 (GET | POST)
					conn.setRequestProperty("request", "select_WatchList"); // header 세팅 (key, velue)
					
					conn.getOutputStream().write(params.getBytes("utf-8")); // 서버에 파라미터 전달 (문자 인코딩 방식 선택)
					
					// URL에 접속 성공하면 (200)
					if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
//						adapter.MyWatchList_info.clear(); //어댑터에서 데이터가 담기는 어레이리스트를 초기화
						
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
						
						System.out.println("response_MyWatchList : " + responseBody);
						
						try {
							JSONObject object = new JSONObject(String.valueOf(responseBody));
							String message = object.getString("message");
							JSONArray contents = object.getJSONArray("contents");
							
							System.out.println("response_MyWatchList : " + message);
							System.out.println("response_MyWatchList : " + contents);
							
							System.out.println("response_MyWatchList : " + message.length());
							System.out.println("response_MyWatchList : " + contents.length());
							
							for (int i = 0; i < contents.length(); i++) {
								JSONObject obj = contents.getJSONObject(i);
								
								String post_id = obj.getString("post_id");
								String Account_ID = obj.getString("Account_ID");
								String image = obj.getString("image");
								String title = obj.getString("title");
								String TownName = obj.getString("TownName");
								String uploaded_time = obj.getString("uploaded_time");
								String upload_count = obj.getString("upload_count");
								String price = obj.getString("price");
								String chatting_count = obj.getString("chatting_count");
								String Like_count = obj.getString("Like_count");
								String PriceOffer_check = obj.getString("PriceOffer_check");
								String views = obj.getString("views");
								String SalesStatus = obj.getString("SalesStatus");

//                                Log.i("tag", "documents.length() : " + contents.length());
//                                Log.i("tag", "post_id : " + post_id);
//                                Log.i("tag", "Account_ID : " + Account_ID);
//                                Log.i("tag", "image : " + image);
//                                Log.i("tag", "title : " + title);
//                                Log.i("tag", "TownName : " + TownName);
//                                Log.i("tag", "uploaded_time : " + uploaded_time);
//                                Log.i("tag", "upload_count : " + upload_count);
//                                Log.i("tag", "price : " + price);
//                                Log.i("tag", "chatting_count : " + chatting_count);
//                                Log.i("tag", "heart_count : " + heart_count);
//                                Log.i("tag", "PriceOffer_check : " + PriceOffer_check);
								
								/*업로드 한 시간을 현재시간과 비교후 계산해서 어댑터에 저장*/
								long result_time = Long.parseLong(uploaded_time);
								formatTimeString(result_time);
								uploaded_time = formatTimeString(result_time);
								
								adapter.addPost(new MyWatchList_data(post_id, Account_ID, image, title, TownName, uploaded_time, upload_count, price, chatting_count, Like_count, PriceOffer_check, views, phone_number, SalesStatus));
							}
							
						}
						catch (JSONException e) {
							Log.i("tag", "MyWatchList_Activity_error : " + e);
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
						
						adapter.notifyDataSetChanged();
						
						progressBar.setVisibility(View.INVISIBLE);
					}
				});
				
			} catch (Exception e) {
				Log.i("tag", "error :" + e);
			}
			
		}
	}
	
	/** 몇분전, 방금 전, */
	static class TIME_MAXIMUM{
		public static final int SEC = 60;
		public static final int MIN = 60;
		public static final int HOUR = 24;
		public static final int DAY = 30;
		public static final int MONTH = 12;
	}
	
	public String formatTimeString(long regTime) {
		long curTime = System.currentTimeMillis()/1000;
		long diffTime = (curTime - regTime);
		String msg = null;
		if (diffTime < TIME_MAXIMUM.SEC) {
			msg = diffTime + "초 전";
		} else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
			msg = diffTime + "분 전";
		} else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
			msg = (diffTime) + "시간 전";
		} else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
			msg = (diffTime) + "일 전";
		} else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH) {
			msg = (diffTime) + "달 전";
		} else {
			msg = (diffTime) + "년 전";
		}
		return msg;
	}
	
}