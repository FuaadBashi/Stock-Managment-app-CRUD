package ws.aperture.stock.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ws.aperture.stock.dto.StockItemDTO;
import ws.aperture.stock.dto.StockItemRequestDTO;
import ws.aperture.stock.dto.StockRecordDTO;
import ws.aperture.stock.dto.StockRecordRequestDTO;
import ws.aperture.stock.enums.Storage;
import ws.aperture.stock.exceptions.ConflictException;
import ws.aperture.stock.exceptions.NoStockItemWithIdException;
import ws.aperture.stock.model.StockItem;
import ws.aperture.stock.model.StockRecord;
import ws.aperture.stock.repository.StockItemRepository;
import ws.aperture.stock.repository.StockRecordRepository;

@Service
@Transactional(readOnly = true)
public class StockService {
  private final StockItemRepository items;
  private final StockRecordRepository records;
  private final SupplierService suppliers;

  public StockService(
      StockItemRepository items, StockRecordRepository records, SupplierService suppliers) {
    this.items = items;
    this.records = records;
    this.suppliers = suppliers;
  }

  private StockItem require(Long id) {
    return items.findById(id).orElseThrow(() -> new NoStockItemWithIdException(id));
  }

  public List<StockItemDTO> all() {
    return items.findAll(Sort.by("id")).stream().map(StockItemDTO::generateDTO).toList();
  }

  public StockItemDTO getById(Long id) {
    return StockItemDTO.generateDTO(require(id));
  }

  @Transactional
  public StockItemDTO addStockItem(StockItemRequestDTO request) {
    return save(new StockItem(), request);
  }

  @Transactional
  public StockItemDTO update(Long id, StockItemRequestDTO request) {
    return save(require(id), request);
  }

  private StockItemDTO save(StockItem item, StockItemRequestDTO request) {
    var supplier = suppliers.require(request.supplierId());
    if (item.getId() != null && !item.getSupplier().getId().equals(supplier.getId()))
      throw new ConflictException(
          "Create a new stock item when changing supplier to preserve provenance");
    item.setSupplier(supplier);
    item.setName(request.name().strip());
    item.setDescription(request.desc());
    item.setRetailPrice(request.retailPrice());
    return StockItemDTO.generateDTO(items.saveAndFlush(item));
  }

  @Transactional
  public StockRecordDTO addStock(Long id, StockRecordRequestDTO request) {
    var item = require(id);
    if (request.stockItemId() != null && !request.stockItemId().equals(id))
      throw new IllegalArgumentException("Path and body stock item IDs differ");
    if (request.expiryDate().isBefore(request.incomingDate()))
      throw new IllegalArgumentException("Expiry date must not precede receipt date");
    var record = new StockRecord();
    record.setStockItem(item);
    record.setIncomingDate(request.incomingDate());
    record.setExpiryDate(request.expiryDate());
    record.setQuantity(request.quantity());
    record.setStorage(request.storage() == null ? Storage.ROOM_TEMP : request.storage());
    return StockRecordDTO.generateDTO(records.saveAndFlush(record));
  }

  public List<StockRecordDTO> records(Long id) {
    require(id);
    return records.findByStockItemIdOrderByIncomingDateDescIdDesc(id).stream()
        .map(StockRecordDTO::generateDTO)
        .toList();
  }

  @Transactional
  public void delete(Long id) {
    var item = require(id);
    if (records.existsByStockItemId(id))
      throw new ConflictException("Stock items with receipt history cannot be deleted");
    items.delete(item);
    items.flush();
  }
}
