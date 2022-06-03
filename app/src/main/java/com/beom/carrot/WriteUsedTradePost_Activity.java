package com.beom.carrot;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class WriteUsedTradePost_Activity extends AppCompatActivity {
	
	ImageButton ib_back;
	TextView tv_ok;
	TextView tv_picture_limit;
	EditText et_title;
	EditText et_price;
	EditText et_contents;
	RadioButton rb_PriceOffer;
	ImageButton ib_picture;
	
	RecyclerView recyclerView;  // 이미지를 보여줄 리사이클러뷰
	WriteUsedTradePost_Adapter adapter;  // 리사이클러뷰에 적용시킬 어댑터
	
	String PriceOffer_check;
	String title;
	String Price;
	String contents;
	String phone_number;
	String town_name;
	String now;
	String image_uri;
	String count_image = "0";
	
	String get_image;
	String get_title;
	String get_price;
	String get_contents;
	String get_PriceOffer_check;
	
	String uploadFilePath = ""; //경로를 모르겠으면, 갤러리 어플리케이션 가서 메뉴->상세 정보
	String now_time;
	int serverResponseCode = 0;
	int REQUEST_SELECT_PHOTO = 1;
	int REQUEST_TAKE_PHOTO = 2;
	
	String upLoadServerUri = null;
	
	String currentPhotoPath;
	
	String edit_post_check; //게시글 수정 여부를 확인하기 위한 변수
	
	String post_id;
	
	ArrayList save_image_uri = new ArrayList();
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_used_trade_post);
		
		/*저장된 로그인 정보를 불러오기 위해 같은 네임파일을 찾음.*/
		SharedPreferences sp = getSharedPreferences("login_data", Activity.MODE_PRIVATE);
		String result = sp.getString("phone_number","no");//test이름을 가진 키에 저장 된 값을 text에 저장. 값이 없을 시"no"이라는 값을 반환
		phone_number = result;
		
		Intent intent = getIntent();
		town_name = intent.getStringExtra("town_name");
		post_id = intent.getStringExtra("post_id");
		edit_post_check = intent.getStringExtra("edit_post_check");
		
		ib_back = findViewById(R.id.ib_back);
		tv_ok = findViewById(R.id.tv_ok);
		et_title = findViewById(R.id.et_title);
		et_price = findViewById(R.id.et_price);
		et_contents = findViewById(R.id.et_contents);
		rb_PriceOffer = findViewById(R.id.rb_PriceOffer);
		ib_picture = findViewById(R.id.ib_picture);
		recyclerView = findViewById(R.id.rv_image_view);
		tv_picture_limit = findViewById(R.id.tv_picture_limit);
		
		rb_PriceOffer.setChecked(false);
		rb_PriceOffer.setSelected(false);
		PriceOffer_check = "0";
		
		setRecyclerView();
		
		count_image = String.valueOf(adapter.getItemCount());
		tv_picture_limit.setText(count_image); // 어레이리스트에 담긴 사진 uri 데이터 갯수
		
		if(Objects.equals(edit_post_check, "edit_start")){
//			Toast.makeText(getApplicationContext(), "게시글 수정 하기", Toast.LENGTH_SHORT).show();
			
			/*서버에 데이터베이스에서 게시물의 내용을 가져오기*/
			Thread search_HomePost_detail = new Thread(new search_HomePost_detail());
			search_HomePost_detail.start();
		}
		else{
//			Toast.makeText(getApplicationContext(), "게시글 수정 안 하기", Toast.LENGTH_SHORT).show();
		}
		
		
		/*사진 추가하기 버튼 클릭했을 때 동작*/
		ib_picture.setOnClickListener(new Button.OnClickListener() {
			@SuppressLint("IntentReset")
			@Override
			public void onClick(View v) {
				//버튼이 클릭 됐을 때
				
				//앨범과 카메라 사용을 위한 매니페스트 권한 요청
				ActivityCompat.requestPermissions(WriteUsedTradePost_Activity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1); //WRITE_EXTERNAL_STORAGE 자리에 원하는 퍼미션 이름으로 변경
				ActivityCompat.requestPermissions(WriteUsedTradePost_Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
				ActivityCompat.requestPermissions(WriteUsedTradePost_Activity.this, new String[]{Manifest.permission.CAMERA}, 1);
				
				/*이미지가 10개 이하로 선택 됐을 때만 실행*/
				if(adapter.getItemCount() < 10) {
					/*사진촬영이나 갤러리 접근을 위한 다이얼로그 띄우기*/
					ShowDialog();
				}
				else{
					Toast.makeText(getApplicationContext(), "이미지는 최대 10장까지 선택 할 수 있습니다.", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		/*뒤로가기 버튼 클릭 했을 때 엑티비티 종료 후 홈으로*/
		ib_back.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				//버튼이 클릭 됐을 때
				Intent intent = new Intent(WriteUsedTradePost_Activity.this, Home_Activity.class) ;
				intent.putExtra("phone_number", phone_number);
				startActivity(intent) ;
				finish();
				adapter.uri_info.clear();
			}
		});
		
		/*라디오 버튼*/
		rb_PriceOffer.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				//버튼이 클릭 됐을 때
				if(rb_PriceOffer.isSelected()){
					rb_PriceOffer.setChecked(false);
					rb_PriceOffer.setSelected(false);
					PriceOffer_check = "0";
				} else {
					rb_PriceOffer.setChecked(true);
					rb_PriceOffer.setSelected(true);
					PriceOffer_check = "1";
				}
			}
		});
		
		/*확인 버튼 눌렀을 때 서버로 데이터 보내고 데이터베이스에 저장*/
		tv_ok.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				//버튼이 클릭 됐을 때
				
				/*이미지를 서버에 업로드하는 부분*/
				new Thread(new Runnable() {
					public void run() {
						/*원하는 내용 작성*/
						
						Log.i("tagyt1", "adapter.temp_save" + adapter.temp_save);
						
						Log.i("tagyt444444", "adapter.temp_save" + adapter.temp_save);
						
						if(!String.valueOf(adapter.temp_save).equals("[]")){
							Log.i("tag", "이미지 넣기");
							
							for(Object i : adapter.temp_save) { //for문을 통한 전체출력
								Log.i("tag", "temp_save : " + i);
								String temp_name = (String) i;
								Log.i("tag", "이미지 넣기 temp_name: "+temp_name);
								
								Log.i("tagyt44",temp_name);
								
								long now = System.currentTimeMillis();
								now_time = String.valueOf(now);
								
								
								Log.i("tagyt2","이미지넣기 : ");
								
								Log.i("tagyt3","temp_name : "+temp_name);
								
								String image_check = String.valueOf(temp_name.contains("http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/UsedTradePost_Image/"));
								
								if(image_check.equals("true")){
									Log.i("tagyt44","true");
									
									temp_name = temp_name.replaceFirst("http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/UsedTradePost_Image/", "");
									save_image_uri.add(temp_name);
									
									Log.i("tagyt44",temp_name);
								}
								else{
									Log.i("tagyt44","false");
								}
								
								uploadFile(temp_name);
								
							}
							image_uri = new Gson().toJson(save_image_uri); // uri 정보들을 담고있는 어레이리스트를 json으로 변환해서 저장
							Log.i("tag","image_uri : "+image_uri);
							
							
							Log.i("tagyt44",image_uri);
							
							
						}
						else{
							Log.i("tag", "기본 이미지 넣기");
							
							Log.i("tagyt4","이미지 안 넣기 : ");
							
							Log.i("tagyt44","??????????");
							
							
							image_uri = "empty_HomePost_image.png"; // 기본이미지 경로를 uri 주소로 저장
							Log.i("tag","image_uri : "+image_uri);
						}
						
						Log.i("tagyt5","image_uri : "+image_uri);
						
						title = String.valueOf(et_title.getText());
						Price = String.valueOf(et_price.getText());
						contents = String.valueOf(et_contents.getText());
						now = String.valueOf(System.currentTimeMillis()/1000);
						
						if(Objects.equals(edit_post_check, "edit_start")){
							Log.i("tag","게시글 수정 하기");
							
							Thread edit = new Thread(new edit());
							edit.start();
						}
						else{
							Log.i("tag","게시글 저장하기");
							
							Thread save = new Thread(new save());
							save.start();
						}
						
						
					}
				}).start();
			}
		});
	}
	
