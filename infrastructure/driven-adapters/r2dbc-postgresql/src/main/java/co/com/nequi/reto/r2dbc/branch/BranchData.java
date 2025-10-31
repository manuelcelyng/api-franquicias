package co.com.nequi.reto.r2dbc.branch;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("branch")
@Data
public class BranchData {
    @Id
    private Long id;
    private String name;
    @Column("franchise_id")
    private Long franchiseId;
}