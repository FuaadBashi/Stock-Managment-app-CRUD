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
import ws.aperture.stock.model.keys.CustomerOrderItemId;

@Entity
@Table(name = "customer_order_item")
@IdClass(CustomerOrderItemId.class)
@Getter
@Setter
public class CustomerOrderItem {
  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_order_id")
  private CustomerOrder customerOrder;

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private Product product;

  @Column(nullable = false)
  private int quantity;

  @Column(nullable = false, precision = 14, scale = 2)
  private BigDecimal unitPrice;
}
