// package ws.aperture.stock.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import ws.aperture.stock.dto.IngredientDTO;
// import ws.aperture.stock.model.Product;
// import ws.aperture.stock.repository.IngredientRepository;
// import ws.aperture.stock.repository.ProductRepository;
// import ws.aperture.stock.service.IngredientService;
// import ws.aperture.stock.service.ProductService;
// import ws.aperture.stock.service.RecipeMappingService;

// @RestController
// @RequestMapping("/recipe_mapping")
// public class RecipeMappingController {

//     private final RecipeMappingService recipeMappingService;
//     private IngredientService ingredientService;
//     private ProductService    productService;

//     @Autowired
// 	RecipeMappingController( RecipeMappingService recipeMappingService, IngredientService ingredientRepository, 
//     ProductService productRepository ) {
// 		this.recipeMappingService = recipeMappingService;
//         this.ingredientService = ingredientService;
//         this.productService = productService;

// 	}

//     private void settingCappacino(ProductService productService, IngredientService ingredientService) {
//         Product cappacino = productService.getReferenceByName("Cappuccino");
//         cappacino.setRecipeMappings(null);
//     }




    

    

// }
