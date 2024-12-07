package ws.aperture.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ws.aperture.stock.model.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Supplier findByName (String supplierName );
    Supplier findByCompanyNumber( String companyNumber );
}
