package co.com.nequi.reto.api.docs;

import co.com.nequi.reto.api.Handler;
import co.com.nequi.reto.api.dto.BranchRequest;
import co.com.nequi.reto.api.dto.BranchResponse;
import co.com.nequi.reto.api.dto.FranchiseRequest;
import co.com.nequi.reto.api.dto.FranchiseResponse;
import co.com.nequi.reto.api.errors.ErrorResponse;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@OpenAPIDefinition(
        info = @Info(
                title = "Franchises API",
                version = "v1",
                description = "API para gestionar franquicias y chequeo de salud"
        )
)
public interface OpenApiDocs {

    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/franchise",
                    produces = {"application/json"},
                    consumes = {"application/json"},
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "listenCreateFranchise",
                    operation = @io.swagger.v3.oas.annotations.Operation(
                            operationId = "createFranchise",
                            summary = "Crear una nueva franquicia",
                            description = "Crea una franquicia a partir de la información enviada",
                            tags = {"Franchises"},
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Datos de la franquicia a crear",
                                    content = @Content(schema = @Schema(implementation = FranchiseRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Franquicia creada", content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/franchise/{id}/branch",
                    produces = {"application/json"},
                    consumes = {"application/json"},
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "listenAddBranchToFranchise",
                    operation = @io.swagger.v3.oas.annotations.Operation(
                            operationId = "addBranchToFranchise",
                            summary = "Agregar una sucursal a una franquicia",
                            description = "Crea una nueva sucursal para la franquicia indicada",
                            tags = {"Branches"},
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Datos de la sucursal a crear",
                                    content = @Content(schema = @Schema(implementation = BranchRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Sucursal creada", content = @Content(schema = @Schema(implementation = BranchResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/branch/{id}/product",
                    produces = {"application/json"},
                    consumes = {"application/json"},
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "listenAddProductToBranch",
                    operation = @io.swagger.v3.oas.annotations.Operation(
                            operationId = "addProductToBranch",
                            summary = "Agregar un producto a una sucursal",
                            description = "Crea un producto asociado a la sucursal indicada",
                            tags = {"Products"},
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Datos del producto a crear",
                                    content = @Content(schema = @Schema(implementation = co.com.nequi.reto.api.dto.ProductRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Producto creado", content = @Content(schema = @Schema(implementation = co.com.nequi.reto.api.dto.ProductResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/branch/{branchId}/product/{productId}",
                    produces = {"application/json"},
                    method = RequestMethod.DELETE,
                    beanClass = Handler.class,
                    beanMethod = "listenDeleteProductFromBranch",
                    operation = @io.swagger.v3.oas.annotations.Operation(
                            operationId = "deleteProductFromBranch",
                            summary = "Eliminar un producto de una sucursal",
                            tags = {"Products"},
                            responses = {
                                    @ApiResponse(responseCode = "204", description = "Producto eliminado"),
                                    @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/product/{productId}/branch/{branchId}",
                    produces = {"application/json"},
                    consumes = {"application/json"},
                    method = RequestMethod.PATCH,
                    beanClass = Handler.class,
                    beanMethod = "listenUpdateProduct",
                    operation = @io.swagger.v3.oas.annotations.Operation(
                            operationId = "updateProduct",
                            summary = "Actualizar un producto de una sucursal",
                            tags = {"Products"},
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Datos del producto a actualizar (parcial)",
                                    content = @Content(schema = @Schema(implementation = co.com.nequi.reto.api.dto.ProductUpdateRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Producto actualizado", content = @Content(schema = @Schema(implementation = co.com.nequi.reto.api.dto.ProductResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/branch/{branchId}/franchise/{franchiseId}",
                    produces = {"application/json"},
                    consumes = {"application/json"},
                    method = RequestMethod.PATCH,
                    beanClass = Handler.class,
                    beanMethod = "listenUpdateBranch",
                    operation = @io.swagger.v3.oas.annotations.Operation(
                            operationId = "updateBranch",
                            summary = "Actualizar una sucursal",
                            tags = {"Branches"},
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Datos de la sucursal a actualizar (parcial)",
                                    content = @Content(schema = @Schema(implementation = BranchRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Sucursal actualizada", content = @Content(schema = @Schema(implementation = BranchResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/franchise/{franchiseId}",
                    produces = {"application/json"},
                    consumes = {"application/json"},
                    method = RequestMethod.PATCH,
                    beanClass = Handler.class,
                    beanMethod = "listenUpdateFranchise",
                    operation = @io.swagger.v3.oas.annotations.Operation(
                            operationId = "updateFranchise",
                            summary = "Actualizar una franquicia",
                            tags = {"Franchises"},
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Datos de la franquicia a actualizar (parcial)",
                                    content = @Content(schema = @Schema(implementation = FranchiseRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Franquicia actualizada", content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/product/top",
                    produces = {"application/json"},
                    method = RequestMethod.GET,
                    beanClass = Handler.class,
                    beanMethod = "listenGetTopProductsFromBranchByFranchiseId",
                    operation = @io.swagger.v3.oas.annotations.Operation(
                            operationId = "getTopProductsFromBranchesByFranchise",
                            summary = "Top de productos por stock agrupado por sucursales de una franquicia",
                            tags = {"Products"},
                            parameters = {
                                    @io.swagger.v3.oas.annotations.Parameter(name = "franchiseId", description = "ID de la franquicia", required = true)
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Lista top por stock", content = @Content(schema = @Schema(implementation = co.com.nequi.reto.model.product.ProductTopStock.class))),
                                    @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/check",
                    produces = {"text/plain", "application/json"},
                    method = RequestMethod.GET,
                    beanClass = Handler.class,
                    beanMethod = "livenessCheck",
                    operation = @io.swagger.v3.oas.annotations.Operation(
                            operationId = "livenessCheck",
                            summary = "Chequeo de salud",
                            description = "Verifica que el servicio esté vivo",
                            tags = {"Health"},
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = String.class)))
                            }
                    )
            )
    })
    RouterFunction<ServerResponse> routerFunction(Handler handler);
}
