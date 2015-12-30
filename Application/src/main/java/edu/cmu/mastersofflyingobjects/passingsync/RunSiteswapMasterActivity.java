package edu.cmu.mastersofflyingobjects.passingsync;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import edu.cmu.mastersofflyingobjects.passingsync.pattern.AbstractPatternGenerator;
import edu.cmu.mastersofflyingobjects.passingsync.pattern.RandomSiteswapGenerator;
import edu.cmu.mastersofflyingobjects.passingsync.pattern.RandomSyncGenerator;
import edu.cmu.mastersofflyingobjects.passingsync.pattern.SiteswapGenerator;
import edu.cmu.mastersofflyingobjects.passingsync.pattern.SyncPatternGenerator;

import java.util.Map;
import java.util.Random;
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
    private PowerManager.WakeLock wakeLock;
    int metronomeDelay = 500;

    @Override
    protected void onStop() {
        super.onStop();
        wakeLock.release();
        timer.cancel();
    }


    int seed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        seed = new Random().nextInt();

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
        final View view = findViewById(R.id.runsiteswaplayout);
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

        final SeekBar metronomeDelaySeekBar = (SeekBar) view.findViewById(R.id.metronomeOffsetSeekBar);
        metronomeDelaySeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        updateMetronomeSettings(view);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );

        final CheckBox metronomeEnabledCheckBox = (CheckBox) view.findViewById((R.id.enableMetronomeCheckBox));
        metronomeEnabledCheckBox.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        updateMetronomeSettings(view);
                    }
                }
        );

        speedEdit.setText("80");
        updateMetronomeSettings(view);
    }

    private void updateMetronomeSettings(View view) {
        final CheckBox metronomeEnabledCheckBox = (CheckBox) view.findViewById(R.id.enableMetronomeCheckBox);
        final SeekBar metronomeDelaySeekBar = (SeekBar) view.findViewById(R.id.metronomeOffsetSeekBar);

        if (metronomeEnabledCheckBox.isChecked()) {
            metronomeDelay = (int) ((metronomeDelaySeekBar.getProgress() / 100.0) * (60000.0 / speed));
        } else {
            metronomeDelay = -1;
        }
        updateRemoteMetronomeDelay(metronomeDelay);
    }

    private void createPattern() {

        if (siteswapkind == 'S')
            pattern = new SyncPatternGenerator(siteswap);
        else if (siteswapkind == 'R')
            pattern = new RandomSyncGenerator(siteswap);
        else if (siteswapkind == 'T')
            pattern = new RandomSiteswapGenerator(seed, siteswap);
        else
            pattern = new SiteswapGenerator(siteswap, 0);
        siteswapFragment.setStart(pattern.getStart(AbstractPatternGenerator.Passer.A));
        siteswapFragment.setDisplay(pattern.getDisplay(AbstractPatternGenerator.Passer.A), AbstractPatternGenerator.Passer.A);
        updateRemoteDisplay(pattern.getDisplay(AbstractPatternGenerator.Passer.B));
        updateRemoteStart(pattern.getStart(AbstractPatternGenerator.Passer.B));
        updateRemoteMetronomeDelay(metronomeDelay);

        siteswapFragment.resetSiteswap();

    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.w("PASS", "onStart");

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        wakeLock.acquire();

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

    private void updateRemoteMetronomeDelay(int delay) {
        getBluetoothService().updateMetronomeDelay(delay);
    }

    private BluetoothService getBluetoothService() {
        return ((PassingSyncApplication) getApplicationContext()).getBluetoothService();
    }

    private final Handler handler = new Handler();

    private void playSound(Character pass) {
        getApp().speech(pass);
        if (metronomeDelay >= 0)
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getApp().speech('b');
                }
            }, metronomeDelay);
    }

    private PassingSyncApplication getApp() {
        return (PassingSyncApplication) getApplicationContext();
    }


    public void stopPassing(View v) {
        timer.cancel();
    }
}
