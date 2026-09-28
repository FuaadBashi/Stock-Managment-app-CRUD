package ws.aperture.stock.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ws.aperture.stock.repository.RecipeMappingRepository;

@Service
public class RecipeMappingService {

    private final RecipeMappingRepository RecipeMappingRepository;

    @Autowired
    RecipeMappingService(RecipeMappingRepository RecipeMappingRepository) {
        this.RecipeMappingRepository = RecipeMappingRepository;
    }

    public boolean existsById(Long id) {
        return RecipeMappingRepository.existsById(id);
    }
}
