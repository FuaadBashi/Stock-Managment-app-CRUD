package ws.aperture.stock.model.keys;

import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RecipeMappingId implements Serializable {
  private Long product;
  private Long ingredient;
}
