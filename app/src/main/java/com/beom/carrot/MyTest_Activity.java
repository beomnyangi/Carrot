package com.beom.carrot;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MyTest_Activity extends Activity {
	
	String now_time;
	
	TextView messageText;
	Button uploadButton;
	int serverResponseCode = 0;
	ProgressDialog dialog = null;
	
	String upLoadServerUri = null;
	
	/**********  File Path *************/
	
	//String uploadFilePath = "/storage/emulated/0/DCIM/Camera/";//경로를 모르겠으면, 갤러리 어플리케이션 가서 메뉴->상세 정보
	//String uploadFileName = "20211022_083517.jpg"; //전송하고자하는 파일 이름
	
	String uploadFilePath = "";//경로를 모르겠으면, 갤러리 어플리케이션 가서 메뉴->상세 정보
	String uploadFileName = ""; //전송하고자하는 파일 이름
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mytest);
		
		uploadButton = (Button)findViewById(R.id.uploadButton);
		messageText  = (TextView)findViewById(R.id.messageText);
		
		messageText.setText("Uploading file path :- '/mnt/sdcard/"+uploadFileName+"'");
		
		/************* Php script path ****************/
		upLoadServerUri = "http://ec2-13-125-69-201.ap-northeast-2.compute.amazonaws.com/insert_WriteUsedTradePost.php";
		
		uploadButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				dialog = ProgressDialog.show(MyTest_Activity.this, "", "Uploading file...", true);
				
				/*이미지 파일 여러개 불러오기*/
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setType("image/*");
				intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
				intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
				startActivityForResult(Intent.createChooser(intent,""), 1); // 다중선택을 하려면 포토로 들어가야 됨
				
			}
		});
	}
	
	/*이미지 가져온 후 실행되는 부분*/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == 1){
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
					uploadFileName = "";
					
					/*현재 utc시간 가져와서 파일명 작명에 사용*/
					long now = System.currentTimeMillis();
					now_time = String.valueOf(now);
					
					new Thread(new Runnable() {
						public void run() {
							runOnUiThread(new Runnable() {
								public void run() {
									messageText.setText("uploading started.....");
								}
							});
							
							uploadFile(uploadFilePath + "" + uploadFileName);
							
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
						
						for (int i = 0; i < clipData.getItemCount(); i++){
							Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
							Log.i("tag", String.valueOf(imageUri));
							try {
								/*절대경로 획득*/
								Cursor c = getContentResolver().query(Uri.parse(imageUri.toString()), null, null, null, null);
								c.moveToNext();
								uploadFilePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
								uploadFileName = "";
								
								/*현재 utc시간 가져와서 파일명 작명에 사용*/
								long now = System.currentTimeMillis();
								now_time = String.valueOf(now);
								
								new Thread(new Runnable() {
									public void run() {
										runOnUiThread(new Runnable() {
											public void run() {
												messageText.setText("uploading started.....");
											}
										});
										
										uploadFile(uploadFilePath + "" + uploadFileName);
										
									}
								}).start();
								
							} catch (Exception e) {
								Log.i("tag", "File select error", e);
							}
						}
						
//						new Thread(new Runnable() {
//							public void run() {
//								runOnUiThread(new Runnable() {
//									public void run() {
//										messageText.setText("uploading started.....");
//									}
//								});
//
//								uploadFile(uploadFilePath + "" + uploadFileName);
//
//							}
//						}).start();
					}
				}
			}
		}
	}
	
	// uri 를 통해 파일 경로를 반환함
	public String getPathFromUri(Uri uri){
		
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		
		cursor.moveToNext();
		
		String path = cursor.getString( cursor.getColumnIndex( "_data" ) );
		
		cursor.close();
		
		return path;
	}
	
	public int uploadFile(String sourceFileUri) {
		
		
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
			
			dialog.dismiss();
			
			Log.e("tag", "Source File not exist :"
				+uploadFilePath + "" + uploadFileName);
			
			runOnUiThread(new Runnable() {
				public void run() {
					messageText.setText("Source File not exist :"
						+uploadFilePath + "" + uploadFileName);
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
				
				// Open a HTTP  connection to  the URL
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true); // Allow Inputs
				conn.setDoOutput(true); // Allow Outputs
				conn.setUseCaches(false); // Don't use a Cached Copy
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("ENCTYPE", "multipart/form-data");
				conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
				conn.setRequestProperty("phone_number", "01012345678"); // 서버에 저장되는 이미지명에 사용 될 사용자 휴대폰 번호
				conn.setRequestProperty("now_time", now_time);
				
				File file = new File(fileName);
				String fileNamee = file.getName();
				String ext = fileNamee.substring(fileNamee.lastIndexOf(".") + 1);
				Log.e("tag", "file name : " + fileName);
				Log.e("tag", "extension : " + ext);
				
				dos = new DataOutputStream(conn.getOutputStream());
				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
					+ fileName + "\"" + lineEnd);
				
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
				
				Log.i("uploadFile", "HTTP Response is : "
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
								+" http://www.androidexample.com/media/uploads/"
								+uploadFileName;
							
							messageText.setText(msg);
							Toast.makeText(MyTest_Activity.this, "File Upload Complete.",
								Toast.LENGTH_SHORT).show();
						}
					});
				}
				
				//close the streams //
				fileInputStream.close();
				dos.flush();
				dos.close();
				
			} catch (MalformedURLException ex) {
				
				dialog.dismiss();
				ex.printStackTrace();
				
				runOnUiThread(new Runnable() {
					public void run() {
						messageText.setText("MalformedURLException Exception : check script url.");
						Toast.makeText(MyTest_Activity.this, "MalformedURLException",
							Toast.LENGTH_SHORT).show();
					}
				});
				
				Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
			} catch (Exception e) {
				
				dialog.dismiss();
				e.printStackTrace();
				
				runOnUiThread(new Runnable() {
					public void run() {
						messageText.setText("Got Exception : see logcat ");
						Toast.makeText(MyTest_Activity.this, "Got Exception : see logcat ",
							Toast.LENGTH_SHORT).show();
					}
				});
				Log.e("Upload file to server Exception", "Exception : "
					+ e.getMessage(), e);
			}
			dialog.dismiss();
			return serverResponseCode;
			
		} // End else block
	}
}