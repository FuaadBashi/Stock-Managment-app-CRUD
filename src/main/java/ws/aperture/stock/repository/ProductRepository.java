package ws.aperture.stock.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ws.aperture.stock.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
  Product findByName(String name);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select p from Product p where p.id = :id")
  Optional<Product> findLockedById(@Param("id") Long id);
}
