package com.example.lahiru.trackapp;

import com.example.lahiru.trackapp.Remote.IGoogleApi;
import com.example.lahiru.trackapp.Remote.RetrofitClient;


public class Common {
    public static final String baseURL="https://googleapis.com";
    public static IGoogleApi getGoogleApi()
    {
        return RetrofitClient.getClient(baseURL).create(IGoogleApi.class);
    }

}
