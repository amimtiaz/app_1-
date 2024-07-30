package com.imtiaz_acedamy.apisecurity.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.imtiaz_acedamy.apisecurity.Database.DatabaseHelper;
import com.imtiaz_acedamy.apisecurity.R;
import com.imtiaz_acedamy.apisecurity.databinding.ActivityExpenseTruckerBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class ExpenseTruckerActivity extends BaseActivity {

    ActivityExpenseTruckerBinding binding;
    DatabaseHelper dbHelper;
    ArrayList<HashMap<String, String>> arrayList;
    HashMap<String, String> hashMap;
    public static  boolean  EXPENSE = false;
    public static boolean TRACKER = true;
    SharedPreferences sharedPreferences, sharedPreferences2;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExpenseTruckerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        sharedPreferences2 = getSharedPreferences(getString(R.string.PROFILE_DATA), MODE_PRIVATE);

        setVariable();
        upToDateUI();
        loadData();
        sharedPreferVaribale();
    }

    private void sharedPreferVaribale() {

        String getImageString = sharedPreferences2.getString("image", null);
        String getNameStr = sharedPreferences2.getString("name", null);

        String base64String = "data:image/png;base64,"+ getImageString;
        String base64Image = base64String.split(",")[1];

        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        binding.picUser.setImageBitmap(decodedByte);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.picUser.setTooltipText(getNameStr);

        }
    }

    // load data
    public void loadData(){
        Cursor cursor = null;

        if (EXPENSE == true) {
            cursor = dbHelper.getExpenseAllData();
        }else {
            cursor = dbHelper.getIncomeAllData();
        }


        if (cursor != null && cursor.getCount() > 0) {

            arrayList = new ArrayList<>();

            while (cursor.moveToNext()) {

                int id = cursor.getInt(0);
                String amount = cursor.getString(1);
                String reason = cursor.getString(2);

                hashMap = new HashMap<>();
                hashMap.put("id", "" + id);
                hashMap.put("amount", "" + amount);
                hashMap.put("reason", "" + reason);
                arrayList.add(hashMap);

            }

            //binding.listCountTxt.setText(arrayList.size()+"");

            binding.view.setAdapter(new incomeAdapter());

            //binding.dataDisplay.setVisibility(View.GONE);
        } else {

            if ( cursor.getCount() > 0  ){

                //binding.dataDisplay.setText("");

            } else {

                //binding.dataDisplay.setVisibility(View.VISIBLE);
                //binding.dataDisplay.setText("\nNo Data Found");
            }

        }
    }

    // Adapter Here
    public class incomeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return arrayList.size();
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

            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.encrypt_item, parent, false);

            TextView amountTxt, reasonTxt;
            ImageView deleteBtn, updateBtn,copyBtn;
            ConstraintLayout layBgItem;

            amountTxt = view.findViewById(R.id.titleTxt);
            reasonTxt = view.findViewById(R.id.encryptedTxt);
            deleteBtn = view.findViewById(R.id.deleteBtn);
            updateBtn = view.findViewById(R.id.updateBtn);
            copyBtn = view.findViewById(R.id.copyBtn);

            hashMap = arrayList.get(position);
            String id = hashMap.get("id");
            String amount = hashMap.get("amount");
            String reason  = hashMap.get("reason");

            amountTxt.setText( amount+"$");
            reasonTxt.setText(reason);

            //interview is feature
            if (TRACKER==true){
                updateBtn.setVisibility(View.VISIBLE);
                copyBtn.setVisibility(View.GONE);
            }else {
                updateBtn.setVisibility(View.GONE);
                copyBtn.setVisibility(View.VISIBLE);
            }


            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (EXPENSE == true) {
                        dbHelper.deleteExpense(id);
                        loadData();
                    } else {
                        dbHelper.deleteIncome(id);
                        loadData();
                    }
                    Toast.makeText(ExpenseTruckerActivity.this, "successfully Deleted", Toast.LENGTH_SHORT).show();

                }
            });


            updateBtn.setOnClickListener(v -> {

                if (EXPENSE == true) {
                    dbHelper.updateExpense(id);
                    loadData();
                } else {
                    dbHelper.updateIncome(id);
                    loadData();
                }
                Toast.makeText(ExpenseTruckerActivity.this, "successfully Updated", Toast.LENGTH_SHORT).show();




            });



            return view;
        }
    }

    private void upToDateUI() {


        binding.totalExpenseTxt.setText("USD " + dbHelper.calculateTotalExpense());
        binding.totalIncomeTxt.setText("USD " + dbHelper.calculateTotalIncome());

        double balance = dbHelper.calculateTotalIncome() - dbHelper.calculateTotalExpense();

        binding.finalBalanceTxt.setText("USD " + balance);

    }

    private void setVariable() {

        binding.backBtn.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.addIncomeTxt.setOnClickListener(v -> {
            AddActivity.EXPENSE = false;
            startActivity(new Intent(this, AddActivity.class));
        });

        binding.addExpenseBtn.setOnClickListener(v -> {
            AddActivity.EXPENSE = true;
            startActivity(new Intent(this, AddActivity.class));
        });

        if (EXPENSE == false){
            binding.view1.setVisibility(View.VISIBLE);
            binding.view2.setVisibility(View.GONE);
            binding.displayExpenseBtn.setVisibility(View.GONE);
            binding.displayIncomeBtn.setVisibility(View.VISIBLE);
            loadData();
        }

        binding.showExpenseBtn.setOnClickListener(v -> {
            EXPENSE = true;

            binding.view1.setVisibility(View.GONE);
            binding.view2.setVisibility(View.VISIBLE);
            binding.displayIncomeBtn.setVisibility(View.GONE);
            binding.displayExpenseBtn.setVisibility(View.VISIBLE);
            loadData();

        });

        binding.showIncomeBtn.setOnClickListener(v -> {
            EXPENSE = false;
            binding.view1.setVisibility(View.VISIBLE);
            binding.view2.setVisibility(View.GONE);
            binding.displayExpenseBtn.setVisibility(View.GONE);
            binding.displayIncomeBtn.setVisibility(View.VISIBLE);
            loadData();
        });

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        upToDateUI();
        loadData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}