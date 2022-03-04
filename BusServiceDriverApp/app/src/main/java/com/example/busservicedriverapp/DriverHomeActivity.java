package com.example.busservicedriverapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.busservicedriverapp.modelclass.DriverInformation;
import com.example.busservicedriverapp.modelclass.ReportModel;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.firebase_auth.zzex;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.zzy;
import com.google.firebase.auth.zzz;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DriverHomeActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{

    private static final String TAG = "DriverHomeActivity";

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    private static final float DEFAULT_ZOOM = 18f;


    private ImageView logout,driverPicture,drag_sign;
    private TextView driverName,driverContact,driverEmail,driverNID;
    private Toolbar toolbar;TextView toolbarTitle;

    private BottomSheetBehavior bottomSheetBehavior;

    RecyclerView recyclerView;
    List<ReportModel> list=new ArrayList<>();

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String uid;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toast.makeText(this, "onCreate called.", Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        logout=findViewById(R.id.logout_img_xml);
        driverPicture=findViewById(R.id.driverPhoto);
        driverName=findViewById(R.id.driverName_xml);
        driverContact=findViewById(R.id.driverContactNumber_xml);
        driverEmail=findViewById(R.id.driverEmail_xml);
        driverNID=findViewById(R.id.driverNID_xml);
        recyclerView=findViewById(R.id.recyclerview_xml);
        drag_sign=findViewById(R.id.drag_sign_xml);
        toolbarTitle=findViewById(R.id.toolbar_title);

        View reportSheet=findViewById(R.id.bottom_sheet);
        bottomSheetBehavior=BottomSheetBehavior.from(reportSheet);


        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                Log.d(TAG, "onStateChanged: "+i);
                switch (i){
                    case 1:
                        toolbarTitle.setText("Driver Details");
                        logout.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        //toolbarTitle.setText("Reports");
                        drag_sign.setImageResource(R.drawable.drag_list_down);
                        break;
                    case 3:
                        toolbarTitle.setText("Reports");
                        logout.setVisibility(View.INVISIBLE);
                        break;
                    case 4:
                        toolbarTitle.setText("Driver Details");
                        logout.setVisibility(View.VISIBLE);
                        drag_sign.setImageResource(R.drawable.drag_list_up);
                        break;
                }


                Toast.makeText(DriverHomeActivity.this, "state tosat:"+i, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });


        RecyclerViewAdapter adapter=new RecyclerViewAdapter(list,getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.Drivermap);
        mapFragment.getMapAsync(this);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent=new Intent(DriverHomeActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

    }//End of onCreate

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);

        /*  // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    protected  synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: updating location...");
        mLastLocation=location;
        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));

        String uid=mAuth.getCurrentUser().getUid();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("availableDriver");

        GeoFire geoFire=new GeoFire(ref);
        geoFire.setLocation(uid, new GeoLocation(location.getLatitude(), location.getLongitude()),
                new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        //do nothing
                    }
                });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest=new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);//can be changed based on need.
        //permission check missing 9:28
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Called");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        String userId =uid;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("availableDriver");

        //ref.child(userId).removeValue();
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId, new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                Log.d(TAG, "onComplete: location removed");
                //do nothing
            }
        });
    }


    private void getUserInformation(FirebaseUser user){
        String uid=user.getUid();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("driverlist").child(uid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DriverInformation df=dataSnapshot.getValue(DriverInformation.class);
                updateUI(df);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //do nothing
            }
        });
    }



    private void updateUI(DriverInformation driver){
        driverName.setText(driver.getDriverName());
        driverContact.setText(driver.getDriverPhoneNumber());
        driverEmail.setText(driver.getDriverEmail());
        driverNID.setText(driver.getDriverNID());

        Uri uri=Uri.parse(driver.getDriverPictureUri());
        Picasso.with(DriverHomeActivity.this).load(uri).fit().centerCrop().into(driverPicture);
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

       /* FirebaseUser user=new FirebaseUser() {
            @NonNull
            @Override
            public String getUid() {
                return "xyz";
            }

            @NonNull
            @Override
            public String getProviderId() {
                return null;
            }

            @Override
            public boolean isAnonymous() {
                return false;
            }

            @Nullable
            @Override
            public List<String> zza() {
                return null;
            }

            @NonNull
            @Override
            public List<? extends UserInfo> getProviderData() {
                return null;
            }

            @NonNull
            @Override
            public FirebaseUser zza(@NonNull List<? extends UserInfo> list) {
                return null;
            }

            @Override
            public FirebaseUser zzb() {
                return null;
            }

            @NonNull
            @Override
            public FirebaseApp zzc() {
                return null;
            }

            @Nullable
            @Override
            public String getDisplayName() {
                return null;
            }

            @Nullable
            @Override
            public Uri getPhotoUrl() {
                return null;
            }

            @Nullable
            @Override
            public String getEmail() {
                return null;
            }

            @Nullable
            @Override
            public String getPhoneNumber() {
                return null;
            }

            @Nullable
            @Override
            public String zzd() {
                return null;
            }

            @NonNull
            @Override
            public zzex zze() {
                return null;
            }

            @Override
            public void zza(@NonNull zzex zzex) {

            }

            @NonNull
            @Override
            public String zzf() {
                return null;
            }

            @NonNull
            @Override
            public String zzg() {
                return null;
            }

            @Nullable
            @Override
            public FirebaseUserMetadata getMetadata() {
                return null;
            }

            @NonNull
            @Override
            public zzz zzh() {
                return null;
            }

            @Override
            public void zzb(List<zzy> list) {

            }

            @Override
            public void writeToParcel(Parcel parcel, int i) {

            }

            @Override
            public boolean isEmailVerified() {
                return false;
            }
        };*/

        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();


        getUserInformation(currentUser);
        uid=currentUser.getUid();



        list.add(new ReportModel("22/02/2019",getString(R.string.r1),"Rajibul Haque"));
        list.add(new ReportModel("22/02/2019",getString(R.string.r2),"Noyon Dotta"));
        list.add(new ReportModel("22/02/2019",getString(R.string.r1),"Toufiq Hasan"));
        list.add(new ReportModel("22/02/2019",getString(R.string.r2),"Umme Habiba"));
        list.add(new ReportModel("22/02/2019",getString(R.string.r2),"Usha RAhman"));
    }

    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}//End of Code
