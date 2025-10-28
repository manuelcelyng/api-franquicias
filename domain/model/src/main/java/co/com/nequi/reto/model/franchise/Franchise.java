package co.com.nequi.reto.model.franchise;
import lombok.*;
//import lombok.NoArgsConstructor;


@Getter
@Setter
@NoArgsConstructor  // El mapper del driven adapter lo necesita
@AllArgsConstructor
@Builder(toBuilder = true)
public class Franchise {
    private Long id;
    private String name;
}
