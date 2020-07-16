package com.shashank.platform.loginui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.jaeger.library.StatusBarUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class Home2_Activity extends AppCompatActivity implements View.OnClickListener {
     TextView textView,textView2;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private ImageView picture1,picture2,picture3;
    private AlertDialog dialog;
    private Uri imageUri;
    private int Show_Choice;
    public static SQLiteDatabase db;
    public int jude=0;//用于判断是否由手动添加按钮跳转而来
    public static final String JUDE="";

    //需要存入数据库的值有：codevalue、time、name、category、price、position、table、imagePath
    private String codevalue1;
    private String time1;
    private ArrayList imagePath1=new ArrayList<String>();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollectorUtil.removeActivity(Home2_Activity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_page_processed);
        //状态栏透明
        StatusBarUtil.setTransparent(this);
        //添加activity到集合，方便统一注销
        ActivityCollectorUtil.addActivity(Home2_Activity.this);

        //获得数据库对象
        SQLiteOpenHelper dbHelper =new DBHelper(this);
        db=dbHelper.getReadableDatabase();

        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        setTitle("输入信息");
        picture1 = (ImageView) findViewById(R.id.V_Image1);
        picture2 = (ImageView) findViewById(R.id.V_Image2);
        picture3 = (ImageView) findViewById(R.id.V_Image3);
        textView=(TextView)findViewById(R.id.username);
        textView2=(TextView)findViewById(R.id.textView2);
        jude=getIntent().getExtras().getInt(JUDE);
        if(jude==0) {
            Log.d("test","成功"+jude);
            Bundle bundle = getIntent().getExtras();
            //用String对象codevalue获取条码值
            assert bundle != null;
            String codevalue = bundle.getString("条码值");   //String值2---条码值codevalue
            codevalue1 = codevalue;
            textView.setText(codevalue);
        }
        //获取记录时间
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour1 = calendar.get(Calendar.HOUR_OF_DAY)/10;
        int hour2 = calendar.get(Calendar.HOUR_OF_DAY)%10;
        int minute1 = calendar.get(Calendar.MINUTE)/10;
        int minute2 = calendar.get(Calendar.MINUTE)%10;
        int second1 = calendar.get(Calendar.SECOND)/10;
        int second2 = calendar.get(Calendar.SECOND)%10;
        String time=year+"年"+month+"月"+day+"日"+hour1+hour2+":"+minute1+minute2+":"+second1+second2;
        time1=time;
        textView2.setText(time);                                              //String值1---录入时间time

        ImageButton add_photo = (ImageButton) findViewById(R.id.add_photo);
        // 对添加图片功能的响应
        add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动对话框
                viewInit();
            }

        });
    }
    /*
   初始化控件方法
   */
    public void viewInit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);//创建对话框
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.dis, null);//获取自定义布局
        builder.setView(layout);//设置对话框的布局
        dialog = builder.create();//生成最终的对话框
        dialog.show();//显示对话框
        TextView takePhotoTV = layout.findViewById(R.id.photograph);//拍照
        TextView choosePhotoTV = layout.findViewById(R.id.photo);//从相册中选择
        TextView cancelTV = layout.findViewById(R.id.cancel);//取消
