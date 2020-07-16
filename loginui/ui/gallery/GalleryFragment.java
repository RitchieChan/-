package com.shashank.platform.loginui.ui.gallery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v4.app.Fragment;
import android.widget.TextView;
import android.widget.Toast;

import com.shashank.platform.loginui.DBHelper;
import com.shashank.platform.loginui.Main3Activity;
import com.shashank.platform.loginui.Object;
import com.shashank.platform.loginui.ObjectAdapter;
import com.shashank.platform.loginui.R;
import com.shashank.platform.loginui.Show3Activity;

import java.util.ArrayList;
import java.util.List;

import static com.shashank.platform.loginui.Home2_Activity.db;

public class GalleryFragment extends Fragment {

    private ListView listView;
    private List<Object> objects=new ArrayList<>();
    boolean flag =true;
    int count =1;
    ObjectAdapter objectAdapter;

    @SuppressLint("ResourceType")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        while (flag) {
            Object object = new Object();
            SQLiteOpenHelper dbHelper = new DBHelper(getActivity());
            db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query("ARTICLE", new String[]{"IMAGEPATH", "TIME",
                            "NAME", "SORT", "PRICE", "POSITION", "TABLE1", "CODEVALUE"}, "_id=?",
                    new String[]{Integer.toString(count++)}, null, null, null, null);
//找出所需要显示的值
            //Move to the first record in the Cursor
            if (cursor.moveToFirst()) {
                String imagePathText = cursor.getString(0);
                String timeText = cursor.getString(1);
                String nameText = cursor.getString(2);
                String sortText = cursor.getString(3);


                object.setName(nameText);
                object.setDate(timeText);
                object.setSort(sortText);
                object.setImagepath(imagePathText);
                objects.add(object);
                object.setId(count-1);
            } else {
                flag = false;
            }

        }
        listView=root.findViewById(R.id.listView);
        objectAdapter = new ObjectAdapter(getActivity(), R.layout.tencent_show, objects);
        listView.setAdapter(objectAdapter);
        return root;
    }
}
