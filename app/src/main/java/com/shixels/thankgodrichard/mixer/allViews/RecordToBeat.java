package com.shixels.thankgodrichard.mixer.allViews;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.github.pwittchen.swipe.library.Swipe;
import com.github.pwittchen.swipe.library.SwipeListener;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBarWrapper;
import com.natasa.progresspercent.CircularProgress;
import com.pitt.library.fresh.FreshDownloadView;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBProgressCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.QBCustomObjectsFiles;
import com.quickblox.customobjects.model.QBCustomObject;
import com.quickblox.customobjects.model.QBCustomObjectFileField;
import com.quickblox.users.model.QBUser;
import com.shixels.thankgodrichard.mixer.MainActivity;
import com.shixels.thankgodrichard.mixer.R;
import com.shixels.thankgodrichard.mixer.functionalities.utils.Constants;
import com.shixels.thankgodrichard.mixer.functionalities.utils.FfmpegQuerries;
import com.shixels.thankgodrichard.mixer.functionalities.utils.Helpers;
import com.shixels.thankgodrichard.mixer.functionalities.utils.ffmpegCallback;
import com.shixels.thankgodrichard.mixer.functionalities.utils.userLogin;
import com.shixels.thankgodrichard.mixer.functionalities.widget.ShadowImageView;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link } subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link #} factory method to
 * create an instance of this fragment.
 */
public class RecordToBeat extends Fragment  implements View.OnClickListener {

    byte[] music;
    MediaPlayer mediaPlayer =  new MediaPlayer();
    //Todo delete this or delete MediaRecorded
    private AudioManager volAudio;
    private MediaRecorder recorder = null;
    ShadowImageView shadowImageView;
    TextView totalMusicDuration, musicDrationProgress;
    AppCompatImageView play;
    AppCompatImageView extraBtn;
    AppCompatImageView stop;
    AppCompatImageView save;
    AppCompatImageView share;
    AppCompatSeekBar musicSeekBar;
    LinearLayout progress;
    CircularProgress downloadView;
    String mFileName;
    boolean playerStated = false;
    boolean playing = false;
    boolean  recordingState;
    Helpers connectQb = Helpers.getInstance();
    private int durationInMillis;
    int lastLength;
    TextView stateReport;
    Context context;
    Thread downloadThread;
    String currentFile;
    FfmpegQuerries queries = FfmpegQuerries.getInstance();



    public RecordToBeat() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();

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
        if(getArguments().getInt("class") == 0){
            recordingState = false;
        }
        else {
            recordingState = true;
        }


        //Assign required Utils
        volAudio = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        View view = inflater.inflate(R.layout.music_player, container, false);
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();

        //Create all required Directories
        createDir("MyStudio/temps/");
        createDir("MyStudio/recorded/");
        createDir("MyStudio/wav/");

        //Assign required Views
        play = (AppCompatImageView) view.findViewById(R.id.play);
        play.setEnabled(false);
        extraBtn= (AppCompatImageView) view.findViewById(R.id.extra);
        extraBtn.setEnabled(false);
        stop = (AppCompatImageView) view.findViewById(R.id.stop);
        stop.setEnabled(false);
        save = (AppCompatImageView) view.findViewById(R.id.save);
        stateReport = (TextView) view.findViewById(R.id.text_view_artist);
        save.setEnabled(false);
        shadowImageView = (ShadowImageView) view.findViewById(R.id.image_view_album);
        share = (AppCompatImageView) view.findViewById(R.id.share);
        musicSeekBar = (AppCompatSeekBar) view.findViewById(R.id.musicSeekBar);
        musicDrationProgress = (TextView) view.findViewById(R.id.music_duration_progress);
        totalMusicDuration = (TextView) view.findViewById(R.id.total_music_duration);


        progress = (LinearLayout) view.findViewById(R.id.progress);
        downloadView = (CircularProgress) view.findViewById(R.id.downloadProgress);


