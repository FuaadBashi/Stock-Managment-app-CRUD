package ws.aperture.stock.model;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ws.aperture.stock.enums.CustomerOrderStatus;


@Entity
@Table(name = "customer_order")
@Getter
@Setter
public class CustomerOrder {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "customer_order_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private SysUser creator;

    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime orderTimeStamp;

    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    private CustomerOrderStatus status = CustomerOrderStatus.NEW;    /* simple assignment for default values! */

    @OneToMany(mappedBy="customerOrder", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<CustomerOrderItem> customerOrderItems;


}
