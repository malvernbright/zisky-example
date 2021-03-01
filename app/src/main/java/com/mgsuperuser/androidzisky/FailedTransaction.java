package com.mgsuperuser.androidzisky;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class FailedTransaction extends AppCompatActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_failed_transaction);

        Intent intent=new Intent(FailedTransaction.this, MainActivity.class);

        textView.setText(intent.getStringExtra("balance"));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}