package com.shashank.platform.loginui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Show1Activity extends AppCompatActivity implements View.OnClickListener{
    private SQLiteDatabase db;
    private Cursor cursor;
    private ListView listThings;
    private String sort;
    private List<Object> objects=new ArrayList<Object>();
    private List<String> sorts=new ArrayList<String>();
    private ObjectAdapter objectAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show1);
        //状态栏透明
        StatusBarUtil.setTransparent(this);

        //添加activity到集合，方便统一注销
        ActivityCollectorUtil.addActivity(Show1Activity.this);
        //实现OnItemClickListener 点击时打开二级界面
        listThings = (ListView)findViewById(android.R.id.list);
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(this);
            db = dbHelper.getReadableDatabase();
//打开数据库，把NAME列中的值全部找出来
            cursor = db.query("ARTICLE", new String[]{"_id", "NAME","SORT"},
                    null, null, null, null, null);
//适配器将所有值放入listview中
            if (cursor.moveToFirst()) {
                do{
                    String sortText = cursor.getString(2);

                    if(!sorts.contains(sortText)){
                        sorts.add(sortText);
                    }
                }while(cursor.moveToNext());
            }

            ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,sorts);
            listThings.setAdapter(arrayAdapter);
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

        listThings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                TextView text1=(TextView)view.findViewById(android.R.id.text1);
                sort=text1.getText().toString();
                Intent intent = new Intent(Show1Activity.this, Show2Activity.class);
                Bundle bundle = new Bundle();
                bundle.putCharSequence("分类", sort);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollectorUtil.removeActivity(Show1Activity.this);
    }

    @Override
    public void onClick(View view) {

    }
    public void back(View view) {
        finish();
    }
}
