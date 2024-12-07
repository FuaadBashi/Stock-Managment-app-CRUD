package ws.aperture.stock.service;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ws.aperture.stock.dto.CustomerOrderDTO;
import ws.aperture.stock.dto.CustomerOrderItemDTO;
import ws.aperture.stock.dto.CustomerOrderRequestDTO;
import ws.aperture.stock.enums.CustomerOrderStatus;
import ws.aperture.stock.exceptions.DuplicateProductInOrderException;
import ws.aperture.stock.exceptions.EmptyCustomerOrderItemsException;
import ws.aperture.stock.exceptions.NoCustomerOrderIdException;
import ws.aperture.stock.exceptions.NoProductWithIdException;
import ws.aperture.stock.exceptions.NoUserWithIdException;
import ws.aperture.stock.exceptions.NonPositiveProductQuantityException;
import ws.aperture.stock.model.CustomerOrder;
import ws.aperture.stock.model.CustomerOrderItem;
import ws.aperture.stock.model.Product;
import ws.aperture.stock.repository.CustomerOrderItemRepository;
import ws.aperture.stock.repository.CustomerOrderRepository;




@Service
public class CustomerOrderService {
    
    private final CustomerOrderRepository     customerOrderRepository;
    private final CustomerOrderItemRepository customerOrderItemRepository;


    /*  REFACTOR TODO: refactor any repository accceses into service methods which we can call from this service
     * 
     *      
     *      Motivation:
     *      -   services should manage the repositories that pertain to them only
     *      -   if services need information from repositories which are managed by other services,
     *          they can communicate with one another to share information and data rather than
     *          directly sharing respository access 
     *      -   The StockOrderService service should be responsible for
     *          both the StockOrderRepository and StockOrderItemRepository only
     * 
     *      Implementation:
     *      e.g. instead of checking if userRepo existsByID in createStockOrder:
     *           -  create a (package-private ideally) boolean helper method in our userService
     *           -  this helper should throw the NoUserWIthIdException
     *      
     *      should exceptions then be grouped into service-related files...?
     *  
     *       - Constructor pattern will be the same for initialising services
     *       - write service based helper methods as required
     *          - mark @Transactional for any database accesses
     *       - call service helper methods in this class, instead of foreign jpa repositories
     *         ( in this case, userRepository and productRepository )
     * 
     *      Public Services we need: (should be called by the StockOrder controller with GET, PUT, etc.)
     * 
     *      Don't DELETE Stock Orders. If the user who created it wants to delete it, they can set its 
     *      StockOrderStatus (defined in enum folder) to StockOrderSTatus.DELETED
     *      to `logically` delete a record, but keep the record in the database for pretend auditing ;)
     *      We have create stock order, we also want:
     *       - delete (logically only, http PUT mapping not DELETE mapping in StockOrderController )
     *          - set record status to DELETED only, return whatever data feels sensible
     * 
     *       - modify a StockOrder:
     *            - give your userId, a StockOrderId, and a list of StockOrderItems
     *            - check:
     *                  - this user exists AND stock order exists
     *                    AND you created this stock order AND StockOrderStatus is NEW
     *            - if all good, modify the stock order with given id, store, and return its DTO
     */

    private final ProductService           productService;
    private final UserService              userService;
    // private final ProductRepository        productRepository;
    // private final UserRepository           userRepository;
    
    @Autowired
    CustomerOrderService( CustomerOrderRepository customerOrderRepository, CustomerOrderItemRepository customerOrderItemRepository,
                       ProductService productService, UserService userService ) {
        
        this.customerOrderRepository     = customerOrderRepository;
        this.customerOrderItemRepository = customerOrderItemRepository;
        this.productService              = productService;
        this.userService                 = userService;
                        
        
    }

    @Transactional
    public CustomerOrderDTO createCustomerOrder( CustomerOrderRequestDTO customerOrderRequest)
        throws  NoUserWithIdException, EmptyCustomerOrderItemsException, DuplicateProductInOrderException, 
                NonPositiveProductQuantityException, NoProductWithIdException 
    {
        List<CustomerOrderItemDTO> itemRequests = customerOrderRequest.itemRequests();

        // check user exists in DB
        Long creatorId = customerOrderRequest.userId();
        if ( !userService.userExistWithId(creatorId) ) {
            throw new NoUserWithIdException( creatorId );
        }

        // Check order requests not empty
        if ( itemRequests.size() == 0 ) {
            throw new EmptyCustomerOrderItemsException();
        }

        // Check no duplicate products in order
        Set<Long> productIds = new HashSet<Long>();
        for ( CustomerOrderItemDTO soir : itemRequests ) {
            Long currProdId = soir.productId();
            int quantity    = soir.quantity();

            if ( productIds.contains(currProdId) ) {
                throw new DuplicateProductInOrderException( currProdId );
            } else {
                productIds.add( currProdId );
            }

            // check each product in the order exists in the DB
            if ( ! productService.existsById( currProdId ) ) {
                throw new NoProductWithIdException( currProdId );
            }

            if ( quantity <= 0 ) {
                throw new NonPositiveProductQuantityException( currProdId, quantity );
            }
        }
 
        
        // PASSED ALL CHECKS

        // Create and Save new stock order to DB
        CustomerOrder customerOrder = new CustomerOrder();
        customerOrder.setCreator( userService.getReferenceById(creatorId) );
        customerOrder.setOrderTimeStamp( LocalDateTime.now() );
        customerOrder = customerOrderRepository.saveAndFlush( customerOrder );
        
        Set<CustomerOrderItem> customerOrderItems = new HashSet<CustomerOrderItem>();
        for ( CustomerOrderItemDTO itemRequest : itemRequests ) {
            Product product = productService.getReferenceById( itemRequest.productId() );
            int quantity = itemRequest.quantity();

            CustomerOrderItem item = new CustomerOrderItem();
            item.setCustomerOrder( customerOrder );
            item.setProduct( product );
            item.setQuantity( quantity );

            customerOrderItems.add(item);
            

            customerOrderItemRepository.saveAndFlush( item );
        }

        // customerOrder = customerOrderRepository.findById (customerOrder.getId() ).get();
        customerOrder.setCustomerOrderItems( customerOrderItems );

        return CustomerOrderDTO.generateDTO( customerOrder );   
    }

