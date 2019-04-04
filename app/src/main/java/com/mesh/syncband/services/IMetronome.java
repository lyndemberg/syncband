package com.mesh.syncband.services;



public interface IMetronome {
    void play(int bpm, Long start);
    void pause();
    boolean isPlaying();
}
