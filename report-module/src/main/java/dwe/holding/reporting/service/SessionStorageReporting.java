package dwe.holding.reporting.service;

import dwe.holding.admin.sessionstorage.SessionStorage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SessionStorageReporting {
    public enum ReportTypePage {
        GENERIC, SEARCH_CUSTOMER, REMINDER
    }

    public enum ActionType {
        EMAIL, REPORT, CONSULT, LATEST_CONSULT
    }

    public static final String REPORT = "report";
    private final SessionStorage storage;

    public ReportingSettings getReporting() {
        return read();
    }

    public void setReporting(ReportingSettings settings) {
        update(settings);
    }

    private ReportingSettings read() {
        return storage.getModule(REPORT, ReportingSettings.class).orElse(
                new ReportingSettings(null, null, "", "")
        );
    }

    private void update(ReportingSettings updatedReportingData) {
        storage.updateModule(REPORT, updatedReportingData);
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class ReportingSettings {
        ReportTypePage report;
        ActionType actionType;
        String callbackUrl;
        String data;
    }
}
