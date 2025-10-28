package co.com.nequi.reto.usecase.exceptions;

public class FranchiseNotExists  extends BusinessException{
    public FranchiseNotExists(String code, String message) {
        super(code, message);
    }
}
