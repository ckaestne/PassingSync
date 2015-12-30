package edu.cmu.mastersofflyingobjects.passingsync;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class RandomSettingActivity extends AppCompatActivity {

    private PassingSyncApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_setting);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        View mStartRandomButton = findViewById(R.id.buttonSync);
        final SeekBar chanceSelf = (SeekBar) findViewById(R.id.chanceSelf);
        final SeekBar chanceDouble = (SeekBar) findViewById(R.id.chanceDouble);
        final SeekBar chanceSingle = (SeekBar) findViewById(R.id.chancePass);
        View mStartRandomSwButton = findViewById(R.id.buttonSiteswap);
        final CheckBox include2 = (CheckBox) findViewById(R.id.checkBox2);
        final CheckBox include4 = (CheckBox) findViewById(R.id.checkBox4);
        final CheckBox include5 = (CheckBox) findViewById(R.id.checkBox5);
        final CheckBox include6 = (CheckBox) findViewById(R.id.checkBox6);
        final CheckBox include7 = (CheckBox) findViewById(R.id.checkBox7);
        final CheckBox include8 = (CheckBox) findViewById(R.id.checkBox8);
        final CheckBox include9 = (CheckBox) findViewById(R.id.checkBox9);
        final EditText nrObjects = (EditText) findViewById(R.id.nrObjects);
        app = (PassingSyncApplication) getApplicationContext();

        // Initialize the send button with a listener that for click events
        mStartRandomButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget

                String config = chanceSelf.getProgress() + ";" + chanceSingle.getProgress() + ";" + chanceDouble.getProgress();

                MainActivity.startSiteswap(app.getBluetoothService(), RandomSettingActivity.this, 'R', config, "Random");

            }
        });

        // Initialize the send button with a listener that for click events
        mStartRandomSwButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                String config = "";
                if (include2.isChecked()) config += "2";
                if (include4.isChecked()) config += "4";
                if (include5.isChecked()) config += "5";
                if (include6.isChecked()) config += "6";
                if (include7.isChecked()) config += "7";
                if (include8.isChecked()) config += "8";
                if (include9.isChecked()) config += "9";
                if (config.isEmpty()) config = "2456789";
                config = nrObjects.getText() + ";" + config;

                MainActivity.startSiteswap(app.getBluetoothService(), RandomSettingActivity.this, 'T', config, "Random Siteswap");

            }
        });

    }

//    /**
//     * Sends a message.
//     *
//     * @param siteswap A string of text to send.
//     */
//    private void startSiteswap(Character kind, String siteswap) {
//        // Check that we're actually connected before trying anything
//        if (app.getBluetoothService().getState() != BluetoothService.STATE_CONNECTED) {
//            Toast.makeText(this, "Warning, starting siteswap without connection!", Toast.LENGTH_LONG).show();
//        }
//
//        // Check that there's actually something to send
//        if (siteswap.length() > 0) {
//            Intent intent = new Intent(this, RunSiteswapMasterActivity.class);
//            intent.putExtra(MainActivity.EXTRA_SITESWAP, siteswap);
//            intent.putExtra(MainActivity.EXTRA_SITESWAP_KIND, kind);
//            startActivity(intent);
//
//            app.getBluetoothService().startSiteswap(siteswap);
//        }
//    }

}
