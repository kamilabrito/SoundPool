package com.kbrtz.fodasseplayer;

import android.annotation.TargetApi;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    private ImageButton play, pause;

    private SoundPool soundPool;
    private int soundID;
    boolean plays = false, loaded = false;
    float actVolume, maxVolume, volume;
    AudioManager audioManager;
    int counter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        play = (ImageButton) findViewById(R.id.play_button);
        pause = (ImageButton) findViewById(R.id.pause_button);

        loadSound(R.raw.fodasse);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSound();
            }
        });

    }

    public void buildBeforeAPI21() {

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actVolume / maxVolume;

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        counter = 0;

        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });

        soundID = soundPool.load(this, R.raw.fodasse, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void playSound() {
        // Is the sound loaded does it already play?
        if (loaded && !plays) {
            soundPool.play(soundID, volume, volume, 1, 0, 1f);
            counter = counter++;
            Toast.makeText(this, "Played sound", Toast.LENGTH_SHORT).show();
            plays = true;
        }
    }

    public void stopSound() {
        if (plays) {
            soundPool.stop(soundID);
            soundID = soundPool.load(this, R.raw.fodasse, counter);
            Toast.makeText(this, "Stop sound", Toast.LENGTH_SHORT).show();
            plays = false;
        }
    }

    private int loadSound(int filename) {
        int soundID = 0;
        if (soundPool == null) {
            soundPool = buildSoundPool();
        }
        try {
            soundID = soundPool.load(this, filename, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return soundID;
    }

    /**
     * Verify device's API before to load soundpool
     * @return
     */
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private SoundPool buildSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(25)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            buildBeforeAPI21();
        }
        return soundPool;
    }


}