//	/*디바이스의 뒤로가기 버튼 눌렀을 때 동작*/
//	@Override
//	public void onBackPressed() {
//		super.onBackPressed();
//		Intent intent = new Intent(WriteUsedTradePost_Activity.this, Home_Activity.class) ;
//		intent.putExtra("phone_number", phone_number);
//		startActivity(intent) ;
//		finish();
//		adapter.uri_info.clear();
//	}
	
	/*게시글 수정 할 때 서버에서 가져온 이미지 정보를 표시하기 위해 재가공*/
	public ArrayList edit_setImageList(String uri){
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
			
			Log.i("tag", "uploadFilePathget_image: " + stringArray[j]);
			
			adapter.addInfo(new WriteUsedTradePost_Data("http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/UsedTradePost_Image/"+stringArray[j]));
			adapter.temp_save.add("http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/UsedTradePost_Image/"+stringArray[j]);
		}
		
		return itemList;
	}
	
	/*게시글 수정을 위해 기존 게시글 내용 조회 후 출력*/
	class search_HomePost_detail implements Runnable {
		@Override
		public void run() {
			try {
				String page = "http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/search_HomePost_detail.php"; // 연결 할 URL 주소
				
				URL url = new URL(page); // URL 객체 생성
				
				HttpURLConnection conn = (HttpURLConnection)url.openConnection(); // 연결 객체 생성
				
				// 서버에 전달 할 파라미터
				String params = "post_id=" + post_id;
				
				Log.i("tag", "post_id : "+post_id);
				
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
								String post_PriceOffer_check = obj.getString("PriceOffer_check");
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
								Log.i("tag", "PriceOffer_check : " + post_PriceOffer_check);
								Log.i("tag", "views : " + views);
								Log.i("tag", "SalesStatus : " + SalesStatus);
								
								
								
								
								get_image = image;
								get_title = title;
								get_price = price;
								get_contents = post_contents;
								get_PriceOffer_check = post_PriceOffer_check;
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
						
						Log.i("tag", "get_image : " + get_image);

						
						
						et_title.setText(get_title);
						et_price.setText(get_price);
						et_contents.setText(get_contents);
						
						if(Objects.equals(get_PriceOffer_check, "0")){
							rb_PriceOffer.setChecked(false);
							rb_PriceOffer.setSelected(false);
							PriceOffer_check = "0";
						}
						else {
							rb_PriceOffer.setChecked(true);
							rb_PriceOffer.setSelected(true);
							PriceOffer_check = "1";
						}
					
					}
					
				});
				
				if(Objects.equals(get_image, "empty_HomePost_image.png")){
					Log.i("tag", "기존 이미지 없음");
				}
				else{
					Log.i("tag", "기존 이미지 있음");
					
					/*원하는 곳에서 바로 사용하는 쓰레드*/
					new Thread(new Runnable() {
						public void run() {
							/*원하는 내용 작성*/
							
							Log.i("tag", "uploadFilePath get_image: " + get_image);
							edit_setImageList(get_image);
							setRecyclerView();
						}
					}).start();
					
				}
				
				
			} catch (Exception e) {
				Log.i("tag", "error :" + e);
			}
			
		}
	}
	
	/*다이얼로그 보여주는 함수*/
	public void ShowDialog() {
		
		AlertDialog.Builder msgBuilder = new AlertDialog.Builder(this)
			.setTitle("사진 추가")
			//.setMessage("선택 하세요")

			.setPositiveButton("갤러리", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int pos) {
					//finish();
					Log.i("tag", String.valueOf(dialog));
					Log.i("tag", String.valueOf(pos));
					Log.i("tag", "갤러리");
					
					/*이미지 파일 여러개 불러오기*/
					Intent SelectImageIntent = new Intent(Intent.ACTION_PICK);
					SelectImageIntent.setType("image/*");
					SelectImageIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
					startActivityForResult(Intent.createChooser(SelectImageIntent,"Select Picture"), REQUEST_SELECT_PHOTO); // 다중선택을 하려면 포토로 들어가야 됨
				} })
			.setNegativeButton("촬영", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int pos) {
					//finish();
					Log.i("tag", String.valueOf(dialog));
					Log.i("tag", String.valueOf(pos));
					Log.i("tag", "촬영");
					
					/*카메라로 사진촬영 후 가져오기*/
					dispatchTakePictureIntent();
				} })
			.setNeutralButton("취소", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int pos) {
					Log.i("tag", String.valueOf(dialog));
					Log.i("tag", String.valueOf(pos));
					Log.i("tag", "취소");
				} });
		
		msgBuilder.create().show();
	}
	
	//recyclerView 와 adapter 를 연결시켜주는 메소드
	void setRecyclerView(){
		LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.removeAllViewsInLayout();
		recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)); // 가로
		
		//액티비티에 context 와 item 데이터를 Adapter 에 넘겨준다.
		adapter = new WriteUsedTradePost_Adapter(this, R.layout.activity_write_used_trade_post_image_item);
		recyclerView.setAdapter(adapter);
	}
	
	/*비트맵정보를 uri정보로 변환 해 줌*/
	public Uri BitmapToUri(Context context, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		
		long now = System.currentTimeMillis();
		Date date = new Date(now);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyymmdd_hhmmss");
		String image_title = sdf.format(date);
		
		String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, image_title, null);
		return Uri.parse(path);
	}
	
	/*카메라로 촬영한 이미지를 파일로 저장해줌*/
	public File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(
			imageFileName,  /* prefix */
			".jpg",         /* suffix */
			storageDir      /* directory */
		);
		
		// Save a file: path for use with ACTION_VIEW intents
		currentPhotoPath = image.getAbsolutePath();
		return image;
	}
	
	/*카메라 인텐트를 실행하는 부분*/
	public void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				// Error occurred while creating the File
				Log.e("tag", "IOException : ", ex);
			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				Uri photoURI = FileProvider.getUriForFile(this,
					"com.beom.carrot.fileprovider",
					photoFile);
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
				startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
			}
		}
	}
	
	/*이미지 가져온 후 실행되는 부분*/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		Log.i("tag", "requestCode"+requestCode);
		Log.i("tag", "resultCode"+resultCode);
		Log.i("tag", "data"+data);
		
		if(requestCode == REQUEST_SELECT_PHOTO){
			if(data == null){   // 어떤 이미지도 선택하지 않은 경우
				Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
			}
			else{   // 이미지를 하나라도 선택한 경우
				if(data.getClipData() == null){     // 이미지를 하나만 선택한 경우
					Log.i("tag", String.valueOf(data.getData()));
					Uri imageUri = data.getData();
					
					Log.i("tag", String.valueOf(imageUri));
					
					//절대경로 획득**
					Cursor c = getContentResolver().query(Uri.parse(imageUri.toString()), null, null, null, null);
					c.moveToNext();
					uploadFilePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
					
					adapter.addInfo(new WriteUsedTradePost_Data(uploadFilePath));
					setRecyclerView();
					
					Log.i("tag", "uploadFilePath : " + uploadFilePath);
					
					new Thread(new Runnable() {
						public void run() {
							/*확인 버튼을 누르기 전 까지 사진 정보를 임시저장 함*/
							adapter.temp_save.add(uploadFilePath);
							
							Log.i("tagyt12","adapter.temp_save : "+adapter.temp_save);
						}
					}).start();
					
					
				}
				else{      // 이미지를 여러장 선택한 경우
					ClipData clipData = data.getClipData();
					Log.i("tag", String.valueOf(clipData.getItemCount()));
					
					if(clipData.getItemCount() > 10){   // 선택한 이미지가 11장 이상인 경우
						Toast.makeText(getApplicationContext(), "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
					}
					else{   // 선택한 이미지가 1장 이상 10장 이하인 경우
						Log.i("tag", "multiple choice");
						
						/*이미지가 10개 이하로 선택 됐을 때만 실행*/
						if(adapter.getItemCount() + clipData.getItemCount()  < 11) {
							for (int i = 0; i < clipData.getItemCount(); i++){
								Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
								Log.i("tag", String.valueOf(imageUri));
								try {
									
									// 절대경로 획득
									Cursor c = getContentResolver().query(Uri.parse(imageUri.toString()), null, null, null, null);
									c.moveToNext();
									uploadFilePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
									
									adapter.addInfo(new WriteUsedTradePost_Data(uploadFilePath));  //uri를 list에 담는다.
									setRecyclerView();
									
									Log.i("tag", "uploadFilePath : " + uploadFilePath);
									
									/*원하는 곳에서 바로 사용하는 쓰레드*/
									new Thread(new Runnable() {
										public void run() {
											/*확인 버튼을 누르기 전 까지 사진 정보를 임시저장 함*/
											adapter.temp_save.add(uploadFilePath);
											Log.i("tagyt13","adapter.temp_save : "+adapter.temp_save);
										}
									}).start();
									
								} catch (Exception e) {
									Log.e("tag", "File select error", e);
								}
							}
						}
						else{
							Toast.makeText(getApplicationContext(), "이미지는 최대 10장까지 선택 할 수 있습니다.", Toast.LENGTH_SHORT).show();
						}
						
					}
				}
			}
		}
		else if(requestCode == REQUEST_TAKE_PHOTO){
			if(resultCode != -1){ // 어떤 이미지도 선택하지 않은 경우
				Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
			}
			else {
				File file = new File(currentPhotoPath);
				Bitmap bitmap = null;
				try {
					bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
				} catch (IOException e) {
					e.printStackTrace();
					Log.e("tag", "IOException : ", e);
				}
				if (bitmap != null) {
					Log.i("tag", "촬영 완료");
					
					Uri imageUri = BitmapToUri(WriteUsedTradePost_Activity.this, bitmap);
					
					Log.i("tag", String.valueOf(imageUri));
					
					//절대경로 획득**
					Cursor c = getContentResolver().query(Uri.parse(imageUri.toString()), null, null, null, null);
					c.moveToNext();
					uploadFilePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
					
					adapter.addInfo(new WriteUsedTradePost_Data(uploadFilePath));
					setRecyclerView();
					
					Log.i("tag", "uploadFilePath : " + uploadFilePath);
					
					new Thread(new Runnable() {
						public void run() {
							/*확인 버튼을 누르기 전 까지 사진 정보를 임시저장 함*/
							adapter.temp_save.add(uploadFilePath);
							Log.i("tagyt14","adapter.temp_save : "+adapter.temp_save);
						}
					}).start();
					
					
				}
			}
			
		}
		
		count_image = String.valueOf(adapter.getItemCount());
		tv_picture_limit.setText(count_image); // 어레이리스트에 담긴 사진 uri 데이터 갯수 표시
	}
	
	/*사진파일 업로드 하는 부분*/
	public int uploadFile(String sourceFileUri) {
		/************* Php script path ****************/
		upLoadServerUri = "http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/insert_WriteUsedTradePost.php";
		
		String fileName = sourceFileUri;
		
		Log.i("tagyt6","sourceFileUri : "+sourceFileUri);
		Log.i("tagyt7","fileName : "+fileName);
		
		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		File sourceFile = new File(sourceFileUri);
		
		Log.i("tagyt8","sourceFile : "+sourceFile);
		
		if (!sourceFile.isFile()) {
			
			runOnUiThread(new Runnable() {
				public void run() {
					//messageText.setText("Source File not exist :" +uploadFilePath + "" + uploadFileName);
					Log.e("tag", "Source File not exist :" +uploadFilePath);
				}
			});
			return 0;
		}
		else
		{
			try {
				// open a URL connection to the Servlet
				FileInputStream fileInputStream = new FileInputStream(sourceFile);
				URL url = new URL(upLoadServerUri);
				
				/*파일 확장자명을 가져오기 위함*/
				File file = new File(fileName);
				String fileNamee = file.getName();
				String extension = fileNamee.substring(fileNamee.lastIndexOf(".") + 1);
				
				Log.i("tagyt9", "fileNamee : " + fileNamee);
				Log.i("tagyt10", "fileName : " + fileName);
				Log.i("tagyt11", "extension : " + extension);
				
				// Open a HTTP  connection to  the URL
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true); // Allow Inputs
				conn.setDoOutput(true); // Allow Outputs
				conn.setUseCaches(false); // Don't use a Cached Copy
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("ENCTYPE", "multipart/form-data");
				conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
				conn.setRequestProperty("phone_number", phone_number); // 서버에 저장되는 이미지명에 사용 될 사용자 휴대폰 번호
				conn.setRequestProperty("now_time", now_time); // 서버에 저장되는 이미지명에 사용 될 현재 시간
				conn.setRequestProperty("extension", extension); // 파일 확장자명
				conn.setRequestProperty("request", "upload_image"); // header 세팅 (key, velue)
				
				save_image_uri.add(phone_number+"_"+now_time+"."+extension);
				
				dos = new DataOutputStream(conn.getOutputStream());
				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
					+ fileName + "\"" + lineEnd);
				
				Log.i("tag","fileName" + fileName);
				
				dos.writeBytes(lineEnd);
				// create a buffer of  maximum size
				bytesAvailable = fileInputStream.available();
				
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];
				
				// read file and write it into form...
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				
				while (bytesRead > 0) {
					
					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);
					
				}
				
				// send multipart form data necesssary after file data...
				dos.writeBytes(lineEnd);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
				
				// Responses from the server (code and message)
				serverResponseCode = conn.getResponseCode();
				String serverResponseMessage = conn.getResponseMessage();
				
				Log.i("tag", "HTTP Response is : "
					+ serverResponseMessage + ": " + serverResponseCode);
				
				if(serverResponseCode == 200){
					
					// 서버에서 받은 결과 값 읽어오는 부분
					StringBuilder responseBody = new StringBuilder(); // 서버에서 받은 결과값을 저장 할 문자열
					BufferedReader br = new BufferedReader(new InputStreamReader(
						conn.getInputStream(), "utf-8"
					));
					String line;
					while ((line = br.readLine()) != null) {
						responseBody.append(line);
					}
					Log.i("tag", String.valueOf(responseBody));
					Log.i("tag", "responseBody"+responseBody);
					
					runOnUiThread(new Runnable() {
						public void run() {
							
							String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
								+" http://www.androidexample.com/media/uploads/";
							
							//messageText.setText(msg);
							Toast.makeText(WriteUsedTradePost_Activity.this, "File Upload Complete.", Toast.LENGTH_SHORT).show();
						}
					});
				}
				
				//close the streams //
				fileInputStream.close();
				dos.flush();
				dos.close();
				
			} catch (MalformedURLException ex) {
				
				ex.printStackTrace();
				
				runOnUiThread(new Runnable() {
					public void run() {
						//messageText.setText("MalformedURLException Exception : check script url.");
						Toast.makeText(WriteUsedTradePost_Activity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
					}
				});
				
				Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
			} catch (Exception e) {
				
				e.printStackTrace();
				
				runOnUiThread(new Runnable() {
					public void run() {
						//messageText.setText("Got Exception : see logcat ");
						Toast.makeText(WriteUsedTradePost_Activity.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
					}
				});
				Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e);
			}
			return serverResponseCode;
			
		} // End else block
	}
	
	/*서버에 글 내용 저장하는 부분*/
	class save implements Runnable {
		@Override
		public void run() {
			try {
				String page = "http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/insert_WriteUsedTradePost.php"; // 연결 할 URL 주소
	
				URL url = new URL(page); // URL 객체 생성
	
				HttpURLConnection conn = (HttpURLConnection)url.openConnection(); // 연결 객체 생성
	
				// 서버에 전달 할 파라미터
				String params = "phone_number=" + phone_number;
				params += "&image_uri=" + image_uri;
				params += "&title=" + title;
				params += "&Price=" + Price;
				params += "&contents=" + contents;
				params += "&PriceOffer_check=" + PriceOffer_check;
				params += "&town_name=" + town_name;
				params += "&uploaded_time=" + now;
	
				Log.i("tag", phone_number);
				Log.i("tag", image_uri);
				Log.i("tag", title);
				Log.i("tag", Price);
				Log.i("tag", contents);
				Log.i("tag", PriceOffer_check);
				Log.i("tag", town_name);
				Log.i("tag", now);
				Log.i("tag", params);
	
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
						/*서버에 업로드 완료 후 새로운 정보를 담기위해 어레이리스트 초기화*/
						save_image_uri.clear();
						adapter.uri_info.clear();
						adapter.temp_save.clear();
						
						Intent intent = new Intent(WriteUsedTradePost_Activity.this, Home_Activity.class) ;
//						intent.putExtra("phone_number", phone_number);
						startActivity(intent) ;
						finish();
						
						
					}
				});
				
			} catch (Exception e) {
				Log.e("tag", "error :" + e);
			}
		}
	}
	
	/*서버에 글 내용 저장하는 부분*/
	class edit implements Runnable {
		@Override
		public void run() {
			try {
				String page = "http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/edit_WriteUsedTradePost.php"; // 연결 할 URL 주소
				
				URL url = new URL(page); // URL 객체 생성
				
				HttpURLConnection conn = (HttpURLConnection)url.openConnection(); // 연결 객체 생성
				
				// 서버에 전달 할 파라미터
				String params = "post_id=" + post_id;
				params += "&image_uri=" + image_uri;
				params += "&title=" + title;
				params += "&Price=" + Price;
				params += "&contents=" + contents;
				params += "&PriceOffer_check=" + PriceOffer_check;
				
				Log.i("tag", post_id);
				Log.i("tag", image_uri);
				Log.i("tag", title);
				Log.i("tag", Price);
				Log.i("tag", contents);
				Log.i("tag", PriceOffer_check);
				Log.i("tag", params);
				
				StringBuilder responseBody = new StringBuilder(); // 서버에서 받은 결과값을 저장 할 문자열
				
				// 연결되면 실행
				if(conn != null) {
					Log.i("tag", "conn 연결");
					
					conn.setRequestProperty("Accept", "application/json"); // 서버에서 받을 데이터 타입 설정 (json | xml)
					conn.setConnectTimeout(10000); // 서버 접속시 연결 타임아웃 설정 (ms 단위)
					conn.setReadTimeout(10000); // 서버 접속시 데이터 가져오기 타임아웃 설정 (ms 단위)
					conn.setRequestMethod("POST"); // 요청 방식 선택 (GET | POST)
					conn.setRequestProperty("request", "edit"); // header 세팅 (key, velue)
					
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
						/*서버에 업로드 완료 후 새로운 정보를 담기위해 어레이리스트 초기화*/
						save_image_uri.clear();
						adapter.uri_info.clear();
						adapter.temp_save.clear();
						
						Intent intent = new Intent(WriteUsedTradePost_Activity.this, Home_Activity.class) ;
//						intent.putExtra("phone_number", phone_number);
						startActivity(intent) ;
						finish();
						
						Toast.makeText(getApplicationContext(), "게시글이 수정되었습니다.", Toast.LENGTH_SHORT).show();
						
						
					}
				});
				
			} catch (Exception e) {
				Log.e("tag", "error :" + e);
			}
		}
	}
	
}