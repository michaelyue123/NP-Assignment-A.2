//package Server;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class GameLogger {

    private static  Logger logger;

    public static Logger getLogger() {

        if(logger == null) {

            logger = Logger.getLogger("GameLogger");

            // use the method "getLogger(String name)" in Logger class to get a Logger object
            logger = Logger.getLogger("MyLogger");

            // Declare a FileHandler variable.
            FileHandler fh;

            // set the level of the logger as INFO
            logger.setLevel(Level.INFO);

            try {
                // create a new FileHandler object with the log file path
                fh = new FileHandler(System.getProperty("user.dir")+"/record.log");

                // create a SimpleFormatter object to setup the format of log
                SimpleFormatter formatterFH = new SimpleFormatter();
                // set the SimpleFormatter to the FileHandler
                fh.setFormatter(formatterFH);

                // add the FileHandler to the logger
                logger.addHandler(fh);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return logger;
    }
}
