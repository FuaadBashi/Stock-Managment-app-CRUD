package ws.aperture.stock.service;

import java.util.List;
import java.util.Locale;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ws.aperture.stock.dto.SupplierDTO;
import ws.aperture.stock.dto.SupplierRequestDTO;
import ws.aperture.stock.exceptions.ConflictException;
import ws.aperture.stock.exceptions.NoSupplierWithIdException;
import ws.aperture.stock.model.Supplier;
import ws.aperture.stock.repository.StockItemRepository;
import ws.aperture.stock.repository.SupplierRepository;

@Service
@Transactional(readOnly = true)
public class SupplierService {
  private final SupplierRepository suppliers;
  private final StockItemRepository stock;

  public SupplierService(SupplierRepository suppliers, StockItemRepository stock) {
    this.suppliers = suppliers;
    this.stock = stock;
  }

  public List<SupplierDTO> all() {
    return suppliers.findAll(Sort.by("id")).stream().map(SupplierDTO::generateDTO).toList();
  }

  public Supplier require(Long id) {
    return suppliers.findById(id).orElseThrow(() -> new NoSupplierWithIdException(id));
  }

  public SupplierDTO getById(Long id) {
    return SupplierDTO.generateDTO(require(id));
  }

  @Transactional
  public SupplierDTO registerSupplier(SupplierRequestDTO request) {
    var supplier = new Supplier();
    return save(supplier, request);
  }

  @Transactional
  public SupplierDTO update(Long id, SupplierRequestDTO request) {
    return save(require(id), request);
  }

  private SupplierDTO save(Supplier supplier, SupplierRequestDTO request) {
    String name = request.name().strip();
    String company = request.companyNumber().toUpperCase(Locale.ROOT);
    if ((supplier.getName() == null || !supplier.getName().equalsIgnoreCase(name))
        && suppliers.existsByNameIgnoreCase(name))
      throw new ConflictException("Supplier name is already registered");
    if (!company.equals(supplier.getCompanyNumber()) && suppliers.existsByCompanyNumber(company))
      throw new ConflictException("Company number is already registered");
    supplier.setName(name);
    supplier.setNormalizedName(name.toLowerCase(Locale.ROOT));
    supplier.setCompanyNumber(company);
    return SupplierDTO.generateDTO(suppliers.saveAndFlush(supplier));
  }

  @Transactional
  public SupplierDTO deleteById(Long id) {
    var supplier = require(id);
    if (stock.existsBySupplierId(id))
      throw new ConflictException("Supplier is referenced by stock items");
    var dto = SupplierDTO.generateDTO(supplier);
    suppliers.delete(supplier);
    suppliers.flush();
    return dto;
  }
}
