package ws.aperture.stock.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ws.aperture.stock.dto.SupplierDTO;
import ws.aperture.stock.exceptions.InvalidRegisterSupplierException;
import ws.aperture.stock.exceptions.NoSupplierWithIdException;
import ws.aperture.stock.model.Supplier;
import ws.aperture.stock.service.SupplierService;






@RestController
@RequestMapping("/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    @Autowired
    SupplierController( SupplierService supplierService ) {
        this.supplierService = supplierService;
    }


    @GetMapping("/all")
    public List<SupplierDTO> all() {
        return supplierService.all();
    }

    @GetMapping("/{id}")
    public SupplierDTO getById(@PathVariable(value = "id")Long id) throws NoSupplierWithIdException {
        return supplierService.getById(id);
    }

    private boolean checkSupplierFields(Supplier inputSupplier) {
        String supplierName = inputSupplier.getName();
        String supplierCompanyNumber = inputSupplier.getCompanyNumber();

        return (supplierName != null
                && supplierCompanyNumber != null
                && supplierCompanyNumber.length() == 8
                && supplierName.length() > 6);
    }

    @PutMapping("/register")
    public SupplierDTO registerSupplier(@RequestBody Supplier inputSupplier) throws InvalidRegisterSupplierException {
        if (checkSupplierFields(inputSupplier)) {
            return supplierService.registerSupplier(inputSupplier);
        } else {
            throw new InvalidRegisterSupplierException();
        }
    }

    @DeleteMapping("/remove/{id}")
    public SupplierDTO deleteById(@PathVariable(value = "id")Long id) throws NoSupplierWithIdException {
            return supplierService.deleteById(id);
    }   

}
