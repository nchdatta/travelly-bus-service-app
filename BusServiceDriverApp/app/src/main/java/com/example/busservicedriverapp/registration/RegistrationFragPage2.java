package com.example.busservicedriverapp.registration;


import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.busservicedriverapp.R;
import com.example.busservicedriverapp.modelclass.BusCatagory;
import com.example.busservicedriverapp.modelclass.DriverInformation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;

public class RegistrationFragPage2 extends Fragment {
    private static final String TAG = "RegistrationFragPage2";

    TextView name,email,nid,phnNumber,worning_text;
    TextView et_dl_exDate,et_vl_exDate;
    TextView bus_catagory,select_bus_catagory;
    EditText et_driving_license;
    EditText et_vehicle_license;
    Button btn_finish;
    ProgressBar progressbar;
    Spinner spinner_bus_catagory;

    DriverInformation driverInformation;
    StorageReference storageRef=FirebaseStorage.getInstance().getReference().child("UserProfilePicture");
    DatabaseReference databaseRef=FirebaseDatabase.getInstance().getReference().child("driverlist");

    private DatePickerDialog.OnDateSetListener dateSetListener1,dateSetListener2;
    int Year,Month,Day;

    private ArrayList<BusCatagory> busCatagoryList;
    String selectedBusCatagory="";

    public RegistrationFragPage2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle=this.getArguments();
        if(bundle!=null){
            driverInformation=bundle.getParcelable("getDriverInformation");
        }
    }//End of onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_registration_frag_page2, container, false);
        name=view.findViewById(R.id.setDriverName_xml);
        email=view.findViewById(R.id.setDriverEmail_xml);
        nid=view.findViewById(R.id.setDriverNID_xml);
        phnNumber=view.findViewById(R.id.setDriverPhnNumber_xml);
        worning_text=view.findViewById(R.id.textView2);
        et_dl_exDate=view.findViewById(R.id.et_drivingLicense_expiryDate_xml);
        et_vl_exDate=view.findViewById(R.id.et_vehicleLicense_expiryDate_xml);
        et_driving_license=view.findViewById(R.id.et_driving_license_number_xml);
        et_vehicle_license=view.findViewById(R.id.et_vehicle_license_number_xml);
        progressbar=view.findViewById(R.id.spin_kit);
        btn_finish=view.findViewById(R.id.btn_finish_xml);
        spinner_bus_catagory=view.findViewById(R.id.spinner);
/*        select_bus_catagory=view.findViewById(R.id.select_bus_catagory_xml);
        bus_catagory=view.findViewById(R.id.bus_catagory_xml);*/

        progressbar.setVisibility(View.INVISIBLE);

        name.setText(driverInformation.getDriverName());
        email.setText(driverInformation.getDriverEmail());
        nid.setText(driverInformation.getDriverNID());
        phnNumber.setText(driverInformation.getDriverPhoneNumber());


        busCatagoryList =new ArrayList<>();
        busCatagoryList.add(new BusCatagory(" Select Bus ","selected bus route will apear here."));
        busCatagoryList.add(new BusCatagory("Bikash 34","MOhammadpur, Sankar, Jhigatola, City College,Bata Signal, Sahbag."));
        busCatagoryList.add(new BusCatagory("RojoniGondha 21","MOhammadpur, Sankar, Jhigatola, City College,Bata Signal, Sahbag, Kolabagan, Dnammondi 32 bus Stop"));
        CustomSpinnerAdapter spinnerAdapter=new CustomSpinnerAdapter(getContext(), busCatagoryList);
        spinner_bus_catagory.setAdapter(spinnerAdapter);
        spinner_bus_catagory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                BusCatagory selectedBus=(BusCatagory)adapterView.getItemAtPosition(position);
                selectedBusCatagory=selectedBus.getBus_catagory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        et_dl_exDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar=Calendar.getInstance();
                int year=calendar.get(Calendar.YEAR);
                int month=calendar.get(Calendar.MONTH);
                int day=calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog=new DatePickerDialog(getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener1,
                        year,
                        month,
                        day);

                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        et_vl_exDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar=Calendar.getInstance();
                int year=calendar.get(Calendar.YEAR);
                int month=calendar.get(Calendar.MONTH);
                int day=calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog=new DatePickerDialog(getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener2,
                        year,
                        month,
                        day);

                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        dateSetListener1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month=month+1;
                Year=year;Month=month;Day=day;
                String selected_date=day+"/"+month+"/"+year;
                et_dl_exDate.setText(selected_date);
            }
        };
        dateSetListener2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month=month+1;
                Year=year;Month=month;Day=day;
                String selected_date=day+"/"+month+"/"+year;
                et_vl_exDate.setText(selected_date);
            }
        };

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String dl_num=et_driving_license.getText().toString();
                String dl_ex=et_dl_exDate.getText().toString();
                String vl_num=et_vehicle_license.getText().toString();
                String vl_ex=et_vl_exDate.getText().toString();

                if(dl_num.isEmpty()|| vl_num.isEmpty() || dl_ex.isEmpty() || vl_ex.isEmpty()){
                    Toast.makeText(getContext(), "Filup Required Fields.", Toast.LENGTH_SHORT).show();
                }else{

                    if(selectedBusCatagory.equals(" Select Bus ")||selectedBusCatagory==""){
                        Toast.makeText(getContext(), "Selecte a bus catagory.", Toast.LENGTH_SHORT).show();
                    }else{
                        worning_text.setTextColor(getResources().getColor(R.color.green));

                        driverInformation.setDriverLicenseNumber(dl_num);
                        driverInformation.setVehicleLicenseNumber(vl_num);
                        driverInformation.setDriverLicenseExpireDate(dl_ex);
                        driverInformation.setVehicleLicenseExpiryDate(vl_ex);
                        driverInformation.setBusCatagory(selectedBusCatagory);

                        storageRef=FirebaseStorage.getInstance().getReference().child("DriverProfilePicture");

                        Uri pickedPhotoUri=Uri.parse(driverInformation.getDriverPictureUri());
                        //upload driverImage
                        final StorageReference imagePath=storageRef.child(pickedPhotoUri.getLastPathSegment());
                        imagePath.putFile(pickedPhotoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.d(TAG,"Upload Complete");
                                getDownloadUri(imagePath);
                            }
                        });

                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        progressbar.setVisibility(View.VISIBLE);
                        btn_finish.setVisibility(View.INVISIBLE);
                    }

                }

            }
        });

        return view;
    }//End of onCreateView

    private void getDownloadUri(StorageReference mStorage){
        Log.d(TAG,"getDownloadUrl Called");
        mStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                setDownloadUri(uri);
                uploadDriverInformation(databaseRef);
            }
        });
    }

    private void setDownloadUri(Uri uri){
        Log.d(TAG,"setDownloadUri Called");
        driverInformation.setDriverPictureUri(uri.toString());
    }

    private void uploadDriverInformation(DatabaseReference mRef){
        Log.d(TAG,"uploadDriverInformation Called");
        Log.d(TAG,"===> "+ driverInformation.getDriverPictureUri());
        String uid=driverInformation.getUid();
        mRef.child(uid).setValue(driverInformation);
    }

}




