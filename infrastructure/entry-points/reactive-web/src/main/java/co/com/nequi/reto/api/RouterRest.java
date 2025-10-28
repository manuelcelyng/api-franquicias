package co.com.nequi.reto.api;

import co.com.nequi.reto.api.docs.OpenApiDocs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest implements OpenApiDocs {

    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/franchise"), handler::listenCreateFranchise)
                .and(route(POST("/api/v1/franchise/{id}/branch"), handler::listenAddBranchToFranchise))
                .and(route(POST("/api/v1/branch/{id}/product"), handler::listenAddProductToBranch))
                .and(route(DELETE("/api/v1/branch/{branchId}/product/{productId}"), handler::listenDeleteProductFromBranch))
                .and(route(PATCH("/api/v1/product/{productId}/branch/{branchId}"), handler::listenUpdateProduct))
                .and(route(PATCH("/api/v1/branch/{branchId}/franchise/{franchiseId}"), handler::listenUpdateBranch))
                .and(route(PATCH("/api/v1/franchise/{franchiseId}"), handler::listenUpdateFranchise))
                .and(route(GET("/api/v1/product/top"), handler::listenGetTopProductsFromBranchByFranchiseId))
                .and(route(GET("/api/v1/check"), handler::livenessCheck));
    }
}
