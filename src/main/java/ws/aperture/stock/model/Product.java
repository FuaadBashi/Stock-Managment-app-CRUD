package ws.aperture.stock.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import ws.aperture.stock.enums.ProductAndStockStatus;

@Entity
@Table(name = "product")
@Getter
@Setter
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "product_id")
  private Long id;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(length = 2000)
  private String description;

  @Column(nullable = false, precision = 14, scale = 2)
  private BigDecimal retailPrice;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ProductAndStockStatus status = ProductAndStockStatus.IN_STOCK;
}
