package com.example.controller;

import com.example.api.LimitControllerApi;
import com.example.dao.model.generated.CurrentLimitDto;
import com.example.service.LimitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LimitController implements LimitControllerApi {

    private final LimitService limitService;

    @Override
    public ResponseEntity<CurrentLimitDto> getLimit(Long userId) {
        return ResponseEntity.ok(limitService.getLimit(userId));
    }

    @Override
    public ResponseEntity<Void> postLimit(Long userId, Long value) {
        limitService.postLimit(userId, value);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> rollbackLimit(Long userId, Long value) {
        limitService.rollbackLimit(userId, value);
        return ResponseEntity.noContent().build();
    }
}
