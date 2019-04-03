package com.mesh.syncband.services;


public interface IMetronome {
    void play(int bpm);
    void pause();
    boolean isPlaying();
}
