package com.beom.carrot;

import android.Manifest;
import android.app.AlertDialog;
import android.content.*;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
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
import com.bumptech.glide.Glide;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditProfile_Activity extends AppCompatActivity {
	String phone_number;
	String nick_name;
	String profile_image;
	String new_nick_name;
	String new_profile_image;
	String currentPhotoPath;
	String uploadFilePath;
	String now_time;
	
	EditText et_nickname;
	
	ImageView iv_profile_image;
	
	ImageButton ib_back;
	ImageButton ib_select_image;
	
	Button bt_ok;
	
	int serverResponseCode = 0;
	int REQUEST_SELECT_PHOTO = 1;
	int REQUEST_TAKE_PHOTO = 2;
	
	int take_photo_check = 0; //사진 촬영 했는지 여부 확인(0일 때 사진촬영 안 됨, 1일 때 사진촬영 완료)
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);
		
		et_nickname = findViewById(R.id.et_nickname);
		iv_profile_image = findViewById(R.id.iv_profile_image);
		ib_back = findViewById(R.id.ib_back);
		bt_ok = findViewById(R.id.bt_ok);
		ib_select_image = findViewById(R.id.ib_select_image);
		
		/*저장된 값을 불러오기 위해 같은 네임파일을 찾음.*/
		SharedPreferences sp = getSharedPreferences("login_data",MODE_PRIVATE);
		phone_number = sp.getString("phone_number","no");//phone_number라는 이름을 가진 키에 저장 된 값을 text에 저장. 값이 없을 시"no"이라는 값을 반환
		
		Intent intent = getIntent();
		nick_name = intent.getStringExtra("nick_name");
		profile_image = intent.getStringExtra("profile_image");
		
		et_nickname.setText(nick_name);
		
		Glide.with(EditProfile_Activity.this)
			.load("http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/UsedTradePost_Image/"+profile_image)
			.fitCenter()
			.into(iv_profile_image);
		
		/*뒤로가기 이미지버튼 눌렀을 때*/
		ib_back.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				//버튼이 클릭 됐을 때
				Intent intent = new Intent(EditProfile_Activity.this, MyCarrot_Activity.class) ;
				startActivity(intent) ;
				finish();
			}
		});
		
		/*확인버튼 눌렀을 때*/
		bt_ok.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				//버튼이 클릭 됐을 때
				
				new_nick_name = et_nickname.getText().toString();
				
				Log.i("tag",new_nick_name);
				Log.i("tag",nick_name);
				
				Log.i("tag",profile_image);
				
				if(new_nick_name.equals(nick_name) && new_profile_image == null){
					Toast.makeText(getApplicationContext(), "기존 프로필 정보와 동일 합니다", Toast.LENGTH_SHORT).show();
					Log.i("tag","수정불가");
				}
				else{
					Log.i("tag","수정가능");
					
					if(new_profile_image != null){
						
						if(new_profile_image.equals("")){
							
							profile_image = "empty_profile_image.png";
							nick_name = new_nick_name;
							
							Thread edit = new Thread(new edit());
							edit.start();
							
						}
						else {
							
							profile_image = new_profile_image;
							nick_name = new_nick_name;
							
							long now = System.currentTimeMillis();
							now_time = String.valueOf(now);
							/*이미지를 서버에 업로드*/
							new Thread(new Runnable() {
								public void run() {
									/*원하는 내용 작성*/
									uploadFile(profile_image);
								}
							}).start();
							
						}
					}
					else{
						Log.i("tag",profile_image);
						nick_name = new_nick_name;
						/*프로필 정보를 서버에 업로드*/
						Thread edit = new Thread(new edit());
						edit.start();
					}
				}
			}
		});
		
		/*사진기모양 버튼 눌렀을 때*/
		ib_select_image.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				//버튼이 클릭 됐을 때
				
				//앨범과 카메라 사용을 위한 매니페스트 권한 요청
				ActivityCompat.requestPermissions(EditProfile_Activity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1); //WRITE_EXTERNAL_STORAGE 자리에 원하는 퍼미션 이름으로 변경
				ActivityCompat.requestPermissions(EditProfile_Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
				ActivityCompat.requestPermissions(EditProfile_Activity.this, new String[]{Manifest.permission.CAMERA}, 1);
				
				ShowDialog();
			}
		});
		
	}
	
