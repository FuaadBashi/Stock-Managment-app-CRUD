package ws.aperture.stock.service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ws.aperture.stock.dto.StockItemDTO;
import ws.aperture.stock.dto.StockRecordDTO;
import ws.aperture.stock.dto.StockRecordRequestDTO;
import ws.aperture.stock.enums.ConditionStatus;
import ws.aperture.stock.enums.Storage;
import ws.aperture.stock.exceptions.NoStockItemWithIdException;
import ws.aperture.stock.exceptions.NoSupplierWithIdException;
import ws.aperture.stock.exceptions.NonPositiveIngredientQuantityException;
import ws.aperture.stock.model.StockItem;
import ws.aperture.stock.model.StockRecord;
import ws.aperture.stock.model.Supplier;
import ws.aperture.stock.repository.StockItemRepository;
import ws.aperture.stock.repository.StockRecordRepository;

@Service
public class StockService {

    private final StockItemRepository stockItemRepository;
    private final StockRecordRepository stockRecordRepository;
    private final SupplierService supplierService;

    @Autowired
    StockService(
            StockItemRepository stockItemRepository,
            StockRecordRepository stockRecordRepository,
            SupplierService supplierService) {
        this.stockItemRepository = stockItemRepository;
        this.stockRecordRepository = stockRecordRepository;
        this.supplierService = supplierService;
    }

    @Transactional
    public List<StockItemDTO> all() {
        return stockItemRepository.findAll().stream()
                .map(stockItem -> StockItemDTO.generateDTO(stockItem))
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean existsById(Long id) {
        return stockItemRepository.existsById(id);
    }

    @Transactional
    public StockItemDTO addStockItem(StockItemDTO stockItemRequest)
            throws NoSupplierWithIdException {

        Long supplierId = stockItemRequest.supplierId();

        if (!supplierService.existsById(supplierId)) {
            throw new NoSupplierWithIdException(supplierId);
        }

        Supplier supplier = supplierService.getReferenceById(supplierId);

        StockItem stockItem = new StockItem();

        stockItem.setName(stockItemRequest.name());
        stockItem.setDescription(stockItemRequest.desc());
        stockItem.setSupplier(supplier);
        stockItem.setRetailPrice(stockItemRequest.retailPrice());

        // The item was built but never saved, so it came back with a null id and never appeared
        // in GET /stock.
        return StockItemDTO.generateDTO(stockItemRepository.save(stockItem));
    }

    /** Records a delivery of an existing stock item. */
    @Transactional
    public StockRecordDTO addStock(Long stockItemId, StockRecordRequestDTO request)
            throws NoStockItemWithIdException, NonPositiveIngredientQuantityException {

        if (!existsById(stockItemId)) {
            throw new NoStockItemWithIdException(stockItemId);
        }
        if (request.quantity() <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        if (request.expiryDate() == null) {
            throw new IllegalArgumentException("expiryDate is required");
        }

        StockRecord stockRecord = new StockRecord();
        stockRecord.setStockItem(stockItemRepository.getReferenceById(stockItemId));
        stockRecord.setIncomingDate(
                request.incomingDate() != null ? request.incomingDate() : LocalDate.now());
        stockRecord.setExpiryDate(request.expiryDate());
        stockRecord.setQuantity(request.quantity());
        stockRecord.setConditionStatus(ConditionStatus.SEALED);
        stockRecord.setStorage(request.storage() != null ? request.storage() : Storage.ROOM_TEMP);

        return StockRecordDTO.generateDTO(stockRecordRepository.save(stockRecord));
    }
}
