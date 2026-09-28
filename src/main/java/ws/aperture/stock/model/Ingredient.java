package ws.aperture.stock.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ws.aperture.stock.enums.Unit;

@Entity
@Table(name = "ingredient")
@Getter
@Setter
public class Ingredient {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ingredient_id")
  private Long id;

  @Column(nullable = false, length = 100)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Unit unit;
}
