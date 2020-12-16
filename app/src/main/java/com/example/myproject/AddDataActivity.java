package com.example.myproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.Cap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddDataActivity extends AppCompatActivity {

    ImageView CaptureImage;
    EditText PlaceTitle, PlaceDescription, PlaceAddress;

    Button Submit;

    String TitleStr, DescStr, AddressStr;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseFirestore firebaseFirestore;
    Bitmap mphoto;


    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int LOCATION_CODE = 104;
    public static final int GALLERY_REQUEST_CODE = 105;


    public double latitude;
    public double longitude;
    public LocationManager locationManager;

    LatLng myCordinates;
    ProgressDialog progressDialog;

    Uri  contentUri;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String UserEmail;

    String currentPhotoPath;
    File f1;
    GpsTracker gpsTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);

        CaptureImage = findViewById(R.id.Captured_Image);
        PlaceTitle = findViewById(R.id.PlaceTitle);
        PlaceDescription = findViewById(R.id.PlaceDescription);
        PlaceAddress = findViewById(R.id.PlaceAddress);
        Submit = findViewById(R.id.Submit);

        sharedPreferences = getSharedPreferences("Main", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        UserEmail = sharedPreferences.getString("Email", "");

        firebaseFirestore = FirebaseFirestore.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);


        CaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dispatchTakePictureIntent();
            }
        });

        //requestLocation();
        requestLoc1();

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImageToFirebase(f1.getName(), contentUri);
            }
        });

    }

    private  void requestLoc1()
    {
        gpsTracker = new GpsTracker(AddDataActivity.this);

        if(gpsTracker.canGetLocation()){
             latitude = gpsTracker.getLatitude();
             longitude = gpsTracker.getLongitude();
            Toast.makeText(getApplicationContext(),""+latitude+"\n"+longitude,Toast.LENGTH_SHORT).show();

            Geocoder geocoder=new Geocoder(AddDataActivity.this, Locale.getDefault());
            try {
                List<Address> addresses=geocoder.getFromLocation(latitude,longitude,1);


                if(addresses.size()>0)
                {
                    String address=addresses.get(0).getAddressLine(0);
                    String CityName=addresses.get(0).getLocality();
                    String Postal=addresses.get(0).getPostalCode();
                    String Country=addresses.get(0).getCountryName();
                    String Area=addresses.get(0).getSubAdminArea();
                    String AdminArea=addresses.get(0).getAdminArea();

                    PlaceAddress.setText(address+","+CityName+","+Postal+","+Country+","+Area+","+AdminArea);

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No location Found",Toast.LENGTH_SHORT).show();
                }




            } catch (IOException e) {
                e.printStackTrace();
            }


        }else{
            gpsTracker.showSettingsAlert();
        }
    }

   /* private void askCameraPermissions() {
        if (Build.VERSION.SDK_INT >= 23)
        {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
            }
            else {
                dispatchTakePictureIntent();
            }

        }

    }

    private void askCameraPermissions1() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_CODE);
        }
        else {
            dispatchTakePictureIntent();
        }
    }*/

    /* @Override
     public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
         if(requestCode == CAMERA_PERM_CODE){
             if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // dispatchTakePictureIntent();
             }else {
                 Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
             }
         }
        *//*if(requestCode == LOCATION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //dispatchTakePictureIntent();
            }else {
                Toast.makeText(this, "Locaton Permission is Required to get Location", Toast.LENGTH_SHORT).show();
            }
        }*//*
    }
*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {


                f1 = new File(currentPhotoPath);
               // CaptureImage.setImageURI(Uri.fromFile(f1));
                Glide.with(AddDataActivity.this).load(Uri.fromFile(f1)).centerCrop().placeholder(R.drawable.ic_account_circle_black_24dp).into(CaptureImage);

                Log.d("tag", "ABsolute Url of Image is " + Uri.fromFile(f1));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                contentUri = Uri.fromFile(f1);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);




            }

        }

        if (requestCode == LOCATION_CODE) {
            if (resultCode == Activity.RESULT_OK) {


                // Toast.makeText(getApplicationContext(),"Premission Granted",Toast.LENGTH_SHORT).show();


            }

        }


    }



    private void uploadImageToFirebase(String name,Uri contentUri) {
        progressDialog.show();
        progressDialog.setMessage("Loading...");

        TitleStr=PlaceTitle.getText().toString();
        DescStr=PlaceDescription.getText().toString();
        AddressStr=PlaceAddress.getText().toString();
        if (TextUtils.isEmpty(TitleStr)) {
            PlaceTitle.setError("Enter title");
            progressDialog.dismiss();
            return;
        }
        if (TextUtils.isEmpty(DescStr)) {
            PlaceDescription.setError("Enter Description");
            progressDialog.dismiss();
            return;
        }

        if (TextUtils.isEmpty(AddressStr)) {
            PlaceDescription.setError("Add Address");
            progressDialog.dismiss();
            return;
        } else {

        final StorageReference image = storageReference.child("pictures/" + name);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Log.d("tag", "onSuccess: Uploaded Image URl is " + uri.toString());

                        progressDialog.show();
                        progressDialog.setMessage("Please wait...");
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("UserImage", uri.toString());
                        hashMap.put("UserEmail", UserEmail);
                        hashMap.put("Lat",latitude);
                        hashMap.put("Lng",longitude);

                        hashMap.put("UserTitle", TitleStr);
                        hashMap.put("UserDesc", DescStr);
                        hashMap.put("UserAddress", AddressStr);


                        GeoPoint geoPoint=new GeoPoint(latitude,longitude);
                        hashMap.put("MyLatLng",geoPoint);

                        firebaseFirestore.collection("UserUploadData").add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                progressDialog.dismiss();

                                if (task.isSuccessful()) {
                                    Toast.makeText(AddDataActivity.this, " Success.", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(new Intent(AddDataActivity.this,HomeActivity.class));
                                    return;
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();

                            }
                        });
                    }
                });

                //Toast.makeText(AddDataActivity.this, " Uploaded.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(AddDataActivity.this, "Upload Failled.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d("MyTag", "" + ex.getMessage());

                Toast.makeText(getApplicationContext(), "" + ex, Toast.LENGTH_SHORT).show();

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.myproject.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }









}