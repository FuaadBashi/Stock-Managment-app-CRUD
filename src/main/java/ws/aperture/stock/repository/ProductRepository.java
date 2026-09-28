package ws.aperture.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ws.aperture.stock.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // @Query(SELECT product FROM Product product WHERE product.findByName = "felix")

    Product findByName(String name);
}
