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
import ws.aperture.stock.dto.ProductDTO;
import ws.aperture.stock.dto.ProductRequestDTO;
import ws.aperture.stock.dto.RecipeDTO;
import ws.aperture.stock.dto.RecipeItemDTO;
import ws.aperture.stock.exceptions.NoIngredientWithIdException;
import ws.aperture.stock.exceptions.NoProductWithIdException;
import ws.aperture.stock.exceptions.NoSupplierWithIdException;
import ws.aperture.stock.exceptions.NonPositiveIngredientQuantityException;
import ws.aperture.stock.exceptions.PriceMustBePositiveException;
import ws.aperture.stock.exceptions.UnfilledProductFieldsException;
import ws.aperture.stock.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    ProductController(ProductService productService) {
        this.productService = productService;
    }

    private void checkProductRequestFields(ProductRequestDTO productRequest)
            throws UnfilledProductFieldsException, PriceMustBePositiveException {

        String name = productRequest.name();
        double retailPrice = productRequest.retailPrice();

        if (name == null || name.length() < 1) {
            throw new UnfilledProductFieldsException();
        }

        if (retailPrice <= 0.0) {
            throw new PriceMustBePositiveException();
        }
    }

    @PutMapping("")
    public ProductDTO createProduct(@RequestBody ProductRequestDTO newProduct)
            throws UnfilledProductFieldsException,
                    PriceMustBePositiveException,
                    NoSupplierWithIdException {
        checkProductRequestFields(newProduct);
        return productService.createProduct(newProduct);
    }

    @GetMapping("")
    public List<ProductDTO> all() {
        return productService.all();
    }

    @PostMapping("update-recipe/{productId}")
    public RecipeDTO updateRecipe(
            @PathVariable Long productId, @RequestBody List<RecipeItemDTO> recipeItems)
            throws NoProductWithIdException,
                    NoIngredientWithIdException,
                    NonPositiveIngredientQuantityException {
        return productService.updateRecipe(productId, recipeItems);
    }
}
