package ws.aperture.stock.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import ws.aperture.stock.enums.CustomerOrderStatus;

@Entity
@Table(name = "customer_order")
@Getter
@Setter
public class CustomerOrder {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "customer_order_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private SysUser creator;

  @Column(nullable = false)
  private LocalDateTime orderTimeStamp;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CustomerOrderStatus status = CustomerOrderStatus.NEW;

  @Version private long version;

  @OneToMany(mappedBy = "customerOrder", cascade = CascadeType.ALL, orphanRemoval = true)
  private java.util.Set<CustomerOrderItem> customerOrderItems = new java.util.LinkedHashSet<>();
}
