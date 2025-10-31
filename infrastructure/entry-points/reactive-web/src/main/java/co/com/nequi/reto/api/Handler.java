package co.com.nequi.reto.api;

import co.com.nequi.reto.api.dto.BranchRequest;
import co.com.nequi.reto.api.dto.FranchiseRequest;
import co.com.nequi.reto.api.dto.ProductRequest;
import co.com.nequi.reto.api.dto.ProductUpdateRequest;
import co.com.nequi.reto.api.mappers.branch.BranchDtoMapper;
import co.com.nequi.reto.api.mappers.franchise.FranchiseDtoMapper;
import co.com.nequi.reto.api.mappers.product.ProductDtoMapper;
import co.com.nequi.reto.model.product.ProductTopStock;
import co.com.nequi.reto.usecase.branch.BranchUseCase;
import co.com.nequi.reto.usecase.franchise.FranchiseUseCase;
import co.com.nequi.reto.usecase.product.ProductUseCase;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Validator;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;


@Component
@RequiredArgsConstructor
@Slf4j
public class Handler {

    private final Validator validator;
    private final FranchiseDtoMapper franchiseDtoMapper;
    private final BranchDtoMapper branchDtoMapper;
    private final ProductDtoMapper productDtoMapper;
    private final FranchiseUseCase franchiseUseCase;
    private final BranchUseCase branchUseCase;
    private final ProductUseCase productUseCase;

    // FRANCHISES ENDPOINTS

