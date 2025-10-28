package co.com.nequi.reto.model.branch;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Branch {
    private Long id;
    private String name;
    private Long franchiseId;
}
