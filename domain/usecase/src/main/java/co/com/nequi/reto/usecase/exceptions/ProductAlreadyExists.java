package co.com.nequi.reto.usecase.exceptions;

public class ProductAlreadyExists extends BusinessException{

    public ProductAlreadyExists(String code, String message) {
        super(code, message);
    }
}
