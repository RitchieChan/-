package com.shashank.platform.loginui;

import android.app.ListActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.ListView;
import android.view.View;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Show2Activity extends ListActivity {
    private SQLiteDatabase db;
    private Cursor cursor;
    public String sort_id;
    public static final String EXTRA_ID="articleId";
    private String choseString;
    private String imagepath22;
    private ImageView photo;
    private List<Object> objects=new ArrayList<Object>();
    private ObjectAdapter objectAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        //状态栏透明
        StatusBarUtil.setTranslucent(this);
        Bundle bundle1=getIntent().getExtras();
        assert bundle1 != null;
        choseString=bundle1.getString("分类");

        //添加activity到集合，方便统一注销
        //choseString= getIntent().getExtras().getString(EXTRA_ID);
        ActivityCollectorUtil.addActivity(Show2Activity.this);
        setContentView(R.layout.activity_show2);

       // ListView listThings = getListView();
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(this);
            db = dbHelper.getReadableDatabase();
//打开数据库，把NAME列中的值全部找出来
            cursor = db.query("ARTICLE", new String[]{"IMAGEPATH","_id", "NAME","SORT","TIME"},
                    "SORT=?",new String[]{choseString}, null, null, null, null);
//适配器将所有值放入listview中
            if (cursor.moveToFirst()) {
                do{
                    Object object = new Object();
                    //取出数据,调用cursor.getInt/getString等方法

                    String imagePathText = cursor.getString(0);
                    int idText=cursor.getInt(1);
                    String timeText = cursor.getString(4);
                    String nameText = cursor.getString(2);
                    String sortText = cursor.getString(3);


                    object.setId(idText);
                    object.setName(nameText);
                    object.setDate(timeText);
                    object.setSort(sortText);
                    object.setImagepath(imagePathText);
                    objects.add(object);

                }while(cursor.moveToNext());
            }


            objectAdapter = new ObjectAdapter(this, R.layout.tencent_show, objects);
            listView = (ListView) Objects.requireNonNull(this).findViewById(android.R.id.list);
            listView.setAdapter(objectAdapter);
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
        ActivityCollectorUtil.removeActivity(Show2Activity.this);
    }

    @Override
    public void onListItemClick(ListView listView,
                                View itemView,
                                int position,
                                long id) {
        Intent intent = new Intent(Show2Activity.this, Show3Activity.class);

        int myid=objects.get((int)id).getId();
        intent.putExtra(Show3Activity.EXTRA_ID, myid);
        startActivity(intent);
        finish();
        //将id传到下一级activity中
    }
    public void back(View view) {
        finish();
    }
}


