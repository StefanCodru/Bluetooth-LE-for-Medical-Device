package com.spinalfluid.bluetooth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.ViewHolder> {

    private static final String TAG = "TheDevicesAdapter";
    ArrayList<String> devicesArray;

    public DevicesAdapter(ArrayList<String> devices) {
        devicesArray = devices;
    }

    @NonNull
    @Override
    public DevicesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.devicesrow, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DevicesAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Log.i(TAG, "onBindViewHolder: " + devicesArray);

        holder.deviceNameTV.setText(devicesArray.get(position));

    }

    @Override
    public int getItemCount() {
        return devicesArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView deviceNameTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            deviceNameTV = itemView.findViewById(R.id.deviceNameTV);

        }
    }







}