    public CustomerOrderDTO getById(Long id){
        
        Optional<CustomerOrder> found = customerOrderRepository.findById(id);
        if (found.isPresent()) {
            CustomerOrder order = found.get();
            CustomerOrderDTO foundDTO = CustomerOrderDTO.generateDTO(order);
            return foundDTO;
        } else {
            throw new NoCustomerOrderIdException(id);
        }
    }

    // public CustomerOrderDTO customerOrderStatusChange(CustomerOrderStatus status, Long id){
    //     Optional<CustomerOrder> found = customerOrderRepository.findById(id);
    //     if (found.isPresent()) {
    //         CustomerOrder order = found.get();
    //         order.setStatus(status);
    //         CustomerOrderDTO foundDTO = CustomerOrderDTO.generateDTO(order);
    //         return foundDTO;
    //     } else {
    //         throw new NoCustomerOrderIdException(id);
    //     }
    // }

    // @Transactional
    // public CustomerOrderDTO completeCustomerOrder(Long id) throws NoCustomerOrderIdException {

    //     return customerOrderStatusChange(CustomerOrderStatus.COMPLETED, id);
    // }

    // @Transactional
    // public CustomerOrderDTO cancleCustomerOrder(Long id) throws NoCustomerOrderIdException {

    //     return customerOrderStatusChange(CustomerOrderStatus.CANCELLED, id);
    // }

    // @Transactional
    // public CustomerOrderDTO startCustomerOrder(Long id) throws NoCustomerOrderIdException {

    //     return customerOrderStatusChange(CustomerOrderStatus.STARTED, id);
    // }

    // @Transactional
    // public CustomerOrderDTO deleteCustomerOrder(Long id) throws NoCustomerOrderIdException {

    //     return customerOrderStatusChange(CustomerOrderStatus.DELETED, id);
    // }

    @Transactional
    public CustomerOrderDTO completeCustomerOrder(Long id) throws NoCustomerOrderIdException {

        Optional<CustomerOrder> found = customerOrderRepository.findById(id);
        if (found.isPresent()) {
            CustomerOrder order = found.get();
            order.setStatus(CustomerOrderStatus.COMPLETED);
            CustomerOrderDTO foundDTO = CustomerOrderDTO.generateDTO(order);
            return foundDTO;
        } else {
            throw new NoCustomerOrderIdException(id);
        }
    }

    @Transactional
    public CustomerOrderDTO cancleCustomerOrder(Long id) throws NoCustomerOrderIdException {

        Optional<CustomerOrder> found = customerOrderRepository.findById(id);
        if (found.isPresent()) {
            CustomerOrder order = found.get();
            
            order.setStatus(CustomerOrderStatus.CANCELLED);
            CustomerOrderDTO foundDTO = CustomerOrderDTO.generateDTO(order);
            return foundDTO;
        } else {
            throw new NoCustomerOrderIdException(id);
        }
    }

    @Transactional
    public CustomerOrderDTO startCustomerOrder(Long id) throws NoCustomerOrderIdException {

        Optional<CustomerOrder> found = customerOrderRepository.findById(id);
        if (found.isPresent()) {
            CustomerOrder order = found.get();
            order.setStatus(CustomerOrderStatus.STARTED);
            CustomerOrderDTO foundDTO = CustomerOrderDTO.generateDTO(order);
            return foundDTO;
        } else {
            throw new NoCustomerOrderIdException(id);
        }
    }

    @Transactional
    public CustomerOrderDTO deleteCustomerOrder(Long id) throws NoCustomerOrderIdException {

        Optional<CustomerOrder> found = customerOrderRepository.findById(id);
        if (found.isPresent()) {
            CustomerOrder order = found.get();
            order.setStatus(CustomerOrderStatus.DELETED);
            CustomerOrderDTO foundDTO = CustomerOrderDTO.generateDTO(order);
            return foundDTO;
        } else {
            throw new NoCustomerOrderIdException(id);
        }
    }





    // @Transactional
    // public List<StockOrderDTO> all() {
    //     return customerOrderRepository.findAll()
    //            .stream()
    //            .map( customerOrder -> StockOrderDTO.generateDTO(customerOrder) )
    //            .collect( Collectors.toList()) ;
    // }

    // @Transactional
    // public StockOrderDTO getById(Long id) throws NoUserwithIdException {

    //     Optional<StockOrder> found = customerOrderRepository.findById(id);
    //     if (found.isPresent()) {
    //         StockOrderDTO foundDTO = StockOrderDTO.generateDTO(found.get());
    //         return foundDTO;
    //     } else {
    //         throw new NoUserwithIdException(id);
    //     }
    // }

    // @Transactional
    // public StockOrderDTO deleteById(Long id) throws NoUserwithIdException {
    //     Optional<StockOrder> found = customerOrderRepository.findById(id);
    //     if (found.isPresent()) {
    //         StockOrderDTO foundDTO = StockOrderDTO.generateDTO(found.get()) ;
    //         customerOrderRepository.deleteById(id);
    //         return foundDTO;
    //     } else {
    //         throw new NoUserwithIdException(id);
    //     }
    // }

   
}


