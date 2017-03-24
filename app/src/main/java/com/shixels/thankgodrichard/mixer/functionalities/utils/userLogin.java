package com.shixels.thankgodrichard.mixer.functionalities.utils;

import com.quickblox.users.model.QBUser;

/**
 * Created by ACECR on 3/15/2017.
 */

public interface userLogin {
    void onSuccess(QBUser user);
    void onError(String Error, int ErrorCode);
}
