package co.com.nequi.reto.r2dbc.product;


import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("product")
@Data
public class ProductData {
    @Id
    private Long id;
    private String name;
    private Integer stock; // Cantidad disponible de productos
    @Column("branch_id")
    private Long branchId;
}
