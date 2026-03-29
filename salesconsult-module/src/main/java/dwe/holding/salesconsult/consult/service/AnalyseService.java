package dwe.holding.salesconsult.consult.service;

import dwe.holding.salesconsult.consult.repository.AnalyseRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AnalyseService {
    private final AnalyseRepository analyseRepository;

@Transactional
    public void delete(List<Long> ids) {
        analyseRepository.deleteAllById(ids);
    }
}
