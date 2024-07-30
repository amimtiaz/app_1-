package com.imtiaz_acedamy.apisecurity;

import static com.imtiaz_acedamy.apisecurity.R.layout.activity_save_dic;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.imtiaz_acedamy.apisecurity.Activity.BaseActivity;
import com.imtiaz_acedamy.apisecurity.Database.DicSavedDB;
import com.imtiaz_acedamy.apisecurity.databinding.ActivitySaveDicBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class SaveDicActivity extends BaseActivity {

    ActivitySaveDicBinding binding;

    DicSavedDB db;
    ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();
    HashMap<String, String> hashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySaveDicBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = new DicSavedDB(this);

        Cursor cursor = db.getAllWordData();



        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String word = cursor.getString(1);
                String meaning = cursor.getString(2);
                String example = cursor.getString(3);

                hashMap = new HashMap<>();
                hashMap.put("id", String.valueOf(id));
                hashMap.put("word", word);
                hashMap.put("meaning",meaning);
                hashMap.put("example", example);
                arrayList.add(hashMap);
            }



        }
        binding.viewList.setAdapter(new MyAdapter());

        binding.backBtn.setOnClickListener(v -> {
            onBackPressed();
        });

    }

    public class MyAdapter extends BaseAdapter{

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

            View view = getLayoutInflater().inflate(R.layout.item, parent,false);

            TextView word = view.findViewById(R.id.word);
            TextView meaning = view.findViewById(R.id.meaning);
            TextView example = view.findViewById(R.id.example);
            ImageView saveBtn = view.findViewById(R.id.saveBtn);

            hashMap = arrayList.get(position);
            String wordTxt = hashMap.get("word");
            String meaningTxt = hashMap.get("meaning");
            String exampleTxt = hashMap.get("example");

            word.setText(wordTxt);
            meaning.setText(meaningTxt);
            example.setText(exampleTxt);

            saveBtn.setVisibility(View.GONE);



            return view;
        }
    }
}