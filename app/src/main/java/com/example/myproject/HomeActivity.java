package com.example.myproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    TextView TakePhoto,TakeMap,Signout;

    ListView PlaceList;
    Adapter adapter;
    List<PlaceModel>  myList=new ArrayList<>();
    PlaceModel placeModel;

    FirebaseFirestore firebaseFirestore;
    ProgressDialog progressDialog;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String UserEmail;

    @Override
    protected void onStart() {
        super.onStart();
        loadList();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TakePhoto=findViewById(R.id.Camera);
        TakeMap=findViewById(R.id.Place);
        PlaceList=findViewById(R.id.PlaceList);
        Signout=findViewById(R.id.Signout);

        permissionMethod();

        sharedPreferences = getSharedPreferences("Main", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        UserEmail = sharedPreferences.getString("Email", "");

        firebaseFirestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);

        adapter=new Adapter(this,myList);
        PlaceList.setAdapter(adapter);

        PlaceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i=new Intent(HomeActivity.this,DataListActivity.class);
                i.putExtra("img",myList.get(position).getImg());
                i.putExtra("tit",myList.get(position).getTitle());
                i.putExtra("dsc",myList.get(position).getDes());
                i.putExtra("add",myList.get(position).getAddress());
                startActivity(i);

            }
        });

       // loadList();


        TakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(HomeActivity.this,AddDataActivity.class));
                Intent i=new Intent(HomeActivity.this,AddDataActivity.class);
                startActivity(i);
            }
        });
        TakeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,MapMarkerActivity.class));
            }
        });
        Signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sharedPreferences = getSharedPreferences("Main", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();

                editor.putInt("Key",0);
                editor.clear();
                editor.apply();
                finish();
               // startActivity(new Intent(HomeActivity.this,LoginActivity.class));

                Toast.makeText(getApplicationContext(),"Logout Success",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadList() {

        progressDialog.show();
        progressDialog.setMessage("Loading...");

        placeModel=new PlaceModel("cfbk","cvj","fcbhm","fcbh",12.213456,76.2435);
        myList.add(placeModel);
        myList.removeAll(myList);

        firebaseFirestore.collection("UserUploadData").whereEqualTo("UserEmail",UserEmail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                        placeModel=new PlaceModel(UTitle,UDes,UAddress,UImg,ULat,ULng);
                        myList.add(placeModel);

                    }

                    adapter.notifyDataSetChanged();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private  void  permissionMethod()
    {
        Dexter.withContext(HomeActivity.this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                /* ... */
                report.areAllPermissionsGranted();


            }
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                /* ... */

                token.continuePermissionRequest();

            }
        }).check();
    }
}
