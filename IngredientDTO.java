package ws.aperture.stock.dto;

import ws.aperture.stock.enums.Unit;
import ws.aperture.stock.model.Ingredient;


public record IngredientDTO(Long id, String name, Unit unit ) {
    
    public static IngredientDTO generateDTO(Ingredient ingredient){
        return new IngredientDTO( ingredient.getId(), ingredient.getName(), ingredient.getUnit());
    }
}
