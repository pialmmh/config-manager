package freeswitch.exception;

import org.slf4j.Logger;

public class TbException extends RuntimeException {
    public TbException(String message,LogSeverity severity) {
        super(message);
        Logger logger= GlobalExceptionHandler.logger;
        switch (severity) {
            case ERROR:
                logger.error(message);
                break;
            case WARN:
                logger.warn(message);
                break;
            case INFO:
                logger.info(message);
                break;
            case DEBUG:
                logger.debug(message);
                break;
            case TRACE:
                logger.trace(message);
                break;
        }
    }
}
