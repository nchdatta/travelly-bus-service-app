package com.example.busservicedriverapp.registration;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.busservicedriverapp.R;
import com.example.busservicedriverapp.modelclass.DriverInformation;

import static android.app.Activity.RESULT_OK;

public class RegistrationFragPage1 extends Fragment implements View.OnClickListener {
    private static final String TAG = "RegistrationFragPage1";

    ImageView imageView;
    EditText et_name,et_email,et_nid;
    Button btn_next;

    static int PIC_IMAGE=1;

    Uri pickedPhotoUri=null;
    String photoDownloadUriString="null";
    DriverInformation driverInformation;

    FragmentCommunication mfragmentCommunication;

    public RegistrationFragPage1() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        driverInformation=mfragmentCommunication.shareDriverPhoneNumber();
    }//End of onCreate

    @Override
    public View onCreateView(LayoutInflater inflater,
                   ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_registration_frag_page1,
                   container, false);
        imageView=view.findViewById(R.id.driverPhoto_xml);
        et_name=view.findViewById(R.id.et_driverName_xml);
        et_email=view.findViewById(R.id.et_driverEmail_xml);
        et_nid=view.findViewById(R.id.et_driverNID_xml);
        btn_next=view.findViewById(R.id.btn_next_xml);

        imageView.setOnClickListener(this);
        btn_next.setOnClickListener(this);

        //Log.d(TAG, "onCreateView: driver = "+ driverInformation.getDriverPhoneNumber());
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.driverPhoto_xml:
                openGallery();
                break;
            case R.id.btn_next_xml:
                String name=et_name.getText().toString();
                String email=et_email.getText().toString();
                String nid=et_nid.getText().toString();

                if(name.isEmpty() || email.isEmpty() || nid.isEmpty() ){
                    Toast.makeText(getContext(), "Fillup All Required Data", Toast.LENGTH_SHORT).show();
                }else {

                    if(pickedPhotoUri==null){
                        Toast.makeText(getContext(), "Must Pick Up a Picture", Toast.LENGTH_SHORT).show();
                    }else {
                        driverInformation.setDriverName(name);
                        driverInformation.setDriverEmail(email);
                        driverInformation.setDriverNID(nid);
                        driverInformation.setDriverPictureUri(pickedPhotoUri.toString());

                        Log.d(TAG, "onClick: driver"+
                                driverInformation.getUid() + " "+
                                driverInformation.getDriverEmail() + " "+
                                driverInformation.getDriverNID() + " "+
                                driverInformation.getDriverPhoneNumber() + " "+
                                driverInformation.getDriverPictureUri() + " "+
                                driverInformation.getDriverName());
                        mfragmentCommunication.inflateFragment("RegistrationFragPage2",driverInformation);
                    }
                }

                break;
        }
    }

    private void openGallery(){
        Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,PIC_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PIC_IMAGE && resultCode==RESULT_OK && data!= null) {
            pickedPhotoUri = data.getData();
            imageView.setBackgroundResource(R.color.trans);
            imageView.setImageURI(pickedPhotoUri);
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mfragmentCommunication=(RegistrationActivity)getActivity();
    }
}//End of Code