        play.setOnClickListener(this);
        extraBtn.setOnClickListener(this);
        stop.setOnClickListener(this);
        save.setOnClickListener(this);
        share.setOnClickListener(this);
        //
        if(getArguments().getInt("class") == 1){
            extraBtn.setVisibility(View.VISIBLE);
            extraBtn.setBackgroundDrawable(null);
            extraBtn.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.ic_play));
        }
        else {
            extraBtn.setVisibility(View.GONE);
            save.setVisibility(View.GONE);
            play.setImageResource(R.drawable.ic_play);
        }

        //If wire headphones is not connected
        if(!volAudio.isWiredHeadsetOn()){
            Toast.makeText(getContext(),"Please Connect headset for better Quality",Toast.LENGTH_LONG).show();
        }

        //implete finishe listener
        finishPlaying();


        return  view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        downloadFile(getArguments().getString("mp"));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.play:
                SecondsTimer();
                shadowImageView.startRotateAnimation();
                playing = true;
                if(!recordingState) {
                    mediaPlayer.start();
                }
                else {
                    mediaPlayer.start();
                    startRecording();
                    play.setEnabled(false);
                }
                    //Todo Change the Image to pause

                break;
            case R.id.stop:
                if(recordingState){
                    stopRecording();
                    shadowImageView.cancelRotateAnimation();
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(0);
                    playMp3(music);
                    break;
                }
                else {
                    shadowImageView.cancelRotateAnimation();
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(0);
                    playMp3(music);

                }
                break;
            case R.id.extra:
                if(mediaPlayer.isPlaying()){
                    stateReport.setText("Please Stop the active recording!");
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    stateReport.setText(null);
                                }
                            });
                        }
                    },5000);
                }
                else {
                    if(mediaPlayer != null){
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                    if(currentFile != null){
                        try {
                            mediaPlayer.setDataSource(getContext(),Uri.parse(currentFile));
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
                break;
            case R.id.share:
                Log.i("burn","share");
                shareIt();
                break;
            case R.id.save:
                if(recorder != null){
                    stopRecording();
                    mediaPlayer.stop();
                    playMp3(music);
                }
                saveRecording();

        }
    }

    private void startRecording() {
        Date d = new Date();
        currentFile = mFileName + "/MyStudio/temps/"+ d.getTime() + ".mp3";
        Log.i("burn",currentFile);
        stateReport.setText("Recording");
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(currentFile);
        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null) {
            mediaPlayer.stop();
        }
        mediaPlayer = null;
        stopRecording();
        recorder = null;
    }
    //Cretate directories
    private boolean createDir(String path){
        boolean ret = true;

        File file = new File(Environment.getExternalStorageDirectory(), path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("TravellerLog :: ", "Problem creating Image folder");
                ret = false;
            }
        }
        return ret;
    }





    private void stopRecording() {
        stateReport.setText(null);
        if(recorder != null) {
            recorder.release();
        }
            recorder = null;


    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mediaPlayer.stop();
        mediaPlayer = null;
        if(downloadThread != null) {
            downloadThread.interrupt();
        }

        downloadThread = null;
        stopRecording();
        recorder = null;
        ((MainActivity)getActivity()).removeView();

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
                else if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
                    volAudio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                    return true;
                }
                else if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
                    volAudio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                    return true;

                }
                return false;
            }
        });
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

    private void downloadFile(final String id){
        downloadView.resetProgress();
        connectQb.digitLogin(((MainActivity) getActivity()).digitsSession, getContext(), new userLogin() {
            @Override
            public void onSuccess(QBUser user) {
                QBCustomObject qbCustomObject = new QBCustomObject("Sound",id);
                QBCustomObjectsFiles.downloadFile(qbCustomObject, "sound", new QBEntityCallback<InputStream>() {
                    @Override
                    public void onSuccess(final InputStream inputStream, Bundle bundle) {
                        downloadThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                music = readIn(inputStream);
                                Log.i("burn","all done");
                                playMp3(music);
                            }
                        });
                        downloadThread.start();
                    }

                    @Override
                    public void onError(final QBResponseException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stateReport.setText(e.getMessage());
                            }
                        });
                    }


                }, new QBProgressCallback() {
                    @Override
                    public void onProgressUpdate(int i) {
                        Log.i("burn",i + "");
                        downloadView.setProgress(i);
                        if(i == 100){
                            downloadView.setVisibility(View.GONE);
                            shadowImageView.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }

            @Override
            public void onError(String Error, int ErrorCode) {

            }
        });
    }

    private void playMp3(byte[] mp3SoundByteArray) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                play.setEnabled(true);
                stop.setEnabled(true);
                save.setEnabled(true);
                share.setEnabled(true);
                extraBtn.setEnabled(true);
            }
        });
        try {
            // create temp file that will hold byte array
            File tempMp3 = File.createTempFile("kurchina9834", "mp3", getContext().getCacheDir());
            tempMp3.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(mp3SoundByteArray);
            fos.close();
            // resetting mediaplayer instance to evade problems
            if(mediaPlayer != null) {
                mediaPlayer.reset();
                FileInputStream fis = new FileInputStream(tempMp3);
                mediaPlayer.setDataSource(fis.getFD());
                mediaPlayer.prepare();
            }
            // In case you run into issues with threading consider new instance like:
            // MediaPlayer mediaPlayer = new MediaPlayer();
            // Tried passing path directly, but kept getting
            // "Prepare failed.: status=0x1"
            // so using file descriptor instead
        } catch (IOException ex) {
            String s = ex.toString();
            ex.printStackTrace();
        }
    }

    protected void setProgressText() {
        int curVolume = 0;
        if(mediaPlayer != null){
            durationInMillis = mediaPlayer.getDuration();
             curVolume = mediaPlayer.getCurrentPosition();
            musicSeekBar.setMax(durationInMillis);

        }
        long HOUR = 60*60*1000;
        if(musicDrationProgress!=null){
            musicSeekBar.setProgress(curVolume);
            if(durationInMillis>HOUR){
                musicDrationProgress.setText(String.format("%1$tH:%1$tM:%1$tS", new Date(curVolume)));
            }else{
                musicDrationProgress.setText(String.format("%1$tM:%1$tS", new Date(curVolume)));
            }
        }
        if(totalMusicDuration != null){
            if(durationInMillis>HOUR){
                totalMusicDuration.setText(String.format("%1$tH:%1$tM:%1$tS", new Date(durationInMillis)));
            }else{
                totalMusicDuration.setText(String.format("%1$tM:%1$tS", new Date(durationInMillis)));
            }
        }
    }
    private void SecondsTimer(){
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setProgressText();
                    }
                });
            }
        }, 0, 1000);
    }

    private void finishPlaying(){
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                shadowImageView.cancelRotateAnimation();
                stopRecording();
            }
        });
    }
    //Todo implement for headphone
    private void combinesounds(String currentRecord, byte[] mp3Bytes) throws IOException, FFmpegNotSupportedException {
        final File someFile = new File(Environment.getExternalStorageDirectory(),"temp.mp3");
        FileOutputStream fos = new FileOutputStream(someFile);
        fos.write(mp3Bytes);
        fos.flush();
        fos.close();
        queries.loadFfmeg(getContext(), new ffmpegCallback() {
            @Override
            public void success(String sucessMesage) {
                String files = "-i " + currentFile + " -i " + someFile.getAbsolutePath();
                String output = mFileName + "/recorded/test.mp3";
                String cmd = "ffmpeg "+files+" -filter_complex amix=inputs=3:duration=first:dropout_transition=3 "+output;
                try {
                    queries.executeCommand(new String[]{cmd}, new ffmpegCallback() {
                        @Override
                        public void success(String sucessMesage) {
                            Log.i("fpeg",sucessMesage);
                        }

                        @Override
                        public void failed(String Failedmessage) {
                            Log.i("fpeg",Failedmessage);
                        }

                        @Override
                        public void progress(String progressmesage) {
                            Log.i("fpeg",progressmesage);
                        }

                        @Override
                        public void started(String started) {
                            Log.i("fpeg",started);
                        }

                        @Override
                        public void finished(String finished) {
                            Log.i("fpeg",finished);
                        }
                    });
                } catch (FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                    Log.i("fpeg",e.getMessage());
                }
            }

            @Override
            public void failed(String Failedmessage) {

            }

            @Override
            public void progress(String progressmesage) {

            }

            @Override
            public void started(String started) {

            }

            @Override
            public void finished(String finished) {

            }
        });

    }
    private void shareIt() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Here is the share content body";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private  void saveRecording(){
        shadowImageView.setVisibility(View.GONE);
        downloadView.setVisibility(View.VISIBLE);
        stateReport.setText("Uploading");
        play.setEnabled(false);
        stop.setEnabled(false);
        save.setEnabled(false);
        extraBtn.setEnabled(false);
        connectQb.digitLogin(((MainActivity)getActivity()).digitsSession, getContext(), new userLogin() {
            @Override
            public void onSuccess(QBUser user) {
                QBCustomObject qbCustomObject = new QBCustomObject("Recorded");
                Date date = new Date();
                qbCustomObject.putString("soundname",date.toGMTString());
                QBCustomObjects.createObject(qbCustomObject, new QBEntityCallback<QBCustomObject>() {
                    @Override
                    public void onSuccess(QBCustomObject object, Bundle bundle) {
                        QBCustomObjectsFiles.uploadFile(new File(currentFile), object, "sound", new QBEntityCallback<QBCustomObjectFileField>() {
                            @Override
                            public void onSuccess(QBCustomObjectFileField qbCustomObjectFileField, Bundle bundle) {
                            }
                            @Override
                            public void onError(final QBResponseException e) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        stateReport.setText(e.getMessage());
                                    }
                                });
                            }
                        }, new QBProgressCallback() {
                            @Override
                            public void onProgressUpdate(int i) {
                                downloadView.setProgress(i);
                                if(i == 100){
                                    downloadView.setVisibility(View.GONE);
                                    shadowImageView.setVisibility(View.VISIBLE);
                                    play.setEnabled(true);
                                    stop.setEnabled(true);
                                    save.setEnabled(true);
                                    extraBtn.setEnabled(true);
                                    stateReport.setText(null);
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
            public void onError(String Error, int ErrorCode) {

            }
        });
    }

}
