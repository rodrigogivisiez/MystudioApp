package com.shixels.thankgodrichard.mixer.functionalities.utils;

import android.media.AudioFormat;

/**
 * Created by ACECR on 3/30/2017.
 */

public class Constants {

    final static public int RECORDER_SAMPLERATE = 44100;
    final static public int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    final static public int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    final static public int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    final static public int BytesPerElement = 2; // 2 bytes in 16bit format


}