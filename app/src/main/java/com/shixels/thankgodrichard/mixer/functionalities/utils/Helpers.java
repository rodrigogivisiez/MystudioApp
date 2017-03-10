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
    public  static String Mx_Pref = "com.shixels.thankgodrichard.mixer";

    static final String APP_ID = "53557";
    static final String AUTH_KEY = "QAN7RGSX4tNzh-x";
    static final String AUTH_SECRET = "6Jsj4Kndpn8kSUd";
    static final String ACCOUNT_KEY = "7fCKUFwp9GFsqcMKBrc8";
    public static final String Pref = "com.dickle.thankgodrichard.mixerApp";

   public void fetchData(final Context c, final int page, final String clasName, final CallbackFuntion callbackFuntion){
       String[] login = new String[2];
       login[0] = "test";
       login[1] = "testtest";
       SignIn(login, c, new CallbackFuntion() {
           @Override
           public void onSuccess() {
               final QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
               requestBuilder.setSkip(page);
               QBCustomObjects.getObjects(clasName, requestBuilder, new QBEntityCallback<ArrayList<QBCustomObject>>() {
                   @Override
                   public void onSuccess(ArrayList<QBCustomObject> qbCustomObjects, Bundle bundle) {
                       Log.i("burn",bundle2string(bundle));
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
    public void Register(String[] details, Context c, final CallbackFuntion callbackFuntion){
        final  QBUser user = new QBUser();
        user.setLogin(details[0]);
        user.setPassword(details[1]);
        user.setEmail(details[2]);
        QBSettings.getInstance().init(c, APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
        QBAuth.createSession(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                QBUsers.signUp(user, new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        callbackFuntion.onSuccess();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        callbackFuntion.onError(e.getMessage());
                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {

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


    //Todo Delete this function
    public static String bundle2string(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        String string = "Bundle{";
        for (String key : bundle.keySet()) {
            string += " " + key + " => " + bundle.get(key) + ";";
        }
        string += " }Bundle";
        return string;
    }

    //fetchmore data

    public void loadMore(Context c,String className, int skip, final qbcallback qbcallback){
        fetchData(c,skip, className, new CallbackFuntion() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(String error) {
                qbcallback.onError(error);
            }

            @Override
            public void gotdata(ArrayList<QBCustomObject> object) {
                qbcallback.onSucess(object);
            }

            @Override
            public void fileId() {

            }
        });
    }

}
