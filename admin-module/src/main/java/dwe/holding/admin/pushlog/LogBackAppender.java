package dwe.holding.admin.pushlog;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class LogBackAppender extends AppenderBase<ILoggingEvent> {

    private static LogSSEController controller;

    public static void setController(LogSSEController ctrl) {
        controller = ctrl;
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (controller != null) {
            controller.sendLog(event.getFormattedMessage());
        }
    }
}
