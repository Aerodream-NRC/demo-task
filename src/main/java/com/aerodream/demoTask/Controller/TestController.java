package com.aerodream.demoTask.Controller;

import com.aerodream.demoTask.Dto.tests.ConcurrentTestRequest;
import com.aerodream.demoTask.Dto.tests.ConcurrentTestResponse;
import com.aerodream.demoTask.Service.ConcurrentTestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final ConcurrentTestService testService;

    @PostMapping("/concurrent/approve")
    public ResponseEntity<ConcurrentTestResponse> testConcurrentApprove(@Valid @RequestBody ConcurrentTestRequest request) {
        return ResponseEntity.ok(testService.runConcurrentApprovalTest(request));
    }
}
