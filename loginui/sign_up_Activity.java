package com.shashank.platform.loginui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;

public class sign_up_Activity extends AppCompatActivity {
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_processed);
        //状态栏透明
        StatusBarUtil.setTransparent(this);

        //添加activity到集合，方便统一注销
        ActivityCollectorUtil.addActivity(sign_up_Activity.this);
        try{
            SQLiteOpenHelper dbHelper = new DBHelper(this);
            db = dbHelper.getReadableDatabase();
        }catch (SQLiteException e){
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void sign_up(View view){
        EditText acc=(EditText)findViewById(R.id.account);
        EditText pass=(EditText)findViewById(R.id.password);
        EditText user_name=(EditText)findViewById(R.id.username);
        String username=user_name.getText().toString();
        String acc1=acc.getText().toString();
        String pass1=pass.getText().toString();

        if(acc1.length()==11) {
            try {
                SQLiteOpenHelper dbHelper = new DBHelper(this);
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.query("SIGN_UP", new String[]{"ACCOUNT","PASSWORD","USERNAME"}, "ACCOUNT=?",
                        new String[]{acc1}, null, null, null, null);

                //Move to the first record in the Cursor
                if (cursor.moveToFirst()) {
                    Toast.makeText(this, "该账号已被注册！", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(pass1.length()>=6) {
                        if(username.length()>0) {
                            insertSIGN_UP(db, acc1, pass1, username);
                            Toast.makeText(this, "注册成功！", Toast.LENGTH_SHORT).show();
                            finish();
                        }else
                        {
                            Toast.makeText(this, "昵称不能为空！", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(this, "密码不少于6位", Toast.LENGTH_SHORT).show();
                    }
                }
                cursor.close();
            }catch (SQLiteException e) {
                Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
                toast.show();
            }

        }else{
            Toast.makeText(this, "请输入正确的手机号（11位）", Toast.LENGTH_SHORT).show();
        }
    }
    public void insertSIGN_UP(SQLiteDatabase db,String account,String password,String user_name){
        ContentValues cv = new ContentValues();
        cv.put("ACCOUNT",account);
        cv.put("PASSWORD",password);
        cv.put("USERNAME",user_name);
        String a="JUST BE COOL!";
        cv.put("MOTTO",a);
        long result =db.insert("SIGN_UP",null,cv);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollectorUtil.removeActivity(sign_up_Activity.this);
    }
}
