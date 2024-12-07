package ws.aperture.stock.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import ws.aperture.stock.model.keys.CustomerOrderItemId;

@Entity
@IdClass(CustomerOrderItemId.class)
@Table(name = "customer_order_item")
@Getter
@Setter
public class CustomerOrderItem {

    @Id
    @ManyToOne
    @JoinColumn(name = "customer_order_id", nullable = false)
    private CustomerOrder customerOrder;

    @Id
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    @PositiveOrZero
    private int quantity;
    
}