//分别设置监听
        //拍照
        takePhotoTV.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Show_Choice=TAKE_PHOTO;
                File outputImage = new File(getExternalCacheDir(), System.currentTimeMillis()+".jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                    outputImage.setWritable(true,false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //判断版本号
                if (Build.VERSION.SDK_INT < 24) {
                    imageUri = Uri.fromFile(outputImage);
                } else {
                    imageUri = FileProvider.getUriForFile(Home2_Activity.this, "com.MapScanner.MapScanner", outputImage);
                }
                // 启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, 666);
                dialog.dismiss();//关闭对话框
            }
        });
        //从相册中选择
        choosePhotoTV.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Show_Choice=CHOOSE_PHOTO;
                //如果没有权限则申请权限
                if (ContextCompat.checkSelfPermission(Home2_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Home2_Activity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                //调用打开相册
                openAlbum();
                dialog.dismiss();//关闭对话框
            }
        });
        //取消
        cancelTV.setOnClickListener(new View.OnClickListener(){

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
    public void setContentView(View view) {
        super.setContentView(view);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (Show_Choice) {
            case 1:
                try {// 将拍摄的照片显示出来
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    //上传图片
                    //成功则记录并显示图片路径
                    if(up_load(bitmap)) {
                        String imagePath=imageUri.getPath();
                        imagePath1.add(imagePath);
                        Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();    //String值8----相册图片路径imagePath
                    }
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
                displayImage(imagePath); // 根据图片路径显示图片
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

//判断上传图片位置以及是否已满
public Boolean up_load(Bitmap bitmap){
    if(picture1.getDrawable()==null){
        picture1.setImageBitmap(bitmap);
        picture1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pic_view((String) imagePath1.get(0));
            }
        });
        return true;
    }else if(picture2.getDrawable()==null){
        picture2.setImageBitmap(bitmap);
        picture2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pic_view((String) imagePath1.get(1));
            }
        });
        return true;
    }else if(picture3.getDrawable()==null){
        picture3.setImageBitmap(bitmap);
        picture3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pic_view((String) imagePath1.get(2));
            }
        });
        return true;
    }else{
        Toast.makeText(this,"最多上传三张图片" , Toast.LENGTH_SHORT).show();
        return false;
    }
}

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            //上传图片
            //成功则记录并显示图片路径
            if(up_load(bitmap)) {
                imagePath1.add(imagePath);
                Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();    //String值8----相册图片路径imagePath
            }
        }
        else {
            Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
        }
    }

    //保存信息并返回Main2Activity
    public void confirm_save(View view) {
         /*   保存信息*/
       //找到名称、分类、价格、位置、标签等值
    EditText name0=(EditText)findViewById(R.id.textView3);
     String name=name0.getText().toString();                    //String值3---name
    EditText category0=(EditText)findViewById(R.id.textView4);
     String category=category0.getText().toString();             //String值4----category
    EditText price0=(EditText)findViewById(R.id.textView5);
     String price=price0.getText().toString();                    //String值5----price
    EditText position0=(EditText)findViewById(R.id.textView6);
     String position=position0.getText().toString();                //String值6----position
    EditText table0=(EditText)findViewById(R.id.textView7);
     String table=table0.getText().toString();                     //String值7-----table


        if(name.length()!=0&&category0.length()!=0){
        insertDrink(db, imagePath1,time1,name,category,price,position,table,codevalue1);
        finish();
        }
        else {
            Toast.makeText(this, "名称和分类不能为空，请重新确认！", Toast.LENGTH_SHORT).show();
        }
    }




    @Override
    public void onClick(View view) {

    }

    //向数据库插入一条记录的函数
    public void insertDrink(SQLiteDatabase db, ArrayList<String> imagePath, String time, String name, String category,
                                    String price, String position, String table, String codevalue){
        ContentValues drinkValues = new ContentValues();
        drinkValues.put("TIME",time);//1
        drinkValues.put("CODEVALUE",codevalue);//2
        drinkValues.put("NAME",name);//3
        drinkValues.put("SORT",category);//4
        drinkValues.put("PRICE",price);//5
        drinkValues.put("POSITION",position);//6
        drinkValues.put("TABLE1",table);//7
        drinkValues.put("IMAGEPATH",imagePath.toString());//8
        long result =db.insert("ARTICLE",null,drinkValues);
        Log.d("sqlite","insert"+name+",_id"+result);

        Toast.makeText(this,"保存成功", Toast.LENGTH_SHORT).show();
    }

    public void backup2(View view) {
        finish();
    }
    public void pic_view(String imagePath){
        Uri imageUri=Uri.fromFile(new File(imagePath));
        Intent it =new Intent(this,ShowPic.class);
        it.setDataAndType(imageUri, "image/*");
        startActivity(it);
    }

}
