package com.shixels.thankgodrichard.mixer.functionalities.utils;

import com.quickblox.customobjects.model.QBCustomObject;

import java.util.ArrayList;

/**
 * Created by thankgodrichard on 2/10/17.
 */

public  interface CallbackFuntion {
    public   void onSuccess();
    public  void onError(String error);
    public  void gotdata(ArrayList<QBCustomObject> object);
    public  void fileId();
}
