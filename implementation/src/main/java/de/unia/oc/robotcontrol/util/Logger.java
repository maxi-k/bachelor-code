/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.util;

public class Logger {

    private static Logger _instance = new Logger();

    private Logger() {}

    public static Logger instance() {
        return _instance;
    }

    public <T extends Exception> void logException(T e) {
        e.printStackTrace();
    }
}
