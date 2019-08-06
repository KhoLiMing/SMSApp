package sg.edu.rp.c346.smsapp;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    Button btn;
    Button Msg;
    private MessageReceiver br;
    EditText phNm;
    EditText Cont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();

        btn=findViewById(R.id.button);
        Msg=findViewById(R.id.buttonViaMsg);
        phNm=findViewById(R.id.editTextNumber);
        Cont=findViewById(R.id.editTextContent);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numbers[] = phNm.getText().toString().split(", *");
                SmsManager smsManager = SmsManager.getDefault();
                for(String number : numbers) {
                    smsManager.sendTextMessage(number,null,Cont.getText().toString(), null, null);
                }
                phNm.setText("");
                Cont.setText("");
            }
        });

        Msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Uri smsUri = Uri.parse("smsto:" + phNm.getText().toString());
                Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
                intent.putExtra("address", phNm.getText().toString());
                intent.putExtra("sms_body", Cont.getText().toString());
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
        br = new MessageReceiver();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        this.registerReceiver(br,filter);
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        this.unregisterReceiver(br);
    }
    private void checkPermission() {
        int permissionSendSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int permissionRecvSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);
        if (permissionSendSMS != PackageManager.PERMISSION_GRANTED &&
                permissionRecvSMS != PackageManager.PERMISSION_GRANTED) {
            String[] permissionNeeded = new String[]{Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS};
            ActivityCompat.requestPermissions(this, permissionNeeded, 1);
        }
    }

}
