package com.wong.livedatademo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.wong.livedatademo.databinding.ActivityAnotherBinding;

public class AnotherActivity extends AppCompatActivity {

    ActivityAnotherBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_another);
        binding.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WnLiveData<String> liveData = (WnLiveData<String>)LiveDataManager.getInstance().getLiveData("Main");
                if(liveData == null)return;
                liveData.postValue("Hello World&&&");
            }
        });
    }
}