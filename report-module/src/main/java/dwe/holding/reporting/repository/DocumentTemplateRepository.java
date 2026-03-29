package dwe.holding.reporting.repository;

import dwe.holding.reporting.model.DocumentReportType;
import dwe.holding.reporting.model.ReportTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentTemplateRepository extends JpaRepository<ReportTemplate, Long> {

    List<ReportTemplate> findByMemberId(Long currentUserMid);

    List<ReportTemplate> findByReportType(DocumentReportType type);

    int deleteByIdAndMemberId(Long templateId, Long currentUserMid);
}
