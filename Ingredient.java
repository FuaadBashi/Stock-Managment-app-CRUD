package ws.aperture.stock.model;
import java.util.Set;

import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ingredient_id")
    private Long id;
    
    @Column(nullable = false, unique=false)
    private String name;

    @Column(nullable = false, unique=false)
    @Enumerated(EnumType.STRING)
    private Unit unit;

    @OneToMany(mappedBy="ingredient")
    private Set<StockItem> stockItems;

    @OneToMany( mappedBy = "ingredient")
    private Set<RecipeMapping> recipeMappings;



}

