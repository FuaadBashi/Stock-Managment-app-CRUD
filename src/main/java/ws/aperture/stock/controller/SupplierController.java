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
import ws.aperture.stock.dto.SupplierDTO;
import ws.aperture.stock.dto.SupplierRequestDTO;
import ws.aperture.stock.service.SupplierService;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {
  private final SupplierService service;

  public SupplierController(SupplierService service) {
    this.service = service;
  }

  @GetMapping({"", "/all"})
  public List<SupplierDTO> all() {
    return service.all();
  }

  @GetMapping("/{id}")
  public SupplierDTO get(@PathVariable Long id) {
    return service.getById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public SupplierDTO create(@Valid @RequestBody SupplierRequestDTO request) {
    return service.registerSupplier(request);
  }

  @PutMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public SupplierDTO register(@Valid @RequestBody SupplierRequestDTO request) {
    return service.registerSupplier(request);
  }

  @PutMapping("/{id}")
  public SupplierDTO update(@PathVariable Long id, @Valid @RequestBody SupplierRequestDTO request) {
    return service.update(id, request);
  }

  @DeleteMapping({"/{id}", "/remove/{id}"})
  public SupplierDTO delete(@PathVariable Long id) {
    return service.deleteById(id);
  }
}
