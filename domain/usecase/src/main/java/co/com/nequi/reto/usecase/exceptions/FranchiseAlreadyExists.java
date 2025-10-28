package co.com.nequi.reto.usecase.exceptions;

public class FranchiseAlreadyExists extends BusinessException{
    public FranchiseAlreadyExists(String code, String message) {
        super(code, message);

    }
}


