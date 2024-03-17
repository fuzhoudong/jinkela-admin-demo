package dev.jinkela.demo.jinkelademo.exceptions;

import java.time.Instant;
import java.util.List;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.http.HttpServletResponse;

@RestControllerAdvice
class ExceptionConfiguration extends ResponseEntityExceptionHandler {


  @Nullable
  @Override
  protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {

    if (request instanceof ServletWebRequest servletWebRequest) {
      HttpServletResponse response = servletWebRequest.getResponse();
      if (response != null && response.isCommitted()) {
        if (logger.isWarnEnabled()) {
          logger.warn("Response already committed. Ignoring: " + ex);
        }
        return null;
      }
    }

    if (body == null && ex instanceof ErrorResponse errorResponse) {
      body = errorResponse.updateAndGetBody(getMessageSource(),
          LocaleContextHolder.getLocale());

      if (ex instanceof MethodArgumentNotValidException) {
        MethodArgumentNotValidException mex = (MethodArgumentNotValidException) ex;
        String defaultMessage = "";
        BindingResult bindingResult = mex.getBindingResult();
        if (bindingResult.hasErrors()) {
          List<ObjectError> allErrors = bindingResult.getAllErrors();
          for (ObjectError objectError : allErrors) {
            defaultMessage = objectError.getDefaultMessage();
          }
        }

        if (body instanceof ProblemDetail) {
          ProblemDetail problemDetail = (ProblemDetail) body;
          problemDetail.setProperty("errorMessage", defaultMessage);
          problemDetail.setProperty("errorCode", statusCode.value());
          problemDetail.setProperty("timestamp", Instant.now());
          body = problemDetail;
        }

      }
    }

    if (statusCode.equals(HttpStatus.INTERNAL_SERVER_ERROR) && body == null) {
      request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex,
          WebRequest.SCOPE_REQUEST);
    }

    return createResponseEntity(body, headers, statusCode, request);
  }

}
