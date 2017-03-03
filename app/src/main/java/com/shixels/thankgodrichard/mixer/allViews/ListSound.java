package com.shixels.thankgodrichard.mixer.allViews;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.quickblox.customobjects.model.QBCustomObject;
import com.quickblox.users.model.QBUser;
import com.shixels.thankgodrichard.mixer.R;
import com.shixels.thankgodrichard.mixer.functionalities.adapters.NotificationAdapter;
import com.shixels.thankgodrichard.mixer.functionalities.model.listModel;
import com.shixels.thankgodrichard.mixer.functionalities.utils.CallbackFuntion;
import com.shixels.thankgodrichard.mixer.functionalities.utils.Helpers;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListSound extends Fragment {

    Helpers helpers = Helpers.getInstance();

    public ListSound() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_list_sound, container, false);
        helpers.dummy(getContext(),"Sound", new CallbackFuntion() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void gotdata(ArrayList<QBCustomObject> object) {
                Log.i("burn","yes");
                setUpRecyclerView(view,object);
            }

            @Override
            public void fileId() {

            }
        });
        return  view;
    }

    private void setUpRecyclerView(View v, ArrayList<QBCustomObject> object) {
        RelativeLayout progressBar = (RelativeLayout) v.findViewById(R.id.progress);
        ProgressBar spinner = (ProgressBar) v.findViewById(R.id.progressspiner);RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        NotificationAdapter adapter = new NotificationAdapter(getContext(), listModel.getData(object));
        recyclerView.setAdapter(adapter);

        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(getContext()); // (Context context, int spanCount)
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManagerVertical);

        recyclerView.setItemAnimator(new DefaultItemAnimator()); // Even if we dont use it then also our items shows default animation. #Check Docs
        progressBar.setVisibility(View.GONE);
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
                    android.os.Process.killProcess(android.os.Process.myPid());
                    return true;
                }
                return false;
            }
        });
    }




}
