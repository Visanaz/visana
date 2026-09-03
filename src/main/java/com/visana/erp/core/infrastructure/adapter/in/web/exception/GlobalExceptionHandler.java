package com.visana.erp.core.infrastructure.adapter.in.web.exception;

import com.visana.erp.ledger.domain.model.InsufficientFundsException;
import com.visana.erp.platform.application.identity.IdentityConflictException;
import com.visana.erp.platform.application.identity.UnlinkedIdentityException;
import com.visana.erp.platform.application.ownership.OwnershipDeniedException;
import com.visana.erp.commerce.order.application.OwnedOrderNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.visana.erp.core.infrastructure.adapter.in.web.filter.CorrelationIdFilter;
import org.slf4j.MDC;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private ProblemDetail withCorrelation(ProblemDetail problemDetail) {
        String correlationId = MDC.get(CorrelationIdFilter.MDC_KEY);
        if (correlationId != null) {
            problemDetail.setProperty("correlationId", correlationId);
        }
        return problemDetail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Bad Request");
        problemDetail.setType(URI.create("https://visana.com/errors/bad-request"));
        return withCorrelation(problemDetail);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ProblemDetail handleInsufficientFundsException(InsufficientFundsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problemDetail.setTitle("Insufficient Funds");
        problemDetail.setType(URI.create("https://visana.com/errors/insufficient-funds"));
        return withCorrelation(problemDetail);
    }
    
    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalStateException(IllegalStateException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Conflict in State");
        problemDetail.setType(URI.create("https://visana.com/errors/conflict-state"));
        return withCorrelation(problemDetail);
    }

    @ExceptionHandler({UnlinkedIdentityException.class, OwnershipDeniedException.class})
    public ProblemDetail handleForbidden(RuntimeException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problemDetail.setTitle("Forbidden");
        problemDetail.setType(URI.create("https://visana.com/errors/forbidden"));
        return withCorrelation(problemDetail);
    }

    @ExceptionHandler(IdentityConflictException.class)
    public ProblemDetail handleIdentityConflict(IdentityConflictException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Identity Conflict");
        problemDetail.setType(URI.create("https://visana.com/errors/identity-conflict"));
        return withCorrelation(problemDetail);
    }

    @ExceptionHandler(OwnedOrderNotFoundException.class)
    public ProblemDetail handleOwnedOrderNotFound(OwnedOrderNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Resource Not Found");
        problemDetail.setType(URI.create("https://visana.com/errors/not-found"));
        return withCorrelation(problemDetail);
    }

    @ExceptionHandler(com.visana.erp.core.domain.exception.DomainException.class)
    public ProblemDetail handleDomainException(com.visana.erp.core.domain.exception.DomainException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Domain Rule Violation");
        return withCorrelation(problemDetail);
    }

    @ExceptionHandler(com.visana.erp.network.domain.exception.NodeNotFoundException.class)
    public ProblemDetail handleNodeNotFoundException(com.visana.erp.network.domain.exception.NodeNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Resource Not Found");
        return withCorrelation(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpectedException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Use the correlation ID for support.");
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setType(URI.create("https://visana.com/errors/internal-error"));
        return withCorrelation(problemDetail);
    }
}
