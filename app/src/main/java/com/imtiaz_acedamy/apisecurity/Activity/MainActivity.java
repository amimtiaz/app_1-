package com.imtiaz_acedamy.apisecurity.Activity;



import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.imtiaz_acedamy.apisecurity.Database.DB;
import com.imtiaz_acedamy.apisecurity.Database.DatabaseHelper;
import com.imtiaz_acedamy.apisecurity.R;
import com.imtiaz_acedamy.apisecurity.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends BaseActivity {

    ActivityMainBinding binding;
    ArrayList<HashMap<String, String>> arrayList;
    HashMap<String, String> hashMap;
    DB dbHelper;
    DatabaseHelper dbHelper2;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Database
        dbHelper = new DB(this);
        dbHelper2 = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        // set variable
        setVariable();
        bottomBarInit();
        loadData();

    }

    private void bottomBarInit() {
        binding.profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, ProfileActivity.class));

            }
        });
        binding.truckerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ExpenseTruckerActivity.class));
            }
        });
    }

    private void setVariable() {

        String getName = sharedPreferences.getString("name", "default value");
        String getImageString = sharedPreferences.getString("image", "default value");

        binding.nameTxt.setText(getName);

        // convert base64String to image
        String base64String = "data:image/png;base64,"+ getImageString;
        String base64Image = base64String.split(",")[1];

        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        binding.pic2.setImageBitmap(decodedByte);


        binding.progressBar.setVisibility(View.GONE);

        binding.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btn.setVisibility(View.GONE);
                requestServer();


                //objectRequest();
            }
        });

        binding.encryptBtn.setOnClickListener(v -> {
            try {

                if (binding.editTextText.getText().length()>0){
                    encrypt();

                    binding.lottieAnim.setVisibility(View.VISIBLE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.lottieAnim.setVisibility(View.GONE);
                            binding.copyBtn.setVisibility(View.VISIBLE);
                        }
                    }, 3500);




                    String title = binding.editTextText.getText().toString();
                    String encryptedData = binding.displayTxt.getText().toString();

                    dbHelper.addEncryptedData(title, encryptedData);
                    loadData();
                } else {
                    binding.editTextText.setError("Write Something!");
                    binding.displayTxt.setText("");
                }



            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        });

        binding.decryptBtn.setOnClickListener(v -> {


            try {
                decrypt();
                binding.lottieAnim.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.lottieAnim.setVisibility(View.GONE);
                        binding.copyBtn.setVisibility(View.VISIBLE);
                    }

                }, 3500);


            } catch (Exception e) {
                binding.editTextText.setError("Something Wrong!");
                binding.displayTxt.setText("");
            }


        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    // load data
    public void loadData(){
        Cursor cursor = null;

        cursor = dbHelper.getAllEncryptedData();

        if (cursor != null && cursor.getCount() > 0) {

            arrayList = new ArrayList<>();

            while (cursor.moveToNext()) {

                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                String encryptedTxt = cursor.getString(2);

                hashMap = new HashMap<>();
                hashMap.put("id", "" + id);
                hashMap.put("title", "" + title);
                hashMap.put("encryptedTxt", "" + encryptedTxt);
                arrayList.add(hashMap);

            }

            binding.listCountTxt.setText(arrayList.size()+"");

            binding.listView.setAdapter(new EncryptAdapter());

            binding.dataDisplay.setVisibility(View.GONE);
        } else {

            if ( cursor.getCount() > 0  ){

                binding.dataDisplay.setText("");

            } else {

                binding.dataDisplay.setVisibility(View.VISIBLE);
                binding.dataDisplay.setText("\nNo Data Found");
            }

        }
    }

    // Adapter Here
    public class EncryptAdapter extends BaseAdapter{

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

            TextView titleTxt, encryptedTxt;
            ImageView deleteBtn, updateBtn,copyBtn;
            ConstraintLayout layBgItem;

            titleTxt = view.findViewById(R.id.titleTxt);
            encryptedTxt = view.findViewById(R.id.encryptedTxt);
            deleteBtn = view.findViewById(R.id.deleteBtn);
            updateBtn = view.findViewById(R.id.updateBtn);
            copyBtn = view.findViewById(R.id.copyBtn);

            hashMap = arrayList.get(position);
            String id = hashMap.get("id");
            String title = hashMap.get("title");
            String encryptedData  = hashMap.get("encryptedTxt");

            titleTxt.setText(title);
            encryptedTxt.setText(encryptedData);

            //interview is feature

            if (ExpenseTruckerActivity.TRACKER==true){
                updateBtn.setVisibility(View.GONE);
                copyBtn.setVisibility(View.VISIBLE);
            }else {
                updateBtn.setVisibility(View.VISIBLE);
                copyBtn.setVisibility(View.GONE);
            }


                deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dbHelper.deleteEncryptedData(id);
                    loadData();
                    Toast.makeText(MainActivity.this, "successfully Deleted", Toast.LENGTH_SHORT).show();

                }
            });




            return view;
        }
    }
    //===============================================

    private void decrypt() throws Exception{

        if (binding.editTextText.getText().length() >= 0){

            String encodedString = binding.editTextText.getText().toString();
            byte[] decodedBytes = Base64.decode(encodedString, Base64.DEFAULT);

            String password = "nhSc7JAMcLAy%qCO";
            byte[] passwordBytes = password.getBytes("UTF-8");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(passwordBytes, "AES"));
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);

            String decryptedTxt = new String(decryptedBytes, "UTF-8");
            binding.displayTxt.setText(decryptedTxt);

            Toast.makeText(this, "Result Shown", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Null", Toast.LENGTH_SHORT).show();
        }



    }

    //=======================================================
    private void encrypt() throws Exception{

        String plainTxt = binding.editTextText.getText().toString();
        byte[] plainTxtBytes = plainTxt.getBytes("UTF-8");

        String password = "nhSc7JAMcLAy%qCO";
        byte[] passwordBytes = password.getBytes("UTF-8");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(passwordBytes, "AES"));
        byte[] secureByte = cipher.doFinal(plainTxtBytes);

        String encodedString = Base64.encodeToString(secureByte, Base64.DEFAULT);
        binding.displayTxt.setText(encodedString.toString()+"");

        Toast.makeText(this, "Result Shown", Toast.LENGTH_SHORT).show();
    }

    //===================================================

    //====================================================

    private void arrayRequest() {

        String url = "https://devimtiaz.000webhostapp.com/apps/API%20&%20SECURITY/view.php";

        JSONArray jsonArray = new JSONArray();

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("pass", "113355");
            jsonObject.put("email", "dev@gmail.com");

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        jsonArray.put(jsonObject);

        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.POST, url, jsonArray, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(arrayRequest);
    }



    private void objectRequest() {
        String url = "https://devimtiaz.000webhostapp.com/apps/API%20&%20SECURITY/view.php";

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("pass", "113355");
            jsonObject.put("email", "dev@gmail.com");

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                binding.progressBar.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        binding.btn.setVisibility(View.VISIBLE);

                    }
                }, 300);


                Dialog builder = new Dialog(MainActivity.this);
                final View customLayout = getLayoutInflater().inflate(R.layout.alert_layout, null);
                builder.setContentView(customLayout);

                TextView title = customLayout.findViewById(R.id.titleTv);
                TextView tvDisplay = customLayout.findViewById(R.id.tvDisplay);
                Button btn = customLayout.findViewById(R.id.okBtn);

                title.setText("Server Response");

                tvDisplay.setText(response.toString());


                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        builder.dismiss();

                    }
                });
                builder.show();


                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(builder.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                builder.getWindow().setAttributes(layoutParams);

                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) customLayout.getLayoutParams();
                params.leftMargin = 50;
                params.rightMargin = 50;

                builder.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Dialog builder = new Dialog(MainActivity.this);
                final View customLayout = getLayoutInflater().inflate(R.layout.alert_layout, null);
                builder.setContentView(customLayout);

                TextView title = customLayout.findViewById(R.id.titleTv);
                TextView tvDisplay = customLayout.findViewById(R.id.tvDisplay);
                Button btn = customLayout.findViewById(R.id.okBtn);

                title.setText("Server Response");

                tvDisplay.setText("Error: "+error.getMessage());

                binding.progressBar.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        binding.btn.setVisibility(View.VISIBLE);

                    }
                }, 300);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        builder.dismiss();

                    }
                });
                builder.show();


                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(builder.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                builder.getWindow().setAttributes(layoutParams);

                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) customLayout.getLayoutParams();
                params.leftMargin = 50;
                params.rightMargin = 50;

                builder.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(jsonObjectRequest);
    }


    //=====================================
    private void requestServer() {

        String url = "https://devimtiaz.000webhostapp.com/apps/API%20&%20SECURITY/view.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                binding.progressBar.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        binding.btn.setVisibility(View.VISIBLE);

                    }
                }, 300);



                Dialog builder = new Dialog(MainActivity.this);
                final View customLayout = getLayoutInflater().inflate(R.layout.alert_layout, null);
                builder.setContentView(customLayout);

                TextView title = customLayout.findViewById(R.id.titleTv);
                TextView tvDisplay = customLayout.findViewById(R.id.tvDisplay);
                Button btn = customLayout.findViewById(R.id.okBtn);

                title.setText("Server Response");

                tvDisplay.setText(response);


                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        builder.dismiss();

                    }
                });
                builder.show();


                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                //layoutParams.copyFrom(builder.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                builder.getWindow().setAttributes(layoutParams);




                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) customLayout.getLayoutParams();
                params.leftMargin = 50;
                params.rightMargin = 50;

                builder.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Dialog builder = new Dialog(MainActivity.this);
                final View customLayout = getLayoutInflater().inflate(R.layout.alert_layout, null);
                builder.setContentView(customLayout);

                TextView title = customLayout.findViewById(R.id.titleTv);
                TextView tvDisplay = customLayout.findViewById(R.id.tvDisplay);
                Button btn = customLayout.findViewById(R.id.okBtn);

                title.setText("Server Response");

                tvDisplay.setText(error.getMessage());


                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        builder.dismiss();

                    }
                });
                builder.show();
                binding.progressBar.setVisibility(View.GONE);
                binding.btn.setVisibility(View.VISIBLE);


                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(builder.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                builder.getWindow().setAttributes(layoutParams);

                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) customLayout.getLayoutParams();
                params.leftMargin = 50;
                params.rightMargin = 50;

                builder.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            }
        }) {

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map myMap = new HashMap<String, String>();
                myMap.put("pass", "113355");
                myMap.put("mail", "dev@gmail.com");

                return myMap;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(request);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        setVariable();
    }
}

