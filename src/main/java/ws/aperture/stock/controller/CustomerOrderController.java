package ws.aperture.stock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ws.aperture.stock.dto.CustomerOrderDTO;
import ws.aperture.stock.dto.CustomerOrderRequestDTO;
import ws.aperture.stock.exceptions.NoProductWithIdException;
import ws.aperture.stock.exceptions.NoUserWithIdException;
import ws.aperture.stock.service.CustomerOrderService;

@RestController
@RequestMapping("/customer-orders")
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;

    @Autowired
    CustomerOrderController(CustomerOrderService customerOrderService) {
        this.customerOrderService = customerOrderService;
    }

    @PostMapping("/new")
    public CustomerOrderDTO createCustomerOrder(
            @RequestBody CustomerOrderRequestDTO customerOrderRequest)
            throws NoUserWithIdException, NoProductWithIdException {
        return customerOrderService.createCustomerOrder(customerOrderRequest);
    }

    @PostMapping("/complete/{id}")
    public CustomerOrderDTO completeCustomerOrder(@PathVariable("id") Long id) {
        return customerOrderService.completeCustomerOrder(id);
    }

    @PostMapping("/cancel/{id}")
    public CustomerOrderDTO cancleCustomerOrder(@PathVariable("id") Long id) {
        return customerOrderService.cancleCustomerOrder(id);
    }

    @PostMapping("/start/{id}")
    public CustomerOrderDTO startCustomerOrder(@PathVariable("id") Long id) {
        return customerOrderService.startCustomerOrder(id);
    }

    @PostMapping("/delete/{id}")
    public CustomerOrderDTO deleteCustomerOrder(@PathVariable("id") Long id) {
        return customerOrderService.deleteCustomerOrder(id);
    }

    @GetMapping("/{id}")
    public CustomerOrderDTO getById(@PathVariable("id") Long id) {
        return customerOrderService.getById(id);
    }
}
