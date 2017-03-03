package com.shixels.thankgodrichard.mixer.functionalities.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBSettings;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.QBCustomObjectsFiles;
import com.quickblox.customobjects.model.QBCustomObject;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by thankgodrichard on 2/10/17.
 */
public class Helpers {
    private static Helpers ourInstance = new Helpers();

    public static Helpers getInstance() {
        return ourInstance;
    }

    private Helpers() {
    }

    static final String APP_ID = "53557";
    static final String AUTH_KEY = "QAN7RGSX4tNzh-x";
    static final String AUTH_SECRET = "6Jsj4Kndpn8kSUd";
    static final String ACCOUNT_KEY = "7fCKUFwp9GFsqcMKBrc8";
    public static final String Pref = "com.dickle.thankgodrichard.mixerApp";

   public void dummy(final Context c, final String clasName, final CallbackFuntion callbackFuntion){
       String[] login = new String[2];
       login[0] = "test";
       login[1] = "testtest";
       SignIn(login, c, new CallbackFuntion() {
           @Override
           public void onSuccess() {
               final QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
               requestBuilder.setLimit(900);
               QBCustomObjects.getObjects(clasName, requestBuilder, new QBEntityCallback<ArrayList<QBCustomObject>>() {
                   @Override
                   public void onSuccess(ArrayList<QBCustomObject> qbCustomObjects, Bundle bundle) {
                       callbackFuntion.gotdata(qbCustomObjects);
                   }

                   @Override
                   public void onError(QBResponseException e) {
                       callbackFuntion.onError(e.getMessage());
                   }
               });
           }

           @Override
           public void onError(String error) {
               Toast.makeText(c,error,Toast.LENGTH_SHORT).show();
           }

           @Override
           public void gotdata(ArrayList<QBCustomObject> object) {

           }

           @Override
           public void fileId() {

           }
       });

   }

    //Signin users to quickblox
    public void SignIn(final String[] login, final Context c, final CallbackFuntion callBackFunc){
        final  QBUser user = new QBUser(login[0],login[1]);
        QBSettings.getInstance().init(c, APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
        QBAuth.createSession(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle params) {
                // success
                Log.i("sesion", "Created");
                QBUsers.signIn( user,new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser user, Bundle args) {
                        // success
                        savePref(c,login,200);
                        callBackFunc.onSuccess();
                    }

                    @Override
                    public void onError(QBResponseException error) {
                        // error
                        savePref(c,login,0);
                        callBackFunc.onError(error.getMessage());
                    }
                });
            }

            @Override
            public void onError(QBResponseException error) {
                // errors
                Log.i("session", "No created");
                callBackFunc.onError(error.getMessage());
                //
            }
        });

    }

    private void savePref(Context context , String[] login, int respondCode){
        SharedPreferences pref = context.getSharedPreferences(Pref, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        if(respondCode == 200){
            String loginInfo = gson.toJson(login,String[].class);
            editor.putString("login",loginInfo);
            editor.commit();
        }
        else{
            editor.putString("login","");
            editor.commit();
        }
    }



}
