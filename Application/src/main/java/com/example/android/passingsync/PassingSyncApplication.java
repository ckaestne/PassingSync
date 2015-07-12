package com.example.android.passingsync;

import android.app.Application;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ckaestne on 7/7/2015.
 */
public class PassingSyncApplication extends Application {
    TextToSpeech mTts;
    static final Map<Character, File> speechCache = new HashMap<>();
    static final Map<Character, String> speechText = new HashMap<>();
    static final Map<Character, MediaPlayer> players = new HashMap<>();

    //    public BlueComms myBlueComms;
    @Override
    public void onCreate() {
        super.onCreate();
//        myBlueComms = new BlueComms();

        speechText.clear();
        speechText.put('0', "hold");
        speechText.put('2', "zip");
        speechText.put('4', "flip");
        speechText.put('5', "zap");
        speechText.put('6', "self");
        speechText.put('7', "pass");
        speechText.put('8', "heff");
        speechText.put('9', "double");
        speechText.put('a', "triple");



        mTts = new TextToSpeech(getBaseContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {

                    for (Map.Entry<Character, String> v : speechText.entrySet()) {
                        String txt = v.getValue();
                        File file = new File(getBaseContext().getExternalCacheDir(), "p" + v.getKey() + ".wav");
                        speechCache.put(v.getKey(), file);
                        HashMap<String, String> myHashRender = new HashMap();
                        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, txt);
                        int r = mTts.synthesizeToFile(txt, myHashRender, file.getPath());
                        Log.e("TTS", "synth " + txt + " in " + file + ": " + r);
                    }


                } else {
                    Log.e("TTS", "Initilization Failed! " + status);
                }
            }
        });
    }

    static void speech(Character p, Context c) {
        if (speechCache.containsKey(p) && !players.containsKey(p)) {
            File f= speechCache.get(p);
            players.put(p, MediaPlayer.create(c, Uri.fromFile(f)) );
        }

        if (players.containsKey(p)) {
            MediaPlayer player = players.get(p);
            player.seekTo(0);
            player.start();
        }

    }

    @Override
    public void onTerminate() {
        mTts.shutdown();
        super.onTerminate();
    }

    //    ((cBaseApplication)this.getApplicationContext()).myBlueComms.SomeMethod();

}
