package co.com.nequi.reto.usecase.exceptions;

public class BranchAlreadyExists extends BusinessException {
    public BranchAlreadyExists(String code, String message) {
        super(code, message);
    }
}
