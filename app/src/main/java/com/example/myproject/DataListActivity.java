package com.example.myproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class DataListActivity extends AppCompatActivity {


    ImageView PlaceImage;
    TextView PlaceTitle,PlaceDesc,PlaceAddress;

    String img,tit,dsc,add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_list);

        PlaceImage=findViewById(R.id.Place_Image);
        PlaceTitle=findViewById(R.id.PlaceTitle);
        PlaceDesc=findViewById(R.id.PlaceDescription);
        PlaceAddress=findViewById(R.id.PlaceAddress);

        img=getIntent().getStringExtra("img");
        tit=getIntent().getStringExtra("tit");
        dsc=getIntent().getStringExtra("dsc");
        add=getIntent().getStringExtra("add");

        PlaceTitle.setText(""+tit);
        PlaceDesc.setText(""+dsc);
        PlaceAddress.setText(""+add);
        Glide.with(DataListActivity.this).load(""+img).centerCrop().placeholder(R.drawable.add_image).into(PlaceImage);





    }
}
