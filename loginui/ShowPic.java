package com.shashank.platform.loginui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.jaeger.library.StatusBarUtil;

public class ShowPic extends AppCompatActivity {
    private ImageView ivShowPic;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actiivity_show_pic);
        //状态栏透明
        StatusBarUtil.setTransparent(this);
        ivShowPic=findViewById(R.id.iv_show_pic);
        Intent getImage=getIntent();
        Uri data=getImage.getData();
        ivShowPic.setImageURI(data);
        ivShowPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
