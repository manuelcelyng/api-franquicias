package co.com.nequi.reto.usecase.exceptions;

public class BranchNotExists  extends BusinessException{
    public BranchNotExists(String code, String message) {
        super(code, message);
    }
}
