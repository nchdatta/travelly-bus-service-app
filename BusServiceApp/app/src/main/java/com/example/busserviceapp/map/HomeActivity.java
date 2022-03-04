package com.example.busserviceapp.map;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import com.appolica.interactiveinfowindow.InfoWindow;
import com.appolica.interactiveinfowindow.InfoWindowManager;
import com.appolica.interactiveinfowindow.fragment.MapInfoWindowFragment;
import com.example.busserviceapp.APIClient.APIClient;
import com.example.busserviceapp.APIClient.DirectionApiInterface;
import com.example.busserviceapp.APIClient.DistanceApiInterface;
import com.example.busserviceapp.DirectionApiModel.OverviewPolyline;
import com.example.busserviceapp.DistanceMatrixApiModel.DistanceResponse;
import com.example.busserviceapp.DistanceMatrixApiModel.Element;
import com.example.busserviceapp.R;
import com.example.busserviceapp.modelclass.BusList;
import com.example.busserviceapp.modelclass.DriverInformation;
import com.example.busserviceapp.modelclass.Parcel;
import com.example.busserviceapp.modelclass.SearchBody;
import com.example.busserviceapp.modelclass.UserInformation;
import com.example.busserviceapp.registration.loginActivity;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.view.View.VISIBLE;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        InfoWindowManager.WindowShowListener,AdapterCallBack{

    private  static String TAG="logHomeActivity";
    public static final int AUTOCOMPLELE_REQUEST_CODE=1;

    TextView nav_userName,searchPlace,adjustDestination;
    ImageView nav_userPhoto,centerLocation;
    Button btn1, btn2, btn3,toggle_drawer;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;


    FirebaseUser currentUser;
    FirebaseAuth firebaseAuth;
    DatabaseReference mRef;

    private static GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private static Location mLastLocation;
    private LocationRequest mLocationRequest;
    private static final float DEFAULT_ZOOM =17f;

    private ArrayList<String> availBusList=new ArrayList<>();
    private HashMap<String,Marker> markerList=new HashMap<>();
    //private HashMap<String,Boolean> removeList=new HashMap<>();
    Marker marker,userMarker,destinationMarker;
    int i,movecamera;

    InfoWindow window;
    InfoWindowManager windowManager;
    MapInfoWindowFragment mapFragment;

    Boolean callForNearbyBus =false, callForDialog =false,doubleBackToExitPressedOnce = false;

    PlacesClient placesClient;
    Place place;

    Retrofit retrofitInstance2= APIClient.instance();
    final DistanceApiInterface distanceApiInterface=retrofitInstance2.create(DistanceApiInterface.class);
    int distanceMeasure=0;


    private ListView searchHistoryListView;
    List<SearchBody> searchHistory;


    LinearLayout search_portion,availablebus_portion;
    List<BusList> list_buses;
    DriverInformation df; //used inside putMarker method
    RecyclerView recyclerView;
    HashMap<String,String> reachingTimeList=new HashMap<>();

/*    private BottomSheetBehavior bottomSheetBehavior;
    LinearLayout drivermore;
    TextView more_tv;*/

    RelativeLayout reachingTimeLayout;
    TextView tv,reachingTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: CALLED");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        movecamera=0;
        //setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        //getWindow().setStatusBarColor(Color.TRANSPARENT);
        //getWindow().setStatusBarColor(Color.parseColor("#20111111"));
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        drawerLayout=findViewById(R.id.drawer_layout_xml);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar_xml);

        search_portion=findViewById(R.id.search_portion);//search_portion.setVisibility(View.INVISIBLE);
        availablebus_portion=findViewById(R.id.availablebus_portion);availablebus_portion.setVisibility(View.INVISIBLE);
        reachingTimeLayout=findViewById(R.id.reachingtime_layout);reachingTimeLayout.setVisibility(View.INVISIBLE);

        reachingTime=findViewById(R.id.reachingtime_xmlX);

        centerLocation=findViewById(R.id.move_camera);
        searchPlace =findViewById(R.id.whereto);
        toggle_drawer=findViewById(R.id.toggle_drawer);

        searchHistoryListView =findViewById(R.id.search_history_xml);
        searchHistory=new ArrayList<>();

        setSupportActionBar(toolbar);
        movecamera=0;

        firebaseAuth=FirebaseAuth.getInstance();
        /*FirebaseUser user=new FirebaseUser() {
            @NonNull
            @Override
            public String getUid() {
                return "lDThhobnMDOLZ1QARIdHA8Eas8F3";
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
            public zzew zze() {
                return null;
            }

            @Override
            public void zza(@NonNull zzew zzew) {

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
        }; */ //for testing purpase only;
        currentUser=firebaseAuth.getCurrentUser();   /*user;*/
        updateHeaderInformation();

        mapFragment = (MapInfoWindowFragment) getSupportFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

        windowManager=mapFragment.infoWindowManager();
        windowManager.setHideOnFling(true);

        recyclerView=findViewById(R.id.recyclerview);

        btn1=findViewById(R.id.btn_test);btn1.setText("Change Destination");;btn1.setVisibility(View.INVISIBLE);
        adjustDestination=findViewById(R.id.adjustLocation);adjustDestination.setVisibility(View.INVISIBLE);

