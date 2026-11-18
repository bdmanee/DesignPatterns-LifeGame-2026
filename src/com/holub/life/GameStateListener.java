package com.holub.life;

public interface GameStateListener {

    void onGameStateChanged(int generation, int aliveCells);

}