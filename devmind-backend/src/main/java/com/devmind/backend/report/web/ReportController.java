package com.devmind.backend.report.web;

import com.devmind.backend.common.api.ApiResponse;
import com.devmind.backend.knowledge.model.PageResult;
import com.devmind.backend.report.model.ReportDetail;
import com.devmind.backend.report.model.ReportSummary;
import com.devmind.backend.report.service.ReportService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public ApiResponse<PageResult<ReportSummary>> list(
        Authentication authentication,
        @RequestParam(required = false) String reportType,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.success(reportService.list(authentication, reportType, page, size));
    }

    @GetMapping("/{reportId}")
    public ApiResponse<ReportDetail> detail(Authentication authentication, @PathVariable long reportId) {
        return ApiResponse.success(reportService.getDetail(authentication, reportId));
    }

    @PostMapping
    public ApiResponse<CreateReportResponse> create(Authentication authentication, @Valid @RequestBody CreateReportRequest request) {
        ReportService.GeneratedReport generated = reportService.create(
            authentication,
            request.sessionId(),
            request.knowledgeBaseId(),
            request.documentIds(),
            request.reportType(),
            request.guidance()
        );
        return ApiResponse.success(new CreateReportResponse(
            generated.report().id(),
            generated.sessionId(),
            generated.agentName(),
            generated.report()
        ));
    }

    public record CreateReportRequest(
        Long sessionId,
        @Min(1) long knowledgeBaseId,
        @NotEmpty List<Long> documentIds,
        @NotBlank String reportType,
        String guidance
    ) {
    }

    public record CreateReportResponse(
        long reportId,
        long sessionId,
        String agentName,
        ReportDetail report
    ) {
    }
}
