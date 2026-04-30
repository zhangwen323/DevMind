package com.devmind.backend.sql.web;

import com.devmind.backend.common.api.ApiResponse;
import com.devmind.backend.sql.service.SqlAnalysisService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sql")
public class SqlAnalysisController {

    private final SqlAnalysisService sqlAnalysisService;

    public SqlAnalysisController(SqlAnalysisService sqlAnalysisService) {
        this.sqlAnalysisService = sqlAnalysisService;
    }

    @PostMapping("/analyze")
    public ApiResponse<SqlAnalysisService.SqlAnalysisResult> analyze(@Valid @RequestBody SqlAnalysisRequest request) {
        return ApiResponse.success(sqlAnalysisService.analyze(request.question()));
    }

    public record SqlAnalysisRequest(@NotBlank String question) {
    }
}
