package com.imtiaz_acedamy.apisecurity.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.imtiaz_acedamy.apisecurity.Database.DB;
import com.imtiaz_acedamy.apisecurity.R;
import com.imtiaz_acedamy.apisecurity.databinding.ActivityProfileBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends BaseActivity {

    ActivityProfileBinding binding;

    ArrayList<HashMap<String, String>> arrayList;
    HashMap<String, String> hashMap;
    DB dbHelper;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static boolean SHARED_PREF = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Database
        dbHelper = new DB(this);
        sharedPreferences = getSharedPreferences(getString(R.string.PROFILE_DATA), MODE_PRIVATE);



        setVariable();
        loadSharePre();


    }

    private void loadSharePre(){

        if (sharedPreferences != null){

            SHARED_PREF = true;
            binding.updateLayout.setVisibility(View.GONE);
            binding.showLayout.setVisibility(View.VISIBLE);

            String getName = sharedPreferences.getString("name", null);
            String getPhoneNo = sharedPreferences.getString("phoneNo", null);
            String getMail = sharedPreferences.getString("mail", null);
            String getImageString = sharedPreferences.getString("image", null);

            binding.tvName.setText(getName);
            binding.tvPhone.setText(getPhoneNo);
            binding.tvMail.setText(getMail);

            // convert base64String to image
            String base64String = "data:image/png;base64,"+ getImageString;
            String base64Image = base64String.split(",")[1];

            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            binding.pic2.setImageBitmap(decodedByte);

        }else {

            SHARED_PREF = false;
            binding.updateLayout.setVisibility(View.VISIBLE);
            binding.showLayout.setVisibility(View.GONE);
        }





    }



    private void setVariable() {

        binding.updateBtn.setOnClickListener(v -> {
            SHARED_PREF = false;
            binding.updateLayout.setVisibility(View.VISIBLE);
            binding.showLayout.setVisibility(View.GONE);

        });

        binding.backBtn.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.editPicBn.setOnClickListener(v -> {

            // dialog


            Dialog dialog = new Dialog(this);

            dialog.setContentView(R.layout.alert_layout_2);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            ImageView cameraIcon = dialog.findViewById(R.id.cameraIcon);
            ImageView galleryIcon = dialog.findViewById(R.id.galleryIcon);

            cameraIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (checkCameraPermission()){
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraLauncher.launch(intent);
                    }
                    dialog.dismiss();

                }
            });

            galleryIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        Intent gallaryIntent = new Intent(Intent.ACTION_PICK);
                        gallaryIntent.setType("image/*");
                        galleryLauncher.launch(gallaryIntent);

                        dialog.dismiss();
                    }catch (Exception e){
                        Log.d("Error", e.getMessage());
                    }



                }
            });

            dialog.show();
        });

        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    SHARED_PREF = true;
                    binding.updateLayout.setVisibility(View.GONE);
                    binding.showLayout.setVisibility(View.VISIBLE);

                    BitmapDrawable bitmapDrawable = (BitmapDrawable) binding.pic.getDrawable();
                    Bitmap bitmap = bitmapDrawable.getBitmap();

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

                    byte[] imageByte = byteArrayOutputStream.toByteArray();
                    String image64 = Base64.encodeToString(imageByte, Base64.DEFAULT);

                    //requestServer(image64);



                    String name = binding.nameTxt.getText().toString();
                    String phoneNo = binding.phoneTxt.getText().toString();
                    String mail = binding.mailTxt.getText().toString();

                    editor = sharedPreferences.edit();
                    editor.putString("name", name);
                    editor.putString("phoneNo", phoneNo);
                    editor.putString("mail", mail);
                    editor.putString("image", image64);
                    editor.apply();



                if (sharedPreferences!=null){
                    Toast.makeText(ProfileActivity.this, "Save Data", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Null", Toast.LENGTH_SHORT).show();
                }


                loadSharePre();

            }

        });

    }


    // request server
    private void requestServer(String image64){

        String url = "https://devimtiaz.000webhostapp.com/apps/API%20&%20SECURITY/uploadImageToServer.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map myMap = new HashMap<String, String>();
                myMap.put("image", image64);

                return myMap;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    // Camera Permission in real time
    private boolean checkCameraPermission() {

        boolean  hasPermission = false;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            hasPermission = true;
        } else {
            hasPermission = false;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
        return hasPermission;
    }




    // camera launcher
    ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        Bundle bundle = data.getExtras();
                        Bitmap bitmap = (Bitmap) bundle.get("data");
                        binding.pic.setImageBitmap(bitmap);
                    } else {
                        Toast.makeText(ProfileActivity.this, "Camera Cancel", Toast.LENGTH_SHORT).show();
                    }

                }
            });



    //Gallery Launcher
    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {

                    if (o.getResultCode() == Activity.RESULT_OK){
                        Intent data = o.getData();
                        Uri uri = data.getData();

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            binding.pic.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }
            });


    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadSharePre();
    }


}