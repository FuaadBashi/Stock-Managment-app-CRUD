package ws.aperture.stock.service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ws.aperture.stock.dto.SupplierDTO;
import ws.aperture.stock.exceptions.DuplicateSupplierDetailsException;
import ws.aperture.stock.exceptions.NoSupplierWithIdException;
import ws.aperture.stock.model.Supplier;
import ws.aperture.stock.repository.SupplierRepository;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    @Autowired
    SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Transactional
    public List<SupplierDTO> all() {
        return supplierRepository.findAll().stream()
                .map(supplier -> SupplierDTO.generateDTO(supplier))
                .collect(Collectors.toList());
    }

    @Transactional
    public SupplierDTO getById(Long id) throws NoSupplierWithIdException {
        Optional<Supplier> found = supplierRepository.findById(id);
        if (found.isPresent()) {
            SupplierDTO foundDTO = SupplierDTO.generateDTO(found.get());
            return foundDTO;
        } else {
            throw new NoSupplierWithIdException(id);
        }
    }

    @Transactional
    public boolean existsById(Long id) {
        return supplierRepository.existsById(id);
    }

    @Transactional
    public Supplier getReferenceById(Long id) {
        return supplierRepository.getReferenceById(id);
    }

    @Transactional
    public SupplierDTO deleteById(Long id) throws NoSupplierWithIdException {
        Optional<Supplier> found = supplierRepository.findById(id);
        if (found.isPresent()) {
            SupplierDTO foundDTO = SupplierDTO.generateDTO(found.get());
            supplierRepository.deleteById(id);
            return foundDTO;
        } else {
            throw new NoSupplierWithIdException(id);
        }
    }

    @Transactional
    public SupplierDTO registerSupplier(Supplier supplier)
            throws DuplicateSupplierDetailsException {
        String supplierName = supplier.getName();
        String companyNumber = supplier.getCompanyNumber();

        Supplier foundByName = supplierRepository.findByName(supplierName);
        Supplier foundByCompanyNum = supplierRepository.findByCompanyNumber(companyNumber);

        if (foundByName == null && foundByCompanyNum != null) {
            throw new DuplicateSupplierDetailsException(
                    "Company number already registered as", foundByCompanyNum);

        } else if (foundByCompanyNum == null && foundByName != null) {
            throw new DuplicateSupplierDetailsException(
                    "Company name already registered as", foundByName);
        }
        if (foundByCompanyNum != null
                && foundByName != null
                && foundByCompanyNum.equals(foundByName)) {
            throw new DuplicateSupplierDetailsException(
                    "Duplicate company details already registered as", foundByName);
        }

        supplier = supplierRepository.saveAndFlush(supplier);
        SupplierDTO supplierDTO = SupplierDTO.generateDTO(supplier);
        return supplierDTO;
    }
}
