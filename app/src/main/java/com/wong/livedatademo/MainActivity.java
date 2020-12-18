package com.wong.livedatademo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.wong.livedatademo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private WnLiveData<String> liveData;
    private ActivityMainBinding binding;
    private int id = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        liveData = new WnLiveData<>();
        liveData = LiveDataManager.getLiveData(String.class);
//        LiveDataManager.getInstance().putLiveData("Main",liveData);
        liveData.observe(MainActivity.this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.i("接收到数据",s+"");
                if (id == binding.button.getId()) {
                    binding.button.setText(s);
                }
                if (id == binding.button2.getId()) {
                    binding.button2.setText(s);
                }

            }
        });

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = binding.button2.getId();
                liveData.setValue("888");

            }
        });

        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = binding.button.getId();
                liveData.setValue("9999");
                startActivity(new Intent(MainActivity.this,AnotherActivity.class));
            }
        });
    }

}