//	/*디바이스의 뒤로가기 버튼 눌렀을 때 동작*/
//	@Override
//	public void onBackPressed() {
//		super.onBackPressed();
//		Intent intent = new Intent(EditProfile_Activity.this, Home_Activity.class) ;
//		intent.putExtra("phone_number", phone_number);
//		startActivity(intent) ;
//		finish();
//	}
	
	/*다이얼로그 보여주는 함수*/
	public void ShowDialog() {
		
		AlertDialog.Builder msgBuilder = new AlertDialog.Builder(this)
			.setTitle("프로필 이미지 변경")
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
					SelectImageIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
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
			.setNeutralButton("프로필 이미지 삭제", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int pos) {
					Log.i("tag", String.valueOf(dialog));
					Log.i("tag", String.valueOf(pos));
					Log.i("tag", "삭제");
					
					new_profile_image = "";
					
					Glide.with(EditProfile_Activity.this)
						.load("http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/UsedTradePost_Image/empty_profile_image.png")
						.fitCenter()
						.into(iv_profile_image);
				} });
		
		msgBuilder.create().show();
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
	
	/*이미지 회전시키는 함수*/
	public static Bitmap rotateImage(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
			matrix, true);
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
		Log.i("tag", "이미지파일저장");
		// Save a file: path for use with ACTION_VIEW intents
		currentPhotoPath = image.getAbsolutePath();
		return image;
	}
	
	/*카메라 인텐트를 실행하는 부분*/
	public void dispatchTakePictureIntent() {
		Log.i("tag", "카메라 인텐트 실행");
		
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
				Log.i("tag", "카메라인텐트 실행2");
				Log.i("tag", String.valueOf(photoURI));
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
			if(data == null){ // 어떤 이미지도 선택하지 않은 경우
				Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
			}
			else{ // 이미지를 하나라도 선택한 경우
				if(data.getClipData() == null){     // 이미지를 하나만 선택한 경우
					Log.i("tag", String.valueOf(data.getData()));
					Uri imageUri = data.getData();
					
					Log.i("tag", String.valueOf(imageUri));
					
					//절대경로 획득**
					Cursor c = getContentResolver().query(Uri.parse(imageUri.toString()), null, null, null, null);
					c.moveToNext();
					uploadFilePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
					
					iv_profile_image.setImageURI(Uri.parse(uploadFilePath));
					
					Log.i("tag", "uploadFilePath : " + uploadFilePath);
					
					new Thread(new Runnable() {
						public void run() {
							/*확인 버튼을 누르기 전 까지 사진 정보를 임시저장 함*/
							new_profile_image = uploadFilePath;
						}
					}).start();
				}
				
				/*아래 else 조건문이 없어서 오류가 났던거임
				* data.getClipData() 가 null이 아닐 때에 대한 조건문*/
				else{      // 이미지를 여러장 선택한 경우
					ClipData clipData = data.getClipData();
					Log.i("tag", String.valueOf(clipData.getItemCount()));

					if(clipData.getItemCount() > 10){   // 선택한 이미지가 11장 이상인 경우
						Toast.makeText(getApplicationContext(), "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
					}
					else{   // 선택한 이미지가 1장 이상 10장 이하인 경우
						Log.i("tag", "multiple choice");

						/*이미지가 10개 이하로 선택 됐을 때만 실행*/
						if(clipData.getItemCount()  < 11) {
							for (int i = 0; i < clipData.getItemCount(); i++){
								Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
								Log.i("tag", String.valueOf(imageUri));
								try {

									// 절대경로 획득
									Cursor c = getContentResolver().query(Uri.parse(imageUri.toString()), null, null, null, null);
									c.moveToNext();
									uploadFilePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));

									Log.i("tag", "uploadFilePath : " + uploadFilePath);

									iv_profile_image.setImageURI(Uri.parse(uploadFilePath));

									/*원하는 곳에서 바로 사용하는 쓰레드*/
									new Thread(new Runnable() {
										public void run() {
											/*확인 버튼을 누르기 전 까지 사진 정보를 임시저장 함*/
											new_profile_image = uploadFilePath;
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
			else{
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
					
					Uri imageUri = BitmapToUri(EditProfile_Activity.this, bitmap);
					
					Log.i("tag", String.valueOf(imageUri));
					
					//절대경로 획득**
					Cursor c = getContentResolver().query(Uri.parse(imageUri.toString()), null, null, null, null);
					c.moveToNext();
					uploadFilePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
					
//					Glide.with(this)
//						.load(imageUri)
//						.fitCenter()
//						.into(iv_profile_image);
					
					iv_profile_image.setImageURI(Uri.parse(uploadFilePath));
					
					Log.i("tag", "uploadFilePath : " + uploadFilePath);
					
					new Thread(new Runnable() {
						public void run() {
							
							new_profile_image = uploadFilePath;
						}
					}).start();
				}
			}
			
		}
	}
	
	/*서버에 변경 된 프로필 정보를 저장하는 부분*/
	class edit implements Runnable {
		@Override
		public void run() {
			try {
				String page = "http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/edit_AccountInfo.php"; // 연결 할 URL 주소

				URL url = new URL(page); // URL 객체 생성

				HttpURLConnection conn = (HttpURLConnection)url.openConnection(); // 연결 객체 생성

				// 서버에 전달 할 파라미터
				String params = "phone_number=" + phone_number;
				params += "&nick_name=" + nick_name;
				params += "&profile_image=" + profile_image;

				Log.i("tag", phone_number);
				Log.i("tag", nick_name);
				Log.i("tag", profile_image);
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
						
						/*SubThread에서 UI를 다루기 위한 쓰레드*/
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// UI를 다루기 위한 코드 작성
								Toast.makeText(getApplicationContext(), "프로필 수정 완료", Toast.LENGTH_SHORT).show();
								Intent intent = new Intent(EditProfile_Activity.this, MyCarrot_Activity.class) ;
								startActivity(intent) ;
								finish();
							}
						});
					}
					else {
						Toast.makeText(getApplicationContext(), "네트워크 문제 발생", Toast.LENGTH_SHORT).show();
					}
					conn.disconnect(); // 연결 끊기
				}
			} catch (Exception e) {
				Log.e("tag", "error :" + e);
			}
		}
	}
	
	/*사진파일 업로드 하는 부분*/
	public int uploadFile(String sourceFileUri) {
		/************* Php script path ****************/
		String upLoadServerUri = "http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/edit_AccountInfo.php";
		
		String fileName = sourceFileUri;
		
		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		File sourceFile = new File(sourceFileUri);
		
		if (!sourceFile.isFile()) {
			
			runOnUiThread(new Runnable() {
				public void run() {
					//messageText.setText("Source File not exist :" +uploadFilePath);
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
				Log.i("tag", "file name : " + fileName);
				Log.i("tag", "extension : " + extension);
				
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
				
				profile_image = phone_number+"_"+now_time+"."+extension;
				
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
					
					runOnUiThread(new Runnable() {
						public void run() {
							
							String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
								+" http://www.androidexample.com/media/uploads/";
							
							//messageText.setText(msg);
							
							/*프로필 정보를 서버에 업로드*/
							Thread edit = new Thread(new edit());
							edit.start();
							
							
							//Toast.makeText(ProfileSettings_Activity.this, "File Upload Complete.", Toast.LENGTH_SHORT).show();
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
						Toast.makeText(EditProfile_Activity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
					}
				});
				
				Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
			} catch (Exception e) {
				
				e.printStackTrace();
				
				runOnUiThread(new Runnable() {
					public void run() {
						//messageText.setText("Got Exception : see logcat ");
						Toast.makeText(EditProfile_Activity.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
					}
				});
				Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e);
			}
			return serverResponseCode;
			
		} // End else block
	}
	
}