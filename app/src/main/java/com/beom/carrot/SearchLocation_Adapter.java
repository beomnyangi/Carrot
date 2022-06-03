package com.beom.carrot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchLocation_Adapter extends RecyclerView.Adapter<SearchLocation_Adapter.ViewHolder> {

	Context context;
	LayoutInflater inflacter;
	
	int layout;
	//adapter에 들어갈 list
	public static ArrayList<SearchLocation_Data> searchaddress_info = new ArrayList<>();

	//MainActivity 에서 context, info 받아오기
	public SearchLocation_Adapter(Context context, int layout){
		this.context = context;
		inflacter = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.layout = layout;
	}
	
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		//recycler_view_item.xml을 inflate 시킨다.
		View view = LayoutInflater.from(context).inflate(R.layout.activity_search_location_item, parent, false);
		
		return new ViewHolder(view);
	}
	
	/*onbindviewholder 란 ListView / RecyclerView 는 inflate를 최소화 하기 위해서 뷰를 재활용 하는데,
	이 때 각 뷰의 내용을 업데이트 하기 위해 findViewById 를 매번 호출 해야합니다.
	이로 인해 성능저하가 일어남에 따라 ItemView의 각 요소를 바로 엑세스 할 수 있도록 저장해두고 사용하기 위한 객체입니다.*/
	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
		//list 에 아이템 하나하나 보여주는 메소드 입니다.
		
		SearchLocation_Data item = searchaddress_info.get(position);
		System.out.println("테스트"+item.getAddress_name());
		holder.address_name.setText(item.getAddress_name());
	}
	
	//리스트의 아이템 갯수
	@Override
	public int getItemCount() {
		return searchaddress_info.size();
	}
	
	// 아이템 뷰를 저장하는 뷰홀더 클래스.
	public class ViewHolder extends RecyclerView.ViewHolder {
		TextView address_name;
		
		ViewHolder(final View view) {
			super(view);
			
			address_name = view.findViewById(R.id.tv_address_name);
			
			//아이템을 클릭 했을 때
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					int pos = getAdapterPosition() ; // 선택 한 위치 저장
					System.out.println("pos"+pos);
					
					// 아이템이 선택 됐을 때 실행
					if (pos != RecyclerView.NO_POSITION) {
						// 선택 된 아이템 정보를 가져오기위해 선언 후 선택 포지션 값 저장
						SearchLocation_Data location_data;
						location_data = searchaddress_info.get(pos);
						
						String address_name;
						String town_name;
						String x;
						String y;
						
						// 선택된 아이템 정보를 가져와 변수에 저장
						address_name = location_data.getAddress_name();
						town_name = location_data.getTown_name();
						x = location_data.getX();
						y = location_data.getY();
						
						// 인텐트로 아이템 정보와 함께 화면 전환
						Intent intent = new Intent(context, SmsVerification_Activity.class);
						intent.putExtra("address_name", address_name);
						intent.putExtra("town_name", town_name);
						intent.putExtra("x", x);
						intent.putExtra("y", y);
						context.startActivity(intent);
						((Activity) context).finish();
					}
					else{
						Log.i("tag","리사이클러뷰 포지션 선택 안 됨");
					}
					
				}
				
			});
		}
	}
	
	public void addInfo(SearchLocation_Data data){
		searchaddress_info.add(data);
	}

	public void remove(int pos) {
		searchaddress_info.remove(pos);
	}

	public void modify(int pos, SearchLocation_Data data){
		searchaddress_info.set(pos, data);
	}
}