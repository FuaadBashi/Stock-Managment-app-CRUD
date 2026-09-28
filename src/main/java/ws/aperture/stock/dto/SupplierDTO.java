package ws.aperture.stock.dto;

import ws.aperture.stock.model.Supplier;

public record SupplierDTO(Long id, String supplierName, String companyNumber) {
    public static SupplierDTO generateDTO(Supplier supplier) {
        return new SupplierDTO(supplier.getId(), supplier.getName(), supplier.getCompanyNumber());
    }
}
