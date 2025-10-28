package co.com.nequi.reto.r2dbc.product;

import co.com.nequi.reto.model.product.Product;
import co.com.nequi.reto.model.product.ProductTopStock;
import co.com.nequi.reto.model.product.gateways.ProductRepository;
import co.com.nequi.reto.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ProductReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Product/* change for domain model */,
        ProductData/* change for adapter model */,
        Long,
        ProductReactiveRepository
> implements ProductRepository {



            private final DatabaseClient databaseClient;


            public ProductReactiveRepositoryAdapter(ProductReactiveRepository repository, ObjectMapper mapper, DatabaseClient databaseClient) {
                /**
                 *  Could be use mapper.mapBuilder if your domain model implement builder pattern
                 *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
                 *  Or using mapper.map with the class of the object model
                 */
                super(repository, mapper, d -> mapper.map(d, Product.class/* change for domain model */));
                this.databaseClient = databaseClient;
            }



            @Override
            public Mono<Product> findByBranchIdAndProductId(Long branchId, Long productId) {
                return repository.findByBranchIdAndId(branchId,productId)
                        .map(this::toEntity);
            }

            @Override
            public Mono<Void> deleteByBranchIdAndId(Long branchId, Long productId) {
                return repository.deleteByBranchIdAndId(branchId,productId);
            }

            @Override
            public Flux<ProductTopStock> findTopStockForBranchByFranchiseId(Long franchiseId) {

                String sqlQuery = "SELECT " +
                        "b.name  AS branch_name, " +
                        "p.name  AS product_name, " +
                        "p.stock AS product_stock " +
                        "FROM franchise f " +
                        "JOIN branch   b ON b.franchise_id = f.id " +
                        "JOIN product  p ON p.branch_id    = b.id " +
                        "JOIN ( " +
                        "   SELECT branch_id, MAX(stock) AS max_stock " +
                        "   FROM product " +
                        "   GROUP BY branch_id " +
                        "    ) mx ON mx.branch_id = p.branch_id AND p.stock = mx.max_stock " +
                        "WHERE f.id = :franchiseId";

                return databaseClient.sql(sqlQuery)
                        .bind("franchiseId",franchiseId)
                        .map((row, meta) -> {
                            String productName = row.get("product_name", String.class);
                            String branchName = row.get("branch_name", String.class);
                            Integer productStock = row.get("product_stock", Integer.class);

                            return ProductTopStock.builder()
                                    .branchName(branchName)
                                    .productName(productName)
                                    .stock(productStock)
                                    .build();


                        }).all();
            }
}
