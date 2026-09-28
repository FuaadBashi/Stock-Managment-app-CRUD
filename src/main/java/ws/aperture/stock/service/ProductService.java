package ws.aperture.stock.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ws.aperture.stock.dto.ProductDTO;
import ws.aperture.stock.dto.ProductRequestDTO;
import ws.aperture.stock.dto.RecipeDTO;
import ws.aperture.stock.dto.RecipeItemDTO;
import ws.aperture.stock.enums.ProductAndStockStatus;
import ws.aperture.stock.exceptions.EmptyRecipeBodyException;
import ws.aperture.stock.exceptions.NoProductWithIdException;
import ws.aperture.stock.model.Product;
import ws.aperture.stock.model.RecipeMapping;
import ws.aperture.stock.repository.ProductRepository;
import ws.aperture.stock.repository.RecipeMappingRepository;

@Service
@Transactional(readOnly = true)
public class ProductService {
  private final ProductRepository products;
  private final RecipeMappingRepository recipes;
  private final IngredientService ingredients;

  public ProductService(
      ProductRepository products, RecipeMappingRepository recipes, IngredientService ingredients) {
    this.products = products;
    this.recipes = recipes;
    this.ingredients = ingredients;
  }

  public Product require(Long id) {
    return products.findById(id).orElseThrow(() -> new NoProductWithIdException(id));
  }

  public Product requireLocked(Long id) {
    return products.findLockedById(id).orElseThrow(() -> new NoProductWithIdException(id));
  }

  public ProductDTO getById(Long id) {
    return ProductDTO.generateDTO(require(id));
  }

  public List<ProductDTO> all() {
    return products.findAll(Sort.by("id")).stream().map(ProductDTO::generateDTO).toList();
  }

  @Transactional
  public ProductDTO createProduct(ProductRequestDTO request) {
    return save(new Product(), request);
  }

  @Transactional
  public ProductDTO update(Long id, ProductRequestDTO request) {
    return save(requireLocked(id), request);
  }

  private ProductDTO save(Product product, ProductRequestDTO request) {
    product.setName(request.name().strip());
    product.setDescription(request.desc());
    product.setRetailPrice(request.retailPrice());
    return ProductDTO.generateDTO(products.saveAndFlush(product));
  }

  @Transactional
  public ProductDTO discontinue(Long id) {
    var product = requireLocked(id);
    product.setStatus(ProductAndStockStatus.DISCONTINUED);
    return ProductDTO.generateDTO(product);
  }

  public RecipeDTO recipe(Long id) {
    require(id);
    return new RecipeDTO(
        id,
        recipes.findByProductIdOrderByIngredientId(id).stream()
            .map(r -> new RecipeItemDTO(r.getIngredient().getId(), r.getQuantity()))
            .toList());
  }

  @Transactional
  public RecipeDTO updateRecipe(Long id, List<RecipeItemDTO> items) {
    var product = requireLocked(id);
    if (items == null || items.isEmpty()) throw new EmptyRecipeBodyException();
    Set<Long> seen = new HashSet<>();
    List<RecipeMapping> mappings = new ArrayList<>();
    for (var item : items) {
      if (!seen.add(item.ingredientId()))
        throw new IllegalArgumentException("Duplicate recipe ingredient");
      var mapping = new RecipeMapping();
      mapping.setProduct(product);
      mapping.setIngredient(ingredients.require(item.ingredientId()));
      mapping.setQuantity(item.quantity());
      mappings.add(mapping);
    }
    recipes.deleteByProductId(id);
    recipes.flush();
    recipes.saveAllAndFlush(mappings);
    return recipe(id);
  }
}
