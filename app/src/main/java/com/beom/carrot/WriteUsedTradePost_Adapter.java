package com.beom.carrot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class WriteUsedTradePost_Adapter extends RecyclerView.Adapter<WriteUsedTradePost_Adapter.ViewHolder> {
	
	Context context;
	LayoutInflater inflacter;
	
	int layout;
	
	public static ArrayList<WriteUsedTradePost_Data> uri_info = new ArrayList<>(); // adapter에 들어갈 list
	
	public static ArrayList temp_save = new ArrayList(); // 서버에 보낼 이미지 정보
	
	//MainActivity 에서 context, info 받아오기
	public WriteUsedTradePost_Adapter(Context context, int layout){
		this.context = context;
		inflacter = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.layout = layout;
	}
	
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		//recycler_view_item.xml을 inflate 시킨다.
		View view = LayoutInflater.from(context).inflate(R.layout.activity_write_used_trade_post_image_item, parent, false);
		
		return new ViewHolder(view);
	}
	
	/*onbindviewholder 란 ListView / RecyclerView 는 inflate를 최소화 하기 위해서 뷰를 재활용 하는데,
	이 때 각 뷰의 내용을 업데이트 하기 위해 findViewById 를 매번 호출 해야합니다.
	이로 인해 성능저하가 일어남에 따라 ItemView의 각 요소를 바로 엑세스 할 수 있도록 저장해두고 사용하기 위한 객체입니다.*/
	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
		//list 에 아이템 하나하나 보여주는 메소드 입니다.
		
		WriteUsedTradePost_Data item = uri_info.get(position);
		Log.i("tag","테스트 : "+item.getUri());
		
		/*그래들 라이브러리를 이용해 홀더에 이미지 세팅*/
		Glide.with(holder.itemView.getContext())
			.load(item.getUri())
			.fitCenter()
			.into(holder.iv_select_image);
	}
	
	//리스트의 아이템 갯수
	@Override
	public int getItemCount() {
		return uri_info.size();
	}
	
	// 아이템 뷰를 저장하는 뷰홀더 클래스.
	public class ViewHolder extends RecyclerView.ViewHolder {
		ImageView iv_select_image;
		ImageButton ib_delete_image;
		
		ViewHolder(final View view) {
			super(view);
			
			iv_select_image = view.findViewById(R.id.iv_select_image);
			ib_delete_image = view.findViewById(R.id.ib_delete_image);
			
			/*이미지 삭제 버튼 클릭했을 때*/
			ib_delete_image.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					//버튼이 클릭 됐을 때
					int pos = getAdapterPosition() ; // 선택 한 위치 저장
//					Toast.makeText(v.getContext(), "클릭 됨 : "+pos, Toast.LENGTH_SHORT).show();
					Log.i("tag","ib_delete_image position : "+pos);
					
					remove(pos); // 리사이클러뷰에서 사용하는 어레이리스트에서 선택한 이미지 정보 삭제
					temp_save.remove(pos); // 서버에 보낼 이미지 정보를 담은 어레이리스트에서 선택한 이미지 정보 삭제
					notifyItemRemoved(pos);
					
				}
			});
			
			//아이템을 클릭 했을 때
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					int pos = getAdapterPosition() ; // 선택 한 위치 저장
					Log.i("tag","itemView position : "+pos);
					
					// 아이템이 선택 됐을 때 실행
					if (pos != RecyclerView.NO_POSITION) {
						// 선택 된 아이템 정보를 가져오기위해 선언 후 선택 포지션 값 저장
						WriteUsedTradePost_Data data;
						data = uri_info.get(pos);
						
						String uri;
						
						// 선택된 아이템 정보를 가져와 변수에 저장
						uri = data.getUri();
						
						
						
						// 인텐트로 아이템 정보와 함께 화면 전환
//						Intent intent = new Intent(context, SmsVerification_Activity.class);
//						intent.putExtra("address_name", address_name);
//						intent.putExtra("town_name", town_name);
//						intent.putExtra("x", x);
//						intent.putExtra("y", y);
//						context.startActivity(intent);
//						((Activity) context).finish();
					}
					else{
						Log.i("tag","리사이클러뷰 포지션 선택 안 됨");
					}
					
				}
				
			});
		}
	}
	
	public void addInfo(WriteUsedTradePost_Data data){
		uri_info.add(data);
	}
	
	public void remove(int pos) {
		uri_info.remove(pos);
	}
	
	public void modify(int pos, WriteUsedTradePost_Data data){
		uri_info.set(pos, data);
	}
}