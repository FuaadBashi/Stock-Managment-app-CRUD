package ws.aperture.stock.model.keys;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite key of {@link ws.aperture.stock.model.CustomerOrderItem}: one line per product in an
 * order. Field names match the entity's {@code @Id} associations, and each holds the referenced
 * entity's primary key, as {@code @IdClass} requires.
 */
public class CustomerOrderItemId implements Serializable {

    private Long customerOrder;
    private Long product;

    public CustomerOrderItemId() {}

    public CustomerOrderItemId(Long customerOrder, Long product) {
        this.customerOrder = customerOrder;
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CustomerOrderItemId other
                && Objects.equals(customerOrder, other.customerOrder)
                && Objects.equals(product, other.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerOrder, product);
    }
}
