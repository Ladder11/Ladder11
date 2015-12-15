package com.lader11.ladder11;

/**
 * Created by jordanbrobots on 11/29/15.
 */
abstract class Program {
    Program() {}

    abstract public void setup();   //User defined features here
    abstract public void loop();    //User defined features here

    //#TODO implement this functionality in the calling class instead
    public void start() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                setup();
                while(true) {
                    loop();
                }
            }
        });
        thread.start();
    }
}
