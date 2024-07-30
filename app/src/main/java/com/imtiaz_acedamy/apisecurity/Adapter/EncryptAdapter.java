package com.imtiaz_acedamy.apisecurity.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.imtiaz_acedamy.apisecurity.Domain.Domain;

import java.util.ArrayList;

public class EncryptAdapter extends BaseAdapter {

    private final ArrayList<Domain> list;
    private Context context;

    public EncryptAdapter(ArrayList<Domain> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

//        context = parent.getContext();
//        ActivityMainBinding binding = ActivityMainBinding.inflate(LayoutInflater.from(context), parent, false);
//
//        TextView titleTxt, encryptedTxt;
//        ImageView deleteBtn;
//        ConstraintLayout layBgItem;
//
//        titleTxt = binding.
//        encryptedTxt = parent.findViewById(R.id.encryptedTxt);
//        deleteBtn = parent.findViewById(R.id.deleteBtn);
//
//        titleTxt.setText(list.get(position).getTitle() + "");
//        encryptedTxt.setText(list.get(position).getTitle() + "");
//
//        parent.
//        //interview is feature



        return null;
    }
}
