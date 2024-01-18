package com.praise.push.common.error;

import com.praise.push.common.error.exception.PraiseUpException;
import com.praise.push.common.error.model.ErrorCode;
import com.praise.push.common.error.model.ErrorResponseDto;
import com.praise.push.common.monitoring.MonitoringProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MonitoringProvider monitoringProvider;

    /**
     * Handle exception that occurs when bean validation check fail
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException exception
    ) {
        String message = exception.getFieldErrors().getFirst().getDefaultMessage();

        return ErrorResponseDto.build(ErrorCode.VALIDATION_CHECK_FAIL, message);
    }

    @ExceptionHandler(PraiseUpException.class)
    public ResponseEntity<ErrorResponseDto> handlePraiseUpException(
            final PraiseUpException exception,
            final HttpServletRequest request
    ) {
        log.error("Exception: {}, Request URI: {}", exception.getMessage(), request.getRequestURL());

        return ErrorResponseDto.build(exception.getErrorCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(
            final Exception exception,
            final HttpServletRequest request
    ) {
        log.error("Exception: {}, Request URI: {}", exception.getMessage(), request.getRequestURL());
        monitoringProvider.push(exception, request);

        return ErrorResponseDto.build(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
