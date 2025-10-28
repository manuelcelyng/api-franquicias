package co.com.nequi.reto.usecase.exceptions;

public class ProductNotExists extends BusinessException{
    public ProductNotExists(String code, String message) {
        super(code, message);
    }
}

