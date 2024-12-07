package ws.aperture.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ws.aperture.stock.model.Ingredient;

public interface  IngredientRepository extends JpaRepository<Ingredient, Long > {
    
}
