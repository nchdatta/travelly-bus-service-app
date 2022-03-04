package com.example.busservicedriverapp.registration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.busservicedriverapp.R;
import com.example.busservicedriverapp.modelclass.DriverInformation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity implements FragmentCommunication {
    private static final String TAG = "RegistrationActivity";

    FrameLayout frameLayout;
    static int REQUESTCODE=2;

    DriverInformation driverInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        frameLayout=findViewById(R.id.fragment_container_xml);

        Intent intent=getIntent();
        driverInformation=intent.getParcelableExtra("getDriverInformation");
        //driverInformation=new DriverInformation("","","","2441139","","","","","","","");

        initialization();
    }


    private void initialization(){
        RegistrationFragPage1 regPage1=new RegistrationFragPage1();
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_xml,regPage1,null).commit();
    }

    private void requestStoragePermission(){
        Log.d(TAG, "requestStoragePermission: called");
        if(ContextCompat.checkSelfPermission(RegistrationActivity.this
                , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){

            if(ActivityCompat.shouldShowRequestPermissionRationale(RegistrationActivity.this
                    ,Manifest.permission.READ_EXTERNAL_STORAGE) ){
                Toast.makeText(this, "Provide Required Permission", Toast.LENGTH_SHORT).show();
            }else{
                ActivityCompat.requestPermissions(RegistrationActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUESTCODE);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestStoragePermission();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("driverlist");
        DriverInformation d=new DriverInformation("1234567890","","","","","","","","","","");

        ref.child("1234567890").setValue(d);
    }


    @Override
    public DriverInformation shareDriverPhoneNumber() {
        return driverInformation;
    }

    @Override
    public void inflateFragment(String fragmentTag, DriverInformation df) {
        RegistrationFragPage2 regPage2=new RegistrationFragPage2();
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();

        Bundle bundle=new Bundle();
        bundle.putParcelable("getDriverInformation",df);
        regPage2.setArguments(bundle);

        transaction.replace(R.id.fragment_container_xml,regPage2,fragmentTag);
        transaction.commit();
    }




}//End of code

