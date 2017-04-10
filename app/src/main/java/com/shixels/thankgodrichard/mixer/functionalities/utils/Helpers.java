package com.shixels.thankgodrichard.mixer.functionalities.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.AuthConfig;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsEventLogger;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsOAuthSigning;
import com.digits.sdk.android.DigitsSession;
import com.digits.sdk.android.events.DigitsEventDetails;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

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
    private TwitterAuthConfig authConfig;
    static final String APP_ID = "53557";
    static final String AUTH_KEY = "QAN7RGSX4tNzh-x";
    static final String AUTH_SECRET = "6Jsj4Kndpn8kSUd";
    static final String ACCOUNT_KEY = "7fCKUFwp9GFsqcMKBrc8";
    public static final String Pref = "com.dickle.thankgodrichard.mixerApp";
    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
   public void fetchData(final int log, DigitsSession session, final Context c, final int page, final String clasName, final CallbackFuntion callbackFuntion){
      digitLogin(session, c, new userLogin() {
          @Override
          public void onSuccess(QBUser user) {
              final QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
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
          public void onError(String Error, int ErrorCode) {

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

    public void loadMore(DigitsSession session,Context c,String className, int skip, final qbcallback qbcallback){
        fetchData(0,session,c,skip, className, new CallbackFuntion() {
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

    public void digitLogin(DigitsSession session ,Context c, final userLogin Callback){
        initTwitterDigits(c);
        Map<String, String> authHeaders = getAuthHeadersBySession(session);

        final String xAuthServiceProvider = authHeaders.get("X-Auth-Service-Provider");
        final String xVerifyCredentialsAuthorization = authHeaders.get("X-Verify-Credentials-Authorization");
        QBSettings.getInstance().init(c, APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
        QBAuth.createSession(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                QBUsers.signInUsingTwitterDigits(xAuthServiceProvider, xVerifyCredentialsAuthorization, new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser user, Bundle params) {
                        Callback.onSuccess(user);
                    }

                    @Override
                    public void onError(QBResponseException errors) {
                        Callback.onError(errors.getMessage(), errors.getHttpStatusCode());
                    }
                });

            }

            @Override
            public void onError(QBResponseException e) {

            }
        });


    }




    public void initTwitterDigits(Context context) {
        if(authConfig == null) {
            // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
            String consumerKey = "LDDuV6DOjwGr5zbed8QRgkP7u";
            String consumerSecret = "Sgf7qFszmJtxqKa5IfjugIXlorvbuWWDsVaOBsC20PCvsamox2";
            authConfig = new TwitterAuthConfig(consumerKey,consumerSecret);
            Fabric.with(context, new TwitterCore(authConfig), new Digits.Builder().build());
        }
    }

    private Map<String, String> getAuthHeadersBySession(DigitsSession digitsSession) {
        Log.i("digit",digitsSession.toString());
        TwitterAuthToken authToken = (TwitterAuthToken) digitsSession.getAuthToken();
        DigitsOAuthSigning oauthSigning = new DigitsOAuthSigning(authConfig, authToken);

        return oauthSigning.getOAuthEchoHeadersForVerifyCredentials();
    }

    public File rawToWave(final File rawFile, final String filePath) throws IOException {

        File waveFile = new File(filePath);

        byte[] rawData = new byte[(int) rawFile.length()];
        DataInputStream input = null;
        try {
            input = new DataInputStream(new FileInputStream(rawFile));
            input.read(rawData);
        } finally {
            if (input != null) {
                input.close();
            }
        }

        DataOutputStream output = null;
        try {
            output = new DataOutputStream(new FileOutputStream(waveFile));
            // WAVE header
            // see http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
            writeString(output, "RIFF"); // chunk id
            writeInt(output, 36 + rawData.length); // chunk size
            writeString(output, "WAVE"); // format
            writeString(output, "fmt "); // subchunk 1 id
            writeInt(output, 16); // subchunk 1 size
            writeShort(output, (short) 1); // audio format (1 = PCM)
            writeShort(output, (short) 1); // number of channels
            writeInt(output, Constants.RECORDER_SAMPLERATE); // sample rate
            writeInt(output, Constants.RECORDER_SAMPLERATE * 2); // byte rate
            writeShort(output, (short) 2); // block align
            writeShort(output, (short) 16); // bits per sample
            writeString(output, "data"); // subchunk 2 id
            writeInt(output, rawData.length); // subchunk 2 size
            // Audio data (conversion big endian -> little endian)
            short[] shorts = new short[rawData.length / 2];
            ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
            ByteBuffer bytes = ByteBuffer.allocate(shorts.length * 2);
            for (short s : shorts) {
                bytes.putShort(s);
            }
            output.write(bytes.array());
        } finally {
            if (output != null) {
                output.close();
            }
        }

        return waveFile;

    }

    private void writeInt(final DataOutputStream output, final int value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
        output.write(value >> 16);
        output.write(value >> 24);
    }

    private void writeShort(final DataOutputStream output, final short value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
    }

    private void writeString(final DataOutputStream output, final String value) throws IOException {
        for (int i = 0; i < value.length(); i++) {
            output.write(value.charAt(i));
        }
    }

    public void saveCustomObject(ArrayList<QBCustomObject> objects, Context c, String type){
        SharedPreferences preferences = c.getSharedPreferences(Pref, c.MODE_PRIVATE);;
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String tempData = gson.toJson(objects,new TypeToken<ArrayList<QBCustomObject>>(){}.getType());
        editor.putString(type,tempData);
        editor.commit();
    }

    public ArrayList<QBCustomObject> fetchSavedCategories(Context c, String type){
        SharedPreferences preferences = c.getSharedPreferences(Pref, c.MODE_PRIVATE);;
        Gson gson = new Gson();
        String tempData = preferences.getString(type,null);
        if(tempData != null ){
            if(tempData.length() > 2) {
                ArrayList<QBCustomObject> objects = gson.fromJson(tempData, new TypeToken<ArrayList<QBCustomObject>>() {
                }.getType());
                return objects;
            }
        }
        return null;

    }

    public void fetchRecorded(final int log, DigitsSession session, final Context c, final int page, final String clasName, final CallbackFuntion callbackFuntion){
        digitLogin(session, c, new userLogin() {
            @Override
            public void onSuccess(QBUser user) {
                final QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
                requestBuilder.eq("user_id", user.getId());
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
            public void onError(String Error, int ErrorCode) {

            }
        });

    }


}
