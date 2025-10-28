package co.com.nequi.reto.usecase.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TypeErrors {

    FRANCHISE_ALREADY_EXISTS("FRN_002", "The franchise already exists"),
    FRANCHISE_NOT_EXISTS("FRN_003", "The franchises not exists"),

    BRANCH_ALREADY_EXISTS("BRN_002", "The branch already exists"),
    BRANCH_NOT_EXISTS("BRN_003", "The branch not exists" ),

    PRODUCT_ALREADY_EXISTS("PRD_002", "The product already exists"),
    PRODUCT_NOT_EXISTS("PRD_003", "The product not exists");

    private final String code;
    private final String message;
}