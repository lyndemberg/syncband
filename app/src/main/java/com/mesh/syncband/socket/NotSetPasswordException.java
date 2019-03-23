package com.mesh.syncband.socket;

public class NotSetPasswordException extends RuntimeException{
    public NotSetPasswordException() {
        super("This is no possible. Restart the server with a new password !!");
    }
}
