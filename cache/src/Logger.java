import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Logger {
    List<LogEntry> log;
    public Logger(){
        log = new ArrayList<>();
    }
    public void record(String action){
        log.add(new LogEntry(action));
    }
    public void record(String action, String message){
        log.add(new LogEntry(action, message));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (LogEntry entry:
             log) {
            sb.append(entry.toString());
            sb.append('\n');
        }
        return sb.toString();
    }
}
class LogEntry {
    Instant timestamp;
    String action, message;
    LogEntry(String action, String message){
        timestamp = Instant.now();
        this.action = action;
        this.message = message;
    }
    LogEntry(String action){
        this(action, "");
    }

    @Override
    public String toString() {
        return timestamp.toString() + " " + action + ":" + message;
    }
}
