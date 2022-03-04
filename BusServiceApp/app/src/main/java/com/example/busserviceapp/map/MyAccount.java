package com.example.busserviceapp.map;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.busserviceapp.R;
import com.example.busserviceapp.modelclass.UserInformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MyAccount extends AppCompatActivity {
    private View myAccountLayout;
    private ImageView imgView;
    private TextView textV1, textV2, textV3, textV4, textV5, textV6, textV7, textV8;
    private FirebaseUser currentUser;
    FirebaseAuth firebaseAuth;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_account);

        toolbar = findViewById(R.id.toolbar_xml);

        setSupportActionBar(toolbar);

        myAccountLayout = findViewById(R.id.my_account_layout);
        imgView = findViewById(R.id.imV1);
        textV1 = findViewById(R.id.textV1);
        textV2 = findViewById(R.id.textV2);
        textV3 = findViewById(R.id.textV3);
        textV4 = findViewById(R.id.textV4);
        textV5 = findViewById(R.id.textV5);
        textV6 = findViewById(R.id.textV6);
        textV7 = findViewById(R.id.textV7);
        textV8 = findViewById(R.id.textV8);

        firebaseAuth= FirebaseAuth.getInstance();
        currentUser=firebaseAuth.getCurrentUser();   /*user;*/
        getUserInformation(currentUser);

    }
    public void getUserInformation(FirebaseUser user){
        String uid=user.getUid();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("userlist").child(uid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserInformation ui=dataSnapshot.getValue(UserInformation.class);
                updateUI(ui);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //do nothing
            }
        });
    }

    public  void updateUI(UserInformation user){
        textV3.setText(user.getUserName());
        textV1.setText(user.getUserEmail());
        textV4.setText(user.getUserPhoneNumber());
        textV8.setText(user.getUserNID());

        Uri uri=Uri.parse(user.getUserProfiePictureUri());
        Picasso.with(MyAccount.this).load(uri).fit().centerCrop().into(imgView);
    }


}
