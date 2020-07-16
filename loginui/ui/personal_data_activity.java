package com.shashank.platform.loginui.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.shashank.platform.loginui.ActivityCollectorUtil;
import com.shashank.platform.loginui.DBHelper;
import com.shashank.platform.loginui.Home2_Activity;
import com.shashank.platform.loginui.Main2Activity;
import com.shashank.platform.loginui.R;
import com.shashank.platform.loginui.Show3Activity;
import com.shashank.platform.loginui.ShowPic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class personal_data_activity extends AppCompatActivity {
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
    public static SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);
        //添加activity到集合，方便统一注销
        ActivityCollectorUtil.addActivity(this);
        //状态栏半透明
        StatusBarUtil.setTransparent(this);
        //获取用户名
        Bundle bundle1=getIntent().getExtras();
        assert bundle1 != null;
        account_text=bundle1.getString("账号");
        user_name=bundle1.getString("昵称");
        motto=bundle1.getString("格言");
        get_imagepath=bundle1.getString("数据库头像路径");        //从数据库找到的头像路径
        TextView account_textView=findViewById(R.id.account);
        account_textView.setText(account_text);
        TextView username=findViewById(R.id.username);
        username.setText(user_name);
        TextView motto1=findViewById(R.id.motto);
        motto1.setText(motto);
        set_Head_image(get_imagepath);
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
        PhotoviewTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri=Uri.fromFile(new File(get_imagepath));
                pic_view(uri);
                dialog.dismiss();//关闭对话框
            }
        });
        //拍照
        takePhotoTV.setOnClickListener(new View.OnClickListener() {
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
                    imageUri = FileProvider.getUriForFile(personal_data_activity.this, "com.MapScanner.MapScanner", outputImage);
                }
                // 启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, 666);
                dialog.dismiss();//关闭对话框
            }
        });
        //从相册中选择
        choosePhotoTV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Show_Choice = CHOOSE_PHOTO;
                //如果没有权限则申请权限
                if (ContextCompat.checkSelfPermission(personal_data_activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(personal_data_activity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                //调用打开相册
                openAlbum();
                dialog.dismiss();//关闭对话框
            }
        });
        //取消
        cancelTV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.dismiss();//关闭对话框
            }
        });
    }

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

    //二将相机或图库中图片设置为头像
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (Show_Choice) {
            case 1:
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    //上传图片
                    ImagePath=imageUri.getPath();
                    ImageView roundedImageView=findViewById(R.id.Head_imageView);
                    roundedImageView.setImageBitmap(bitmap);
                    Toast.makeText(this, "上传成功", Toast.LENGTH_SHORT).show();
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
    private void displayImage(String imagePath) throws FileNotFoundException {
        if (imagePath != null) {
            FileInputStream fis = new FileInputStream(imagePath);
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeStream(fis,null,options);
            //上传图片
            ImageView roundedImageView = findViewById(R.id.Head_imageView);
            roundedImageView.setImageBitmap(bitmap);
            Toast.makeText(this, "上传成功", Toast.LENGTH_SHORT).show();

        }
        else {
            Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
        }
    }
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
    public void pic_view(Uri imageUri){
        Intent it =new Intent(this, ShowPic.class);
        it.setDataAndType(imageUri, "image/*");
        startActivity(it);
    }
    //点击保存编辑的头像、昵称、个性签名
    public void SAVE(View view){
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

        }catch (SQLiteException e){
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
        Flush();
        finish();


    }


    public void back(View view) {
        finish();
    }

    public void alert_edit1(View view){
        final EditText et = new EditText(this);
        et.setText(user_name);
        new AlertDialog.Builder(this).setTitle("昵称")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//按下确定键后的事件
                        user_name=et.getText().toString();
                        TextView account_textView=findViewById(R.id.username);
                        account_textView.setText(user_name);
                    }
                }).setNegativeButton("取消",null).show();
    }
    public void alert_edit2(View view){
        final EditText et = new EditText(this);
        et.setText(motto);
        new AlertDialog.Builder(this).setTitle("格言")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//按下确定键后的事件
                        motto=et.getText().toString();
                        TextView account_textView=findViewById(R.id.motto);
                        account_textView.setText(motto);
                    }
                }).setNegativeButton("取消",null).show();
    }

    private void Flush() {
        String username;
        String motto;
        String temp_imahepath;

        try {
            SQLiteOpenHelper dbHelper = new DBHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query("SIGN_UP", new String[]{"ACCOUNT","PASSWORD","USERNAME","MOTTO","HEADIMAGE"}, "ACCOUNT=?",
                    new String[]{account_text}, null, null, null, null);

            //Move to the first record in the Cursor
            if (cursor.moveToFirst()) {
                username=cursor.getString(2);
                motto=cursor.getString(3);
                temp_imahepath=cursor.getString(4);
                if (temp_imahepath== null){
                    temp_imahepath=get_imagepath;
                }

                Intent intent=new Intent(personal_data_activity.this,Main2Activity.class);
                Bundle bundle = new Bundle();
                bundle.putCharSequence("账号", account_text);
                bundle.putCharSequence("昵称", username);
                bundle.putCharSequence("格言",motto);
                bundle.putCharSequence("数据库头像路径",temp_imahepath);
                intent.putExtras(bundle);
                startActivity(intent);
            }
            cursor.close();
        }catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollectorUtil.removeActivity(this);
    }
}

