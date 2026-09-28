package ws.aperture.stock.service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ws.aperture.stock.dto.ProductDTO;
import ws.aperture.stock.dto.ProductRequestDTO;
import ws.aperture.stock.dto.RecipeDTO;
import ws.aperture.stock.dto.RecipeItemDTO;
import ws.aperture.stock.exceptions.EmptyRecipeBodyException;
import ws.aperture.stock.exceptions.NoIngredientWithIdException;
import ws.aperture.stock.exceptions.NoProductWithIdException;
import ws.aperture.stock.exceptions.NonPositiveIngredientQuantityException;
import ws.aperture.stock.model.Ingredient;
import ws.aperture.stock.model.Product;
import ws.aperture.stock.model.RecipeMapping;
import ws.aperture.stock.repository.ProductRepository;
import ws.aperture.stock.repository.RecipeMappingRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final RecipeMappingRepository recipeMappingRepository;

    private final IngredientService ingredientService;

    @Autowired
    ProductService(
            ProductRepository productRepository,
            RecipeMappingRepository recipeMappingRepository,
            IngredientService ingredientService) {
        this.productRepository = productRepository;
        this.recipeMappingRepository = recipeMappingRepository;
        this.ingredientService = ingredientService;
    }

    @Transactional
    public List<ProductDTO> all() {
        return productRepository.findAll().stream()
                .map(product -> ProductDTO.generateDTO(product))
                .collect(Collectors.toList());
    }

    public RecipeDTO updateRecipe(Long productId, List<RecipeItemDTO> recipeItems)
            throws NoProductWithIdException,
                    NoIngredientWithIdException,
                    NonPositiveIngredientQuantityException {
        if (!existsById(productId)) {
            throw new NoProductWithIdException(productId);
        }

        if (recipeItems.isEmpty()) {
            throw new EmptyRecipeBodyException();
        }

        for (RecipeItemDTO recipeItem : recipeItems) {
            Long ingredientId = recipeItem.ingredientId();
            if (!ingredientService.existsById(ingredientId)) {
                throw new NoIngredientWithIdException(ingredientId);
            }

            double quantity = recipeItem.quantity();
            if (quantity <= 0.0) {
                throw new NonPositiveIngredientQuantityException(ingredientId, quantity);
            }
        }

        // PASSED ALL CHECKS

        // Create and Save new Recipe Mapping to DB

        Product product = getReferenceById(productId);

        List<RecipeMapping> recipeMappings = new ArrayList<RecipeMapping>();

        for (RecipeItemDTO recipeItem : recipeItems) {
            RecipeMapping recipeMapping = new RecipeMapping();

            double quantity = recipeItem.quantity();
            Ingredient ingredient = ingredientService.getReferenceById(recipeItem.ingredientId());

            recipeMapping.setIngredient(ingredient);
            recipeMapping.setProduct(product);
            recipeMapping.setQuantity(quantity);

            recipeMappings.add(recipeMapping);
        }

        recipeMappingRepository.saveAllAndFlush(recipeMappings);

        return new RecipeDTO(productId, recipeItems);
    }

    @Transactional
    public ProductDTO createProduct(ProductRequestDTO productRequest) {

        Product product = new Product();

        product.setName(productRequest.name());
        product.setDescription(productRequest.desc());
        product.setRetailPrice(productRequest.retailPrice());

        productRepository.saveAndFlush(product);

        return ProductDTO.generateDTO(product);
    }

    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }

    public Product getReferenceById(Long id) {
        return productRepository.getReferenceById(id);
    }

    public Product getReferenceByName(String name) {
        return productRepository.findByName(name);
    }
}
