package com.example.busserviceapp.modelclass;

import android.os.Parcel;
import android.os.Parcelable;

public class DriverInformation implements Parcelable {

    private  String uid,driverName,driverEmail,driverPhoneNumber,driverNID,
        driverPictureUri,driverLicenseNumber,driverLicenseExpireDate,
        vehicleLicenseNumber,vehicleLicenseExpiryDate,busCatagory;

    public DriverInformation() {
    }

    public DriverInformation(String uid, String driverName, String driverEmail, String driverPhoneNumber, String driverNID, String driverPictureUri, String driverLicenseNumber, String driverLicenseExpireDate, String vehicleLicenseNumber, String vehicleLicenseExpiryDate, String busCatagory) {
        this.uid = uid;
        this.driverName = driverName;
        this.driverEmail = driverEmail;
        this.driverPhoneNumber = driverPhoneNumber;
        this.driverNID = driverNID;
        this.driverPictureUri = driverPictureUri;
        this.driverLicenseNumber = driverLicenseNumber;
        this.driverLicenseExpireDate = driverLicenseExpireDate;
        this.vehicleLicenseNumber = vehicleLicenseNumber;
        this.vehicleLicenseExpiryDate = vehicleLicenseExpiryDate;
        this.busCatagory = busCatagory;
    }

    protected DriverInformation(Parcel in) {
        uid = in.readString();
        driverName = in.readString();
        driverEmail = in.readString();
        driverPhoneNumber = in.readString();
        driverNID = in.readString();
        driverPictureUri = in.readString();
        driverLicenseNumber = in.readString();
        driverLicenseExpireDate = in.readString();
        vehicleLicenseNumber = in.readString();
        vehicleLicenseExpiryDate = in.readString();
        busCatagory = in.readString();
    }

    public static final Creator<DriverInformation> CREATOR = new Creator<DriverInformation>() {
        @Override
        public DriverInformation createFromParcel(Parcel in) {
            return new DriverInformation(in);
        }

        @Override
        public DriverInformation[] newArray(int size) {
            return new DriverInformation[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    }

    public String getDriverPhoneNumber() {
        return driverPhoneNumber;
    }

    public void setDriverPhoneNumber(String driverPhoneNumber) {
        this.driverPhoneNumber = driverPhoneNumber;
    }

    public String getDriverNID() {
        return driverNID;
    }

    public void setDriverNID(String driverNID) {
        this.driverNID = driverNID;
    }

    public String getDriverPictureUri() {
        return driverPictureUri;
    }

    public void setDriverPictureUri(String driverPictureUri) {
        this.driverPictureUri = driverPictureUri;
    }

    public String getDriverLicenseNumber() {
        return driverLicenseNumber;
    }

    public void setDriverLicenseNumber(String driverLicenseNumber) {
        this.driverLicenseNumber = driverLicenseNumber;
    }

    public String getDriverLicenseExpireDate() {
        return driverLicenseExpireDate;
    }

    public void setDriverLicenseExpireDate(String driverLicenseExpireDate) {
        this.driverLicenseExpireDate = driverLicenseExpireDate;
    }

    public String getVehicleLicenseNumber() {
        return vehicleLicenseNumber;
    }

    public void setVehicleLicenseNumber(String vehicleLicenseNumber) {
        this.vehicleLicenseNumber = vehicleLicenseNumber;
    }

    public String getVehicleLicenseExpiryDate() {
        return vehicleLicenseExpiryDate;
    }

    public void setVehicleLicenseExpiryDate(String vehicleLicenseExpiryDate) {
        this.vehicleLicenseExpiryDate = vehicleLicenseExpiryDate;
    }

    public String getBusCatagory() {
        return busCatagory;
    }

    public void setBusCatagory(String busCatagory) {
        this.busCatagory = busCatagory;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(driverName);
        parcel.writeString(driverEmail);
        parcel.writeString(driverPhoneNumber);
        parcel.writeString(driverNID);
        parcel.writeString(driverPictureUri);
        parcel.writeString(driverLicenseNumber);
        parcel.writeString(driverLicenseExpireDate);
        parcel.writeString(vehicleLicenseNumber);
        parcel.writeString(vehicleLicenseExpiryDate);
        parcel.writeString(busCatagory);
    }
}
