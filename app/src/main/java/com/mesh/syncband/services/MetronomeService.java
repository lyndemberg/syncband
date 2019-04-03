package com.mesh.syncband.services;


import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mesh.syncband.R;
import com.mesh.syncband.metronome.Metronome;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MetronomeService extends Service implements IMetronome {

    private static final String TAG = ".MetronomeService";

    private boolean playing;
    private SoundPool soundPool;
    private ScheduledExecutorService executor;
    private Runnable task;
    private int load;

    public class MetronomeServiceBinder extends Binder {
        public IMetronome getInterface(){
            return MetronomeService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build())
                    .build();
        } else soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        executor = Executors.newSingleThreadScheduledExecutor();

        load = soundPool.load(this, R.raw.click, 100);

        task = new Runnable() {
            @Override
            public void run() {
                soundPool.play(load,1,1,1,0,1);
            }
        };

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MetronomeServiceBinder();
    }

    @Override
    public void onDestroy() {
        executor.shutdownNow();
        super.onDestroy();
    }

    @Override
    public void play(int bpm) {
//        Log.d(TAG,"play: "+bpm+" - start: "+start);
//
        long timePerClick = 60000 / bpm;
        executor.scheduleAtFixedRate(task, 0, timePerClick, TimeUnit.MILLISECONDS);
//
//        long timeServer = Long.valueOf(start);
        long actual = System.currentTimeMillis();
//        long delay = actual - timeServer;
        //quantos já tocaram de acordo com o momento atual....
        //prever próximo intervalo
//        quantos BPM cabem no delay
//        long l = delay / timePerClick;


//        Log.d(TAG,"chegou aqui depois do schedule");
        playing = true;
    }

    @Override
    public void pause() {
        Log.d(TAG,"mandou pausar");
        executor.shutdownNow();
        executor = Executors.newSingleThreadScheduledExecutor();
        playing = false;
    }


    @Override
    public boolean isPlaying() {
        return playing;
    }
}
