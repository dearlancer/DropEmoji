package com.example.wangzhicheng.dropemoji;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private DropImg dropImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void showDropImg(View view){
        dropImg=new DropImg(MainActivity.this,R.drawable.lu000)
        .setSpeed(DropImg.ViewPosInfo.SPEED_MEDIUM)
        .setImgNum(30);
        dropImg.show();
    }
}
