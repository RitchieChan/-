package com.shashank.platform.loginui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

//import cn.leancloud.AVLogger;
//import cn.leancloud.AVOSCloud;


public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView;
    String pri_imagepath;


    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        //添加activity到集合，方便统一注销
        ActivityCollectorUtil.addActivity(MainActivity.this);
//        AVOSCloud.setLogLevel(AVLogger.Level.DEBUG);
//        AVOSCloud.initialize(this, "xyDDPKKM7rEgx6ddPOrBIvoL-gzGzoHsz", "g6gb1fBVTcXQCViHzBGF5zHT", "https://housekeeper.com");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.username);
        //根据系统自动获取时间判断选择背景图片
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.HOUR_OF_DAY)<6||calendar.get(Calendar.HOUR_OF_DAY)>=18){
            imageView.setImageResource(R.drawable.bg3);
            textView.setText("Night");
        }else if(calendar.get(Calendar.HOUR_OF_DAY)<12){
            imageView.setImageResource(R.drawable.wood0);
            textView.setText("Morning");
        }else if(calendar.get(Calendar.HOUR_OF_DAY)<18){
            imageView.setImageResource(R.drawable.bg0);
            textView.setText("Afternooon");
        }
        //将原始头像保存进系统文件
        Drawable drawable=getDrawable(R.mipmap.my_home);
        BitmapDrawable bitmapDrawable= (BitmapDrawable)drawable;
        Bitmap BITMAP=bitmapDrawable.getBitmap();
        File outputImage = new File(getExternalCacheDir(), "默认头像"+".jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream fileOutputStream=new FileOutputStream(outputImage);
            BITMAP.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pri_imagepath=outputImage.getPath();

        readInfo();


    }

    //点击Sign in按钮，页面跳转函数实现,跳转到Main2Activity
    public void main2_activity(View view)
    {
        String account;
        String password;
        String username;
        String motto;
        String get_imagepath="";
        EditText ed_name = (EditText) findViewById(R.id.ed_name);
        String acc = ed_name.getText().toString();
        EditText ed_passwd = (EditText) findViewById(R.id.ed_passwd);
        String passwd = ed_passwd.getText().toString();



        //自动登录功能
        if((acc.equals("")) || (passwd.equals("")))
        {
            Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();

        }
        else
        {
            //如果选中自动登录，我们就需要保存用户名和密码
            CheckBox cb=(CheckBox) findViewById(R.id.checkBox);
            if(cb.isChecked())
            {
                //创建一个文件，用户保存用户名和密码
                File file = new File(getFilesDir(), "info.txt");
                try {

                    FileOutputStream fos = new FileOutputStream(file);
                    //写入用户名和密码，以name##passwd的格式写入
                    fos.write((acc + "##" + passwd).getBytes());
                    //关闭输出流
                    fos.close();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            try {
                SQLiteOpenHelper dbHelper = new DBHelper(this);
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.query("SIGN_UP", new String[]{"ACCOUNT","PASSWORD","USERNAME","MOTTO","HEADIMAGE"}, "ACCOUNT=?",
                        new String[]{acc}, null, null, null, null);

                //Move to the first record in the Cursor
                if (cursor.moveToFirst()) {
                    account = cursor.getString(0);
                    password=cursor.getString(1);
                    username=cursor.getString(2);
                    motto=cursor.getString(3);
                    get_imagepath=cursor.getString(4);
                    if (get_imagepath == null){
                        get_imagepath=pri_imagepath;
                    }

                    if(account.equals(acc)&&password.equals(passwd)){
                        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                        //账户和密码都正确时，才可跳转
                        Intent intent=new Intent(MainActivity.this,Main2Activity.class);
                        Bundle bundle = new Bundle();
                        bundle.putCharSequence("账号", acc);
                        bundle.putCharSequence("昵称", username);
                        bundle.putCharSequence("格言",motto);
                        bundle.putCharSequence("数据库头像路径",get_imagepath);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast toast = Toast.makeText(this, "账号或密码错误，请重新确认！", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }else{
                    Toast toast = Toast.makeText(this, "该账号尚未注册，请重新确认！", Toast.LENGTH_SHORT);
                    toast.show();
                }
                cursor.close();
            }catch (SQLiteException e) {
                Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
                toast.show();
            }

        }

    }

    //读取登录数据
    public void  readInfo()
    {
        EditText ed_name = (EditText) findViewById(R.id.ed_name);
        EditText ed_passwd = (EditText) findViewById(R.id.ed_passwd);
        File file = new File(getFilesDir(), "info.txt");

        //如果文件存在，则读取，已登录过，则读取其账号和密码
        if(file.exists())
        {
            try {

                FileInputStream fin = new FileInputStream(file);
                //把字节流转化为字符流
                BufferedReader buffer = new BufferedReader(new InputStreamReader(fin));
                //读取文件中的用户名和密码
                String text = buffer.readLine();
                //以##为关键字分割字符
                String s[] = text.split("##");

                //设置进EditText
                ed_name.setText(s[0]);
                ed_passwd.setText(s[1]);


            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public void sign_up(View view){
        Intent intent=new Intent(MainActivity.this,sign_up_Activity.class);
        startActivity(intent);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollectorUtil.removeActivity(MainActivity.this);
    }
}
