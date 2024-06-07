package com.example.exception.handler;

import com.example.dao.model.generated.ErrorResponseDto;
import com.example.exception.InsufficientFunds;
import com.example.exception.ProductNotFound;
import com.example.exception.UserNotFound;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.springframework.core.NestedExceptionUtils.getRootCause;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @SuppressWarnings("unused")
    private final MessageSource messageSource;
    private final HttpSession httpSession;

    private record Violation(
            String field,
            String message
    ) {
    }

    @ExceptionHandler(InsufficientFunds.class)
    public ResponseEntity<ErrorResponseDto> handleInsufficientFounds(InsufficientFunds exception) {
        log.error(exception.getMessage(), exception);
        return errorResponseDtoEntity(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
    }

    @ExceptionHandler(ProductNotFound.class)
    public ResponseEntity<ErrorResponseDto> handleProductNotFound(ProductNotFound exception) {
        log.error(exception.getMessage(), exception);
        return errorResponseDtoEntity(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<ErrorResponseDto> handleUserNotFound(UserNotFound exception) {
        log.error(exception.getMessage(), exception);
        return errorResponseDtoEntity(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    // *****************************************************************************************************************
    // Базовые исключения
    // *****************************************************************************************************************

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDto> handleRuntimeException(RuntimeException exception) {
        log.error(exception.getMessage(), exception);
        return errorResponseDtoEntity(HttpStatus.INTERNAL_SERVER_ERROR,
                "Внутренняя ошибка сервера. Тип %s".formatted(exception.getClass().getName()));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNoHandlerFoundException(NoHandlerFoundException exception) {
        log.error(exception.getMessage(), exception);
        return errorResponseDtoEntity(HttpStatus.BAD_REQUEST,
                "Не найден обработчик для запроса %s %s".formatted(exception.getHttpMethod(), exception.getRequestURL()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDto> handleMissingServletRequestParameterException(MissingServletRequestParameterException exception) {
        log.error(exception.getMessage(), exception);
        return errorResponseDtoEntity(HttpStatus.BAD_REQUEST,
                "Обязательный параметр запроса '%s' тип %s отсутствует в запросе".formatted(exception.getParameterName(), exception.getParameterType()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        Throwable cause = getRootCause(exception);
        String exceptionMessage = nonNull(cause) ? cause.getMessage().toLowerCase() : exception.getMessage();
        return errorResponseDtoEntity(HttpStatus.CONFLICT, "Ошибка целостности данных. Сообщение: %s".formatted(exceptionMessage));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolationException(ConstraintViolationException exception) {
        List<Violation> violations = exception.getConstraintViolations()
                .stream()
                .map(violation -> new Violation(violation.getPropertyPath().toString(), violation.getMessage()))
                .toList();
        return errorResponseDtoEntity(HttpStatus.UNPROCESSABLE_ENTITY, createViolationsMessage(violations));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        List<Violation> violations = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .toList();
        return errorResponseDtoEntity(HttpStatus.UNPROCESSABLE_ENTITY, createViolationsMessage(violations));
    }

    private String createViolationsMessage(List<Violation> violations) {
        StringBuilder sb = new StringBuilder("Введены недопустимые данные:");
        violations.forEach(violation -> sb.append("%n%s: %s".formatted(violation.field, violation.message)));
        return sb.toString();
    }

    private ResponseEntity<ErrorResponseDto> errorResponseDtoEntity(HttpStatus httpStatus, String message) {
        ErrorResponseDto responseDto = ErrorResponseDto.builder()
                .errorCode(httpStatus.getReasonPhrase())
                .sessionId(httpSession.getId())
                .timestamp(LocalDateTime.now())
                .errorMessage(message)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.put(HttpHeaders.CONTENT_TYPE, List.of(MediaType.APPLICATION_JSON_VALUE));
        return new ResponseEntity<>(responseDto, headers, httpStatus);
    }
}
