package com.beom.carrot;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class SearchLocation_Activity extends AppCompatActivity {
	//카카오 로컬 api 키
	String REST_API_KEY_KAKAO_LOCAL = "073bda1251248e5de32b313df6c28674";
	
	private FusedLocationProviderClient fusedLocationClient;    // 현재 위치를 가져오기 위함
	int REQUEST_CODE = 1;
	
	EditText et_search;
	String get_value;
	String addr;
	int search_type = 0; // 1일 때 에디트텍스트로 검색, 2일 때 gps를 이용한 검색
	
	String address_name;
	String region_1depth_name;
	String region_2depth_name;
	String region_3depth_h_name;
	String region_3depth_name;
	String town_name;
	String x;
	String y;
	
	SearchLocation_Adapter adapter;
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
		setContentView(R.layout.activity_search_location);
		
		adapter.searchaddress_info.clear();
		et_search = findViewById(R.id.et_search_town);
		
		recyclerView = (RecyclerView) findViewById(R.id.rv_search_result_town);
		recyclerView.addItemDecoration(new SearchLocation_RecycleDiv_height(0));
		recyclerView.addItemDecoration(new SearchLocation_RecycleDiv_width(0));
		setRecyclerView();
		
		
		//gps로 현재위치를 찾기 위해 선언
		fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
		
		//현재 위치로 주소 찾기
		Button bt_search_current_location = findViewById(R.id.bt_search_current_town);
		bt_search_current_location.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				search_type = 2;

				//위치정보 접근을 위한 매니페스트 권한 요청
				ActivityCompat.requestPermissions(SearchLocation_Activity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
				
				Log.i("tag","현재위치로 주소 찾기 버튼 클릭");
				
				Thread get_address_info = new Thread(new get_address_info());
				get_address_info.start();
			}
		});
		
		//검색으로 주소 찾기
		Button bt_search_location = findViewById(R.id.bt_search_town);
		bt_search_location.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				//버튼이 클릭 됐을 때
				search_type = 1;
				get_value = et_search.getText().toString();
				addr = get_value;
				
				Log.i("tag","검색으로 주소 찾기 버튼 클릭");
				
				Thread get_address_info = new Thread(new get_address_info());
				get_address_info.start();
			}
		});
		
	}
	
	//recyclerView 와 adapter 를 연결시켜주는 메소드
	void setRecyclerView(){
		LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.removeAllViewsInLayout();
		recyclerView.setLayoutManager(layoutManager); // 세로
		//recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)); // 가로
		
		//액티비티에 context 와 item 데이터를 Adapter 에 넘겨준다.
		adapter = new SearchLocation_Adapter(this, R.layout.activity_search_location_item);
		recyclerView.setAdapter(adapter);
	}
	
	class get_address_info implements Runnable {
		@Override
		public void run() {
			String REST_API_KEY = REST_API_KEY_KAKAO_LOCAL; // 다음 REST_API_KEY;
			String header = "KakaoAK " + REST_API_KEY; // KakaoAK 다음에 공백 추가

			String apiURL = null;

			if(search_type == 1){
				Log.i("tag","검색으로 주소 찾기 진입");
				
				//주소로 위치 찾기
				apiURL = "https://dapi.kakao.com/v2/local/search/address.json?page=45&query="+addr;
				Log.i("tag",apiURL);
			}
			else if(search_type == 2){
				Log.i("tag","현위치로 주소 찾기 진입");
				
				//gps 좌표로 위치 찾기
				apiURL = "https://dapi.kakao.com/v2/local/geo/coord2regioncode.json?x="+x+"&y="+y;
				Log.i("tag",apiURL);
			}
			else{
				Log.i("tag","위치 찾기 선택 에러");
			}

			Map<String, String> requestHeaders = new HashMap<>();
			requestHeaders.put("Authorization", header);
			String responseBody = get(apiURL,requestHeaders);

			Log.i("tag","responseBody : "+ responseBody);
			System.out.println(responseBody);

			try {
				//넘어온 result 값을 JSONObject 로 변환해주고, 값을 가져오면 되는데요.
				// result 를 Log에 찍어보면 어떻게 가져와야할 지 감이 오실거에요.
				
				JSONObject object = new JSONObject(responseBody);
				JSONArray documents = object.getJSONArray("documents");
				JSONObject meta = object.getJSONObject("meta");

				adapter.searchaddress_info.clear();

				for (int i = 0; i < documents.length(); i++) {
					JSONObject obj = documents.getJSONObject(i);
					Log.i("tag","documents.length() : "+ documents.length());

					//주소로 위치 찾았을 때 실행
					if(search_type == 1){
						JSONObject address = obj.getJSONObject("address");
						Log.i("tag","address"+address);
						
						address_name = address.getString("address_name");
						Log.i("tag","address_name"+address_name);
						
						region_3depth_h_name = address.getString("region_3depth_h_name");
						Log.i("tag","region_3depth_h_name"+region_3depth_h_name);
						region_3depth_name = address.getString("region_3depth_name");
						Log.i("tag","region_3depth_name"+region_3depth_name);
						
						if(Objects.equals(region_3depth_h_name, "")){
							town_name = region_3depth_name;
						}
						else if(Objects.equals(region_3depth_name, "")){
							town_name = region_3depth_h_name;
						}
						else{
							town_name = region_3depth_name;
						}

						x = address.getString("x");
						Log.i("tag","x"+x);

						y = address.getString("y");
						Log.i("tag","y"+y);
						
						adapter.addInfo(new SearchLocation_Data(address_name,town_name ,x, y));
					}

					//gps 좌표로 위치 찾았을 때 실행
					else if(search_type == 2){
						address_name = obj.getString("address_name");
						Log.i("tag","address_name"+address_name);
						
						region_3depth_name = obj.getString("region_3depth_name");
						Log.i("tag","region_3depth_name"+region_3depth_name);
						town_name = region_3depth_name;
						
						x = obj.getString("x");
						Log.i("tag","x"+x);
						
						y = obj.getString("y");
						Log.i("tag","y"+y);
						
						adapter.addInfo(new SearchLocation_Data(address_name,town_name ,x, y));
					}
					else{
						Toast.makeText(getApplicationContext(), "에러", Toast.LENGTH_SHORT).show();
					}
				}
				String total_count = meta.getString("total_count");

				Log.i("tag","total_count : "+ total_count);
			}
			catch (Exception e) {
				Log.i("tag","e : "+ e);
			}
			handler_send.post(runnable_send);
		}
		
		final private String get(String apiUrl, Map<String, String> requestHeaders){
			HttpURLConnection con = connect(apiUrl);
			try {
				con.setRequestMethod("GET");
				for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
					con.setRequestProperty(header.getKey(), header.getValue());
				}
				
				int responseCode = con.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
					return readBody(con.getInputStream());
				} else { // 에러 발생
					return readBody(con.getErrorStream());
				}
			} catch (IOException e) {
				throw new RuntimeException("API 요청과 응답 실패", e);
			} finally {
				con.disconnect();
			}
		}
		private HttpURLConnection connect(String apiUrl){
			try {
				URL url = new URL(apiUrl);
				return (HttpURLConnection)url.openConnection();
			} catch (MalformedURLException e) {
				throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
			} catch (IOException e) {
				throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
			}
		}
		private String readBody(InputStream body){
			InputStreamReader streamReader = new InputStreamReader(body);
			
			try (BufferedReader lineReader = new BufferedReader(streamReader)) {
				StringBuilder responseBody = new StringBuilder();
				
				String line;
				while ((line = lineReader.readLine()) != null) {
					responseBody.append(line);
				}
				
				return responseBody.toString();
			} catch (IOException e) {
				throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
			}
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		
		if (requestCode == REQUEST_CODE) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {   // 권한요청이 허용된 경우
				
				if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
					// TODO: Consider calling
					//    ActivityCompat#requestPermissions
					Toast.makeText(this, "권한이 없어 해당 기능을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show();    // 권한요청이 거절된 경우
					return;
				}
				
				// 위도와 경도를 가져온다.
				fusedLocationClient.getLastLocation()
					.addOnSuccessListener(this, new OnSuccessListener<Location>() {
						@Override
						public void onSuccess(Location location) {
							// Got last known location. In some rare situations this can be null.
							if (location != null) {
								// 파라미터로 받은 location을 통해 위도, 경도 정보를 텍스트뷰에 set.
								
								Log.i("tag","위도(y): " + location.getLatitude() + " / 경도(x): " + location.getLongitude());
								
								y = String.valueOf(location.getLatitude());
								x = String.valueOf(location.getLongitude());
								
								Log.i("tag", "x : "+x);
								Log.i("tag", "y : "+y);
								
								Thread get_address_info = new Thread(new get_address_info());
								get_address_info.start();
								
								
							}
						}
					});
			}
			else{
				Toast.makeText(this, "권한이 없어 해당 기능을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show();    // 권한요청이 거절된 경우
			}
		}
	}

}