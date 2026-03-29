package dwe.holding.reporting.model;

import dwe.holding.admin.model.base.MemberBaseBO;
import dwe.holding.reporting.converter.DocumentReportTypeConverter;
import dwe.holding.reporting.converter.GzipConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Table(name = "REPORT_TEMPLATE")
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReportTemplate extends MemberBaseBO {
    @NotNull
    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = DocumentReportTypeConverter.class)
    private DocumentReportType reportType;

    @NotNull
    @Column(nullable = false, unique = true)
    private String purpose;

    private String subject;

    @Lob
    @NotNull
    @Column(nullable = false, columnDefinition = "MEDIUMBLOB")
    @Convert(converter = GzipConverter.class)
    private String content;
}
