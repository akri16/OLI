package com.akribase.oauthloginimplementation.util;

import android.util.TimeUtils;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;

public class GeneralUtils {

    public static boolean hasDatePassed(Date expiryDate){
        Date cDate = new Date();
        return cDate.after(expiryDate);
    }

}
