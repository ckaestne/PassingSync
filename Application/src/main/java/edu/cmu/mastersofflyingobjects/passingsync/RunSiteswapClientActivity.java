package edu.cmu.mastersofflyingobjects.passingsync;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import edu.cmu.mastersofflyingobjects.passingsync.pattern.AbstractPatternGenerator;


public class RunSiteswapClientActivity extends ActionBarActivity {

    int metronomeDelay = 500;
    private SiteswapFragment siteswapFragment;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_PASS:
                    Character p = (char) msg.arg1;
                    getApp().speech(p);
                    if (metronomeDelay>=0)
                        this.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getApp().speech('b');
                            }
                        }, metronomeDelay);
                    break;
                case Constants.MESSAGE_DISPLAYUPDATE:
                    AbstractPatternGenerator.Display d = (AbstractPatternGenerator.Display) msg.obj;
                    siteswapFragment.setDisplay(d, AbstractPatternGenerator.Passer.B);
                    break;
                case Constants.MESSAGE_UPDATE_START:
                    String v = (String) msg.obj;
                    siteswapFragment.setStart(v);
                    break;
                case Constants.MESSAGE_UPDATE_METRONOMEDELAY:
                    metronomeDelay=msg.arg1;
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_siteswap_client);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            siteswapFragment = SiteswapFragment.newInstance();
            transaction.replace(R.id.body, siteswapFragment);
            transaction.commit();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        getBluetoothService().addHandler(mHandler);

    }

    @Override
    protected void onStop() {
        getBluetoothService().removeHandler(mHandler);
        super.onStop();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_run_siteswap_client, menu);
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

    private BluetoothService getBluetoothService() {
        return getApp().getBluetoothService();
    }

    private PassingSyncApplication getApp() {
        return (PassingSyncApplication) getApplicationContext();
    }


}
