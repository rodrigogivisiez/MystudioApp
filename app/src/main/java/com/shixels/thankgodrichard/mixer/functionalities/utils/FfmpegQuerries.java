package com.shixels.thankgodrichard.mixer.functionalities.utils;

import android.content.Context;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

/**
 * Created by ACECR on 3/22/2017.
 */

public class FfmpegQuerries {
    private static final FfmpegQuerries ourInstance = new FfmpegQuerries();

    public static FfmpegQuerries getInstance() {
        return ourInstance;
    }

    private FfmpegQuerries() {
    }

    FFmpeg fFmpeg;

    public void loadFfmeg(Context c,final ffmpegCallback callback) throws FFmpegNotSupportedException {
        fFmpeg = FFmpeg.getInstance(c);
        fFmpeg.loadBinary(new FFmpegLoadBinaryResponseHandler() {
            @Override
            public void onFailure() {
                callback.failed("failed");
            }

            @Override
            public void onSuccess() {
                callback.success("success");
            }

            @Override
            public void onStart() {
                callback.started("Starting...");
            }

            @Override
            public void onFinish() {
                callback.finished("Finished");
            }
        });
    }

    public void executeCommand(String[] cmd, final ffmpegCallback callback) throws FFmpegCommandAlreadyRunningException {
        fFmpeg.execute(cmd, new FFmpegExecuteResponseHandler() {
            @Override
            public void onSuccess(String message) {
                callback.success(message);
            }

            @Override
            public void onProgress(String message) {
                callback.progress(message);
            }

            @Override
            public void onFailure(String message) {
                callback.failed(message);
            }

            @Override
            public void onStart() {
                callback.started("Starting ....");
            }

            @Override
            public void onFinish() {
                callback.finished("finished");
            }
        });
    }
}
