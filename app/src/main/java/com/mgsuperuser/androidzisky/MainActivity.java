package com.mgsuperuser.androidzisky;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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


    public static final String INTENT_FILTER_PACKAGE = "co.zisky.android.TRANSACTION_CONFIRMATION"; // Manifest package-name
    private Button button;
    private TextView textView;

    private SharedPreferences sp;
    private Intent successIntent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeLogic();
        initialize(savedInstanceState);
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

        sp = getSharedPreferences("sp", Activity.MODE_PRIVATE);


    }

    private void initialize(Bundle savedInstanceState) {
    }


    private void initializeLogic() {
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(INTENT_FILTER_PACKAGE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(messageReceiver, intentFilter);

        if (textView.getText().toString().contains("successfully")) {
            sp.edit().putString("successTransaction", "successTransaction").apply();
            successIntent.setClass(getApplicationContext(), SuccessPage.class);
            startActivity(successIntent);
        }

        if (sp.getString("successTransaction", "").contains("successTransaction")) {
            successIntent.setClass(getApplicationContext(), SuccessPage.class);
            startActivity(successIntent);

        } else {
            Toast toast = Toast.makeText(this, "Looks like you haven't paid, pls pay", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (messageReceiver != null) {
            if (sp.getString("successTransaction", "").equals("successTransaction")) {
                successIntent.setClass(getApplicationContext(), SuccessPage.class);
                startActivity(successIntent);
            }
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

                if (parsed_variables != null && !parsed_variables.isEmpty() && parsed_variables.containsKey("confirmCode")) {
                    String defaultValue = "PP210301.1753.L69700";
                    String balance = parsed_variables.get("confirmCode");
                    if (balance != defaultValue) {
                        textView.setText(intent.getStringExtra(USSD_MESSAGE));

                        Toast toast = Toast.makeText(MainActivity.this, "So you've paid " + sp.getString("successTransaction", ""), Toast.LENGTH_LONG);
                        toast.show();

                        successIntent.setClass(getApplicationContext(), SuccessPage.class);
                        startActivity(successIntent);
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