package com.shashank.platform.loginui.ui.home;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.widget.Toast;

import com.shashank.platform.loginui.DBHelper;
import com.shashank.platform.loginui.Main2Activity;
import com.shashank.platform.loginui.MainActivity;
import com.shashank.platform.loginui.Object;
import com.shashank.platform.loginui.ObjectAdapter;
import com.shashank.platform.loginui.R;
import com.shashank.platform.loginui.Show1Activity;
import com.shashank.platform.loginui.Show3Activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.shashank.platform.loginui.Home2_Activity.db;

public class HomeFragment extends Fragment {
    private ListView listView;
    private TextView textView;
    private HomeViewModel homeViewModel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        listView=root.findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                int i=(int)view.findViewById(R.id.textView8).getTag();
                Intent intent = new Intent(getContext(), Show3Activity.class);
                intent.putExtra(Show3Activity.EXTRA_ID,i);
                startActivity(intent);
            }
        });
        textView  = root.findViewById(R.id.textview11);
        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
        ObjectAdapter objectAdapter = new ObjectAdapter(getActivity(), R.layout.tencent_show, getObjects());
        listView.setAdapter(objectAdapter);
    }


    public  List<Object> getObjects(){
        List<Object> objects=new ArrayList<>();
        try {

            SQLiteOpenHelper dbHelper = new DBHelper(getContext());
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
            }

        }catch (SQLiteException e) {
            Toast toast = Toast.makeText(getContext(), "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
        Collections.reverse(objects);
        for(int i=objects.size();i>6;i--){
            objects.remove(i-1);
        }
        if(objects.size()!=0) {

            textView.setVisibility(View.INVISIBLE);
        }
        return objects;
    }
}
