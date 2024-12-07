package ws.aperture.stock.dto;

import java.util.ArrayList;
import java.util.List;

import ws.aperture.stock.model.RecipeMapping;

public record RecipeDTO(Long productId, List<RecipeItemDTO> recipeItems ) {}
