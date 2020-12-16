package com.example.myproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class Adapter  extends BaseAdapter {

    Context context;
    List<PlaceModel> myList=new ArrayList<>();
    LayoutInflater layoutInflater;

    public Adapter(Context context, List<PlaceModel> myList) {
        this.context = context;
        this.myList = myList;
        layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v=layoutInflater.inflate(R.layout.custom_place,null);

        TextView Title=v.findViewById(R.id.Title);
        TextView Desc=v.findViewById(R.id.Description);
        TextView Address=v.findViewById(R.id.Address);
        ImageView Image=v.findViewById(R.id.Image);

        PlaceModel placeModel=myList.get(position);

        Title.setText(""+placeModel.getTitle());
        Desc.setText(""+placeModel.getDes());
        Address.setText(""+placeModel.getAddress());
        Glide.with(context).load(""+placeModel.getImg()).centerCrop().placeholder(R.drawable.ic_account_circle_black_24dp).into(Image);

        return v;
    }
}
