package com.mgsuperuser.androidzisky;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

import co.zisky.ussd.sdk.ZiSky;
import co.zisky.ussd.sdk.model.ParserType;
import co.zisky.ussd.sdk.model.USSDParameters;

import static co.zisky.ussd.sdk.Constants.USSD_MESSAGE;
import static co.zisky.ussd.sdk.Constants.USSD_STATUS;

public class MainActivity extends AppCompatActivity {


    public static final String INTENT_FILTER_PACKAGE = "co.zisky.android.TRANSACTION_CONFIRMATION";
    private Button button;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ZiSky.init(this);
        button = findViewById(R.id.btn_check_balance);
        textView = findViewById(R.id.txt_balance);
        button.setOnClickListener(view -> {
            Intent intent = new USSDParameters
                    .Builder(getBaseContext())
                    .processId("603cdfb339856f5ad593d209")  //required Action ID
                    .build();

            startActivity(intent);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(INTENT_FILTER_PACKAGE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(messageReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (messageReceiver != null) {
            unregisterReceiver(messageReceiver);
        }
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            HashMap<String, String> parsed_variables =
                    (HashMap<String, String>) intent.getSerializableExtra("parsed_variables");

            Log.d("BALANCE", "" + parsed_variables + " status" + intent.getStringExtra(USSD_STATUS));
            if (intent.hasExtra(USSD_MESSAGE)
                    && intent.hasExtra(USSD_STATUS)
                    && intent.getStringExtra(USSD_STATUS).equals(ParserType.SUCCESS.toString())
                    && parsed_variables != null && !parsed_variables.isEmpty()) {
//                Intent successIntent = new Intent();
//                successIntent.setClass(getApplicationContext(), SuccessPage.class);
//                startActivity(successIntent);
                if (parsed_variables != null && !parsed_variables.isEmpty() && parsed_variables.containsKey("confirmCode")) {
                    String defaultValue = "PP210301.1753.L69700";
                    String balance = parsed_variables.get("confirmCode");
                    if (balance != defaultValue) {
                        textView.setText(intent.getStringExtra(USSD_MESSAGE));

                    } else if (intent.hasExtra(USSD_MESSAGE) && intent.hasExtra(USSD_STATUS)
                            && USSD_STATUS.equals(ParserType.PENDING.toString())) {
                        Toast.makeText(getBaseContext(), intent.getStringExtra(USSD_MESSAGE),
                                Toast.LENGTH_LONG).show();
                    }
                }

            } else if (intent.hasExtra(USSD_MESSAGE)
                    && intent.hasExtra(USSD_STATUS)
                    && intent.getStringExtra(USSD_STATUS).equals(ParserType.FAILED.toString())
                    && parsed_variables != null && !parsed_variables.isEmpty()) {

                textView.setText(intent.getStringExtra(USSD_MESSAGE));

            } else if (intent.hasExtra(USSD_MESSAGE) && intent.hasExtra(USSD_STATUS)
                    && intent.getStringExtra(USSD_STATUS).equals(ParserType.PENDING.toString())) {

                textView.setText(intent.getStringExtra(USSD_MESSAGE));
            }
        }
    };
}

// Buy airtime: *111*4*1*1*20*pin*1#