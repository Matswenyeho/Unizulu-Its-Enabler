package com.example.unizuluitsenabler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {
        Handler handler;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            setTitle("");

            handler = new Handler();
            handler.postDelayed(() -> {
                Intent intent = new Intent(MainActivity.this,RegistrationActivity.class);
                startActivity(intent);
                finish();
            },3000);
        }
    }