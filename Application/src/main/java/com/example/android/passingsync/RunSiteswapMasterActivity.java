package com.example.android.passingsync;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class RunSiteswapMasterActivity extends ActionBarActivity {

    private int speed = 80;
    private boolean skipUpdate = false;
    private Timer timer = new Timer("timer", true);

    private int sw_position = 0;
    private int sw_hand = 0;
    private String siteswap;
    private SiteswapFragment siteswapFragment;

    @Override
    protected void onDestroy() {
        timer.cancel();
//        for (MediaPlayer m: mediaPlayer.values())
//            m.release();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_run_siteswap);
        Intent intent = getIntent();
        siteswap = intent.getStringExtra(MainActivity.EXTRA_SITESWAP);


        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            siteswapFragment = SiteswapFragment.newInstance();
            transaction.replace(R.id.body, siteswapFragment);
            transaction.commit();
        }


//        mSiteswapList = (ListView) view.findViewById(R.id.siteswaplist);
        View view = findViewById(R.id.runsiteswaplayout);
        TextView siteswapName = (TextView) view.findViewById(R.id.siteswapName);
        siteswapName.setText(siteswap);

        final EditText speedEdit = (EditText) view.findViewById(R.id.speedEdit);
        final SeekBar speedSeekbar = (SeekBar) view.findViewById(R.id.speedSeekbar);
     

        speedEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    speed = Math.min(300, Math.max(1, Integer.valueOf(speedEdit.getText().toString())));
                    if (!skipUpdate)
                        speedSeekbar.setProgress(Math.min(100, Math.max(0, speed - 50)));
                } catch (NumberFormatException e) {
                    speedEdit.setText("" + speed);
                }
            }
        });

        speedSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                    @Override
                                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                        if (fromUser) {
                                                            skipUpdate = true;
                                                            speedEdit.setText("" + (progress + 50));
                                                            skipUpdate = false;
                                                        }
                                                    }

                                                    @Override
                                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                                    }

                                                    @Override
                                                    public void onStopTrackingTouch(SeekBar seekBar) {

                                                    }
                                                }

        );


        speedEdit.setText("80");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_run_siteswap, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startPassing(View v) {
        timer.cancel();

        sw_hand = 0;
        sw_position = 0;
        siteswapFragment.resetSiteswap();

        timer = new Timer("timer", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                performPassingStep();

            }
        }, 1000, getDelay());
    }

    private long getDelay() {
        return 1000 * 60 / speed / 2;
    }

    private synchronized void performPassingStep() {
        sw_position = (sw_position + 1) % siteswap.length();
        sw_hand = (sw_hand + 1) % 4;
        final int pos = sw_position;
        final int hand = sw_hand;
        final Character pass=siteswap.charAt(pos);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                siteswapFragment.setThrow(pass,hand);

            }
        });

        if (sw_hand % 2 == 0)
            playSound(pass);
        if (sw_hand % 2 == 1)
            getBluetoothService().pass(pass);
    }

    private BluetoothService getBluetoothService() {
        return ((PassingSyncApplication) getApplicationContext()).getBluetoothService();
    }

    private void playSound(Character pass) {
        ((PassingSyncApplication)getApplicationContext()).speech(pass);
    }


    public void stopPassing(View v) {
        timer.cancel();
    }
}
