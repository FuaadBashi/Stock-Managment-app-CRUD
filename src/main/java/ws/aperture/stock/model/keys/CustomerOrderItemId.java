package ws.aperture.stock.model.keys;

import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrderItemId implements Serializable {
  private Long customerOrder;
  private Long product;
}
