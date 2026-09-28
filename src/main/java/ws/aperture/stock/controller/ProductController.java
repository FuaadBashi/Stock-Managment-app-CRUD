package ws.aperture.stock.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
import ws.aperture.stock.dto.ProductDTO;
import ws.aperture.stock.dto.ProductRequestDTO;
import ws.aperture.stock.dto.RecipeDTO;
import ws.aperture.stock.dto.RecipeItemDTO;
import ws.aperture.stock.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {
  private final ProductService service;

  public ProductController(ProductService service) {
    this.service = service;
  }

  @GetMapping
  public List<ProductDTO> all() {
    return service.all();
  }

  @GetMapping("/{id}")
  public ProductDTO get(@PathVariable Long id) {
    return service.getById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ProductDTO create(@Valid @RequestBody ProductRequestDTO request) {
    return service.createProduct(request);
  }

  @PutMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ProductDTO legacyCreate(@Valid @RequestBody ProductRequestDTO request) {
    return service.createProduct(request);
  }

  @PutMapping("/{id}")
  public ProductDTO update(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO request) {
    return service.update(id, request);
  }

  @DeleteMapping("/{id}")
  public ProductDTO discontinue(@PathVariable Long id) {
    return service.discontinue(id);
  }

  @GetMapping("/{id}/recipe")
  public RecipeDTO recipe(@PathVariable Long id) {
    return service.recipe(id);
  }

  @PutMapping("/{id}/recipe")
  public RecipeDTO replace(
      @PathVariable Long id,
      @RequestBody @NotEmpty @Size(max = 100) List<@NotNull @Valid RecipeItemDTO> items) {
    return service.updateRecipe(id, items);
  }

  @PostMapping("/update-recipe/{id}")
  public RecipeDTO legacyReplace(
      @PathVariable Long id,
      @RequestBody @NotEmpty @Size(max = 100) List<@NotNull @Valid RecipeItemDTO> items) {
    return service.updateRecipe(id, items);
  }
}
