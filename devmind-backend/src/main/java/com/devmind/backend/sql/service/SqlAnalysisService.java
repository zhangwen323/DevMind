package com.devmind.backend.sql.service;

import com.devmind.backend.common.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SqlAnalysisService {

    public SqlAnalysisResult analyze(String question) {
        String normalized = question.toLowerCase();
        if (normalized.contains("delete") || normalized.contains("drop") || normalized.contains("update") || normalized.contains("insert")) {
            throw new BusinessException(400, "sql safety policy violation");
        }

        return new SqlAnalysisResult(
            "SELECT fail_reason, COUNT(*) AS total FROM order_log GROUP BY fail_reason ORDER BY total DESC LIMIT 5",
            List.of(),
            "Returns the top failure reasons in descending order."
        );
    }

    public record SqlAnalysisResult(String sql, List<Object> result, String explanation) {
    }
}
