package ws.aperture.stock.exceptions;

public class NonPositiveIngredientQuantityException extends RuntimeException {
    public NonPositiveIngredientQuantityException( Long ingredientId, double quantity ) {
        super( "Ingredient with ID: " + ingredientId + " has non-positive quantity: " + quantity );
    }
}