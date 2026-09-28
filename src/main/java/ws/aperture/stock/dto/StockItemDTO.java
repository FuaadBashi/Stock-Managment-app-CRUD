package ws.aperture.stock.dto;

import ws.aperture.stock.model.StockItem;

public record StockItemDTO(
        Long stockItemId, String name, Long supplierId, double retailPrice, String desc) {
    public static StockItemDTO generateDTO(StockItem stockItem) {
        return new StockItemDTO(
                stockItem.getId(),
                stockItem.getName(),
                stockItem.getSupplier().getId(),
                stockItem.getRetailPrice(),
                stockItem.getDescription());
    }
}
