package com.example.win_p.toyvpn;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.VpnService;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ToyVpnClient extends Activity {
    public interface Prefs {
        String NAME = "connection";
        String SERVER_ADDRESS = "server.address";
        String SERVER_ADDRESS2 = "server.address2";
        String SERVER_PORT = "server.port";
        String SHARED_SECRET = "shared.secret";
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toy_vpn_client);
        final TextView serverAddress = (TextView) findViewById(R.id.address);
        final TextView serverAddress2 = (TextView) findViewById(R.id.address2);
        final TextView serverPort = (TextView) findViewById(R.id.port);
        final TextView sharedSecret = (TextView) findViewById(R.id.secret);
        final SharedPreferences prefs = getSharedPreferences(Prefs.NAME, MODE_PRIVATE);
        serverAddress.setText(prefs.getString(Prefs.SERVER_ADDRESS, ""));
        serverAddress2.setText(prefs.getString(Prefs.SERVER_ADDRESS2, ""));
        serverPort.setText(prefs.getString(Prefs.SERVER_PORT, ""));
        sharedSecret.setText(prefs.getString(Prefs.SHARED_SECRET, ""));
        findViewById(R.id.connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefs.edit()
                        .putString(Prefs.SERVER_ADDRESS, serverAddress.getText().toString())
                        .putString(Prefs.SERVER_ADDRESS2, serverAddress2.getText().toString())
                        .putString(Prefs.SERVER_PORT, serverPort.getText().toString())
                        .putString(Prefs.SHARED_SECRET, sharedSecret.getText().toString())
                        .commit();

                Intent intent = VpnService.prepare(ToyVpnClient.this);
                if (intent != null) {
                    startActivityForResult(intent, 0);
                } else {
                    onActivityResult(0, RESULT_OK, null);
                }
            }
        });
        findViewById(R.id.disconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(getServiceIntent().setAction(ToyVpnService.ACTION_DISCONNECT));
            }
        });
    }
    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        if (result == RESULT_OK) {
            startService(getServiceIntent().setAction(ToyVpnService.ACTION_CONNECT));
        }
    }
    private Intent getServiceIntent() {
        return new Intent(this, ToyVpnService.class);
    }

    public void checkStatus(View view){
        Intent intent = new Intent( this, checkStatusActivity.class);
        startActivity(intent);
    }
}
