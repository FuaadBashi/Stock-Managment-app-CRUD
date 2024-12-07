package ws.aperture.stock.model;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;




@Entity
@Getter
@Setter
@Table(name = "supplier")
public class Supplier {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    private Long id;

    @Size(min=6, max=100)
    @Column(nullable = false, unique=true)
    private String name;

    @Column(columnDefinition = "DATE")
    LocalDate establishedDate;
    
    @Size(min=8,max=12)
    @Column(nullable = false, unique=true)
    private String companyNumber;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<StockItem> stockItems;
    
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<SupplierAddress> addresses;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<SupplierContact> contacts;



}
