package com.example.busservicedriverapp.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.busservicedriverapp.DriverHomeActivity;
import com.example.busservicedriverapp.R;
import com.example.busservicedriverapp.modelclass.DriverInformation;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {
    private static final String TAG = "VerificationActivity";

    Pinview pinview;
    Button sc;
    TextView showNumber, note;
    ProgressBar progressBar;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    FirebaseAuth mAuth;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    DatabaseReference driverlistRef;

    List<DriverInformation> driverList;
    DriverInformation driverInformation;

    int count=0;
    int callForRegistration=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        pinview = findViewById(R.id.pinview_xml);
        showNumber = findViewById(R.id.show_number_xml);
        note = findViewById(R.id.note_xml);
        sc = findViewById(R.id.sendcode_xml);
        progressBar = findViewById(R.id.spin_kit_ver);
        Sprite threeDot=new ThreeBounce();
        progressBar.setIndeterminateDrawable(threeDot);

        note.setVisibility(View.INVISIBLE);
        sc.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        driverList=new ArrayList<>();
        driverInformation=new DriverInformation("","","","","","","","","","","");

        Intent getDriverPhoneNumber=getIntent();
        final String driverPhoneNumber=getDriverPhoneNumber.getStringExtra("DriverPhnNumber");
        driverInformation.setDriverPhoneNumber(driverPhoneNumber);

        String message = showNumber.getText().toString() + driverPhoneNumber;
        showNumber.setText(message);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                sc.performClick();
            }
        }, 500);


        sc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        driverPhoneNumber
                        ,
                        60,
                        TimeUnit.SECONDS,
                        VerificationActivity.this,
                        mCallbacks);

            }
        });

        pinview.setFocusable(false);
        pinview.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {
                String code = pinview.getValue();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(
                        mVerificationId, code);
                signInWithPhoneAuthCredential(credential); //SignIn Call

            }
        });


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential); //Auto Verification
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(VerificationActivity.this, "VerificationFailed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {

                Log.d(TAG, "onCodeSent:" + verificationId);
                Toast.makeText(VerificationActivity.this, "onCodeSent", Toast.LENGTH_SHORT).show();

                progressBar.setVisibility(View.INVISIBLE);
                note.setVisibility(View.VISIBLE);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

            }
        };

    }//End of onCreate

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(VerificationActivity.this, "Sign in done.", Toast.LENGTH_SHORT).show();

                            FirebaseUser user = task.getResult().getUser();

                            String userUID=user.getUid();
                            driverInformation.setUid(userUID);
                            Log.d(TAG, "onComplete: driverUID="+driverInformation.getUid());

                            driverlistRef= FirebaseDatabase.getInstance().getReference("driverlist");
                            driverlistRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                                       DriverInformation driverInfo=ds.getValue(DriverInformation.class);
                                       driverList.add(driverInfo);
                                       count++;
                                       //Log.d(TAG, "onDataChange: count = "+count);
                                       //Log.d(TAG, "onDataChange: driverlist[0].uid = "+driverList.get(0).getUid());
                                    }
                                    actionBasedOnStatus(checkUserStatus(driverList));
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    //do Nothing
                                }
                            });
                        } else {

                            Log.d(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(VerificationActivity.this, "signInWithCredential:failure", Toast.LENGTH_SHORT).show();

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                                Toast.makeText(VerificationActivity.this, "The verification code entered was invalid", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private int checkUserStatus(List<DriverInformation> list){
        Log.d(TAG, "checkUserStatus: called");
        Log.d(TAG,"Count,ArraySize "+count+" "+list.size());

        for(int i=0;i<driverList.size();i++){
            DriverInformation df=driverList.get(i);
            Log.d(TAG,driverInformation.getUid()+" "+df.getUid());
            if( driverInformation.getUid().equals( df.getUid()) ){
                callForRegistration++;
                break;
            }
        }
        int temp=callForRegistration;
        callForRegistration=0;
        count=0;
        driverList.clear();
        return temp;
    }

    public void actionBasedOnStatus(int statusResult){
        Log.d(TAG, "actionBasedOnStatus: called");
        if(statusResult!=0){
            //Just login.Registration not required
            Log.d(TAG,"kalpit");
            Intent gotoHome=new Intent(VerificationActivity.this, DriverHomeActivity.class);
            Toast.makeText(VerificationActivity.this, "Just login.Registration not required", Toast.LENGTH_SHORT).show();
            startActivity(gotoHome);
        }else if(statusResult==0){
            //New User.Registration Required
            Intent goToRegistration=new Intent(VerificationActivity.this, RegistrationActivity.class);
            goToRegistration.putExtra("getDriverInformation",driverInformation);
            Toast.makeText(VerificationActivity.this, "New User.Registration Required,", Toast.LENGTH_SHORT).show();
            startActivity(goToRegistration);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*driverlistRef= FirebaseDatabase.getInstance().getReference("driverlist");
        DriverInformation dff=new DriverInformation("123456789","null",
                "null", "null","null","null","null",
                "null","null","null");
        driverlistRef.child("123456789").setValue(dff);*/
        //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Log.d(TAG,"Verification Activity is Called");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
