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
import ws.aperture.stock.dto.CustomerOrderDTO;
import ws.aperture.stock.dto.CustomerOrderRequestDTO;
import ws.aperture.stock.enums.CustomerOrderStatus;
import ws.aperture.stock.service.CustomerOrderService;

@RestController
@RequestMapping("/customer-orders")
public class CustomerOrderController {
  private final CustomerOrderService service;

  public CustomerOrderController(CustomerOrderService service) {
    this.service = service;
  }

  @GetMapping
  public List<CustomerOrderDTO> all() {
    return service.all();
  }

  @PostMapping({"", "/new"})
  @ResponseStatus(HttpStatus.CREATED)
  public CustomerOrderDTO create(@Valid @RequestBody CustomerOrderRequestDTO request) {
    return service.createCustomerOrder(request);
  }

  @GetMapping("/{id}")
  public CustomerOrderDTO get(@PathVariable Long id) {
    return service.getById(id);
  }

  @PutMapping("/{id}")
  public CustomerOrderDTO update(
      @PathVariable Long id, @Valid @RequestBody CustomerOrderRequestDTO request) {
    return service.update(id, request);
  }

  @PostMapping({"/{id}/start", "/start/{id}"})
  public CustomerOrderDTO start(@PathVariable Long id) {
    return service.transition(id, CustomerOrderStatus.STARTED);
  }

  @PostMapping({"/{id}/complete", "/complete/{id}"})
  public CustomerOrderDTO complete(@PathVariable Long id) {
    return service.transition(id, CustomerOrderStatus.COMPLETED);
  }

  @PostMapping({"/{id}/cancel", "/cancel/{id}", "/cancle/{id}"})
  public CustomerOrderDTO cancel(@PathVariable Long id) {
    return service.transition(id, CustomerOrderStatus.CANCELLED);
  }

  @DeleteMapping("/{id}")
  public CustomerOrderDTO delete(@PathVariable Long id) {
    return service.transition(id, CustomerOrderStatus.DELETED);
  }

  @PostMapping("/delete/{id}")
  public CustomerOrderDTO legacyDelete(@PathVariable Long id) {
    return delete(id);
  }
}
