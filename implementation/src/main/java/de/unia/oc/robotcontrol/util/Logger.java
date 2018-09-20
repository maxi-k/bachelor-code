/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.util;

import java.io.PrintStream;
import java.util.Arrays;

/**
 * Class for Logging - mostly used for debugging the application.
 * This is a singleton class. The instance can be gotten using
 * {@link #instance()}.
 */
public class Logger {

    /**
     * Whether to send debug messages. Used by {@link #debug(String)}
     * and {@link #debugException(Throwable, String...)};
     */
    public static final boolean DEBUG = true;

    /**
     * The singleton instance.
     */
    private static Logger _instance = new Logger();

    /**
     * The stream used for the standard output.
     */
    private final PrintStream outStream;
    /**
     * The stream used for error outputs.
     */
    private final PrintStream errStream;

    /**
     * Creates a new logger, using {@link System#out} and
     * {@link System#err} for standard and option output,
     * respectively.
     */
    private Logger() {
        this.outStream = System.out;
        this.errStream = System.err;
    }

    /**
     * @return the Logger Instance
     */
    public static Logger instance() {
        return _instance;
    }

    /**
     * Log the given exception to the error stream.
     * @param e the exception to log
     * @param <T> the subtype of exception the parameter has
     */
    public synchronized <T extends Throwable> void logException(T e) {
        e.printStackTrace(errStream);
    }

    /**
     * Print the given message, only if {@link #DEBUG}
     * is set to true.
     * @param message the message to print
     */
    public synchronized void debug(String message) {
        if (DEBUG) outStream.println(message);
    }

    /**
     * Print the given exception and the messages,
     * if {@link #DEBUG} is set to true
     * @param e the exception to print
     * @param messages the messages to print
     * @param <T> the subtype of the exception to print
     */
    public synchronized <T extends Throwable> void debugException(T e, String... messages) {
        if (DEBUG) {
            outStream.println(Arrays.toString(messages));
            e.printStackTrace(errStream);
        }
    }

    /**
     * Print the given String to {@link #outStream}
     * @param s the string to print
     */
    public synchronized void println(String s) {
       outStream.println(s);
    }

    /**
     * Print the given string to the {@link #outStream},
     * without creating a new line.
     * @param s the string to print
     */
    public synchronized void print(String s) {
        outStream.print(s);
    }
}
