package com.shashank.platform.loginui;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.shashank.platform.loginui.Home2_Activity.db;

public class Main3Activity extends AppCompatActivity implements View.OnClickListener {
    private ListView listView;
    private List<Object> objects=new ArrayList<Object>();
    private ObjectAdapter objectAdapter;
    boolean flag=true;
    SearchView searchview;
    int count=1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //状态栏透明
        StatusBarUtil.setTransparent(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        //添加activity到集合，方便统一注销
        ActivityCollectorUtil.addActivity(Main3Activity.this);
        try {

            SQLiteOpenHelper dbHelper = new DBHelper(this);
            db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query("ARTICLE", new String[]{"_id","IMAGEPATH", "TIME",
                            "NAME", "SORT", "PRICE", "POSITION", "TABLE1", "CODEVALUE"}, null,
                    null, null, null, null);
//找出所需要显示的值
            //Move to the first record in the Cursor
            if (cursor.moveToFirst()) {
                do {
                    Object object = new Object();
                    String imagePathText = cursor.getString(1);
                    String timeText = cursor.getString(2);
                    String nameText = cursor.getString(3);
                    String sortText = cursor.getString(4);


                    object.setName(nameText);
                    object.setDate(timeText);
                    object.setSort(sortText);
                    object.setImagepath(imagePathText);
                    object.setId(Integer.parseInt(cursor.getString(0)));
                    objects.add(object);
                }while(cursor.moveToNext());
            } else {
                Toast toast = Toast.makeText(this, "Database went wrong", Toast.LENGTH_SHORT);
                toast.show();
            }

        }catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
        Collections.reverse(objects);
        objectAdapter = new ObjectAdapter(this, R.layout.tencent_show, objects);
        listView = (ListView) Objects.requireNonNull(this).findViewById(R.id.listView);
        listView.setAdapter(objectAdapter);
        searchview= this.findViewById(R.id.searchView);
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //隐藏键盘
                InputMethodManager manager = (InputMethodManager)searchview.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(searchview.getWindowToken(), 0);
                List<Object> objects1 = new ArrayList<Object>();
                for(int i=0;i< objects.size();i++){
                    if(objects.get(i).getName().equals(s)||objects.get(i).getSort().equals(s)||objects.get(i).getDate().equals(s)){
                        objects1.add(objects.get(i));
                    }
                 }
                if(objects1.size()!=0) {
                    Toast.makeText(Main3Activity.this,"Got it!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Main3Activity.this,"Didn't found!", Toast.LENGTH_SHORT).show();
                }
                ObjectAdapter objectAdapter1 = new ObjectAdapter(Main3Activity.this, R.layout.tencent_show, objects1);
                listView.setAdapter(objectAdapter1);
                    return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(TextUtils.isEmpty(s)){
                    ObjectAdapter objectAdapter = new ObjectAdapter(Main3Activity.this, R.layout.tencent_show,objects);
                    listView.setAdapter(objectAdapter);
                    return true;
                }else{
                    List<Object> objects2 = new ArrayList<Object>();
                    for(int i=0;i< objects.size();i++){
                        if(objects.get(i).getName().contains(s)||objects.get(i).getSort().contains(s)||objects.get(i).getDate().contains(s)){
                            objects2.add(objects.get(i));
                        }
                    }
                    ObjectAdapter objectAdapter2 = new ObjectAdapter(Main3Activity.this, R.layout.tencent_show, objects2);
                    listView.setAdapter(objectAdapter2);
                    return true;
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                int i=(int)view.findViewById(R.id.textView8).getTag();
                Intent intent = new Intent(Main3Activity.this, Show3Activity.class);
                intent.putExtra(Show3Activity.EXTRA_ID,i);
                startActivity(intent);
            }
        });

    }




    public void back(View view) {
        finish();
    }


    @Override
    public void onClick(View view) {

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollectorUtil.removeActivity(this);
    }

}

