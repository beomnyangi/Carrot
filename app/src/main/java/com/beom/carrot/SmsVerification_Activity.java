package com.beom.carrot;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.Random;

public class SmsVerification_Activity extends AppCompatActivity {
	static final int SMS_SEND_PERMISSON = 1;
	
	String checknum;
	String phone_number;
	String address_name;
	String town_name;
	String x;
	String y;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smsverification);
		
		EditText et_phonenumber = findViewById(R.id.et_phonenumber);
		EditText et_checknum = findViewById(R.id.et_checknum);
		
		Intent intent = getIntent();
  		address_name = intent.getStringExtra("address_name");
		town_name = intent.getStringExtra("town_name");
		x = intent.getStringExtra("x");
		y = intent.getStringExtra("y");
		
		Log.i("tag", "intent_info"+address_name);
		Log.i("tag", "town_name"+town_name);
		Log.i("tag", "intent_info"+x);
		Log.i("tag", "intent_info"+y);
		
		/*SMS 발송 권한 확인*/
		int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
		if(permissionCheck != PackageManager.PERMISSION_GRANTED){
			//권한 거부
			if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)){
				Toast.makeText(getApplicationContext(), "SMS 발송을 위한 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
			}
			//권한 허용
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_SEND_PERMISSON);
		}
		
		/*뒤로가기 이미지버튼 눌렀을 때*/
		ImageButton ib_back = findViewById(R.id.ib_back);
		ib_back.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SmsVerification_Activity.this, SearchLocation_Activity.class) ;
				startActivity(intent) ;
				finish();
			}
		});
		
		/*인증문자 확인*/
		Button bt_sms_search = findViewById(R.id.bt_checknum);
		bt_sms_search.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				/*인증번호 일치 여부 확인*/
				if(Objects.equals(checknum, et_checknum.getText().toString())){

					/*가입 된 회원정보가 있나 검색 후 조건에 따라 다음 화면으로 전환*/
					Thread search_AccountInfo = new Thread(new search_AccountInfo());
					search_AccountInfo.start();
				}
				else{
					Toast.makeText(getApplicationContext(), "인증번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		
		/*인증문자 전송*/
		Button bt_get_verification_sms = findViewById(R.id.bt_get_verification_sms);
		bt_get_verification_sms.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				checknum = numberGen(4, 2);
				phone_number = et_phonenumber.getText().toString();
				
				String pattern = "^010\\d{4}\\d{4}$"; //010으로 시작하는 휴대폰번호만 가능한 정규식
				
				if(phone_number.matches(pattern)){
					String message_contents = "[CARROT]\n인증번호 ["+checknum+"]";
					
					/**
					 * todo 테스트 끝나면 주석 지우기
					 * */
					//sendSMS(phone_number, message_contents);
					Log.i("tag", "인증번호 : "+checknum);
					
				}
				else{
					Toast.makeText(getApplicationContext(), "휴대폰 번호를 올바로 입력하세요", Toast.LENGTH_SHORT).show();
				}
				
				
				
			}
		});
	}
	
	/*SMS 발송 기능*/
	public void sendSMS(String phoneNumber, String message){
		SmsManager sms = SmsManager.getDefault();
		
		sms.sendTextMessage(phoneNumber, null, message, null, null);
		
		Toast.makeText(getApplicationContext(), "메세지가 전송 되었습니다.", Toast.LENGTH_SHORT).show();
	}
	
	/**
	전달된 파라미터에 맞게 난수를 생성한다
	@param len : 생성할 난수의 길이
	@param dupCd : 중복 허용 여부 (1: 중복허용, 2:중복제거)
	*/
	public String numberGen(int len, int dupCd){
		Random rand = new Random();
		String numStr = ""; //난수가 저장될 변수
		for(int i=0;i<len;i++) {
			//0~9 까지 난수 생성
			String ran = Integer.toString(rand.nextInt(10));
			if(dupCd==1) {
				//중복 허용시 numStr에 append
				numStr += ran;
			}else if(dupCd==2) {
				//중복을 허용하지 않을시 중복된 값이 있는지 검사한다
				if(!numStr.contains(ran)) {
					//중복된 값이 없으면 numStr에 append
					numStr += ran;
				}else {
					//생성된 난수가 중복되면 루틴을 다시 실행한다
					i-=1;
				}
			}
		}
		return numStr;
	}
	
	class search_AccountInfo implements Runnable {
		@Override
		public void run() {
			try {
				String page = "http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/search_AccountInfo.php"; // 연결 할 URL 주소
				//String page = "https://webhook.site/ab420874-fd80-48a6-9b3b-1750ebfdb40f"; // 연결 할 URL 주소
				
				URL url = new URL(page); // URL 객체 생성
				
				HttpURLConnection conn = (HttpURLConnection)url.openConnection(); // 연결 객체 생성
				
				// 서버에 전달 할 파라미터
				String params = "phone_number=" + phone_number;
				
				StringBuilder sb = new StringBuilder(); // 서버에서 받은 결과값을 저장 할 문자열
				
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
							sb.append(line);
						}
						
						br.close(); // 버퍼리더 종료
						
						Log.i("tag", "결과 문자열 :" +sb.toString());
						
						// 서버에서 받은 데이터가 json 타입일 경우
						//JSONArray jsonResponse = new JSONArray(sb.toString());
						//Log.i("tag", "확인 jsonArray : " + jsonResponse);
						
						// 백그라운드 스레드에서는 메인화면을 변경 할 수 없음
						// runOnUiThread(메인 스레드영역)
						// URL에 접속 성공했을 때 실행
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								//txt_result.setText(sb.toString()); // textview를 받아온 결과값으로 세팅
								String result = sb.toString();
								if(result.equals("")){
									//회원정보 없음
									
									
									/*프로필 설정으로 이동해서 회원가입을 완료하기 위해 필요한 정보와 함께 화면전환*/
									Intent intent = new Intent(SmsVerification_Activity.this, ProfileSettings_Activity.class);
									intent.putExtra("phone_number", phone_number);
									intent.putExtra("address_name", address_name);
									intent.putExtra("town_name", town_name);
									intent.putExtra("x", x);
									intent.putExtra("y", y);
									startActivity(intent);
									finish();
									
								}
								else{
									//회원정보 있음
									
									Toast.makeText(getApplicationContext(), "이미 가입 된 회원입니다.", Toast.LENGTH_SHORT).show();
								}
							}
						});
						
					}
					else {
						// runOnUiThread 기본
						// URL에 접속 실패했을 때 실행
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
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