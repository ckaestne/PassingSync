package com.example.android.passingsync;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.android.passingsync.pattern.AbstractPatternGenerator;
import com.example.android.passingsync.pattern.SiteswapGenerator;
import com.example.android.passingsync.pattern.SyncPatternGenerator;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class RunSiteswapMasterActivity extends ActionBarActivity {

    private int speed = 80;
    private boolean skipUpdate = false;
    private Timer timer = new Timer("timer", true);

    private String siteswap;
    private SiteswapFragment siteswapFragment;
    private AbstractPatternGenerator pattern;
    private char siteswapkind;

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
        siteswapkind = intent.getCharExtra(MainActivity.EXTRA_SITESWAP_KIND, 'W');


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

    private void createPattern() {
        if (siteswapkind == 'S')
            pattern = new SyncPatternGenerator(siteswap);
        else
            pattern = new SiteswapGenerator(siteswap, 0);
        siteswapFragment.setStart(pattern.getStart(AbstractPatternGenerator.Passer.A));
        siteswapFragment.setDisplay(pattern.getDisplay(AbstractPatternGenerator.Passer.A), AbstractPatternGenerator.Passer.A);
        updateRemoteDisplay(pattern.getDisplay(AbstractPatternGenerator.Passer.B));
        updateRemoteStart(pattern.getStart(AbstractPatternGenerator.Passer.B));

        siteswapFragment.resetSiteswap();

    }


    @Override
    protected void onStart() {
        super.onStart();
        createPattern();
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

        createPattern();

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
        Map<AbstractPatternGenerator.Passer, Pair<AbstractPatternGenerator.Side, Character>> actions = pattern.step();
        for (final Map.Entry<AbstractPatternGenerator.Passer, Pair<AbstractPatternGenerator.Side, Character>> action : actions.entrySet()) {
            final Character pass = action.getValue().second;
            final AbstractPatternGenerator.Side side = action.getValue().first;
            final AbstractPatternGenerator.Passer passer = action.getKey();


            updateRemoteDisplay(pattern.getDisplay(AbstractPatternGenerator.Passer.B));

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (passer == AbstractPatternGenerator.Passer.A)
                        siteswapFragment.setThrow(pass, side);
                    siteswapFragment.setDisplay(pattern.getDisplay(AbstractPatternGenerator.Passer.A), AbstractPatternGenerator.Passer.A);
                }
            });

            if (action.getKey() == AbstractPatternGenerator.Passer.A)
                playSound(pass);
            else
                getBluetoothService().pass(pass);


        }


    }

    private void updateRemoteDisplay(AbstractPatternGenerator.Display display) {
        getBluetoothService().updateDisplay(display);
    }


    private void updateRemoteStart(AbstractPatternGenerator.StartPos start) {
        getBluetoothService().updateStart(start);
    }

    private BluetoothService getBluetoothService() {
        return ((PassingSyncApplication) getApplicationContext()).getBluetoothService();
    }

    private void playSound(Character pass) {
        ((PassingSyncApplication) getApplicationContext()).speech(pass);
    }


    public void stopPassing(View v) {
        timer.cancel();
    }
}
