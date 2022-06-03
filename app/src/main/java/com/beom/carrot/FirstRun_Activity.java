package com.beom.carrot;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class FirstRun_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstrun);
        
        Log.i("tag","onCreate");
        
        Button bt_click = findViewById(R.id.bt_start);
        bt_click.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstRun_Activity.this, SearchLocation_Activity.class) ;
                startActivity(intent) ;
                finish();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.i("life_cycle_test","onStart");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("life_cycle_test","onResume");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.i("life_cycle_test","onPause");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.i("life_cycle_test","onStop");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("life_cycle_test","onDestroy");
    }
}