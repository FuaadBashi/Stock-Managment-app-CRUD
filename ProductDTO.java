package ws.aperture.stock.dto;

import ws.aperture.stock.model.Product;

public record ProductDTO(Long id, String productName, double retailPrice) {
    
    public static ProductDTO generateDTO(Product product) {
        return new ProductDTO( product.getId(), product.getName(), product.getRetailPrice());
    }
                                                        
}
