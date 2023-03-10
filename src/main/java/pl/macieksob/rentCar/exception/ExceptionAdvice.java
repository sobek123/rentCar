package pl.macieksob.rentCar.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(UserDuplicateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String userDuplicateHandler(UserDuplicateException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(CarNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String carNotExistHandler(CarNotFoundException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String informationNotExistHandler(OrderNotFoundException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(OrderDuplicateException.class)
    public String orderDuplicateHandler(OrderDuplicateException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(RoleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String roleNotExistHandler(RoleNotFoundException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(CarDuplicateException.class)
    public String carDuplicateHandler(CarDuplicateException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String userNotExistHandler(UserNotFoundException exception) {
        return exception.getMessage();
    }
}
