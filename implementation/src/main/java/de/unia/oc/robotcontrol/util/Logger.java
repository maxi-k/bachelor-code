/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.util;

import java.io.PrintStream;
import java.util.Arrays;

public class Logger {

    public static final boolean DEBUG = true;
    private static Logger _instance = new Logger();

    private final PrintStream outStream;
    private final PrintStream errStream;

    private Logger() {
        this.outStream = System.out;
        this.errStream = System.err;
    }

    public static Logger instance() {
        return _instance;
    }

    public synchronized <T extends Throwable> void logException(T e) {
        e.printStackTrace(errStream);
    }

    public synchronized void debug(String message) {
        outStream.println(message);
    }

    public synchronized <T extends Throwable> void debugException(T e, String... messages) {
        outStream.println(Arrays.toString(messages)) ;
        e.printStackTrace(errStream);
    }

    public synchronized void println(String s) {
       outStream.println(s);
    }

    public synchronized void print(String s) {
        outStream.print(s);
    }
}
