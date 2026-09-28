package ws.aperture.stock.model.keys;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite key of {@link ws.aperture.stock.model.RecipeMapping}: one row per product and
 * ingredient. Field names match the entity's {@code @Id} associations, and each holds the
 * referenced entity's primary key, as {@code @IdClass} requires.
 */
public class RecipeMappingId implements Serializable {

    private Long product;
    private Long ingredient;

    public RecipeMappingId() {}

    public RecipeMappingId(Long product, Long ingredient) {
        this.product = product;
        this.ingredient = ingredient;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof RecipeMappingId other
                && Objects.equals(product, other.product)
                && Objects.equals(ingredient, other.ingredient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, ingredient);
    }
}
