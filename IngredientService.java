package ws.aperture.stock.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ws.aperture.stock.dto.IngredientDTO;
import ws.aperture.stock.model.Ingredient;
import ws.aperture.stock.repository.IngredientRepository;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    @Autowired
    IngredientService( IngredientRepository ingredientRepository ) {
        this.ingredientRepository = ingredientRepository;
    }

    @Transactional
    public List<IngredientDTO> all() {
      return ingredientRepository.findAll()
        .stream()
        .map(ingredient -> IngredientDTO.generateDTO(ingredient))
        .collect(Collectors.toList());
    }


    public boolean existsById(Long id) {
        return ingredientRepository.existsById( id );
    }
    
   public Ingredient getReferenceById(Long id){
        return ingredientRepository.getReferenceById(id);
   }


  
}
