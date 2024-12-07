package ws.aperture.stock.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ws.aperture.stock.exceptions.DuplicateEmailException;
import ws.aperture.stock.exceptions.DuplicateProductInOrderException;
import ws.aperture.stock.exceptions.DuplicateSKUException;
import ws.aperture.stock.exceptions.DuplicateSupplierDetailsException;
import ws.aperture.stock.exceptions.EmptyCustomerOrderItemsException;
import ws.aperture.stock.exceptions.EmptyRecipeBodyException;
import ws.aperture.stock.exceptions.InvalidRegisterSupplierException;
import ws.aperture.stock.exceptions.NoCustomerOrderIdException;
import ws.aperture.stock.exceptions.NoIngredientWithIdException;
import ws.aperture.stock.exceptions.NoProductWithIdException;
import ws.aperture.stock.exceptions.NoSupplierWithIdException;
import ws.aperture.stock.exceptions.NoUserWithIdException;
import ws.aperture.stock.exceptions.NoUserwithUserNameException;
import ws.aperture.stock.exceptions.NonPositiveIngredientQuantityException;
import ws.aperture.stock.exceptions.NonPositiveProductQuantityException;
import ws.aperture.stock.exceptions.PriceMustBePositiveException;
import ws.aperture.stock.exceptions.ProductSKUFormatException;
import ws.aperture.stock.exceptions.UnfilledProductFieldsException;
import ws.aperture.stock.exceptions.UnfilledRegistrationFieldsException;
import ws.aperture.stock.exceptions.NoStockItemWithIdException;


@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String duplicateUserEmailHandler(DuplicateEmailException e) {
        return e.getMessage();
    }

    @ExceptionHandler(UnfilledRegistrationFieldsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String unfilledRegistrationField(UnfilledRegistrationFieldsException e) {
        return e.getMessage();
    }

    @ExceptionHandler(NoUserwithUserNameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String userNameNotFound(NoUserwithUserNameException e) {
        return e.getMessage();
    }

    @ExceptionHandler(NoUserWithIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String userIDNotFound(NoUserWithIdException e) {
        return e.getMessage();
    }

    @ExceptionHandler(InvalidRegisterSupplierException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String invalidRegisterSupplier(InvalidRegisterSupplierException e) {
        return e.getMessage();
    }


    @ExceptionHandler(DuplicateSupplierDetailsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String invalidRegisterSupplier(DuplicateSupplierDetailsException e) {
        return e.getMessage();
    }

    @ExceptionHandler(NoSupplierWithIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String noSupplierWithId(NoSupplierWithIdException e) {
        return e.getMessage();
    }


    @ExceptionHandler(NoProductWithIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String noProductWithId(NoProductWithIdException e) {
        return e.getMessage();
    }

    
    @ExceptionHandler(EmptyCustomerOrderItemsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String emptyCustomerOrderItems(EmptyCustomerOrderItemsException e) {
        return e.getMessage();
    }

    
    @ExceptionHandler(DuplicateProductInOrderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String duplicateProductInOrder(DuplicateProductInOrderException e) {
        return e.getMessage();
    }

    
    @ExceptionHandler(NonPositiveProductQuantityException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String nonPositiveProductQuantity(NonPositiveProductQuantityException e) {
        return e.getMessage();
    }

    @ExceptionHandler(UnfilledProductFieldsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String unfilledProductFields(UnfilledProductFieldsException e) {
        return e.getMessage();
    }
    
    @ExceptionHandler(ProductSKUFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String productSKUFormat(ProductSKUFormatException e) {
        return e.getMessage();
    }

    @ExceptionHandler(DuplicateSKUException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String duplicateSKU(DuplicateSKUException e) {
        return e.getMessage();
    }

    @ExceptionHandler(NoCustomerOrderIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String NoCustomerOrderId(NoCustomerOrderIdException e) {
        return e.getMessage();
    }

    @ExceptionHandler(PriceMustBePositiveException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String priceMustBePositive(PriceMustBePositiveException e) {
        return e.getMessage();
    }

    @ExceptionHandler(NoIngredientWithIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String noIngredientWithId(NoIngredientWithIdException e) {
        return e.getMessage();
    }

    
    @ExceptionHandler(NonPositiveIngredientQuantityException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String ingredientQuantityNonnegative(NonPositiveIngredientQuantityException e) {
        return e.getMessage();
    }

    @ExceptionHandler(EmptyRecipeBodyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String emptyRecipeBody(EmptyRecipeBodyException e) {
        return e.getMessage();
    }

    @ExceptionHandler(NoStockItemWithIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String NoStockItemWithIdException(NoStockItemWithIdException e) {
        return e.getMessage();
    }
    

}

