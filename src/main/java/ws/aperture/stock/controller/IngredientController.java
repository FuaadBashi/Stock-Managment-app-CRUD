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
import ws.aperture.stock.dto.IngredientDTO;
import ws.aperture.stock.dto.IngredientRequestDTO;
import ws.aperture.stock.service.IngredientService;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {
  private final IngredientService service;

  public IngredientController(IngredientService service) {
    this.service = service;
  }

  @GetMapping
  public List<IngredientDTO> all() {
    return service.all();
  }

  @GetMapping("/{id}")
  public IngredientDTO get(@PathVariable Long id) {
    return service.getById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public IngredientDTO create(@Valid @RequestBody IngredientRequestDTO request) {
    return service.create(request);
  }

  @PutMapping("/{id}")
  public IngredientDTO update(
      @PathVariable Long id, @Valid @RequestBody IngredientRequestDTO request) {
    return service.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}
