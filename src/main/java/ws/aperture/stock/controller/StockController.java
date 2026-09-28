package ws.aperture.stock.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ws.aperture.stock.dto.StockItemDTO;
import ws.aperture.stock.dto.StockRecordDTO;
import ws.aperture.stock.dto.StockRecordRequestDTO;
import ws.aperture.stock.exceptions.NoStockItemWithIdException;
import ws.aperture.stock.exceptions.NonPositiveIngredientQuantityException;
import ws.aperture.stock.service.StockService;

@RestController
@RequestMapping("/stock")
public class StockController {

    private final StockService stockService;

    @Autowired
    StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("")
    public List<StockItemDTO> all() {
        return stockService.all();
    }

    @PostMapping("")
    public StockItemDTO addStockItem(@RequestBody StockItemDTO stockItem) {
        return stockService.addStockItem(stockItem);
    }

    @PutMapping("/{id}")
    // The path says {id}; binding it to a parameter named stockItemId failed on every call.
    public StockRecordDTO addStock(
            @PathVariable("id") Long stockItemId, @RequestBody StockRecordRequestDTO stockRecordDTO)
            throws NoStockItemWithIdException, NonPositiveIngredientQuantityException {
        return stockService.addStock(stockItemId, stockRecordDTO);
    }
}
