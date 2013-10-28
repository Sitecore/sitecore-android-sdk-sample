package net.sitecore.android.sdk.sample.js;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import net.sitecore.android.qr.QrReaderActivity;

public class QrReaderSample extends FragmentActivity {

    private static final int SCAN = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button button = new Button(this);
        button.setText("Open barcode scanner");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(QrReaderSample.this, QrReaderActivity.class);
                startActivityForResult(intent, SCAN);
            }
        });
        setContentView(button);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SCAN && resultCode == Activity.RESULT_OK) {
            String result  = data.getStringExtra(QrReaderActivity.EXTRA_SCAN_RESULT);
            Toast.makeText(this, "Scan result: " + result, Toast.LENGTH_LONG).show();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}