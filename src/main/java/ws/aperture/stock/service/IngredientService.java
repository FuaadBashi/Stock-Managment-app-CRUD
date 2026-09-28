package ws.aperture.stock.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ws.aperture.stock.dto.IngredientDTO;
import ws.aperture.stock.dto.IngredientRequestDTO;
import ws.aperture.stock.exceptions.ConflictException;
import ws.aperture.stock.exceptions.NoIngredientWithIdException;
import ws.aperture.stock.model.Ingredient;
import ws.aperture.stock.repository.IngredientRepository;
import ws.aperture.stock.repository.RecipeMappingRepository;

@Service
@Transactional(readOnly = true)
public class IngredientService {
  private final IngredientRepository ingredients;
  private final RecipeMappingRepository recipes;

  public IngredientService(IngredientRepository ingredients, RecipeMappingRepository recipes) {
    this.ingredients = ingredients;
    this.recipes = recipes;
  }

  public List<IngredientDTO> all() {
    return ingredients.findAll(Sort.by("id")).stream().map(IngredientDTO::generateDTO).toList();
  }

  public Ingredient require(Long id) {
    return ingredients.findById(id).orElseThrow(() -> new NoIngredientWithIdException(id));
  }

  public IngredientDTO getById(Long id) {
    return IngredientDTO.generateDTO(require(id));
  }

  @Transactional
  public IngredientDTO create(IngredientRequestDTO request) {
    return save(new Ingredient(), request);
  }

  @Transactional
  public IngredientDTO update(Long id, IngredientRequestDTO request) {
    var ingredient = require(id);
    if (ingredient.getUnit() != request.unit() && recipes.existsByIngredientId(id))
      throw new ConflictException("Cannot change the unit of an ingredient used in a recipe");
    return save(ingredient, request);
  }

  private IngredientDTO save(Ingredient ingredient, IngredientRequestDTO request) {
    ingredient.setName(request.name().strip());
    ingredient.setUnit(request.unit());
    return IngredientDTO.generateDTO(ingredients.saveAndFlush(ingredient));
  }

  @Transactional
  public void delete(Long id) {
    var ingredient = require(id);
    if (recipes.existsByIngredientId(id))
      throw new ConflictException("Ingredient is used in a recipe");
    ingredients.delete(ingredient);
    ingredients.flush();
  }
}