/*        final View reportSheet=findViewById(R.id.bottom_sheet);
        bottomSheetBehavior=BottomSheetBehavior.from(reportSheet);
        more_tv=findViewById(R.id.more);
        drivermore=findViewById(R.id.drivermore);drivermore.setVisibility(View.INVISIBLE);
        reportSheet.setVisibility(View.INVISIBLE);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i){
                    case 1:
                        Log.d(TAG, "onStateChanged:bottomSheetBehavior "+bottomSheetBehavior.getState());
                        drivermore.setVisibility(VISIBLE);
                        more_tv.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        drivermore.setVisibility(VISIBLE);
                        more_tv.setVisibility(View.INVISIBLE);
                        Log.d(TAG, "onStateChanged:bottomSheetBehavior "+bottomSheetBehavior.getState());
                        break;
                    case 3:
                        drivermore.setVisibility(VISIBLE);
                        more_tv.setVisibility(View.INVISIBLE);
                        Log.d(TAG, "onStateChanged:bottomSheetBehavior "+bottomSheetBehavior.getState());
                        break;
                    case 4:
                        drivermore.setVisibility(View.INVISIBLE);
                        more_tv.setVisibility(VISIBLE);
                        Log.d(TAG, "onStateChanged:bottomSheetBehavior "+bottomSheetBehavior.getState());
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float slideOffset) {

            }
        });*/  //bottomsheet part


        //================initialization ends here============================\\


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_portion.setVisibility(VISIBLE);
                availablebus_portion.setVisibility(View.INVISIBLE);
                btn1.setVisibility(View.INVISIBLE);

                callForNearbyBus=false;
                Toast.makeText(HomeActivity.this, "before markerList size="+markerList.size(), Toast.LENGTH_SHORT).show();
                Set s=markerList.entrySet();
                Iterator iterator=s.iterator();
                while(iterator.hasNext()){
                    Map.Entry me=(Map.Entry)iterator.next();
                    marker=(Marker)me.getValue();
                    marker.remove();
                }
                destinationMarker.remove();destinationMarker=null;

                Toast.makeText(HomeActivity.this, "after markerList size="+markerList.size(), Toast.LENGTH_SHORT).show();
                availBusList.clear();
                markerList.clear();
            }
        });


        toggle_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.logout_menu_xml:
                        firebaseAuth.signOut();
                        Intent intent=new Intent(HomeActivity.this, loginActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(HomeActivity.this, "Logged Out Successfully", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.my_account_menu_xml:
                        Intent i = new Intent(HomeActivity.this, MyAccount.class);
                        startActivity(i);
                        Toast.makeText(HomeActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.journey_info_menu_xml:
                        Toast.makeText(HomeActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.about_us_menu_xml:
                        Toast.makeText(HomeActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.share2:
                        Toast.makeText(HomeActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.share3:
                        Toast.makeText(HomeActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                        break;
                }
                        drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        //getting remove list
        DatabaseReference updateMarkerlistRef=FirebaseDatabase.getInstance().getReference("removeListRef").child(currentUser.getUid());
        updateMarkerlistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: for removeListRef");
              if(dataSnapshot.exists()){
                  for(DataSnapshot ds:dataSnapshot.getChildren()){
                      String removeKey=ds.getValue(String.class);
                      Log.d(TAG, "onDataChange: getting remove list =>"+removeKey);
                      removeMarker(removeKey);
                  }
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Do Nothing
            }
        });

        //showing search History
        DatabaseReference ref_searchHistory=FirebaseDatabase.getInstance().getReference("searchhistory").child(currentUser.getUid());
        ref_searchHistory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null){
                    searchHistory.clear();
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                        SearchBody searchBody=ds.getValue(SearchBody.class);
                        searchHistory.add(searchBody);
                    }
                    SearchListAdapter searchListAdapter=new SearchListAdapter(HomeActivity.this,searchHistory);
                    searchHistoryListView.setAdapter(searchListAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Do Nothing.
            }
        });


        //getting available bus list for recyclerview
        list_buses=new ArrayList<>();
        DatabaseReference ref_availableBusList=FirebaseDatabase.getInstance().getReference("buslist").child(currentUser.getUid());
        ref_availableBusList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
       /***/        if(callForNearbyBus){
                    if(dataSnapshot!=null){
                        list_buses.clear();
                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                            BusList busList=ds.getValue(BusList.class);
                            list_buses.add(busList);
                        }
                        BusListAdapter busListAdapter=new BusListAdapter(HomeActivity.this,list_buses,HomeActivity.this);
                        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                        recyclerView.setAdapter(busListAdapter);
                    }
                }else{
                    DatabaseReference ref_availableBusList=FirebaseDatabase.getInstance().getReference("buslist").child(currentUser.getUid());
                    ref_availableBusList.removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        drawerToggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buildGoogleApiClient();

       searchPlace.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Places.initialize(HomeActivity.this,"AIzaSyBXzTJ4tsJOnGHcrDCR4GszzeI5spUp8mg");
               placesClient=Places.createClient(HomeActivity.this);
               List<Place.Field> fields= Arrays.asList(Place.Field.ID,Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS);

               Intent intent=new Autocomplete
                       .IntentBuilder(AutocompleteActivityMode.OVERLAY,fields)
                       .build(HomeActivity.this);
               startActivityForResult(intent,AUTOCOMPLELE_REQUEST_CODE);
           }
       });

       centerLocation.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               moveCamera(mLastLocation);
           }
       });

        adjustDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveCamera(destinationMarker.getPosition());
                adjustDestination.setVisibility(View.INVISIBLE);
                toggle_drawer.setVisibility(VISIBLE);
                Toast.makeText(HomeActivity.this, "Drag to Adjust Location.", Toast.LENGTH_SHORT).show();
            }
        });

    }//end of onCreate

    /*public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }*/

    /*private void viewGoneAnimator(View view) {
        final View v=view;
        v.animate()
                .alpha(0f)
                .setDuration(1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        v.setVisibility(View.GONE);
                    }
                });

    }*/

    /*private void viewVisibleAnimator(View view) {
        final View v=view;
        v.animate()
                .alpha(1f)
                .setDuration(1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        v.setVisibility(VISIBLE);
                    }
                });

    }*/

    /*public void removeAllMArker(HashMap<String,Marker> List){
        Set set=List.entrySet();
        Iterator iterator=set.iterator();
        while (iterator.hasNext()){
            Map.Entry me=(Map.Entry)iterator.next();
            Marker m=(Marker)me.getValue();m.remove();
            Log.d(TAG, "removeAllMArker: "+((Marker) me.getValue()).getSnippet());
            String k=(String)me.getKey();markerList.remove(k);
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLELE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getLatLng());
                //searchPlace.setText(place.getName());
                LatLng destinationlatLng=place.getLatLng();

                adjustDestination.setVisibility(VISIBLE);
                toggle_drawer.setVisibility(View.INVISIBLE);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        adjustDestination.setVisibility(View.INVISIBLE);
                        toggle_drawer.setVisibility(VISIBLE);
                    }
                }, 5000);

                if(destinationMarker==null){
                    destinationMarker=mMap.addMarker(new MarkerOptions()
                            .position(destinationlatLng)
                            .title(place.getName())
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker)));

                }else{
                    Toast.makeText(this, "repositioned", Toast.LENGTH_SHORT).show();
                    destinationMarker.setPosition(destinationlatLng);
                }

                //pushing search history to db
                DatabaseReference ref=FirebaseDatabase.getInstance().getReference("searchhistory").child(currentUser.getUid());
                String key=ref.push().getKey();
                //Toast.makeText(this, ""+place.getAddress()+"\n"+place.getName(),Toast.LENGTH_SHORT).show();
                SearchBody sb=new SearchBody(place.getName(),place.getAddress());
                ref.child(key).setValue(sb);

                search_portion.setVisibility(View.INVISIBLE);
                availablebus_portion.setVisibility(VISIBLE);
                btn1.setVisibility(VISIBLE);

                callForNearbyBus=true;

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
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

        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
/*        windowManager.setWindowShowListener(HomeActivity.this);

        final int offsetX = (int) getResources().getDimension(R.dimen.marker_offset_x);
        final int offsetY = (int) getResources().getDimension(R.dimen.marker_offset_y);
        markerSpec = new InfoWindow.MarkerSpecification(offsetX, offsetY);*/


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                /*if(!callForNearbyBus){
                    InfoWindow infoWindow=null;

                    String key=marker.getSnippet();
                    Set set=infoWindowList.entrySet();
                    Iterator iterator=set.iterator();
                    while(iterator.hasNext()){
                        Map.Entry me=(Map.Entry)iterator.next();
                        String id=me.getKey().toString();
                        if(key.equals(id)){
                            infoWindow=(InfoWindow) me.getValue();
                            MarkerTag tag=new MarkerTag(key,marker.getPosition(),mLastLocation);
                            Bundle bundle=new Bundle();
                            bundle.putParcelable("sharedata",tag);
                            infoWindow.getWindowFragment().setArguments(bundle);
                        }
                    }

                    if (infoWindow != null) {
                        windowManager.toggle(infoWindow, true);
                    }
                }else if(callForNearbyBus){
                    Toast.makeText(HomeActivity.this, "marker clicked", Toast.LENGTH_SHORT).show();
                }*/

                return  false;
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                    destinationMarker.setPosition(marker.getPosition());
            }
        });

    }//End of onMapReady


    private void getAvailableBus(Location mLocation){

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("availableDriver");
        GeoFire geoFire=new GeoFire(reference);

        GeoQuery geoQuery=geoFire.queryAtLocation(new GeoLocation(mLocation.getLatitude(),
                mLocation.getLongitude()),2);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.d(TAG, "onKeyEntered: called");
                availBusList.add(key);
            }

            @Override
            public void onKeyExited(String key) {
                //Do Nothing
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                //Do Nothing
            }