    public Mono<ServerResponse> listenCreateFranchise(ServerRequest serverRequest) {
        // useCase.logic();
        log.info("Handling {} {}", serverRequest.methodName(), serverRequest.path());
        return serverRequest.bodyToMono(FranchiseRequest.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("RequestBody is required!")))
                .flatMap(this::validateJakarta)
                .map(franchiseDtoMapper::toModel)
                .flatMap(franchiseUseCase::createFranchise)
                .map(franchiseDtoMapper::toResponse)
                .flatMap(franchise -> ServerResponse.ok().bodyValue(franchise));


    }

    public Mono<ServerResponse> listenAddBranchToFranchise(ServerRequest serverRequest) {
        log.info("Handling {} {}", serverRequest.methodName(), serverRequest.path());
        Long franchiseId;
        try {
            franchiseId = Long.valueOf(serverRequest.pathVariable("id"));
        } catch (Exception e) {
            return Mono.error(new IllegalArgumentException("Path variable 'id' must be a number"));
        }
        return serverRequest.bodyToMono(BranchRequest.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("RequestBody is required!")))
                .flatMap(this::validateJakarta)
                .map(branchDtoMapper::toModel)
                .flatMap(branch -> franchiseUseCase.addBranch(franchiseId, branch))
                .map(branchDtoMapper::toResponse)
                .flatMap(branch -> ServerResponse.ok().bodyValue(branch));
    }


    public Mono<ServerResponse> listenUpdateFranchise(ServerRequest serverRequest) {
        log.info("Handling {} {}", serverRequest.methodName(), serverRequest.path());

        Long franchiseId;
        try {
            franchiseId = Long.valueOf(serverRequest.pathVariable("franchiseId"));
        } catch (Exception e) {
            return Mono.error(new IllegalArgumentException("Path variable 'id' must be a number"));
        }

        return serverRequest.bodyToMono(FranchiseRequest.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("RequestBody is required!")))
                .flatMap(this::validateJakarta)
                .map(franchiseDtoMapper::toModel)
                .flatMap(franchiseRequest -> franchiseUseCase.updateFranchise(franchiseRequest.toBuilder().id(franchiseId).build()))
                .map(franchiseDtoMapper::toResponse)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }


    // BRANCH ENDPOINTS

    public Mono<ServerResponse> listenAddProductToBranch(ServerRequest serverRequest) {
        log.info("Handling {} {}", serverRequest.methodName(), serverRequest.path());
        Long branchId;
        try {
            branchId = Long.valueOf(serverRequest.pathVariable("id"));
        } catch (Exception e) {
            return Mono.error(new IllegalArgumentException("Path variable 'id' must be a number"));
        }
        return serverRequest.bodyToMono(ProductRequest.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("RequestBody is required!")))
                .flatMap(this::validateJakarta)
                .map(productDtoMapper::toModel)
                .flatMap(product -> branchUseCase.addProduct(branchId, product))
                .map(productDtoMapper::toResponse)
                .flatMap(resp -> ServerResponse.ok().bodyValue(resp));
    }


    public Mono<ServerResponse> listenDeleteProductFromBranch(ServerRequest serverRequest) {
        log.info("Handling {} {}", serverRequest.methodName(), serverRequest.path());
        Long branchId;
        Long productId;
        try {
            branchId = Long.valueOf(serverRequest.pathVariable("branchId"));
            productId = Long.valueOf(serverRequest.pathVariable("productId"));
        }catch (Exception e) {
            return Mono.error(new IllegalArgumentException("Path variable 'branchId' and 'productId' must be a number"));
        }

        return branchUseCase.deleteProductFromBranch(branchId, productId).then(ServerResponse.noContent().build());


    }


    public Mono<ServerResponse> listenUpdateBranch(ServerRequest serverRequest) {
        log.info("Handling {} {}", serverRequest.methodName(), serverRequest.path());
        Long branchId;
        Long franchiseId;
        try {
            branchId = Long.valueOf(serverRequest.pathVariable("branchId"));
            franchiseId = Long.valueOf(serverRequest.pathVariable("franchiseId"));
        }catch (Exception e) {
            return Mono.error(new IllegalArgumentException("Path variable 'id' and 'franchiseId' must be a number"));
        }

        return serverRequest.bodyToMono(BranchRequest.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("RequestBody is required!")))
                .flatMap(this::validateJakarta)
                .map(branchDtoMapper::toModel)
                .flatMap(branch -> branchUseCase.updateBranch(branch.toBuilder().id(branchId).franchiseId(franchiseId).build()))
                .map(branchDtoMapper::toResponse)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }





    // PRODUCT ENDPOINTS

    public Mono<ServerResponse> listenUpdateProduct(ServerRequest serverRequest) {
        log.info("Handling {} {}", serverRequest.methodName(), serverRequest.path());
        Long branchId;
        Long productId;
        try {
            branchId = Long.valueOf(serverRequest.pathVariable("branchId"));
            productId = Long.valueOf(serverRequest.pathVariable("productId"));
        }catch (Exception e) {
            return Mono.error(new IllegalArgumentException("Path variable 'branchId' and 'productId' must be a number"));
        }

        return serverRequest.bodyToMono(ProductUpdateRequest.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("RequestBody is required!")))
                .flatMap(this::validateJakarta)
                .map(productDtoMapper::toModel)
                .flatMap(product -> productUseCase.updateProduct( product.toBuilder().id(productId).branchId(branchId).build()))
                .map(productDtoMapper::toResponse)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }


    public Mono<ServerResponse> listenGetTopProductsFromBranchByFranchiseId(ServerRequest serverRequest) {
        log.info("Handling {} {}", serverRequest.methodName(), serverRequest.path());
        Optional<String> franchiseIdOpt = serverRequest.queryParam("franchiseId");
        if (franchiseIdOpt.isEmpty()) {
            return Mono.error(new IllegalArgumentException("Query param 'franchiseId' is required"));
        }
        Long franchiseId;
        try {
            franchiseId = Long.valueOf(franchiseIdOpt.get());
        } catch (NumberFormatException e) {
            return Mono.error(new IllegalArgumentException("Query param 'franchiseId' must be a number"));
        }
        return ServerResponse.ok().body(productUseCase.findTopStockForBranchByFranchiseId(franchiseId), ProductTopStock.class);

    }

    public Mono<ServerResponse> livenessCheck(ServerRequest serverRequest) {
        log.info("Handling {} {}", serverRequest.methodName(), serverRequest.path());
        return ServerResponse.ok().bodyValue("I'm alive!");
    }

    // VALIDATE JAKARTA FUNCTIÓN -> Valid data and throws an exception if notValid
    private <T>  Mono<T> validateJakarta(T req) {
        return Mono.fromCallable(() -> {
            var violations = validator.validate(req);
            if(!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
            return req;
        }).subscribeOn(Schedulers.boundedElastic());
    }





}
