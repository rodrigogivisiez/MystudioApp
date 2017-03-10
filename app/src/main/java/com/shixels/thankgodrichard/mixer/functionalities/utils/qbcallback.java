package com.shixels.thankgodrichard.mixer.functionalities.utils;

import com.quickblox.customobjects.model.QBCustomObject;

import java.util.ArrayList;

/**
 * Created by ACECR on 3/10/2017.
 */

public interface qbcallback {
    void  onSucess(ArrayList<QBCustomObject> objects);
    void  onError(String errorMessage);
}
