package ws.aperture.stock.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import ws.aperture.stock.model.keys.RecipeMappingId;

@Entity
@Table(name = "recipe_mapping")
@IdClass(RecipeMappingId.class)
@Getter
@Setter
public class RecipeMapping {
  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private Product product;

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ingredient_id")
  private Ingredient ingredient;

  @Column(nullable = false, precision = 14, scale = 3)
  private BigDecimal quantity;
}
