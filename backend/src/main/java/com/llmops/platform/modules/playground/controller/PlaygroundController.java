package com.llmops.platform.modules.playground.controller;

import com.llmops.platform.common.dto.ApiResponse;
import com.llmops.platform.modules.playground.dto.PlaygroundDto.*;
import com.llmops.platform.modules.playground.service.PlaygroundService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/playground")
public class PlaygroundController {

    private final PlaygroundService playgroundService;

    public PlaygroundController(PlaygroundService playgroundService) {
        this.playgroundService = playgroundService;
    }

    @PostMapping("/run")
    public ResponseEntity<ApiResponse<PlaygroundResponse>> run(@RequestBody PlaygroundRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Executed successfully", playgroundService.run(request)));
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestBody PlaygroundRequest request) {
        return playgroundService.stream(request);
    }
}
