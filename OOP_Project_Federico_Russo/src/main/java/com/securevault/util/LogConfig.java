package com.securevault.util;

import java.io.IOException;
import java.util.logging.*;

public class LogConfig {

    public static void configure() {
        Logger rootLogger = Logger.getLogger("");

        for (Handler handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }

        try {
            FileHandler fileHandler = new FileHandler("securevault.log", true);
            fileHandler.setEncoding("UTF-8");
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());

            rootLogger.addHandler(fileHandler);
            rootLogger.setLevel(Level.ALL);

        } catch (IOException e) {
            System.err.println("⚠️ Errore durante la configurazione del logger: " + e.getMessage());
        }
    }
}