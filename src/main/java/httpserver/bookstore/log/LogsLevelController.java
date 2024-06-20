package httpserver.bookstore.log;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/logs/level")
public class LogsLevelController {

    //Gets the current level of a logger
    @GetMapping
    public ResponseEntity<String> getLevel(
            HttpServletRequest request,
            @RequestParam (name ="logger-name") String loggerName) {
        Logger logger = LogManager.getLogger(loggerName);
        return ResponseEntity.ok(logger.getLevel().toString());
    }

    @PutMapping
    public ResponseEntity<String> setLevel(
            HttpServletRequest request,
            @RequestParam (name ="logger-name") String loggerName,
            @RequestParam (name ="logger-level") String loggerLevel){
        Logger logger = LogManager.getLogger(loggerName);
        Level level = Level.valueOf(loggerLevel);

        if (Arrays.stream(LogLevels.values()).noneMatch(v-> v.name().equals(loggerLevel))){
            return ResponseEntity.ok("Unsupported logger level: " + loggerLevel);
        }

        Configurator.setLevel(logger ,level);

        return ResponseEntity.ok(loggerLevel);
    }
}
