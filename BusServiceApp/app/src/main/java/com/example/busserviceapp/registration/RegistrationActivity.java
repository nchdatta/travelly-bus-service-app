package com.example.busserviceapp.registration;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.busserviceapp.R;
import com.example.busserviceapp.modelclass.UserInformation;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegistrationActivity extends AppCompatActivity {
    private  static String TAG="logRegActivity";

    ImageView imageView;
    EditText et_name,et_email,et_nid;
    Button btn_continue;
    ProgressBar progressbar;

    static int PIC_IMAGE=1;
    static int REQUESTCODE=2;

    Uri pickedPhotoUri;
    String photoDownloadUriString="null";

    StorageReference storageRef;
    DatabaseReference databaseRef;

    UserInformation userNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        imageView=findViewById(R.id.userPhoto_xml);
        et_name=findViewById(R.id.et_userName_xml);
        et_email=findViewById(R.id.et_useremail_xml);
        et_nid=findViewById(R.id.et_userNID_xml);
        btn_continue=findViewById(R.id.btn_continue_xml);

        progressbar = findViewById(R.id.spin_kit_reg);
        Sprite threeDot=new ThreeBounce();
        progressbar.setIndeterminateDrawable(threeDot);
        progressbar.setVisibility(View.INVISIBLE);

        Intent getUserInformation=getIntent();
        userNew=getUserInformation.getParcelableExtra("getUserInformation");
        Log.d(TAG,"=> "+userNew.getUid()+" "+userNew.getUserPhoneNumber());

        storageRef=FirebaseStorage.getInstance().getReference().child("UserProfilePicture");
        databaseRef=FirebaseDatabase.getInstance().getReference().child("userlist");

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    openGallery();
            }
        });

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=et_name.getText().toString();
                String email=et_email.getText().toString();
                String nid=et_nid.getText().toString();

                if(name.isEmpty() || email.isEmpty() || nid.isEmpty()){
                    Toast.makeText(RegistrationActivity.this, "Fillup All Required Data", Toast.LENGTH_SHORT).show();
                }else {
                    if(pickedPhotoUri==null){
                        Toast.makeText(RegistrationActivity.this, "Must Pick Up a Picture", Toast.LENGTH_SHORT).show();
                    }else {
                        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        progressbar.setVisibility(View.VISIBLE);
                        btn_continue.setVisibility(View.INVISIBLE);

                        userNew.setUserName(name);
                        userNew.setUserEmail(email);
                        userNew.setUserNID(nid);

                        //upload userImage
                        final StorageReference imagePath=storageRef.child(pickedPhotoUri.getLastPathSegment());
                        imagePath.putFile(pickedPhotoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.d(TAG,"Upload Complete");
                                getDownloadUri(imagePath);
                            }
                        });
                    }

                }
            }
        });

/*        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"UserName => "+userNew.getUserName());
                Log.d(TAG,"UriString => "+userNew.getUserProfiePictureUri());
            }
        });*/


    }//End Of onCreate

    private void getDownloadUri(StorageReference mStorage){
        Log.d(TAG,"getDownloadUrl Called");
        mStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                setDownloadUri(uri);
                uploadUserInformation(databaseRef);
            }
        });
    }

    private void setDownloadUri(Uri uri){
        Log.d(TAG,"setDownloadUri Called");
        photoDownloadUriString=uri.toString();
        userNew.setUserProfiePictureUri(photoDownloadUriString);
    }

    private void uploadUserInformation(DatabaseReference mRef){
        Log.d(TAG,"uploadUserInformation Called");
        Log.d(TAG,"===> "+ userNew.getUserProfiePictureUri());
        String uid=userNew.getUid();
        mRef.child(uid).setValue(userNew);
    }

    private void requestStoragePermission(){
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

    private void openGallery(){
        Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,PIC_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PIC_IMAGE && resultCode==RESULT_OK && data!= null){
            pickedPhotoUri=data.getData();
            imageView.setImageURI(pickedPhotoUri);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestStoragePermission();
    }
}
