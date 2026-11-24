package com.insights.rh.analytics;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = AnalyticsController.class)
class ErroConsulta {
    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail entradaInvalida(IllegalArgumentException erro) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Parâmetros da consulta inválidos.");
    }
}
