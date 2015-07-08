package com.example.android.passingsync;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class RunSiteswapActivity extends ActionBarActivity {

    private int speed = 80;
    private boolean skipUpdate = false;
    private Timer timer = new Timer("timer", true);
    private final Map<String,MediaPlayer> mediaPlayer=new HashMap<>();
    private TextView mLHLabel;
    private TextView mRHLabel;

    private int sw_position = 0;
    private int sw_hand = 0;
    private String siteswap;

    @Override
    protected void onDestroy() {
        timer.cancel();
        for (MediaPlayer m: mediaPlayer.values())
            m.release();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_run_siteswap);
        Intent intent = getIntent();
        siteswap = intent.getStringExtra(MainActivity.EXTRA_SITESWAP);

//        mSiteswapList = (ListView) view.findViewById(R.id.siteswaplist);
        View view = findViewById(R.id.runsiteswaplayout);
        TextView siteswapName = (TextView) view.findViewById(R.id.siteswapName);
        siteswapName.setText(siteswap);

        final EditText speedEdit = (EditText) view.findViewById(R.id.speedEdit);
        final SeekBar speedSeekbar = (SeekBar) view.findViewById(R.id.speedSeekbar);
        mLHLabel = (TextView) view.findViewById(R.id.lhLabel);
        mRHLabel = (TextView) view.findViewById(R.id.rhLabel);
        mLHLabel.setText("");
        mRHLabel.setText("");

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
        mediaPlayer.put("0", MediaPlayer.create(this.getApplicationContext(), R.raw.p0));
        mediaPlayer.put("2", MediaPlayer.create(this.getApplicationContext(), R.raw.p2));
        mediaPlayer.put("4", MediaPlayer.create(this.getApplicationContext(), R.raw.p4));
        mediaPlayer.put("5", MediaPlayer.create(this.getApplicationContext(), R.raw.p5));
        mediaPlayer.put("6", MediaPlayer.create(this.getApplicationContext(), R.raw.p6));
        mediaPlayer.put("7", MediaPlayer.create(this.getApplicationContext(), R.raw.p7));
        mediaPlayer.put("8", MediaPlayer.create(this.getApplicationContext(), R.raw.p8));
        mediaPlayer.put("9", MediaPlayer.create(this.getApplicationContext(), R.raw.p9));
        mediaPlayer.put("a", MediaPlayer.create(this.getApplicationContext(), R.raw.p10));

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
        mRHLabel.setTextColor(Color.BLACK);
        mLHLabel.setTextColor(Color.BLACK);
        mRHLabel.setText("");
        mLHLabel.setText("");

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
        final String pass="" + siteswap.charAt(pos);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRHLabel.setTextColor(Color.BLACK);
                mLHLabel.setTextColor(Color.BLACK);
                if (hand == 0) {
                    mRHLabel.setText(pass);
                    mRHLabel.setTextColor(Color.RED);
                }
                if (hand == 2) {
                    mLHLabel.setText(pass);
                    mLHLabel.setTextColor(Color.RED);
                }

            }
        });

        if (sw_hand % 2 == 0)
            playSound(pass);
    }

    private MediaPlayer lastPlayer=null;
    private void playSound(String pass) {
        if (lastPlayer!=null && lastPlayer.isPlaying()) {
            lastPlayer.pause();
        }
        MediaPlayer player = mediaPlayer.get(pass);
        player.seekTo(0);
        player.start();
    }


    public void stopPassing(View v) {
        timer.cancel();
    }
}
