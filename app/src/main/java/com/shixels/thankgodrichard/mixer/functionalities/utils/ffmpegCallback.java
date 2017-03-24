package com.shixels.thankgodrichard.mixer.functionalities.utils;

/**
 * Created by ACECR on 3/22/2017.
 */

public interface ffmpegCallback {
    void success(String sucessMesage);
    void failed(String Failedmessage);
    void progress(String progressmesage);
    void started(String started);
    void finished(String finished);
}
