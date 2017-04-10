package com.shixels.thankgodrichard.mixer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsSession;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.google.gson.Gson;
import com.shixels.thankgodrichard.mixer.allViews.AllMixed;
import com.shixels.thankgodrichard.mixer.allViews.ListSound;
import com.shixels.thankgodrichard.mixer.allViews.RecordToBeat;
import com.shixels.thankgodrichard.mixer.allViews.RecordToBeat2;
import com.shixels.thankgodrichard.mixer.allViews.Recorded;
import com.shixels.thankgodrichard.mixer.allViews.profile;
import com.shixels.thankgodrichard.mixer.functionalities.utils.ConvertCallBack;
import com.shixels.thankgodrichard.mixer.functionalities.utils.Helpers;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Todo Delete
    Helpers helpers = Helpers.getInstance();
    SharedPreferences sharedPreferences;
    String tempSession;
    AlertDialog.Builder alertDialogBuilder ;
    AlertDialog alertDialog;
    public DigitsSession digitsSession;
    private boolean permissionToRecordAccepted = false;
    private boolean permissionToWriteAccepted = false;
    private String [] permissions = {"android.permission.RECORD_AUDIO",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.WAKE_LOCK",
            "android.permission.MODIFY_AUDIO_SETTINGS" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        sharedPreferences = getSharedPreferences(helpers.Mx_Pref,MODE_PRIVATE);
        alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        tempSession = sharedPreferences.getString("session",null);
        Log.i("session",tempSession);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Gson gson = new Gson();
        digitsSession = gson.fromJson(tempSession,DigitsSession.class);
        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
        //Keep chacking for earpiease
        new Thread(new Runnable() {
            @Override
            public void run() {
                SecondsTimer();
            }
        }).start();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Fragment fragment = new ListSound();
        openFragment2(fragment,R.id.containerMain);

    }
    public void removeView(){
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.containerMain);
        layout.removeAllViews();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.music) {
            Fragment frag = new ListSound();
            openFragment2(frag,R.id.containerMain);
        } else if (id == R.id.mysounds) {
            Fragment frag = new Recorded();
            openFragment2(frag,R.id.containerMain);

        } else if (id == R.id.myacccount) {
            Fragment frag = new profile();
            openFragment2(frag,R.id.containerMain);
        }
        else if(id == R.id.allmixed){
            Fragment frag = new AllMixed();
            openFragment2(frag,R.id.containerMain);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openFragment2(Fragment frag, int containerId){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.addToBackStack(null);
        ft.replace(containerId,frag);
        ft.commit();
    }
    @Override
    public void onResume() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);

        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 200:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                permissionToWriteAccepted  = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) MainActivity.super.finish();
        if (!permissionToWriteAccepted ) MainActivity.super.finish();

    }

    private void SecondsTimer(){
        final AudioManager ad = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(ad.isWiredHeadsetOn()){
                    Log.i("burn", "none conected");
                    openAlert(false);
                }
                else{
                    if(alertDialog != null) {
                        alertDialog.dismiss();
                    }

                }
            }
        }, 0, 500);
    }


    private void openAlert(boolean status) {
        if(!status) {
            alertDialogBuilder.setTitle("Plug in earphone");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setMessage("You need an earphone to use this app");

            alertDialogBuilder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // cancel the alert box and put a Toast to the user
                    dialog.cancel();
                    Toast.makeText(getApplicationContext(), "You chose a negative answer",
                            Toast.LENGTH_LONG).show();
                }
            });
            // set neutral button: Exit the app message
            alertDialogBuilder.setNeutralButton("Exit the app", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // exit the app and go to the HOME
                    MainActivity.this.finish();
                }
            });
            if(alertDialog == null){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                });
            }
            else {
              runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      alertDialog.show();
                  }
              });
            }

        }
    }



}
