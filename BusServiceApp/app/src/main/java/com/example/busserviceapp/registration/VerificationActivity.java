package com.example.busserviceapp.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.busserviceapp.R;
import com.example.busserviceapp.map.HomeActivity;
import com.example.busserviceapp.modelclass.UserInformation;
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

    Pinview pinview;
    Button sc;
    TextView showNumber, note;
    ProgressBar progressBar;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    static final String TAG="logVerificationActivity";
    FirebaseAuth mAuth;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    DatabaseReference userlistRef;

    List<UserInformation>userList;


    UserInformation userInformation;
    //Uri userPicUri;

    int count=0;
    int callForRegistration=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        userList=new ArrayList<>();

        //Intent getUserImformation = getIntent();
        //userInformation = getUserImformation.getParcelableExtra("UserInformation");
        //userPicUri = Uri.parse(userInformation.getUserProfiePictureUri());
        //final String userPhoneNumber = userInformation.getUserPhoneNumber();

        userInformation=new UserInformation("","","","","","");

        Intent getUserPhoneNumber=getIntent();
        final String userPhoneNumber=getUserPhoneNumber.getStringExtra("UserPhnNumber");
        userInformation.setUserPhoneNumber(userPhoneNumber);

        String message = showNumber.getText().toString() + userPhoneNumber;
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
                        userPhoneNumber
                        ,
                        60,
                        TimeUnit.SECONDS,
                        VerificationActivity.this,
                        mCallbacks);

            }
        });


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
                /*The SMS verification code has been sent to the provided phone number,we
                now need to ask the user to enter the code and then construct a credential
                by combining the code with a verification ID.*/
                Log.d(TAG, "onCodeSent:" + verificationId);
                Toast.makeText(VerificationActivity.this, "onCodeSent", Toast.LENGTH_SHORT).show();

                progressBar.setVisibility(View.INVISIBLE);
                note.setVisibility(View.VISIBLE);


                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

            }
        };


    }//End Of onCreate

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(VerificationActivity.this, "Sign in done.", Toast.LENGTH_SHORT).show();

                            FirebaseUser user = task.getResult().getUser();

                            String userUID=user.getUid();
                            userInformation.setUid(userUID);

                            //======================================================================
                            userlistRef=FirebaseDatabase.getInstance().getReference("userlist");
                            userlistRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                                        UserInformation userInfo=ds.getValue(UserInformation.class);
                                        userList.add(userInfo);
                                        count++;
                                    }
                                    actionBasedOnStatus(checkUserStatus(userList));
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                          //do Nothing
                                }
                            });
                           //=======================================================================

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

    private int checkUserStatus(List<UserInformation> list){

        Log.d(TAG,"Count,ArraySize "+count+" "+list.size());

        for(int i=0;i<userList.size();i++){
            UserInformation uf=userList.get(i);
            Log.d(TAG,userInformation.getUid()+" "+uf.getUid());
            if( userInformation.getUid().equals( uf.getUid()) ){
                callForRegistration++;
                break;
            }
        }
        int temp=callForRegistration;
        callForRegistration=0;
        count=0;
        userList.clear();
        return temp;
    }

    public void actionBasedOnStatus(int statusResult){

        if(statusResult!=0){
            //Just login.Registration not required
            Log.d(TAG,"kalpit");
            Intent gotoHome=new Intent(VerificationActivity.this, HomeActivity.class);
            Toast.makeText(VerificationActivity.this, "Just login.Registration not required", Toast.LENGTH_SHORT).show();
            startActivity(gotoHome);
        }else if(statusResult==0){
            //New User.Registration Required
            Intent goToRegistration=new Intent(VerificationActivity.this, RegistrationActivity.class);
            goToRegistration.putExtra("getUserInformation",userInformation);
            Toast.makeText(VerificationActivity.this, "New User.Registration Required,", Toast.LENGTH_SHORT).show();
            startActivity(goToRegistration);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG,"Verification Activity is Called");
    }
}//end of code
