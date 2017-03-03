package com.shixels.thankgodrichard.mixer.functionalities.model;


import android.util.Log;

import com.quickblox.customobjects.model.QBCustomObject;

import java.util.ArrayList;

/**
 * Created by thankgodrichard on 10/5/16.
 */
public class listModel {


    private String title;
    private String className;
    private String id;



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return className;
    }

    public void setDescription(String description) {
        this.className = description;
    }

    public static ArrayList<listModel> getData(ArrayList<QBCustomObject> objects) {

        ArrayList<listModel> dataList = new ArrayList<>();

        if(objects != null){
            if(objects.size() >= 1){

                for (int i = 0; i < objects.size(); i++) {

                    listModel landscape = new listModel();
                    landscape.setTitle(objects.get(i).getString("soundname"));
                    landscape.setId(objects.get(i).getCustomObjectId());
                    landscape.setDescription(objects.get(i).getClassName());
                    dataList.add(landscape);

                }

            }
        }
        return dataList;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