/*            public void onGeoQueryReady() {
                Log.d(TAG, "onGeoQueryReady:availableBusList Size = "+availBusList.size()+" list=>"+availBusList+" markerlist size= "+markerList.size());
                ArrayList<String> mlist=new ArrayList<>(availBusList);
                if(!markerList.isEmpty()) {

                    Set set=markerList.entrySet();
                    Iterator iterator=set.iterator();
                    while(iterator.hasNext()){
                        Map.Entry me=(Map.Entry)iterator.next();
                        String key=me.getKey().toString();
                        Log.d(TAG, "onGeoQueryReady: updating Markerlist for key="+key);
                        if(mlist.isEmpty()){
                            removeList.put(key,false);
                        }else{
                            removeList.put(key,mlist.contains(key));
                        }
                    }
                    DatabaseReference removeListRef=FirebaseDatabase.getInstance().getReference("removeListRef");
                    removeListRef.setValue(removeList);
                    removeList.clear();
                }

                Log.d(TAG, "onGeoQueryReady: before mlist="+mlist.size()+" markarlist="+markerList.size());

                if(!mlist.isEmpty()){
                    for(i=0;i<mlist.size();i++){
                        Log.d(TAG, "onGeoQueryReady: after mlist="+mlist.size()+" markarlist="+markerList.size());
                        setMarkerLocation(mlist.get(i));
                    }
                }
                numberOfAvailableBus=availBusList.size();
                availBusList.clear();
            }*/   //onGeoQueryReady.old

            @Override
            public void onGeoQueryReady() {
                Log.d(TAG, "onGeoQueryReady:availableBusList Size = "+availBusList.size()+" list=>"+availBusList+" markerlist size= "+markerList.size());
                    ArrayList<String> mlist=new ArrayList<>(availBusList);

                        if(!markerList.isEmpty()) {

                            DatabaseReference removeListRef=FirebaseDatabase.getInstance().getReference("removeListRef").child(currentUser.getUid());

                            Set set=markerList.entrySet();
                            Iterator iterator=set.iterator();
                            while(iterator.hasNext()){
                                Map.Entry me=(Map.Entry)iterator.next();
                                String key=me.getKey().toString();
                                Log.d(TAG, "onGeoQueryReady: updating Markerlist for key="+key);
                                if(mlist.isEmpty()){
                                    Log.d(TAG, "onGeoQueryReady: mlist.isEmpty()");
                                    removeListRef.child( key ).setValue(key);
                                }else{
                                    if(!mlist.contains(key)){
                                        Log.d(TAG, "onGeoQueryReady: !mlist.contains(key)");
                                        removeListRef.child( key ).setValue(key);
                                    }
                                }
                            }
                        }

                        Log.d(TAG, "onGeoQueryReady: before mlist="+mlist.size()+" markarlist="+markerList.size());

                        if(!mlist.isEmpty()){
                            for(i=0;i<mlist.size();i++){
                                Log.d(TAG, "onGeoQueryReady: after mlist="+mlist.size()+" markarlist="+markerList.size());
                                setMarkerLocation(mlist.get(i));
                            }
                        }
                    /*numberOfAvailableBus=availBusList.size();*/
                    availBusList.clear();
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                  //Do Nothing
            }
        });

    }

    private void setMarkerLocation(String key){
        Log.d(TAG, "setMarkerLocation: called for =>"+key);
        final String key2=key;
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("availableDriver").child(key).child("l");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              if(callForNearbyBus){
                if(dataSnapshot.exists()) {
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
                    try {
                        putMarker(buslatlng,key2);
                    }catch (Exception e){
                        Log.d(TAG, "onDataChange:setMarkerLocation: exception ="+e);
                    }
                }
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                 //Do Nothing
            }
        });
    }

    void putMarker(LatLng latLng,String key){
        final String key2=key;
        final LatLng latLng2=latLng;
        Log.d(TAG, "putMarker: called1 "+markerList.containsKey(key));
        if(!markerList.containsKey(key)){
            //CustomInfoWindowAdapter adapter=new CustomInfoWindowAdapter(HomeActivity.this);
            //mMap.setInfoWindowAdapter(adapter);
            //window=new InfoWindow(marker,markerSpec,new CustomInfoWindowFrag());

            marker=mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Updating...")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_marker)) );

            markerList.put(key,marker);

            DatabaseReference r1=FirebaseDatabase.getInstance().getReference("driverlist");
            r1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                            df=ds.getValue(DriverInformation.class);
                            if(df.getUid().equals(key2)){

                                DatabaseReference ref=FirebaseDatabase.getInstance().getReference("buslist").child(currentUser.getUid());
                                String busname=df.getBusCatagory();
                                markerList.get(key2).setTitle(busname);
                                String busroute="MOhammadpur, Sankar, Jhigatola, City College,Bata Signal, Sahbag, Kolabagan, Dnammondi 32 bus Stop";
                                String reachingtime;
                                getDistanceInformation(mLastLocation,latLng2,key2);
                                if(reachingTimeList.containsKey(key2)){
                                    //Log.d(TAG, "putMarker: reachingtime (if)");
                                    reachingtime=reachingTimeList.get(key2);
                                }else{
                                    //Log.d(TAG, "putMarker: reachingtime (else)");
                                    reachingtime="N/A";
                                }
                                ref.child(key2).setValue(new BusList(busname,busroute,reachingtime,key2));
                            }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            //infoWindowList.put(key,window);
            //windowManager.show(Win);
            Log.d(TAG, "putMarker: called (if) ====>"+key);
        }else {
            Marker m=markerList.get(key);
            m.setPosition(latLng);
            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("buslist").child(currentUser.getUid()).child(key2);
            String reachingtime;
            getDistanceInformation(mLastLocation,latLng2,key2);
            if(reachingTimeList.containsKey(key2)){
                //Log.d(TAG, "putMarker: reachingtime (if) +"+reachingTimeList.get(key2));
                reachingtime=reachingTimeList.get(key2);
            }else{
                //Log.d(TAG, "putMarker: reachingtime (else)");
                reachingtime="N/A";
            }
            ref.child("reachingtime").setValue(reachingtime);

            Log.d(TAG, "putMarker: marker replacing... (else)");

        }
    }

    public  void removeMarker(String key){
        Log.d(TAG, "removeMarker: CALLED ");

        DatabaseReference ref1=FirebaseDatabase.getInstance().getReference("removeListRef").child(currentUser.getUid()).child(key);
        ref1.removeValue();

        Log.d(TAG, "removeMarker: test"+" markerlist="+markerList.containsKey(key)+
                " reachingTimeList="+reachingTimeList.containsKey(key));

        try{
            Marker m=markerList.get(key);
            m.remove();
            markerList.remove(key);
            reachingTimeList.remove(key);
        }catch (Exception e){
            Log.d(TAG, "removeMarker: exception ==== "+e);
        }

        DatabaseReference ref2=FirebaseDatabase.getInstance().getReference("buslist").child(currentUser.getUid()).child(key);
        ref2.removeValue();



        Log.d(TAG, "removeMarker: removed key="+key+" markerlist size="+markerList.size());

/*
        Set set=rl.entrySet();
        Iterator iterator=set.iterator();

        while(iterator.hasNext()){

            Map.Entry me=(Map.Entry)iterator.next();
            String key=me.getKey().toString();

            if(rl.get(key)){
                Log.d(TAG, "removeMarker: true.do nothing");
            }else{
                Marker m=markerList.get(key);
                m.remove();
                markerList.remove(key);
                reachingTimeList.remove(key);

                DatabaseReference ref=FirebaseDatabase.getInstance().getReference("buslist").child(key);
                ref.removeValue();
*/ /*      windowManager.hide(infoWindowList.get(key));
                infoWindowList.remove(key);*//*
                Log.d(TAG, "removeMarker: removed key="+key+" markerlist size="+markerList.size());
            }
        } */
    }

    @Override
    public void onButtonClickCallBack(final BusList busList) {


        /*Intent intent=new Intent(HomeActivity.this,RideStartActivity.class);
        intent.putExtra("uid",busList.getKey());
        startActivity(intent);*/

        Log.d(TAG, "onButtonClickCallBack: called");
        availablebus_portion.setVisibility(View.INVISIBLE);
        reachingTimeLayout.setVisibility(VISIBLE);
        btn1.setVisibility(View.INVISIBLE);

        callForNearbyBus=false;

        final String key=busList.getKey();
        Set s=markerList.entrySet();
        Iterator iterator=s.iterator();
        while(iterator.hasNext()){
            Map.Entry me=(Map.Entry)iterator.next();
            if(!me.getKey().equals(busList.getKey())){
                marker=(Marker)me.getValue();
                marker.remove();
            }
        }

       try{
           DatabaseReference ref=FirebaseDatabase.getInstance().getReference("availableDriver").child(busList.getKey()).child("l");
           ref.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   if(!callForNearbyBus){
                       if(dataSnapshot.exists()) {
                           Log.d(TAG, "onDataChange: onButtonClickCallBack called");
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

                           markerList.get(busList.getKey()).setPosition(buslatlng);

                           getDistanceInformation(mLastLocation,buslatlng,reachingTime);
                           Log.d(TAG, "onDataChange: distance = "+distanceMeasure);
                           if(/*distanceMeasure!=0 && distanceMeasure<=500 && !callForDialog*/true){

                               showUpdateDialog(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()),destinationMarker.getPosition(),buslatlng,key,busList.getBusname());   //showing dialog
                           }

                       }
                   }

               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });
       }catch(Exception e){
           Log.d(TAG, "onButtonClickCallBack: calling database exception= "+e);
       }


        availBusList.clear();
        Log.d(TAG, "onButtonClickCallBack:availBusList cleared= "+availBusList.size());
        Log.d(TAG, "onButtonClickCallBack:markerlist size= "+markerList.size());
        //showUpdateDialog(busList.getKey());
        //baki kaj.......................
    }

    @Override
    public void onViewClickCallBack(BusList busList) {
            //Toast.makeText(this, "busList Key="+busList.getKey(), Toast.LENGTH_SHORT).show();
            Marker marker=markerList.get(busList.getKey());
            Location tempLocation=new Location(LocationManager.GPS_PROVIDER);
            tempLocation.setLongitude(markerList.get(busList.getKey()).getPosition().longitude);
            tempLocation.setLatitude(markerList.get(busList.getKey()).getPosition().latitude);
            moveCamera(tempLocation);

            marker.showInfoWindow();

    }

    private  void showUpdateDialog(LatLng A,LatLng B,LatLng C,String key,String busname) {
        callForDialog =true;
        distanceMeasure=0;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_layout_old, null);

        dialogBuilder.setView(view);
        dialogBuilder.setTitle("Did you get on the bus?");

        Button btn_ok=view.findViewById(R.id.ok_btn_dialog);
        Button btn_missed=view.findViewById(R.id.missed_btn_dialog);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        final Intent  intent=new Intent(HomeActivity.this,RideStartActivity.class);
        intent.putExtra("parcel",new Parcel(A,B,C,key,busname));

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeActivity.this, "Thread name="+Thread
                        .currentThread().getName(), Toast.LENGTH_SHORT).show();

                Toast.makeText(HomeActivity.this, "Opening new Activity", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                alertDialog.dismiss();
            }
        });

        btn_missed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                reachingTimeLayout.setVisibility(View.INVISIBLE);
                availablebus_portion.setVisibility(VISIBLE);

                callForDialog =false;
                distanceMeasure=0;
                callForNearbyBus=true;

                alertDialog.dismiss();
            }
        });


    }


    /*    @Override  //Overridden from FragmentCommunication Interface
    public void getSelectedMarkerLocation(String mrkerKey, LatLng latLng) {
        Log.d(TAG, "getSelectedMarkerLocation: called, Thread Name= "+ Thread.currentThread().getName());

        callForNearbyBus=true;
        selectedMarkerLocation=latLng;
        Set set=markerList.entrySet();
        Iterator iterator=set.iterator();
        while(iterator.hasNext()){
            Map.Entry me=(Map.Entry)iterator.next();
            marker=(Marker)me.getValue();
            marker.remove();
        }

        selectedBusMarker=mMap.addMarker(new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_marker)));

        windowManager.hide(infoWindowList.get(mrkerKey));

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("availableDriver").child(mrkerKey).child("l");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(callForNearbyBus){
                    if(dataSnapshot.exists()) {
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

                        selectedBusMarker.setPosition(buslatlng);
                        getDistanceInformation(mLastLocation,selectedBusMarker.getPosition(),); error
                        Log.d(TAG, "onDataChange: distance = "+distanceMeasure);
                        if(distanceMeasure!=0 && distanceMeasure<=500 && !isDialogShowing){
                            showUpdateDialog();   //showing dialog
                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        availBusList.clear();
        infoWindowList.clear();
        removeList.clear();
        markerList.clear();

        Log.d(TAG, "getSelectedMarkerLocation: updated markerList size="+markerList.size());
    }*/



    /*private  void showUpdateDialog(String key) {

        Log.d(TAG, "showUpdateDialog: called=======================================##");

        final String key2=key;

        // Hide Status Bar.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        callForDialog=true;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_layout_new, null);

        dialogBuilder.setView(view);
        TextView tv1;
        Button btn_cancel,btn_catch,btn_miss;
        LinearLayout catch_miss_dialog;

        tv1=view.findViewById(R.id.tv1);
        time=view.findViewById(R.id.time_xml);
        btn_cancel=view.findViewById(R.id.btn_cancel);
        btn_catch=view.findViewById(R.id.btn_catch);
        btn_miss=view.findViewById(R.id.btn_missed);
        catch_miss_dialog=view.findViewById(R.id.catch_miss_linear_dialog);catch_miss_dialog.setVisibility(View.INVISIBLE);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        Drawable d = new ColorDrawable(Color.BLACK);
        d.setAlpha(130);
        alertDialog.getWindow().setBackgroundDrawable(d);
        //alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("availableDriver").child(key).child("l");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(callForDialog){
                    if(dataSnapshot.exists()) {
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

                        getDistanceInformation(mLastLocation,buslatlng);
                        Log.d(TAG, "onDataChange: updating time............."+getDistanceInformation(mLastLocation,buslatlng));
                        Log.d(TAG, "onDataChange: showUpdateDialog , distance = "+distanceMeasure);

                        if(distanceMeasure!=0 && distanceMeasure<=500){
                            //change dialog state
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        btn_miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btn_catch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
*//*        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeActivity.this, "Thread name="+Thread
                        .currentThread().getName(), Toast.LENGTH_SHORT).show();


                //startMarker=mMap.addMarker(new MarkerOptions().position(selectedBusMarker.getPosition()));
                //getDirectionInformation(startMarker.getPosition(),destinationMarker.getPosition());
                //alertDialog.dismiss();
            }
        });*//*
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //callForNearbyBus=false;
                //selectedBusMarker.remove();

                //isDialogShowing=false;


                // Show Status Bar.
                getWindow().setFlags(WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT, WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT);
                View decorView = getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
                decorView.setSystemUiVisibility(uiOptions);

                distanceMeasure=0;
                callForDialog=false;
                alertDialog.dismiss();

            }
        });


    }*/ //show dialog.new

    /*   public void getDirectionInformation(){

        Map<String, String> mapQuery = new HashMap<>();
        mapQuery.put("key","AIzaSyBXzTJ4tsJOnGHcrDCR4GszzeI5spUp8mg");
        mapQuery.put("origin", 23.7512191+","+90.3783289);
        mapQuery.put("destination", 23.7560154+","+90.3741217);

        mMap.addMarker(new MarkerOptions().position(new LatLng(23.7512191,90.3783289)).title("Start"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(23.7560154,90.3741217)).title("End"));


        Call<DirectionResponse> call=directionApiInterface.getDirectionInfo(mapQuery);
        call.enqueue(new Callback<DirectionResponse>() {
            @Override
            public void onResponse(Call<DirectionResponse> call, Response<DirectionResponse> response) {
                Toast.makeText(HomeActivity.this, "Response body"+response, Toast.LENGTH_SHORT).show();


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
                        polyline=mMap.addPolyline(new PolylineOptions().color(Color.BLUE).geodesic(true).add(
                                new LatLng(origin.latitude,origin.longitude),
                                new LatLng(destination.latitude,destination.longitude)).width(10));
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
    }*/


    public void getDistanceInformation(Location userLocation, LatLng selectedMarkerLatLng,String key){

        String lat2= String.valueOf(userLocation.getLatitude());
        String long2= String.valueOf(userLocation.getLongitude());

        String lat1= String.valueOf(selectedMarkerLatLng.latitude);
        String long1= String.valueOf(selectedMarkerLatLng.longitude);

        final String key2=key;

        Map<String, String> mapQuery = new HashMap<>();
        mapQuery.put("units", "imperial");
        mapQuery.put("origins", lat1+","+long1);
        mapQuery.put("destinations", lat2+","+long2);
        mapQuery.put("key","AIzaSyBXzTJ4tsJOnGHcrDCR4GszzeI5spUp8mg");

        Call<DistanceResponse> call=distanceApiInterface.getDistanceInfo(mapQuery);
        call.enqueue(new Callback<DistanceResponse>() {
            @Override
            public void onResponse(Call<DistanceResponse> call, Response<DistanceResponse> response) {
                DistanceResponse distanceResponse=response.body();
                Element element=distanceResponse.getRows().get(0).getElements().get(0);
                Log.d(TAG, "onResponse: Distance Respomse=>"+element.getDistance().getText()+" "+element.getDuration().getText());
                //textView.setText(element.getDuration().getText());
                //distanceMeasure=element.getDistance().getValue();

                try{
                    reachingTimeList.put(key2,element.getDuration().getText());
                    Log.d(TAG, "onResponse:getDirectionInformation reachingTimeList Value "+reachingTimeList.get(key2));
                }catch (Exception e){
                        Log.d(TAG, "onResponse:getDirectionInformation exception=> "+e);
                    }
            }

            @Override
            public void onFailure(Call<DistanceResponse> call, Throwable t) {

            }
        });
    }

    public void getDistanceInformation(Location userLocation, LatLng selectedMarkerLatLng, final TextView textview){

        String lat2= String.valueOf(userLocation.getLatitude());
        String long2= String.valueOf(userLocation.getLongitude());

        String lat1= String.valueOf(selectedMarkerLatLng.latitude);
        String long1= String.valueOf(selectedMarkerLatLng.longitude);

        Map<String, String> mapQuery = new HashMap<>();
        mapQuery.put("units", "imperial");
        mapQuery.put("origins", lat1+","+long1);
        mapQuery.put("destinations", lat2+","+long2);
        mapQuery.put("key","AIzaSyBXzTJ4tsJOnGHcrDCR4GszzeI5spUp8mg");

        Call<DistanceResponse> call=distanceApiInterface.getDistanceInfo(mapQuery);
        call.enqueue(new Callback<DistanceResponse>() {
            @Override
            public void onResponse(Call<DistanceResponse> call, Response<DistanceResponse> response) {
                DistanceResponse distanceResponse=response.body();
                Element element=distanceResponse.getRows().get(0).getElements().get(0);
                Log.d(TAG, "onResponse: Distance Respomse=>"+element.getDistance().getText()+" "+element.getDuration().getText());
                textview.setText(element.getDuration().getText());
                distanceMeasure=element.getDistance().getValue();
            }

            @Override
            public void onFailure(Call<DistanceResponse> call, Throwable t) {

            }
        });
    }

    public void moveCamera(Location location){
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),DEFAULT_ZOOM));
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

