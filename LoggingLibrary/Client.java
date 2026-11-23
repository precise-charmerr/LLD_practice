import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

enum LogLevel {
    DEBUG(1), INFO(2), WARN(3), ERROR(4);

    private final int severity;
    LogLevel(int severity) {
        this.severity = severity;
    }

    public int getSeverity() {
        return severity;
    }

    public boolean allows(LogLevel threshold) {
        return this.severity >= threshold.severity;
    }
}

class LogRecord {
    private final String message;
    private final String thread;
    private final long timestamp;
    private final LogLevel logLevel;

    public LogRecord(String message, LogLevel logLevel) {
        this.message = message;
        this.thread = Thread.currentThread().getName();
        this.timestamp = System.currentTimeMillis();
        this.logLevel = logLevel;
    }

    public String getMessage() {
        return message;
    }

    public String getThread() {
        return thread;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }
}

interface Appender {
    public void append(LogRecord logRecord);
}

class ConsoleAppender implements Appender {
    @Override
    public void append(LogRecord logRecord) {
        System.out.println("[" +
                logRecord.getTimestamp() + " " +
                logRecord.getThread() + " " +
                logRecord.getLogLevel() + " " +
                logRecord.getMessage() + " ]"
        );
    }
}

class Logger {
    private final String name;
    private volatile LogLevel logLevel;
    private final Logger parent;
    private volatile boolean additive;
    private final List<Appender> logAppender = new CopyOnWriteArrayList<>();

    public Logger(LogLevel logLevel, Logger parent, String name) {
        this.name = name;
        this.logLevel = logLevel;
        this.parent = parent;
        this.additive = true;
    }

    public String getName() {
        return name;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public Logger getParent() {
        return parent;
    }

    public boolean isAdditive() {
        return additive;
    }

    public void setAdditive(boolean additive) {
        this.additive = additive;
    }

    public void addAppender(Appender appender) {
        this.logAppender.add(java.util.Objects.requireNonNull(appender));
    }

    public List<Appender> getLogAppender() {
        return List.copyOf(logAppender);
    }

    public void info(String message) {
        log(LogLevel.INFO, message);
    }

    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    public void warn(String message) {
        log(LogLevel.WARN, message);
    }

    public void error(String message) {
        log(LogLevel.ERROR, message);
    }

    private void log(LogLevel logLevel, String message) {
        // check if we have to drop message or take it further
        if(!logLevel.allows(getEffectiveLevel())) {
            return;
        }
        LogRecord logRecord = new LogRecord(message,logLevel);
        callAppenders(logRecord);

        if(additive && this.parent != null) {
            parent.bubble(logRecord);
        }
    }

    private void bubble(LogRecord logRecord) {
        callAppenders(logRecord);
        if(additive && this.parent != null) {
            this.parent.bubble(logRecord);
        }
    }

    private void callAppenders(LogRecord logRecord) {
        for(Appender appender: logAppender) {
            appender.append(logRecord);
        }
    }

    private LogLevel getEffectiveLevel() {
        if(this.logLevel != null) {
            return this.logLevel;
        }
        if(this.parent != null) {
            return this.parent.getEffectiveLevel();
        }
        return LogLevel.INFO;
    }
}

class LogManager {
    private final Map<String, Logger> loggerMap = new ConcurrentHashMap<>();
    private final Logger rootLogger;

    public LogManager() {
        rootLogger = new Logger(LogLevel.INFO, null, "ROOT");
        loggerMap.put("ROOT", rootLogger);
    }

    public synchronized Logger addLogger(LogLevel logLevel, String name) {
        Logger existingLogger = loggerMap.get(name);
        if (existingLogger != null) {
            if (logLevel != null) {
                existingLogger.setLogLevel(logLevel);
            }
            return existingLogger;
        }
        int last_index = name.lastIndexOf(".");
        Logger parent = rootLogger;
        if (last_index != -1) {
            String parentName = name.substring(0, last_index);
            parent = addLogger(null, parentName);
        }
        Logger logger = new Logger(logLevel, parent, name);
        loggerMap.put(name, logger);
        return logger;
    }

    public void printHashmap() {
        for(Map.Entry<String, Logger> current: loggerMap.entrySet()) {
            if(current.getValue().getParent() != null) {
                System.out.println(current.getKey() + " " + current.getValue().getParent().getName() + " " + current.getValue().getLogLevel());
            }
        }
        System.out.println(loggerMap);
    }
}

public class Client {
    public static void main(String[] args) {
        LogManager logManager = new LogManager();
        Logger rootLogger = logManager.addLogger(LogLevel.INFO, "ROOT");
        rootLogger.addAppender(new ConsoleAppender());

        Logger logger1 = logManager.addLogger(LogLevel.INFO, "com.users");
        logger1.debug("This is logger1 debug message"); // it should not print
        logger1.info("This is logger1 info message");
        logger1.warn("This is logger1 warn message");
        logger1.error("This is logger1 error message");

        Logger logger = logManager.addLogger(LogLevel.WARN, "com");
        logger.debug("This is logger debug message"); // should not print
        logger.info("This is logger info message"); // should not print
        logger.warn("This is logger warn message");
        logger.error("This is logger error message");

        logManager.printHashmap();
    }
}