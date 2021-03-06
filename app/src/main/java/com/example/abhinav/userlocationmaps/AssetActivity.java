package com.example.abhinav.userlocationmaps;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class AssetActivity extends AppCompatActivity {

    ImageView imageView;
    TextView latitude;
    TextView longitude;
    EditText nameEditText;
    Button saveButton;
    LocationManager locationManager;
    LocationListener locationListener;

    public void getPhoto() {

        Intent in = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(in, 1);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                getPhoto();


            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset);

        imageView=(ImageView) findViewById(R.id.imageView);
        latitude=findViewById(R.id.latTextView);
        saveButton=findViewById(R.id.saveButton);
        longitude=findViewById(R.id.longTextView);
        nameEditText = findViewById(R.id.nameEditText);
        latitude.setText(String.format("%.3f", 28.610));
        longitude.setText(String.format("%.3f", 77.037));

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(AssetActivity.this, MainActivity.class);
                startActivity(myIntent);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(nameEditText.getText().toString());

                myRef.setValue("Latitude: "+latitude.getText().toString() + "  " + "Longitude: " + longitude.getText().toString());
                Toast.makeText(AssetActivity.this,"Your Asset has been saved",Toast.LENGTH_LONG).show();
            }
        });

        final ProgressDialog progressDoalog;
        progressDoalog = new ProgressDialog(AssetActivity.this);
        progressDoalog.setMax(100);
        progressDoalog.setMessage("Please wait...");
        progressDoalog.setTitle("Fetching location");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // show it
        //progressDoalog.show();
        locationManager=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("Latitude",String.valueOf(location.getLatitude()));
                Log.i("Longitude",String.valueOf(location.getLongitude()));
                latitude.setText(String.format("%.3f", location.getLatitude()));
                longitude.setText(String.format("%.3f", location.getLongitude()));
                //progressDoalog.dismiss();

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.i("Location",s);
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.i("Location",s);

            }

            @Override
            public void onProviderDisabled(String s) {
                Log.i("Location",s);

            }
        };
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
        else{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            getPhoto();
        }




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if(requestCode==1 && resultCode==RESULT_OK && data!=null ){

            Uri image=data.getData();

            try {

                Bitmap bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(),image);

                int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);

                imageView.setImageBitmap(scaled);

                Log.i("work","done");

            } catch (IOException e) {

                e.printStackTrace();

            }
        }
    }
}
