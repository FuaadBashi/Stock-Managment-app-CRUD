package ws.aperture.stock.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ws.aperture.stock.model.RecipeMapping;
import ws.aperture.stock.model.keys.RecipeMappingId;

public interface RecipeMappingRepository extends JpaRepository<RecipeMapping, RecipeMappingId> {
  List<RecipeMapping> findByProductIdOrderByIngredientId(Long id);

  boolean existsByIngredientId(Long id);

  @Modifying
  @Query("delete from RecipeMapping r where r.product.id = :id")
  void deleteByProductId(@Param("id") Long id);
}
