package com.shashank.platform.loginui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.jaeger.library.StatusBarUtil;
import com.shashank.platform.loginui.ui.personal_data_activity;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

public class Main2Activity extends AppCompatActivity implements OnClickListener {
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private AlertDialog dialog;
    private Uri imageUri;
    private int Show_Choice=0;
    private String account_text="account_text";
    private String user_name="";
    private String motto="";
    private String ImagePath;
    private String get_imagepath;
    private AppBarConfiguration mAppBarConfiguration;
    private SQLiteDatabase db;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollectorUtil.removeActivity(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //状态栏透明
        StatusBarUtil.setTransparent(this);

        //添加activity到集合，方便统一注销
        ActivityCollectorUtil.addActivity(Main2Activity.this);

        //获取用户名
        Bundle bundle1=getIntent().getExtras();
        assert bundle1 != null;
        account_text=bundle1.getString("账号");
        user_name=bundle1.getString("昵称");
        motto=bundle1.getString("格言");
        get_imagepath=bundle1.getString("数据库头像路径");        //从数据库找到的头像路径

//获取objects
//         String objects=bundle1.getString("objects");
//        Toast.makeText(this,objects, Toast.LENGTH_SHORT).show();

        //设置工具栏toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);

        //设置工具栏
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        TextView account_textView=findViewById(R.id.account);
        account_textView.setText(account_text);
        TextView username=findViewById(R.id.username);
        username.setText(user_name);
        TextView motto1=findViewById(R.id.motto);
        motto1.setText(motto);
        set_Head_image(get_imagepath);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }
/* 利用反射机制调用MenuBuilder的setOptionalIconsVisible方法设置mOptionalIconsVisible为true，给菜单设置图标时才可见
            让菜单同时显示图标和文字
/*/
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }
    //工具栏菜单点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_scan) {
            View view = getWindow().getDecorView();
            onScancode(view);
            return true;
        } else if (item.getItemId() == R.id.action_search) {
            Intent intent1=new Intent(Main2Activity.this, Main3Activity.class);
            startActivity(intent1);
            return true;
        }else if (item.getItemId() == R.id.action_add) {
            Intent intent=new Intent(Main2Activity.this,Home2_Activity.class);
            intent.putExtra(Home2_Activity.JUDE,1);
            startActivity(intent);
            return true;
        }else if (item.getItemId() == R.id.action_house) {
            Intent intent=new Intent(Main2Activity.this,Show1Activity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    //扫描条形码或二维码
    public void onScancode(View v) {
        Show_Choice=0;
        Intent intent = new Intent(Main2Activity.this, CaptureActivity.class);
        ZxingConfig config = new ZxingConfig();
        config.setShowbottomLayout(true);//底部布局（包括闪光灯和相册）
        config.setPlayBeep(true);//是否播放提示音
        config.setShake(true);//是否震动
        config.setShowAlbum(true);//是否显示相册
        config.setShowFlashLight(true);//是否显示闪光灯
        intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
        startActivityForResult(intent, REQUEST_CODE);
    }


    @Override
    public void onClick(View view) {
    }

    //头像更换
    /*
     * 以下代码和Home2_Activity中几乎相同
     *
     */
    public void chose_header_picture(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);//创建对话框
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.dialog, null);//获取自定义布局
        builder.setView(layout);//设置对话框的布局
        dialog = builder.create();//生成最终的对话框
        dialog.show();//显示对话框
        TextView PhotoviewTV = layout.findViewById(R.id.photoview);//拍照
        TextView takePhotoTV = layout.findViewById(R.id.photograph);//拍照
        TextView choosePhotoTV = layout.findViewById(R.id.photo);//从相册中选择
        TextView cancelTV = layout.findViewById(R.id.cancel);//取消
//分别设置监听
        //查看
        PhotoviewTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri  uri=Uri.fromFile(new File(get_imagepath));
                pic_view(uri);
                dialog.dismiss();//关闭对话框
            }
        });
        //拍照
        takePhotoTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Show_Choice = TAKE_PHOTO;
                File outputImage = new File(getExternalCacheDir(), "头像"+".jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //判断版本号
                if (Build.VERSION.SDK_INT < 24) {
                    imageUri = Uri.fromFile(outputImage);
                } else {
                    imageUri = FileProvider.getUriForFile(Main2Activity.this, "com.MapScanner.MapScanner", outputImage);
                }
                // 启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, 666);
                dialog.dismiss();//关闭对话框
            }
        });
        //从相册中选择
        choosePhotoTV.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                Show_Choice = CHOOSE_PHOTO;
                //如果没有权限则申请权限
                if (ContextCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Main2Activity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                //调用打开相册
                openAlbum();
                dialog.dismiss();//关闭对话框
            }
        });
        //取消
        cancelTV.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.dismiss();//关闭对话框
            }
        });
    }

    //打开系统相册
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==666)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openAlbum();
            }
            else {
                Toast.makeText(this, "您已拒绝权限请求", Toast.LENGTH_SHORT).show();
            }

    }

    //一扫码的实现，扫码成功则跳转到Home2_Activity
    //二将相机或图库中图片设置为头像

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (Show_Choice) {
            case 0:
                //接收扫描条形码或二维码结果
                super.onActivityResult(requestCode, resultCode, data);
                if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
                    if (data != null) {
                        String content = data.getStringExtra(Constant.CODED_CONTENT);
                        Toast.makeText(this, "扫描成功!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Main2Activity.this, Home2_Activity.class);
                        Bundle bundle = new Bundle();
                        bundle.putCharSequence("条码值", content);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
                break;
            case 1:
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    //上传图片
                    ImagePath=imageUri.getPath();
                    ImageView roundedImageView=findViewById(R.id.Head_imageView);
                    roundedImageView.setImageBitmap(bitmap);
                    //数据保存进数据库
                    save();
                } catch (Exception e) {
                    e.printStackTrace();

                }
                break;

            case 2:
                Uri uri = data.getData();
                Log.d("TAG", "handleImageOnKitKat: uri is " + uri);

                String imagePath = null;
                if (DocumentsContract.isDocumentUri(this, uri)) {
                    // 如果是document类型的Uri，则通过document id处理
                    String docId = DocumentsContract.getDocumentId(uri);
                    assert uri != null;
                    if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                        String id = docId.split(":")[1]; // 解析出数字格式的id
                        String selection = MediaStore.Images.Media._ID + "=" + id;
                        imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                    }
                    else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                        Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                        imagePath = getImagePath(contentUri, null);
                    }
                }
                else {
                    assert uri != null;
                    if ("content".equalsIgnoreCase(uri.getScheme())) {
                        // 如果是content类型的Uri，则使用普通方式处理
                        imagePath = getImagePath(uri, null);
                    }
                    else if ("file".equalsIgnoreCase(uri.getScheme())) {
                        // 如果是file类型的Uri，直接获取图片路径即可
                        imagePath = uri.getPath();
                    }
                }
                try {
                    ImagePath=imagePath;
                    displayImage(imagePath); // 根据图片路径显示图片
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    //展示图片
    private void displayImage(String imagePath) throws FileNotFoundException {
        if (imagePath != null) {
            FileInputStream fis = new FileInputStream(imagePath);
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeStream(fis,null,options);
            //上传图片
            ImageView roundedImageView = findViewById(R.id.Head_imageView);
            roundedImageView.setImageBitmap(bitmap);
            //数据保存进数据库
            save();
        }
        else {
            Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
        }
    }

    //保存物品信息
    public void exit(View view) {
        ActivityCollectorUtil.finishAllActivity();
        Intent intent=new Intent(Main2Activity.this,MainActivity.class);
        startActivity(intent);
    }

    //查看图片
    public void pic_view(Uri imageUri){
        Intent it =new Intent(this,ShowPic.class);
        it.setDataAndType(imageUri, "image/*");
        startActivity(it);
    }

    //点击保存编辑的头像、昵称、个性签名
    public void save(){
        TextView _username=findViewById(R.id.username);
        TextView _motto=findViewById(R.id.motto);
        String _username1=_username.getText().toString();
        String _motto1=_motto.getText().toString();
        try{
            SQLiteOpenHelper dbHelper = new DBHelper(this);
            db = dbHelper.getReadableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("USERNAME",_username1);
            cv.put("MOTTO",_motto1);
            cv.put("HEADIMAGE",ImagePath);
            String[] args={account_text};
            int a=db.update("SIGN_UP",cv,"ACCOUNT=?",args);
            Toast.makeText(this, "保存成功！", Toast.LENGTH_SHORT).show();
            get_imagepath=ImagePath;

        }catch (SQLiteException e){
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //设置头像
    private void set_Head_image(String get_imagepath){
        String imagepath = get_imagepath.replace("/my_images", "/storage/emulated/0");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(imagepath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeStream(fis, null, options);
        ImageView head_imageView = findViewById(R.id.Head_imageView);
        head_imageView.setImageBitmap(bitmap);
    }

    //进入个人中心
    public void go_to_personal_center(View view) {
        Intent intent=new Intent(Main2Activity.this,personal_data_activity.class);
        Bundle bundle = new Bundle();
        bundle.putCharSequence("账号", account_text);
        bundle.putCharSequence("昵称", user_name);
        bundle.putCharSequence("格言",motto);
        bundle.putCharSequence("数据库头像路径",get_imagepath);
        intent.putExtras(bundle);
        startActivity(intent);

    }


}
