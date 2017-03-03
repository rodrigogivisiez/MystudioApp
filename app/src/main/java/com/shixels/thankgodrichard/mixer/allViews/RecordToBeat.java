package com.shixels.thankgodrichard.mixer.allViews;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pitt.library.fresh.FreshDownloadView;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBProgressCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.QBCustomObjectsFiles;
import com.quickblox.customobjects.model.QBCustomObject;
import com.quickblox.customobjects.model.QBCustomObjectFileField;
import com.shixels.thankgodrichard.mixer.MainActivity;
import com.shixels.thankgodrichard.mixer.R;
import com.shixels.thankgodrichard.mixer.functionalities.utils.CallbackFuntion;
import com.shixels.thankgodrichard.mixer.functionalities.utils.Helpers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link } subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link #} factory method to
 * create an instance of this fragment.
 */
public class RecordToBeat extends Fragment  implements View.OnClickListener{

    byte[] music;
    MediaPlayer mediaPlayer = new MediaPlayer();
    MediaPlayer temp;
    Button play,pause,stop, save,playback;
    MediaRecorder mRecorder;
    LinearLayout progress;
    TextView  statusText, pleasewait;
    FreshDownloadView downloadView;
    String mFileName;
    int  status = 0;
    Helpers connectQb = Helpers.getInstance();
    String[] login = new String[2];
    File file;
    int playbackStatus = 0;








    public RecordToBeat() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        login[0] = "test";
        login[1] = "testtest";

        int permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            String[] PERMISSIONS_STORAGE = {
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.WAKE_LOCK,
            };
            int REQUEST_EXTERNAL_STORAGE = 1;

            ActivityCompat.requestPermissions(
                    getActivity(),
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_record_to_beat, container, false);
        mFileName = getContext().getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest.mp3";
        file = new File(mFileName);


        play = (Button) view.findViewById(R.id.play);
        play.setEnabled(false);
        pause= (Button) view.findViewById(R.id.pause);
        pause.setEnabled(false);
        stop = (Button) view.findViewById(R.id.stop);
        stop.setEnabled(false);
        playback = (Button) view.findViewById(R.id.playback);
        playback.setEnabled(false);
        save = (Button) view.findViewById(R.id.save);
        save.setEnabled(false);


        progress = (LinearLayout) view.findViewById(R.id.progress);
        statusText = (TextView) view.findViewById(R.id.statustext);
        downloadView = (FreshDownloadView) view.findViewById(R.id.pitt);
        pleasewait = (TextView) view.findViewById(R.id.pleasewait);


        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        stop.setOnClickListener(this);
        playback.setOnClickListener(this);
        save.setOnClickListener(this);
        downloadile(getArguments().getString("mp"));
        if(getArguments().getInt("class") == 0){
            save.setVisibility(View.GONE);
            playback.setVisibility(View.GONE);
        }





