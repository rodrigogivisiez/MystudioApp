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
import android.widget.TextView;
import android.widget.Toast;

import com.digits.sdk.android.DigitsSession;
import com.github.pwittchen.infinitescroll.library.InfiniteScrollListener;
import com.quickblox.customobjects.model.QBCustomObject;
import com.shixels.thankgodrichard.mixer.MainActivity;
import com.shixels.thankgodrichard.mixer.R;
import com.shixels.thankgodrichard.mixer.functionalities.adapters.NotificationAdapter;
import com.shixels.thankgodrichard.mixer.functionalities.model.listModel;
import com.shixels.thankgodrichard.mixer.functionalities.utils.CallbackFuntion;
import com.shixels.thankgodrichard.mixer.functionalities.utils.Helpers;
import com.shixels.thankgodrichard.mixer.functionalities.utils.qbcallback;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class Recorded extends Fragment {
    Helpers helpers = Helpers.getInstance();
    int skip = 1;
    DigitsSession digitsSession;
    ProgressBar loadMoreSpinner;
    RelativeLayout errorContainer;
    ProgressBar progressBar ;
    TextView errorText;
    private TimerTask timerTask;

    public Recorded() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_recorded, container, false);
        digitsSession = ((MainActivity)getActivity()).digitsSession;
        errorContainer = (RelativeLayout) view.findViewById(R.id.error);
        errorText = (TextView) view.findViewById(R.id.errorText);
        progressBar = (ProgressBar) view.findViewById(R.id.progressspiner);
        ArrayList<QBCustomObject> tempObjects =  helpers.fetchSavedCategories(getContext(),"recordrd");
        if( tempObjects == null){
            calldata(view);
        }
        else {
            setUpRecyclerView(view,tempObjects);
        }
        loadMoreSpinner = (ProgressBar) view.findViewById(R.id.loadmoreSpiner);
        refresh(view);
        return  view;
    }
    private void calldata(final View view){
        helpers.fetchRecorded(1,digitsSession,getContext(),0,"Recorded", new CallbackFuntion() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(final String error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        errorContainer.setVisibility(View.VISIBLE);
                        errorText.setText(error);
                    }
                });
                Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void gotdata(ArrayList<QBCustomObject> object) {
                Log.i("size",object.size() + "");
                if(object.size() == 0){
                    setUpRecyclerView(view,object);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            errorContainer.setVisibility(View.VISIBLE);
                            errorText.setText("No Sound Avialable");
                        }
                    });
                }
                else {
                    helpers.saveCustomObject(object,getContext(),"recordrd");
                    setUpRecyclerView(view,object);
                }
            }

            @Override
            public void fileId() {

            }
        });
    }

    private void setUpRecyclerView(View v, ArrayList<QBCustomObject> object) {

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        NotificationAdapter adapter = new NotificationAdapter(getContext(), listModel.getData(object,0));
        recyclerView.setAdapter(adapter);
        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(getContext()); // (Context context, int spanCount)
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManagerVertical);
        recyclerView.hasFixedSize();
        recyclerView.smoothScrollToPosition(View.SCROLLBAR_POSITION_LEFT);
        recyclerView.setItemAnimator(new DefaultItemAnimator()); // Even if we dont use it then also our items shows default animation. #Check Docs
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(timerTask != null){
            timerTask.cancel();
        }
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

    private void refresh(final View v){
        Timer timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        calldata(v);
                    }
                });

            }
        };
        timer.scheduleAtFixedRate(timerTask,0,180000);
    }




}
