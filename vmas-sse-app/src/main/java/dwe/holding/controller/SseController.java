package dwe.holding.controller;

import dwe.holding.service.SseService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Map;

@RestController
@AllArgsConstructor
public class SseController {

    private final SseService service;
    private final ObjectMapper mapper;

    @GetMapping("/sse/stream")
    public SseEmitter stream() {
        return service.subscribe();
    }

    @PostMapping("/internal/event")
    public void push(@RequestBody Map<String, Object> body) throws Exception {
        var json = mapper.writeValueAsString(Map.of(
                "type", body.get("type"),
                "timestamp", Instant.now().toString(),
                "payload", body.get("payload")
        ));

        service.broadcast(json);
    }
}
