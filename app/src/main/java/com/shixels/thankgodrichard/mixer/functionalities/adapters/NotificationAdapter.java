package com.shixels.thankgodrichard.mixer.functionalities.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.shixels.thankgodrichard.mixer.MainActivity;
import com.shixels.thankgodrichard.mixer.R;
import com.shixels.thankgodrichard.mixer.allViews.RecordToBeat;
import com.shixels.thankgodrichard.mixer.functionalities.model.listModel;
import com.shixels.thankgodrichard.mixer.functionalities.utils.Helpers;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {
    List<listModel> mData;
    private LayoutInflater inflater;
    Helpers connectQB = Helpers.getInstance();
    Context cont;


    public NotificationAdapter(Context context, List<listModel> data) {
        inflater = LayoutInflater.from(context);
        this.cont = context;
        this.mData = data;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_notification, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        listModel current = mData.get(position);
        holder.setData(current, position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        AppCompatImageView play;
        int position;
        listModel current;
        String id;

        public MyViewHolder(View itemView) {
            super(itemView);
           title = (TextView) itemView.findViewById(R.id.title);
            play = (AppCompatImageView) itemView.findViewById(R.id.paly);

        }

        public void setData(final listModel current, int position) {
            this.position = position;
            this.current = current;
            this.title.setText(current.getTitle());
            this.play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle arg = new Bundle();
                    Fragment fragment = new RecordToBeat();
                    Log.i("burn","I am here now");
                    arg.putInt("class",current.getType());
                    arg.putString("mp",current.getId());
                    fragment.setArguments(arg);
                    MainActivity mainActivity = (MainActivity)cont;
                    FragmentManager fm= mainActivity.getSupportFragmentManager();
                    FragmentTransaction ft=fm.beginTransaction();
                    ft.replace(R.id.containerMain, fragment);
                    ft.commit();
                }
            });


            }


    }


}
