package com.example.myproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class DataList2Activity extends AppCompatActivity {

    String TitleStr,Uemail;
    FirebaseFirestore firebaseFirestore;
    ProgressDialog progressDialog;

    ImageView PlaceImage;
    TextView PlaceTitle,PlaceDesc,PlaceAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_list2);
        PlaceImage=findViewById(R.id.Place_Image);
        PlaceTitle=findViewById(R.id.PlaceTitle);
        PlaceDesc=findViewById(R.id.PlaceDescription);
        PlaceAddress=findViewById(R.id.PlaceAddress);

        TitleStr=getIntent().getStringExtra("Key");
        Uemail=getIntent().getStringExtra("email");
        firebaseFirestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);

        loadMethod();

    }

    private void loadMethod() {

        firebaseFirestore.collection("UserUploadData").whereEqualTo("UserEmail",Uemail).whereEqualTo("UserTitle",TitleStr).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                progressDialog.dismiss();
                if(task.isSuccessful())
                {
                    for(QueryDocumentSnapshot qb:task.getResult())
                    {
                        String UTitle=qb.getString("UserTitle");
                        String UDes=qb.getString("UserDesc");
                        String UAddress=qb.getString("UserAddress");
                        String UImg=qb.getString("UserImage");
                        Double ULat=qb.getDouble("Lat");
                        Double ULng=qb.getDouble("Lng");

                        PlaceTitle.setText(""+UTitle);
                        PlaceDesc.setText(""+UDes);
                        PlaceAddress.setText(""+UAddress);
                        Glide.with(DataList2Activity.this).load(""+UImg).centerCrop().placeholder(R.drawable.add_image).into(PlaceImage);
                    }


                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
