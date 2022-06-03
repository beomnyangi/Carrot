package com.beom.carrot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ViewUsedTradePost_Adapter extends RecyclerView.Adapter<ViewUsedTradePost_Adapter.ViewHolder>{
	
	private List<String> Items;
	
	public ViewUsedTradePost_Adapter(List<String> Items) {
		this.Items = Items;
	}
	
	@NonNull
	@Override
	public ViewUsedTradePost_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_view_used_trade_post_item, parent, false);
		return new ViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(@NonNull ViewUsedTradePost_Adapter.ViewHolder holder, int position) {
		//holder.iv_post_image.setText(Items.get(position));
		
		/*그래들 라이브러리를 이용해 홀더에 이미지 세팅*/
		Glide.with(holder.itemView.getContext())
			.load(Items.get(position))
			.fitCenter()
			//.override(500,500) // 이미지 크기 조절
			.into(holder.iv_post_image);
	}
	
	@Override
	public int getItemCount() {
		return Items.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder {
		ImageView iv_post_image;
		
		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			iv_post_image = itemView.findViewById(R.id.iv_post_image);
		}
	}
	
}