        return  view;
    }

    private void playMp3(byte[] mp3SoundByteArray) {
        try {
            // create temp file that will hold byte array
            File tempMp3 = File.createTempFile("kurchina9834", "mp3", getContext().getCacheDir());
            tempMp3.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(mp3SoundByteArray);
            fos.close();
            // resetting mediaplayer instance to evade problems
            mediaPlayer.reset();
            // In case you run into issues with threading consider new instance like:
            // MediaPlayer mediaPlayer = new MediaPlayer();
            // Tried passing path directly, but kept getting
            // "Prepare failed.: status=0x1"
            // so using file descriptor instead
            FileInputStream fis = new FileInputStream(tempMp3);
            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.prepare();
        } catch (IOException ex) {
            String s = ex.toString();
            ex.printStackTrace();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.play:
                if(getArguments().getInt("class") == 0){
                    mediaPlayer.start();
                }
                else{
                    mediaPlayer.start();
                    startRecording();

                }
                break;

            case R.id.pause:
                if(getArguments().getInt("class") == 0){
                        mediaPlayer.pause();
                }
                else{
                    mediaPlayer.pause();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        mRecorder.pause();
                    }
                    status = 22;

                }
                break;
            case R.id.stop:
                if(getArguments().getInt("class") == 0){
                    mediaPlayer.stop();
                }
                else{
                    stopRecording();
                    playMp3(music);
                }
                break;
            case R.id.playback:
                if(status != 0){
                    mediaPlayer.stop();
                    stopRecording();
                }
                Log.i("burn",0+"");
                if(playbackStatus == 0) {
                    temp = new MediaPlayer();
                    try {
                        temp.setDataSource(mFileName);
                        temp.prepare();
                        temp.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    play.setEnabled(false);
                    pause.setEnabled(false);
                    stop.setEnabled(false);
                    playback.setText("Stop");
                    playbackStatus = 22;
                }
                else {
                    playback.setText("Play Back");
                    play.setEnabled(true);
                    pause.setEnabled(true);
                    stop.setEnabled(true);
                    temp.stop();
                    playbackStatus =0;
                    temp = null;
                }


                break;
            case R.id.save:
                if(status != 0) {
                    stopRecording();
                }
                saveRecording();
                break;
        }
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }

    }

    private void startRecording() {

        if(status == 0){
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(mFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

            try {
                mRecorder.prepare();
            } catch (IOException e) {
            }
            mRecorder.start();
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mRecorder.resume();
            }
        }

    }

    private void stopRecording() {
        mRecorder.release();
        status = 0;
        mediaPlayer.stop();
        mRecorder = null;
    }
    private  void saveRecording(){
        downloadView.reset();
        pleasewait.setVisibility(View.VISIBLE);
        statusText.setText("Uploading");
        play.setEnabled(false);
        pause.setEnabled(false);
        stop.setEnabled(false);
        save.setEnabled(false);
        if(status != 0){
            stopRecording();
        }
        connectQb.SignIn(login, getContext(), new CallbackFuntion() {
            @Override
            public void onSuccess() {
                QBCustomObject qbCustomObject = new QBCustomObject("Recorded");
                Date date = new Date();
                qbCustomObject.putString("soundname",date.toGMTString());
                QBCustomObjects.createObject(qbCustomObject, new QBEntityCallback<QBCustomObject>() {
                    @Override
                    public void onSuccess(QBCustomObject object, Bundle bundle) {
                        QBCustomObjectsFiles.uploadFile(file, object, "sound", new QBEntityCallback<QBCustomObjectFileField>() {
                            @Override
                            public void onSuccess(QBCustomObjectFileField qbCustomObjectFileField, Bundle bundle) {
                                statusText.setText("Uploaded successfully");
                            }
                            @Override
                            public void onError(QBResponseException e) {
                                statusText.setText("Error Uploading");
                            }
                        }, new QBProgressCallback() {
                            @Override
                            public void onProgressUpdate(int i) {
                                downloadView.upDateProgress(i);
                                if(i == 100){
                                    play.setEnabled(true);
                                    pause.setEnabled(true);
                                    stop.setEnabled(true);
                                    save.setEnabled(true);
                                    playback.setEnabled(true);
                                    pleasewait.setVisibility(View.GONE);
                                    statusText.setText("Sound Ready");
                                }
                            }
                        });

                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });


            }

            @Override
            public void onError(String error) {

            }

            @Override
            public void gotdata(ArrayList<QBCustomObject> object) {
                //passs
            }

            @Override
            public void fileId() {
                //pass
            }
        });
    }
    private void pauseRecord(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mRecorder.pause();
        }
    }

    private byte[] readIn(InputStream is){
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] returni = null;

        int nRead;
        byte[] data = new byte[16384];

        try {
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
                buffer.flush();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        returni = buffer.toByteArray();


        return returni;
    }

    public void downloadile(final String id){
        statusText.setText("Downloading...");
        downloadView.reset();
        connectQb.SignIn(login, getContext(), new CallbackFuntion() {
            @Override
            public void onSuccess() {
                QBCustomObject qbCustomObject = new QBCustomObject("Sound", id);
                QBCustomObjectsFiles.downloadFile(qbCustomObject, "sound", new QBEntityCallback<InputStream>() {
                    @Override
                    public void onSuccess(final InputStream inputStream, Bundle bundle) {
                        Log.i("burn", "Downloaded");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                music = readIn(inputStream);
                                playMp3(music);
                                Log.i("burn","all done");

                            }
                        }).start();


                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                }, new QBProgressCallback() {
                    @Override
                    public void onProgressUpdate(int i) {
                        Log.i("burn",i + "%");
                        downloadView.upDateProgress(i);
                        if(i == 100){
                            play.setEnabled(true);
                            pause.setEnabled(true);
                            stop.setEnabled(true);
                            save.setEnabled(true);
                            playback.setEnabled(true);
                            pleasewait.setVisibility(View.GONE);
                            statusText.setText("Sound Ready");
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {

            }

            @Override
            public void gotdata(ArrayList<QBCustomObject> object) {

            }

            @Override
            public void fileId() {

            }
        });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(status != 0){
            stopRecording();
        }
        if(mediaPlayer != null){
            mediaPlayer = null;
        }
    }
    @Override
    public void onResume() {
        super.onResume();

        if(getView() == null){
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    Fragment frag ;
                    if(getArguments().getInt("class") == 0){
                        frag = new Recorded();
                    }
                    else {
                        frag = new ListSound();
                    }
                    ((MainActivity)getActivity()).openFragment2(frag,R.id.containerMain);
                    return true;
                }
                return false;
            }
        });
    }
}
