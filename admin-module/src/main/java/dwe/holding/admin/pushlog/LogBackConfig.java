package dwe.holding.admin.pushlog;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LogBackConfig {

    private final LogSSEController logStreamController;

    @PostConstruct
    public void setupSseAppender() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        LogBackAppender sseAppender = new LogBackAppender();
        sseAppender.setContext(context);
        sseAppender.start();

        // Optionally pass your controller to the appender
        LogBackAppender.setController(logStreamController);

        // Attach to root logger
        Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.addAppender(sseAppender);
    }
}
