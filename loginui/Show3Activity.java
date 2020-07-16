package com.shashank.platform.loginui;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.jaeger.library.StatusBarUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Show3Activity extends AppCompatActivity {

    public static final String EXTRA_ID="articleId";
    private int choseId;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_page_processed);
        StatusBarUtil.setTransparent(this);

        //添加activity到集合，方便统一注销
        ActivityCollectorUtil.addActivity(Show3Activity.this);
         choseId = getIntent().getExtras().getInt(EXTRA_ID);
        //Create a cursor
        List<String> imagePath = null;
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(this);
            db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query("ARTICLE", new String[]{"IMAGEPATH", "TIME",
                            "NAME", "SORT", "PRICE", "POSITION", "TABLE1", "CODEVALUE"}, "_id=?",
                    new String[]{Integer.toString(choseId)}, null, null, null, null);
//找出所需要显示的值
            //Move to the first record in the Cursor
            if (cursor.moveToFirst()) {
                //Get the drink details from the cursor
                String imagePathText = cursor.getString(0);
                String timeText = cursor.getString(1);
                String nameText = cursor.getString(2);
                String sortText = cursor.getString(3);
                String priceText = cursor.getString(4);
                String positionText = cursor.getString(5);
                String labelText = cursor.getString(6);
                String codevalueText = cursor.getString(7);
                imagePath = Arrays.asList(imagePathText.substring(1, imagePathText.length() - 1).split(", "));
                List<Bitmap> bitmapArrayList = new ArrayList<>();
//全部显示到不同的textview中
                EditText name =  findViewById(R.id.name);
                name.setText(nameText);

                TextView time = (TextView) findViewById(R.id.date);
                time.setText(timeText);

                EditText price =  findViewById(R.id.price);
                price.setText(priceText);

                EditText label =  findViewById(R.id.label);
                label.setText(labelText);

                EditText position =  findViewById(R.id.position);
                position.setText(positionText);

                TextView codevalue = (TextView) findViewById(R.id.codevalue);
                if(!codevalue.equals(null))
                codevalue.setText(codevalueText);

                EditText sort =  findViewById(R.id.sort);
                sort.setText(sortText);


                    //Populate the image
                    for (int i = 0; i < imagePath.size(); i++) {
                        String imagepath = imagePath.get(i).replace("/my_images", "/storage/emulated/0");
                        FileInputStream fis = new FileInputStream(imagepath);
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = false;
                        options.inSampleSize = 10;   //width，hight设为原来的十分一
                        Bitmap bitmap = BitmapFactory.decodeStream(fis, null, options);
                        bitmapArrayList.add(bitmap);
                    }
                    switch (imagePath.size()) {
                        case 3:
                            ImageView photo3 = findViewById(R.id.photo3);
                            photo3.setImageBitmap(bitmapArrayList.get(2));
                            photo3.setContentDescription(nameText);
                            final String path3 = imagePath.get(2);
                            photo3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    pic_view(path3);
                                }
                            });
                        case 2:
                            ImageView photo2 = findViewById(R.id.photo2);
                            photo2.setImageBitmap(bitmapArrayList.get(1));
                            photo2.setContentDescription(nameText);
                            final String path2 = imagePath.get(1);
                            photo2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    pic_view(path2);
                                }
                            });
                        case 1:
                            ImageView photo1 = findViewById(R.id.photo1);
                            photo1.setImageBitmap(bitmapArrayList.get(0));
                            photo1.setContentDescription(nameText);
                            final String path1 = imagePath.get(0);
                            photo1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    pic_view(path1);
                                }
                            });
                            break;
                        default:
                            Toast.makeText(this, +imagePath.size(), Toast.LENGTH_SHORT).show();
                            break;
                    }
                }

            cursor.close();
        } catch (SQLiteException e) {
         Toast.makeText(this, "Database111 unavailable", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public void backup1(View view){
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollectorUtil.removeActivity(Show3Activity.this);
    }
    public void pic_view(String imagePath){
        Uri imageUri=Uri.fromFile(new File(imagePath));
        Intent it =new Intent(this,ShowPic.class);
        it.setDataAndType(imageUri, "image/*");
        startActivity(it);
    }
    public void edit(View view){
        EditText name =  findViewById(R.id.name);
        name.setEnabled(true);
        name.setFocusable(true);
        name.setFocusableInTouchMode(true);
        EditText price =  findViewById(R.id.price);
        price.setEnabled(true);
        price.setFocusable(true);
        price.setFocusableInTouchMode(true);
        EditText label =  findViewById(R.id.label);
        label.setEnabled(true);
        label.setFocusable(true);
        label.setFocusableInTouchMode(true);
        EditText position =  findViewById(R.id.position);
        position.setEnabled(true);
        position.setFocusable(true);
        position.setFocusableInTouchMode(true);
        EditText sort =  findViewById(R.id.sort);
        sort.setEnabled(true);
        sort.setFocusable(true);
        sort.setFocusableInTouchMode(true);
        ImageView delete=findViewById(R.id.image_delete);
        delete.setVisibility(View.VISIBLE);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(Show3Activity.this).setTitle("确定要删除吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
//按下确定键后的事件
                                try{

                                    SQLiteOpenHelper dbHelper = new DBHelper(Show3Activity.this);
                                    db = dbHelper.getReadableDatabase();
                                    String[] args={Integer.toString(choseId)};
                                    int b=db.delete("ARTICLE","_id=?",args);
                                    Toast toast = Toast.makeText(Show3Activity.this, "删除成功！", Toast.LENGTH_SHORT);
                                    toast.show();
                                    finish();
                                }catch (SQLiteException e){
                                    Toast toast = Toast.makeText(Show3Activity.this, "Database unavailable", Toast.LENGTH_SHORT);
                                    toast.show();
                                }

                            }
                        }).setNegativeButton("取消",null).show();

            }
        });
        ImageView save=findViewById(R.id.image_save);
        save.setVisibility(View.VISIBLE);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    SQLiteOpenHelper dbHelper = new DBHelper(Show3Activity.this);
                    EditText name =  findViewById(R.id.name);
                    EditText price =  findViewById(R.id.price);
                    EditText label =  findViewById(R.id.label);
                    EditText position =  findViewById(R.id.position);
                    EditText sort =  findViewById(R.id.sort);
                    db = dbHelper.getReadableDatabase();
                    ContentValues cv = new ContentValues();
                    cv.put("NAME",name.getText().toString());
                    cv.put("SORT",sort.getText().toString());
                    cv.put("TABLE1",label.getText().toString());
                    cv.put("PRICE",price.getText().toString());
                    cv.put("POSITION",position.getText().toString());
                    String[] args={Integer.toString(choseId)};
                    int a=db.update("ARTICLE",cv,"_id=?",args);
                    Toast.makeText(Show3Activity.this, "保存成功！", Toast.LENGTH_SHORT).show();
                    finish();

                }catch (SQLiteException e){
                    Toast toast = Toast.makeText(Show3Activity.this, "Database unavailable", Toast.LENGTH_SHORT);
                    toast.show();
                }


            }
        });

    }


}
