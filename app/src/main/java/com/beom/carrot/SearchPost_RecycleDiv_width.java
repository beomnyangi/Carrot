package com.beom.carrot;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class SearchPost_RecycleDiv_width extends RecyclerView.ItemDecoration {

    private final int divWidth;

    public SearchPost_RecycleDiv_width(int divWidth) {
        this.divWidth = divWidth;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.right = divWidth;//아이템 우측 여백 설정
        outRect.left = divWidth;//아이템 좌측 여백 설정
    }
}