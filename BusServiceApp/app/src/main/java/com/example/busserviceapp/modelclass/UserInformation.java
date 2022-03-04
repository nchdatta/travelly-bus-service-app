package com.example.busserviceapp.modelclass;


import android.os.Parcel;
import android.os.Parcelable;

public class UserInformation implements Parcelable  {

    private String uid,userName,userEmail,userNID,userPhoneNumber;
    private String userProfiePictureUri;

    public UserInformation() {
    }

    public UserInformation(String uid, String userName, String userEmail, String userNID, String userPhoneNumber, String userProfiePictureUri) {
        this.uid = uid;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userNID = userNID;
        this.userPhoneNumber = userPhoneNumber;
        this.userProfiePictureUri = userProfiePictureUri;
    }

    protected UserInformation(Parcel in) {
        uid = in.readString();
        userName = in.readString();
        userEmail = in.readString();
        userNID = in.readString();
        userPhoneNumber = in.readString();
        userProfiePictureUri = in.readString();
    }

    public static final Creator<UserInformation> CREATOR = new Creator<UserInformation>() {
        @Override
        public UserInformation createFromParcel(Parcel in) {
            return new UserInformation(in);
        }

        @Override
        public UserInformation[] newArray(int size) {
            return new UserInformation[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserNID() {
        return userNID;
    }

    public void setUserNID(String userNID) {
        this.userNID = userNID;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserProfiePictureUri() {
        return userProfiePictureUri;
    }

    public void setUserProfiePictureUri(String userProfiePictureUri) {
        this.userProfiePictureUri = userProfiePictureUri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(userName);
        parcel.writeString(userEmail);
        parcel.writeString(userNID);
        parcel.writeString(userPhoneNumber);
        parcel.writeString(userProfiePictureUri);
    }
}