/*        if(userMarker==null){
            userMarker=mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker)) );
        }else{
            userMarker.setPosition(latLng);
        }*/

        if(movecamera==0){
            Log.d(TAG, "onLocationChanged: move camera called");
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            movecamera++;
        }

        //shows avail bus before selecting a bus
        Log.d(TAG, "onLocationChanged: callForNearbyBus="+callForNearbyBus);
        if(callForNearbyBus){
            getAvailableBus(mLastLocation);
        }
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



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }

    private  void updateHeaderInformation(){

        String uid=currentUser.getUid();
        mRef= FirebaseDatabase.getInstance().getReference("userlist").child(uid);
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
    }

    private  void updateUI(UserInformation uf){
        Log.d(TAG,"=>"+uf.getUserName()+"\n=>"+uf.getUserProfiePictureUri());
        NavigationView nv=findViewById(R.id.nav_view);
        View header=nv.getHeaderView(0);
        nav_userName = header.findViewById(R.id.nav_userName_xml);
        nav_userPhoto=header.findViewById(R.id.nav_userPic_xml);

        nav_userName.setText(uf.getUserName());
        Uri uri=Uri.parse(uf.getUserProfiePictureUri());
        Picasso.with(HomeActivity.this).load(uri).fit().centerCrop().into(nav_userPhoto);
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
        Log.d(TAG, "onStart: called");
        super.onStart();
        movecamera=0;
        //callForNearbyBus =false;callForDialog=false;
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: called");
        super.onResume();
        movecamera=0;

    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart: called");
        super.onRestart();

    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: called");
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: called");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called");
        super.onDestroy();
    }

    @Override
    public void onBackPressed(){

        //Toast.makeText(this, "back prass disabled", Toast.LENGTH_SHORT).show();

        if (doubleBackToExitPressedOnce) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}//End Of Code




