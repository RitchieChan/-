package com.shashank.platform.loginui;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shashank.platform.loginui.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

public class ObjectAdapter extends ArrayAdapter<Object> {
    private int resourceId;
    Context context;
    public ObjectAdapter(Context context, int textViewResourceId,
                         List<Object> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        this.context=context;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Object object= getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();

            viewHolder.name = (TextView)view.findViewById(R.id.textView8);

            viewHolder.data = (TextView)view.findViewById(R.id.textView10);
            viewHolder.sort = (TextView)view.findViewById(R.id.textView9);
            viewHolder.photo = (ImageView) view.findViewById(R.id.imageView_show);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        //将imagepath转换为bitmap
        if(object.getImagepath()!=null){
            List<String> imagePath = Arrays.asList(object.getImagepath().substring(1, object.getImagepath().length() - 1).split(", "));
            String imagepath = imagePath.get(0).replace("/my_images", "/storage/emulated/0");
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(imagepath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = 10;   //width，hight设为原来的十分一
            Bitmap bitmap = BitmapFactory.decodeStream(fis, null, options);
            viewHolder.photo.setImageBitmap(bitmap);

        }else{
            viewHolder.photo.setImageResource(R.drawable.camera);
        }


        viewHolder.name.setText(object.getName());
        viewHolder.data.setText(object.getDate());
        viewHolder.sort.setText(object.getSort());
        viewHolder.name.setTag(getItem(position).getId());
//            viewHolder.name.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int tag = (Integer) v.getTag();
//                    System.out.println(tag);
//                    if (tag != 0) {
//                        Intent intent = new Intent(context, Show3Activity.class);
//                        intent.putExtra(Show3Activity.EXTRA_ID,tag);
//                        context.startActivity(intent);
//                    }
//                }
//            });
        return view;
    }
     class ViewHolder {
        TextView name;
        TextView data;
        TextView sort;
        ImageView photo;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

}

