package com.example.android.passingsync;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.media.MediaPlayer;

import java.util.HashMap;
import java.util.Map;

/**
 * central application class that manages audio and bluetooth across multiple activities
 */
public class PassingSyncApplication extends Application {

//    TextToSpeech mTts;

//    private final Map<Character, File> speechFiles = new HashMap<>();
    private final Map<Character, String> speechText = new HashMap<>();
    private final Map<Character, MediaPlayer> players = new HashMap<>();


    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    private BluetoothService mBluetoothService = null;


    @Override
    public void onCreate() {
        super.onCreate();

        speechText.clear();
        speechText.put('0', "wait");
        speechText.put('2', "zip");
        speechText.put('4', "flip");
        speechText.put('5', "zap");
        speechText.put('6', "self");
        speechText.put('7', "pass");
        speechText.put('8', "heff");
        speechText.put('9', "double");
        speechText.put('a', "triple");
        speechText.put('u', "up");
        speechText.put('d', "down");

        players.put('0', MediaPlayer.create(this, R.raw.p0));
        players.put('2', MediaPlayer.create(this, R.raw.p2));
        players.put('4', MediaPlayer.create(this, R.raw.p4));
        players.put('5', MediaPlayer.create(this, R.raw.p5));
        players.put('6', MediaPlayer.create(this, R.raw.p6));
        players.put('7', MediaPlayer.create(this, R.raw.p7));
        players.put('8', MediaPlayer.create(this, R.raw.p8));
        players.put('9', MediaPlayer.create(this, R.raw.p9));
        players.put('a', MediaPlayer.create(this, R.raw.pa));
        players.put('u', MediaPlayer.create(this, R.raw.p4));
        players.put('d', MediaPlayer.create(this, R.raw.p4));

//        mTts = new TextToSpeech(getBaseContext(), new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if (status == TextToSpeech.SUCCESS) {
//
//                    for (Map.Entry<Character, String> v : speechText.entrySet()) {
//                        String txt = v.getValue();
//                        File file = new File(getBaseContext().getExternalCacheDir(), "p" + v.getKey() + ".wav");
//                        speechFiles.put(v.getKey(), file);
//                        HashMap<String, String> myHashRender = new HashMap();
//                        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, txt);
//                        int r = mTts.synthesizeToFile(txt, myHashRender, file.getPath());
//                        Log.e("TTS", "synth " + txt + " in " + file + ": " + r);
//                    }
//
//
//                } else {
//                    Log.e("TTS", "Initilization Failed! " + status);
//                }
//            }
//        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothService = new BluetoothService(this);


    }

    public void speech(Character p) {
//        if (speechFiles.containsKey(p) && !players.containsKey(p)) {
//            File f = speechFiles.get(p);
//            players.put(p, MediaPlayer.create(this, Uri.fromFile(f)));
//        }

        if (players.containsKey(p)) {
            MediaPlayer player = players.get(p);
            player.seekTo(0);
            player.start();
        }

    }

    @Override
    public void onTerminate() {
//        mTts.shutdown();
        super.onTerminate();
    }

    public synchronized BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public synchronized BluetoothService getBluetoothService() {
        return mBluetoothService;
    }

}
