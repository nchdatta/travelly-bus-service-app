package com.example.busservicedriverapp.registration;

import com.example.busservicedriverapp.modelclass.DriverInformation;

public interface FragmentCommunication {

    //fragment2activity communication
    public DriverInformation shareDriverPhoneNumber();

    //fragmenr2fragment communication
    public void  inflateFragment(String fragmentTag,DriverInformation df);
}
