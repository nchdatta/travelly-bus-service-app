package com.example.busserviceapp.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appolica.interactiveinfowindow.InfoWindow;
import com.appolica.interactiveinfowindow.InfoWindowManager;
import com.appolica.interactiveinfowindow.fragment.MapInfoWindowFragment;
import com.example.busserviceapp.APIClient.APIClient;
import com.example.busserviceapp.APIClient.DirectionApiInterface;
import com.example.busserviceapp.DirectionApiModel.DirectionResponse;
import com.example.busserviceapp.DirectionApiModel.OverviewPolyline;
import com.example.busserviceapp.R;
import com.example.busserviceapp.modelclass.DriverInformation;
import com.example.busserviceapp.modelclass.Parcel;
import com.example.busserviceapp.modelclass.UserInformation;
import com.example.busserviceapp.registration.loginActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;
import com.squareup.picasso.Picasso;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RideStartActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        InfoWindowManager.WindowShowListener{

    private static final String TAG = "RideStartActivity";

    private TextView nav_userName;
    private ImageView nav_userPhoto;

    FirebaseUser currentUser;
    FirebaseAuth firebaseAuth;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    InfoWindowManager windowManager;
    MapInfoWindowFragment mapFragment;

    private static final float DEFAULT_ZOOM = 17f;

    Marker destinationMarker,startMarker,busMarker;
    String buskey="abc";

    private BottomSheetBehavior bottomSheetBehavior;
    View reportSheet;ImageView fourdot,driverImage;
    TextView driverName,busNAme,driverLic,vehicleLic,driverExp,vehicleExp,driverphnoe;
    Parcel parcel;

    Retrofit retrofitInstance1 = APIClient.instance();
    final DirectionApiInterface directionApiInterface= retrofitInstance1.create(DirectionApiInterface.class);
    OverviewPolyline overviewPolyline;
    Polyline polyline;List<Polyline> lineList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_start);

        Intent intent=getIntent();
        parcel=intent.getParcelableExtra("parcel");

        drawerLayout=findViewById(R.id.drawer_layout_ridestart_activity_xml);
        navigationView = findViewById(R.id.nav_view_ridestart_activity);
        toolbar = findViewById(R.id.toolbar_xml);

        firebaseAuth=FirebaseAuth.getInstance();
        currentUser=firebaseAuth.getCurrentUser();


        mapFragment = (MapInfoWindowFragment) getSupportFragmentManager().findFragmentById(R.id.map_ridestart_activity);
        mapFragment.getMapAsync(this);

        windowManager=mapFragment.infoWindowManager();
        windowManager.setHideOnFling(true);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.logout_menu_xml:
                        firebaseAuth.signOut();
                        Intent intent=new Intent(RideStartActivity.this, loginActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(RideStartActivity.this, "Logged Out Successfully", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.my_account_menu_xml:
                        Toast.makeText(RideStartActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.journey_info_menu_xml:
                        Toast.makeText(RideStartActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.about_us_menu_xml:
                        Toast.makeText(RideStartActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.share2:
                        Toast.makeText(RideStartActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.share3:
                        Toast.makeText(RideStartActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        setSupportActionBar(toolbar);

        drawerToggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buildGoogleApiClient();

        reportSheet=findViewById(R.id.bottom_sheet);
        bottomSheetBehavior=BottomSheetBehavior.from(reportSheet);
        fourdot =reportSheet.findViewById(R.id.four_dot_bottomsheet);
        updateHeaderInformation(parcel.getKey());
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i){
                    case 1:
                        fourdot.setVisibility(View.INVISIBLE);
                        Log.d(TAG, "onStateChanged:bottomSheetBehavior "+bottomSheetBehavior.getState());
                        break;
                    case 2:
                        fourdot.setVisibility(View.INVISIBLE);
                        Log.d(TAG, "onStateChanged:bottomSheetBehavior "+bottomSheetBehavior.getState());
                        break;
                    case 3:
                        fourdot.setVisibility(View.INVISIBLE);
                        Log.d(TAG, "onStateChanged:bottomSheetBehavior "+bottomSheetBehavior.getState());
                        break;
                    case 4:
                        fourdot.setVisibility(View.VISIBLE);
                        Log.d(TAG, "onStateChanged:bottomSheetBehavior "+bottomSheetBehavior.getState());
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float slideOffset) {

            }
        });

    }//End of onCreate

    protected  synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: Called");
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

        mMap.setMyLocationEnabled(false);
        //mMap.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        startMarker=mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker))
                .title("Start Location")
                .position(parcel.getStart()) );

        destinationMarker=mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker))
                .title("Destination Location")
                .position(parcel.getDestination()));

        //Puting bus marker
        busMarker=mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_marker))
                .title(parcel.getBusName())
                .position(parcel.getBusLatlng()));

        getDirectionInformation(startMarker.getPosition(),destinationMarker.getPosition());

        moveCamera(startMarker.getPosition());

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("availableDriver").child(buskey).child("l");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Log.d(TAG, "onDataChange: putBusMarker called");
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng buslatlng = new LatLng(locationLat,locationLng);
                    if(busMarker!=null){
                        busMarker.setPosition(buslatlng);
                    }
                        /*markerList.get(busList.getKey()).setPosition(buslatlng);

                        getDistanceInformation(mLastLocation,buslatlng,reachingTime);
                        Log.d(TAG, "onDataChange: distance = "+distanceMeasure);
                        if(distanceMeasure!=0 && distanceMeasure<=500 && !callForDialog){
                            showUpdateDialog(key);   //showing dialog
                        }*/
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }//End Of onMapReady

    public void getDirectionInformation(LatLng latLng1,LatLng latLng2){

        String lat2= String.valueOf(latLng2.latitude);
        String long2= String.valueOf(latLng2.longitude);

        String lat1= String.valueOf(latLng1.latitude);
        String long1= String.valueOf(latLng1.longitude);

        Map<String, String> mapQuery = new HashMap<>();
        mapQuery.put("key","AIzaSyBXzTJ4tsJOnGHcrDCR4GszzeI5spUp8mg");
        mapQuery.put("origin", lat1+","+long1);
        mapQuery.put("destination", lat2+","+long2);

        //mMap.addMarker(new MarkerOptions().position(new LatLng(23.7512191,90.3783289)).title("Start"));
        // mMap.addMarker(new MarkerOptions().position(new LatLng(23.7560154,90.3741217)).title("End"));


        Call<DirectionResponse> call=directionApiInterface.getDirectionInfo(mapQuery);
        call.enqueue(new Callback<DirectionResponse>() {
            @Override
            public void onResponse(Call<DirectionResponse> call, Response<DirectionResponse> response) {
                Toast.makeText(RideStartActivity.this, "Response body"+response, Toast.LENGTH_SHORT).show();


                DirectionResponse directionResponse=response.body();

                overviewPolyline=directionResponse.getRoutes().get(0).getOverviewPolyline();
                if(overviewPolyline!= null){

                    List<LatLng> latLngList= PolyUtil.decode(overviewPolyline.getPoints());
                    Log.d(TAG, "onResponse: latlngList Size=>"+latLngList.size());

                    if(!lineList.isEmpty()){
                        lineList.clear();
                    }
                    for(int k=0;k<latLngList.size()-1;k++){
                        LatLng origin=latLngList.get(k);
                        LatLng destination=latLngList.get(k+1);
                        polyline=mMap.addPolyline(new PolylineOptions().color(R.color.ash).geodesic(true).add(
                                new LatLng(origin.latitude,origin.longitude),
                                new LatLng(destination.latitude,destination.longitude)).width(12));
                        lineList.add(polyline);
                    }

                } else {
                    Log.d(TAG, "onResponse:overviewPolyline = null ");
                }
            }
            @Override
            public void onFailure(Call<DirectionResponse> call, Throwable t) {

            }
        });
    }

    public void moveCamera(LatLng latLng ){
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: updating location...");

        mLastLocation=location;
        final LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest=new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(3000);
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


    private  void updateHeaderInformation(String key){
        String uid=currentUser.getUid();
        DatabaseReference mRef= FirebaseDatabase.getInstance().getReference("userlist").child(uid);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserInformation uf=dataSnapshot.getValue(UserInformation.class);
                updateUI(uf);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference mRef2= FirebaseDatabase.getInstance().getReference("driverlist").child(key);
        mRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DriverInformation df=dataSnapshot.getValue(DriverInformation.class);
                updateBottomSheet(df);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private  void updateBottomSheet(DriverInformation df){
        driverImage=reportSheet.findViewById(R.id.driver_image_bottomSheet);
        driverName=reportSheet.findViewById(R.id.driver_name_bottomSheet);
        busNAme=reportSheet.findViewById(R.id.bus_name_bottomSheet);
        driverLic=reportSheet.findViewById(R.id.driving_license_bottomsheet);
        vehicleLic=reportSheet.findViewById(R.id.vehicle_license_bottomsheet);
        driverExp=reportSheet.findViewById(R.id.driving_license_exp_bottomsheet);
        vehicleExp=reportSheet.findViewById(R.id.vehicle_license_exp_bottomsheet);
        driverphnoe=reportSheet.findViewById(R.id.driver_phn_bottomsheet);

        Uri uri=Uri.parse(df.getDriverPictureUri());
        Picasso.with(RideStartActivity.this).load(uri).fit().centerCrop().into(driverImage);
        driverName.setText(df.getDriverName());
        busNAme.setText(df.getBusCatagory());
        driverLic.setText(df.getDriverLicenseNumber());
        driverExp.setText(df.getDriverLicenseExpireDate());
        vehicleLic.setText(df.getVehicleLicenseNumber());
        vehicleExp.setText(df.getVehicleLicenseExpiryDate());
        driverphnoe.setText(df.getDriverPhoneNumber());

    }

    private  void updateUI(UserInformation uf){
        Log.d(TAG,"=>"+uf.getUserName()+"\n=>"+uf.getUserProfiePictureUri());
        NavigationView nv=findViewById(R.id.nav_view_ridestart_activity);
        View header=nv.getHeaderView(0);
        nav_userName = header.findViewById(R.id.nav_userName_xml);
        nav_userPhoto=header.findViewById(R.id.nav_userPic_xml);

        nav_userName.setText(uf.getUserName());
        Uri uri=Uri.parse(uf.getUserProfiePictureUri());
        Picasso.with(RideStartActivity.this).load(uri).fit().centerCrop().into(nav_userPhoto);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onWindowShowStarted(@NonNull InfoWindow infoWindow) {

    }

    @Override
    public void onWindowShown(@NonNull InfoWindow infoWindow) {

    }

    @Override
    public void onWindowHideStarted(@NonNull InfoWindow infoWindow) {

    }

    @Override
    public void onWindowHidden(@NonNull InfoWindow infoWindow) {

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(RideStartActivity.this,HomeActivity.class);
        finish();
        startActivity(intent);
    }
}//End of Code
