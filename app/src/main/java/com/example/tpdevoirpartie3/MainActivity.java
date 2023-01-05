package com.example.tpdevoirpartie3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button buttonSendEvent;

    TextView textViewBatteryStatus;
    TextView textViewCallerNumber;

    MyBroadcastBatteryLow myBroadcastBatteryLow = new MyBroadcastBatteryLow();

    private IntentFilter batteryFilter = new IntentFilter();
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSendEvent = findViewById(R.id.button_send_event);

        buttonSendEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("FAKE_EVENT_INFO");
                sendBroadcast(intent);
            }
        });
        // Vérifie si la permission est accordée
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // Demande la permission si elle n'est pas accordée
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
        textViewBatteryStatus = findViewById(R.id.textView);
        batteryFilter.addAction(Intent.ACTION_BATTERY_LOW);
        textViewCallerNumber = findViewById(R.id.receive_call);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                // Si la demande est annulée, le tableau de résultats est vide
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // La permission est accordée, enregistrement du broadcast receiver
                    IntentFilter filter = new IntentFilter();
                    filter.addAction("android.intent.action.PHONE_STATE");
                    registerReceiver(new MyBroadcastCallReceiver(), filter);
                } else {
                    // La permission est refusée, affiche un message d'erreur
                    Toast.makeText(getApplicationContext(), "Permission refusée, appel entrant non affiché", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(myBroadcastBatteryLow, batteryFilter);
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myBroadcastBatteryLow);
    }



    public class MyBroadcastBatteryLow extends MyReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            textViewBatteryStatus.setText("Evenement Batterie faible reçu");
        }
    }
    private class MyBroadcastCallReceiver extends MyReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String incomingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            textViewCallerNumber.setText("Appel entrant de : " + incomingNumber);
        }
    }


}



