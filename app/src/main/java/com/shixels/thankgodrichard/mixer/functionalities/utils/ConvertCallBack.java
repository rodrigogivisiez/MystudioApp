package com.shixels.thankgodrichard.mixer.functionalities.utils;

import java.io.File;

/**
 * Created by ACECR on 4/1/2017.
 */

public interface ConvertCallBack {
    void onSuccess(File fil);
    void onError(String error);
}
