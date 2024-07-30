package com.imtiaz_acedamy.apisecurity.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.imtiaz_acedamy.apisecurity.Database.DatabaseHelper2;
import com.imtiaz_acedamy.apisecurity.Database.DicSavedDB;
import com.imtiaz_acedamy.apisecurity.R;
import com.imtiaz_acedamy.apisecurity.SaveDicActivity;
import com.imtiaz_acedamy.apisecurity.databinding.ActivityDictionaryBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class DictionaryActivity extends AppCompatActivity {

    ActivityDictionaryBinding binding;
    ArrayList<HashMap<String, String>>  arrayList = new ArrayList<>(); ;
    HashMap<String, String> hashMap;
    DicSavedDB db2;
    DatabaseHelper2 db;
    SharedPreferences sharedPreferences,sharedPreferences2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDictionaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = new DatabaseHelper2(this);
        db2 = new DicSavedDB(this);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.anim_1);

        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                arrayList.clear();

                String wordToSearch = binding.searchTxt.getText().toString();

                Cursor cursor = db.searchDataById(wordToSearch);



                if (cursor != null && cursor.getCount() > 0) {

                    while (cursor.moveToNext()) {
                        int id = cursor.getInt(0);
                        String word = cursor.getString(1);
                        String meaning = cursor.getString(2);
                        String partOfSpeech = cursor.getString(3); // You might not need this
                        String example = cursor.getString(4);

                        hashMap = new HashMap<>();
                        hashMap.put("id", String.valueOf(id));
                        hashMap.put("word", word);
                        hashMap.put("meaning",meaning);
                        hashMap.put("partOfSpeech", partOfSpeech); // You might not need this
                        hashMap.put("example", example);
                        arrayList.add(hashMap);
                    }


                }

                if (cursor != null && cursor.getCount() > 0){
                    binding.searchAlert.setVisibility(View.GONE);
                    binding.listView.setVisibility(View.VISIBLE);
                    binding.listView.setAdapter(new MY_ADAPTER());
                    binding.listView.setLayoutAnimation(new LayoutAnimationController(animation));


                } else {
                    binding.searchAlert.setVisibility(View.VISIBLE);
                    binding.listView.setVisibility(View.GONE);
                    binding.listView.setVisibility(View.GONE);
                }
            }
        });
        
        binding.showSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               startActivity(new Intent(DictionaryActivity.this, SaveDicActivity.class));
                
            }
        });


        binding.back.setOnClickListener(v -> {
            onBackPressed();
        });

        sharedPreferences2 = getSharedPreferences(getString(R.string.PROFILE_DATA), MODE_PRIVATE);

        sharedPreferVaribale();


    }

    private void sharedPreferVaribale() {

        String getImageString = sharedPreferences2.getString("image", null);
        String getNameStr = sharedPreferences2.getString("name", null);

        String base64String = "data:image/png;base64,"+ getImageString;
        String base64Image = base64String.split(",")[1];

        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        binding.pic.setImageBitmap(decodedByte);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.pic.setTooltipText(getNameStr);

        }
    }

    public class MY_ADAPTER extends BaseAdapter {

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
            View myView = getLayoutInflater().inflate(R.layout.item, parent,false);

            TextView word = myView.findViewById(R.id.word);
            TextView meaning = myView.findViewById(R.id.meaning);
            TextView example = myView.findViewById(R.id.example);
            ImageView saveBtn = myView.findViewById(R.id.saveBtn);

            hashMap = arrayList.get(position);
            String wordTxt = hashMap.get("word");
            String meaningTxt = hashMap.get("meaning");
            String exampleTxt = hashMap.get("example");


            word.setText(wordTxt);
            meaning.setText(meaningTxt);
            example.setText(exampleTxt);

            saveBtn.setOnClickListener(v -> {

                db2.addWordData(wordTxt, meaningTxt, exampleTxt);
                Toast.makeText(DictionaryActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            });



            return myView;
        }
    }
}