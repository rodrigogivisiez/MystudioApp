package com.shixels.thankgodrichard.mixer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.AuthConfig;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.google.gson.Gson;
import com.quickblox.users.model.QBUser;
import com.shixels.thankgodrichard.mixer.functionalities.utils.Helpers;
import com.shixels.thankgodrichard.mixer.functionalities.utils.userLogin;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;

public class Auth extends AppCompatActivity implements  View.OnClickListener {
    Helpers helpers = Helpers.getInstance();
    private AuthCallback authCallback;
    RelativeLayout digitsButton;
    EditText phoneNo;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_auth);
        helpers.initTwitterDigits(this);
        final LinearLayout progress = (LinearLayout) findViewById(R.id.progress);
        phoneNo = (EditText) findViewById(R.id.phoneNumber);
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(this);

        digitsButton = (RelativeLayout) findViewById(R.id.auth_button);
        authCallback = new AuthCallback() {
            @Override
            public void success(DigitsSession session, final String phoneNumber) {
                //Save seetion if login is successsfull and proceed
                digitsButton.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                SharedPreferences sharedPreferences = getSharedPreferences(helpers.Mx_Pref,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String savedata = gson.toJson(session, DigitsSession.class);
                editor.putString("session",savedata);
                editor.apply();
                helpers.digitLogin(session, Auth.this ,new userLogin() {
                    @Override
                    public void onSuccess(QBUser user) {
                        Intent intent = new Intent(Auth.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(final String Error, int ErrorCode) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                digitsButton.setVisibility(View.VISIBLE);
                                progress.setVisibility(View.GONE);
                                phoneNo.setError(Error);
                            }
                        });
                    }
                });

            }

            @Override
            public void failure(DigitsException exception) {
                Log.d("Digits", "Sign in with Digits failure", exception);
            }
        };

    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.submit:
                String phone = phoneNo.getText().toString();
                AuthConfig.Builder authConfigBuilder = new AuthConfig.Builder()
                        .withAuthCallBack(authCallback)
                        .withPhoneNumber("+234" + phone);
                Digits.authenticate(authConfigBuilder.build());
        }
    }
}
