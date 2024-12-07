package ws.aperture.stock.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ws.aperture.stock.dto.StockItemDTO;
import ws.aperture.stock.dto.StockRecordDTO;
import ws.aperture.stock.dto.StockRecordRequestDTO;
import ws.aperture.stock.exceptions.NoProductWithIdException;
import ws.aperture.stock.exceptions.NoSupplierWithIdException;
import ws.aperture.stock.model.StockItem;
import ws.aperture.stock.model.Supplier;
import ws.aperture.stock.repository.StockItemRepository;
import ws.aperture.stock.repository.StockRecordRepository;
import ws.aperture.stock.exceptions.NoStockItemWithIdException;

@Service
public class StockService {

    private final StockItemRepository stockItemRepository;
    private final SupplierService supplierService;
    

    @Autowired
    StockService( StockItemRepository stockItemRepository, SupplierService supplierService ) {
        this.stockItemRepository = stockItemRepository;
        this.supplierService   = supplierService;
    }

    @Transactional 
    public List<StockItemDTO> all() {
    return stockItemRepository.findAll()
               .stream()
               .map( stockItem -> StockItemDTO.generateDTO( stockItem ))
               .collect( Collectors.toList());
    }
    
    @Transactional
    public boolean existsById(Long id) {
        return stockItemRepository.existsById(id);
    }


    @Transactional
    public StockItemDTO addStockItem(StockItemDTO stockItemRequest)
        throws NoSupplierWithIdException {

        Long supplierId = stockItemRequest.supplierId();

        if (!supplierService.existsById(supplierId) ){
            throw new NoSupplierWithIdException(supplierId);
        }

        Supplier supplier = supplierService.getReferenceById(supplierId);

        StockItem stockItem = new StockItem();

        stockItem.setName(stockItemRequest.name());
        stockItem.setDescription(stockItemRequest.desc());
        stockItem.setSupplier(supplier);
        stockItem.setRetailPrice(stockItemRequest.retailPrice());

        return StockItemDTO.generateDTO(stockItem);
    }

    public StockRecordDTO addStock(Long stockItemId, StockRecordRequestDTO stockRecordRequestDTO) 
                throws  NoStockItemWithIdException {

        if ( ! existsById( stockItemId ) ) {
         throw new NoStockItemWithIdException(stockItemId);
        }

        return new StockRecordDTO(stockItemId, null, null, 0);
    }



}
