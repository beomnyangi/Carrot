package com.beom.carrot;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class splash_Activity extends AppCompatActivity {
	int count = 0;
	String result;
	
	Handler handler = new Handler(Looper.myLooper());
	Runnable no_login = new Runnable() {
		@Override
		public void run() {
			System.out.println("start runnable run");
			count++;
			if(count == 2){
				handler.removeCallbacks(no_login);
				Intent intent = new Intent(splash_Activity.this, FirstRun_Activity.class);
				startActivity(intent);
				finish();
			}
			else{
				handler.postDelayed(no_login, 500);
			}
			System.out.println("end runnable run");
		}
	};
	Runnable ok_login = new Runnable() {
		@Override
		public void run() {
			System.out.println("start runnable run");
			count++;
			if(count == 2){
				handler.removeCallbacks(ok_login);
				Intent intent = new Intent(splash_Activity.this, Home_Activity.class);
    			intent.putExtra("phone_number", result);
				startActivity(intent);
				finish();
			}
			else{
				handler.postDelayed(ok_login, 500);
			}
			System.out.println("end runnable run");
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		SharedPreferences sp = getSharedPreferences("login_data", Activity.MODE_PRIVATE);
		SharedPreferences.Editor edit = sp.edit();
		
		//클릭 됐을 때 할 내용 작성
		//저장된 값을 불러오기 위해 같은 네임파일을 찾음.
		result = sp.getString("phone_number","no");//test이름을 가진 키에 저장 된 값을 text에 저장. 값이 없을 시"no"이라는 값을 반환
		if(Objects.equals(result, "no")){
			handler.post(no_login);
		}
		else{
			handler.post(ok_login);
		}
		
		getHashKey();
		
	}
	
	private void getHashKey(){
		PackageInfo packageInfo = null;
		try {
			packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		if (packageInfo == null) {
			Log.e("KeyHash", "KeyHash:null");
		}
		for (Signature signature : packageInfo.signatures) {
			try {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
			} catch (NoSuchAlgorithmException e) {
				Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
			}
		}
	}
	
}