package ws.aperture.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ws.aperture.stock.model.RecipeMapping;

public interface RecipeMappingRepository extends JpaRepository<RecipeMapping, Long>  {
    
}
