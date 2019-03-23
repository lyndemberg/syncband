package com.mesh.syncband.socket;

public class NotPossibleBindServer extends RuntimeException{
    public NotPossibleBindServer() {
        super("No possible start server!");
    }
}
