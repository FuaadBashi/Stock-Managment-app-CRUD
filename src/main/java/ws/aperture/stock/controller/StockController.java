package ws.aperture.stock.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ws.aperture.stock.dto.StockItemDTO;
import ws.aperture.stock.dto.StockItemRequestDTO;
import ws.aperture.stock.dto.StockRecordDTO;
import ws.aperture.stock.dto.StockRecordRequestDTO;
import ws.aperture.stock.service.StockService;

@RestController
@RequestMapping("/stock")
public class StockController {
  private final StockService service;

  public StockController(StockService service) {
    this.service = service;
  }

  @GetMapping
  public List<StockItemDTO> all() {
    return service.all();
  }

  @GetMapping("/{id}")
  public StockItemDTO get(@PathVariable Long id) {
    return service.getById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public StockItemDTO create(@Valid @RequestBody StockItemRequestDTO request) {
    return service.addStockItem(request);
  }

  @PutMapping("/{id}/details")
  public StockItemDTO update(
      @PathVariable Long id, @Valid @RequestBody StockItemRequestDTO request) {
    return service.update(id, request);
  }

  @PostMapping("/{id}/receipts")
  @ResponseStatus(HttpStatus.CREATED)
  public StockRecordDTO receive(
      @PathVariable Long id, @Valid @RequestBody StockRecordRequestDTO request) {
    return service.addStock(id, request);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.CREATED)
  public StockRecordDTO legacyReceive(
      @PathVariable Long id, @Valid @RequestBody StockRecordRequestDTO request) {
    return service.addStock(id, request);
  }

  @GetMapping("/{id}/receipts")
  public List<StockRecordDTO> receipts(@PathVariable Long id) {
    return service.records(id);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}
