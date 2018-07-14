package traha.taxi.Utils;

import android.Manifest;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import traha.taxi.models.Result;


/**
 * Created by Vijendra Patidar on 16/10/17.
 */

public class UserModel {
    private static UserModel instance;
    public String isoCode;
    public String Header = "tarha@taxi";
    public String userId;
    public String lastame;
    public String firstName;
    public String email;
    public String userName;
    public String phoneNumber;
    public String userImage;

    public static void initInstance(Context _applicationContext) {
        if (instance == null) {
            instance = new UserModel();
            instance.isoCode = Utility.GetCountryZipCode(_applicationContext);
            if (Utility.getBooleaPreferences(_applicationContext, "isLogin")) {
                Type type = new TypeToken<Result>() {
                }.getType();
                Result user = new Gson().fromJson(Utility.getStringPreferences(_applicationContext, "userinfo"), type);
                try {

                   instance.userId = user.userId;
                   instance.userImage = user.userImage;
                   instance.phoneNumber = user.phoneNumber;
                   instance.userName = user.username;
                   instance.email = user.email;
                   instance.firstName = user.firstName;
                   instance.lastame = user.lastName;
//                    instancetance.getUserDetail(_applicationContext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static UserModel getInstance() {
        return instance;
    }




}
