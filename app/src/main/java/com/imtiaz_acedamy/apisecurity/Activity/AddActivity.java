package com.imtiaz_acedamy.apisecurity.Activity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.imtiaz_acedamy.apisecurity.Database.DatabaseHelper;
import com.imtiaz_acedamy.apisecurity.R;
import com.imtiaz_acedamy.apisecurity.databinding.ActivityAddBinding;
import com.imtiaz_acedamy.apisecurity.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class AddActivity extends BaseActivity {

    ActivityAddBinding binding;
    public static boolean  EXPENSE = true;
    DatabaseHelper dbHelper;
    ArrayList<HashMap<String, String>> arrayList;
    HashMap<String, String> hashMap;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);

//        if (EXPENSE == true) {
//            binding.titleTxt.setText("Expense Added Successfully!");
//        } else {
//            binding.titleTxt.setText("Income Added Successfully!");
//        }
        if (EXPENSE == true){
            binding.titleTxt.setText("Add Expense");
            //binding.titleTxt.setTextColor(R.color.income);
        } else {
            binding.titleTxt.setText("Add Income");
            //binding.titleTxt.setTextColor(R.color.expense);
        }

        setVariable();
        loadData();
    }

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

            amountTxt.setText(amount);
            reasonTxt.setText(reason);

            //interview is feature
            if (ExpenseTruckerActivity.TRACKER==true){
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
                    Toast.makeText(AddActivity.this, "successfully Deleted", Toast.LENGTH_SHORT).show();

                }
            });


//            updateBtn.setOnClickListener(v -> {
//
//                if (EXPENSE == true) {
//                    dbHelper.updateExpense(id);
//                    loadData();
//
//                } else {
//                    dbHelper.updateIncome(id);
//                    loadData();
//                }
//                Toast.makeText(AddActivity.this, "successfully Updated", Toast.LENGTH_SHORT).show();
//
//            });


            return view;
        }
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.insertBtn.setOnClickListener(v -> {

            String amountTxt = binding.amountTxt.getText().toString();
            String reasonTxt = binding.reasonTxt.getText().toString();
            double amount = Double.parseDouble(amountTxt);

            if (EXPENSE == true){
                dbHelper.addExpense(amount,reasonTxt);
                binding.titleTxt.setText("Expense Added!");
                loadData();
            }else {
                dbHelper.addIncome(amount, reasonTxt);
                binding.titleTxt.setText("Income Added!");
                loadData();
            }

            binding.titleTxt.setText("Data Inserted!");
        });



    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadData();
    }
}