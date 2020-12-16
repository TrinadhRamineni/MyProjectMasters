package com.example.myproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MapMarkerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    PlaceModel placeModel;

    FirebaseFirestore firebaseFirestore;
    ProgressDialog progressDialog;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String UserEmail;

    ArrayList<Object> AllList=new ArrayList<>();
    Marker marker;

    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        mapFragment.getMapAsync(this);

        sharedPreferences = getSharedPreferences("Main", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        UserEmail = sharedPreferences.getString("Email", "");

        firebaseFirestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(true);

        
        loadPlaces();
    }

    private void loadPlaces() {

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
                        AllList.add(placeModel);


                        for( i=0;i<AllList.size();i++)
                        {


                            LatLng latLng = new LatLng(placeModel.getLat(), placeModel.getLng());
                            marker=mMap.addMarker(new MarkerOptions().position(latLng).title(placeModel.getTitle()));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f));


                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker1) {

                                    Toast.makeText(getApplicationContext(),""+marker1.getTitle(),Toast.LENGTH_SHORT).show();

                                    Intent i=new Intent(MapMarkerActivity.this,DataList2Activity.class);
                                    i.putExtra("Key",marker1.getTitle());
                                    i.putExtra("email",UserEmail);
                                    startActivity(i);


                                    return false;
                                }
                            });
                        }

                    }

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

       /* LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

    }

    /*  GeoPoint geoPoint=qb.getGeoPoint("MyLatLng");
                        double lat=geoPoint.getLatitude();
                        double lng=geoPoint.getLongitude();

                        LatLng latLng=new LatLng(lat,lng);*/





    //  Toast.makeText(getApplicationContext(),""+UTitle+""+latLng,Toast.LENGTH_SHORT).show();
}
