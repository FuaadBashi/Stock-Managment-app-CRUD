package ws.aperture.stock.dto;

import java.util.List;

public record RecipeDTO(Long productId, List<RecipeItemDTO> recipeItems) {}
