package ws.aperture.stock.controller;

import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ws.aperture.stock.exceptions.ConflictException;
import ws.aperture.stock.exceptions.DuplicateEmailException;
import ws.aperture.stock.exceptions.DuplicateProductInOrderException;
import ws.aperture.stock.exceptions.EmptyCustomerOrderItemsException;
import ws.aperture.stock.exceptions.EmptyRecipeBodyException;
import ws.aperture.stock.exceptions.NoCustomerOrderIdException;
import ws.aperture.stock.exceptions.NoIngredientWithIdException;
import ws.aperture.stock.exceptions.NoProductWithIdException;
import ws.aperture.stock.exceptions.NoStockItemWithIdException;
import ws.aperture.stock.exceptions.NoSupplierWithIdException;
import ws.aperture.stock.exceptions.NoUserWithIdException;
import ws.aperture.stock.exceptions.NoUserwithUserNameException;

@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {
  @ExceptionHandler({
    NoUserWithIdException.class,
    NoUserwithUserNameException.class,
    NoSupplierWithIdException.class,
    NoProductWithIdException.class,
    NoIngredientWithIdException.class,
    NoStockItemWithIdException.class,
    NoCustomerOrderIdException.class
  })
  ProblemDetail notFound(RuntimeException ex) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage().strip());
  }

  @ExceptionHandler({ConflictException.class, DuplicateEmailException.class})
  ProblemDetail conflict(RuntimeException ex) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage().strip());
  }

  @ExceptionHandler({DataIntegrityViolationException.class, ConcurrencyFailureException.class})
  ProblemDetail integrity(RuntimeException ex) {
    return ProblemDetail.forStatusAndDetail(
        HttpStatus.CONFLICT, "Conflicting or referenced record; reload and retry");
  }

  @ExceptionHandler({
    IllegalArgumentException.class,
    EmptyRecipeBodyException.class,
    EmptyCustomerOrderItemsException.class,
    DuplicateProductInOrderException.class
  })
  ProblemDetail invalid(RuntimeException ex) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage().strip());
  }
}
