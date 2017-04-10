package com.shixels.thankgodrichard.mixer.allViews;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBProgressCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.shixels.thankgodrichard.mixer.MainActivity;
import com.shixels.thankgodrichard.mixer.R;
import com.shixels.thankgodrichard.mixer.functionalities.utils.Helpers;
import com.shixels.thankgodrichard.mixer.functionalities.utils.userLogin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import rx.functions.Action1;

/**
 * A simple {@link Fragment} subclass.
 */
public class profile extends Fragment {


    private ImageView profileImg;

    public profile() {
        // Required empty public constructor
    }
    EditText name, email , phone , nickname;
    ImageView propic;
    Helpers helpers = Helpers.getInstance();
    Button edith , submit;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        propic = (ImageView) view.findViewById(R.id.profilePic);
        name = (EditText) view.findViewById(R.id.firstname);
        nickname = (EditText) view.findViewById(R.id.lasname);
        email = (EditText) view.findViewById(R.id.email);
        phone = (EditText) view.findViewById(R.id.phoneNumber);
        edith = (Button) view.findViewById(R.id.edit);
        submit = (Button) view.findViewById(R.id.submit);
        profileImg = (ImageView) view.findViewById(R.id.profilePic);
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });
        edith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setEnabled(true);
                email.setEnabled(true);
                submit.setEnabled(true);

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });


        helpers.digitLogin(((MainActivity) getActivity()).digitsSession, getContext(), new userLogin() {
            @Override
            public void onSuccess(final QBUser user) {
                phone.setText(user.getPhone());
                if(user.getEmail() != null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            email.setText(user.getEmail());
                        }
                    });
                }
                if(user.getLogin() != null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            nickname.setText(user.getLogin());
                        }
                    });
                }
                if(user.getFullName() != null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            name.setText(user.getFullName());
                        }
                    });
                }
                if(user.getFileId() != null){
                    downloadProfilePic(user,profileImg,getContext());
                }
            }

            @Override
            public void onError(String Error, int ErrorCode) {

            }
        });

        return view;
    }

    private void updateProfile() {
        helpers.digitLogin(((MainActivity) getActivity()).digitsSession, getContext(), new userLogin() {
            @Override
            public void onSuccess(QBUser user) {
                user.setEmail(email.getText().toString());
                user.setFullName(name.getText().toString());
                QBUsers.updateUser(user, new QBEntityCallback<QBUser>(){
                    @Override
                    public void onSuccess(QBUser user, Bundle args) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                email.setEnabled(false);
                                name.setEnabled(false);

                            }
                        });

                    }

                    @Override
                    public void onError(final QBResponseException errors) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), errors.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });
            }

            @Override
            public void onError(String Error, int ErrorCode) {

            }
        });
    }

    private void pickImage(){
        RxImagePicker.with(getContext()).requestImage(Sources.GALLERY).subscribe(new Action1<Uri>() {
            @Override
            public void call(Uri uri) {
                profileImg.setImageURI(uri);

                try {
                    Bitmap bitmap =  MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                    final File picTure = bitmapToFile(bitmap);
                    helpers.digitLogin(((MainActivity) getActivity()).digitsSession, getContext(), new userLogin() {
                        @Override
                        public void onSuccess(QBUser user) {
                            uploadprofilePic(user.getId(),picTure);
                        }

                        @Override
                        public void onError(String Error, int ErrorCode) {

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    //Convert bitmap to a file
    public File bitmapToFile(Bitmap bitmap) {
        File f = new File(getContext().getCacheDir(), "pir.png");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
//Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    public void uploadprofilePic(final int userId, File file1){
        Boolean fileIsPublic = false;

        QBContent.uploadFileTask(file1, fileIsPublic, null, new QBEntityCallback<QBFile>() {
            @Override
            public void onSuccess(QBFile qbFile, Bundle params) {

                int uploadedFileID = qbFile.getId();
                Log.i("propic", uploadedFileID + "");

                // Connect image to user
                QBUser user = new QBUser();
                user.setId(userId);
                user.setFileId(uploadedFileID);

                QBUsers.updateUser(user, new QBEntityCallback<QBUser>(){
                    @Override
                    public void onSuccess(QBUser user, Bundle args) {

                    }

                    @Override
                    public void onError(QBResponseException errors) {

                    }
                });
            }

            @Override
            public void onError(QBResponseException errors) {

            }
        }, new QBProgressCallback() {
            @Override
            public void onProgressUpdate(int progress) {

            }
        });
    }

    //Download propic
    public void downloadProfilePic(QBUser user, final ImageView v, final Context c){
                QBContent.downloadFileById(user.getFileId(), new QBEntityCallback<InputStream>(){

                    @Override
                    public void onSuccess(final InputStream inputStream, Bundle params) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        v.setImageBitmap(bmp);
                                    }
                                });
                            }
                        }).start();



                    }

                    @Override
                    public void onError(QBResponseException errors) {
                        Log.i("propic",errors.getMessage());
                    }
                }, new QBProgressCallback() {
                    @Override
                    public void onProgressUpdate(int progress) {

                    }
                });
    }



}
