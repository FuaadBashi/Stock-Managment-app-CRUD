package ws.aperture.stock.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ws.aperture.stock.dto.CustomerOrderDTO;
import ws.aperture.stock.dto.CustomerOrderItemDTO;
import ws.aperture.stock.dto.CustomerOrderRequestDTO;
import ws.aperture.stock.enums.CustomerOrderStatus;
import ws.aperture.stock.enums.ProductAndStockStatus;
import ws.aperture.stock.exceptions.ConflictException;
import ws.aperture.stock.exceptions.DuplicateProductInOrderException;
import ws.aperture.stock.exceptions.EmptyCustomerOrderItemsException;
import ws.aperture.stock.exceptions.NoCustomerOrderIdException;
import ws.aperture.stock.model.CustomerOrder;
import ws.aperture.stock.model.CustomerOrderItem;
import ws.aperture.stock.repository.CustomerOrderRepository;

@Service
@Transactional(readOnly = true)
public class CustomerOrderService {
  private final CustomerOrderRepository orders;
  private final ProductService products;
  private final UserService users;

  public CustomerOrderService(
      CustomerOrderRepository orders, ProductService products, UserService users) {
    this.orders = orders;
    this.products = products;
    this.users = users;
  }

  public CustomerOrderDTO getById(Long id) {
    return CustomerOrderDTO.generateDTO(
        orders.findById(id).orElseThrow(() -> new NoCustomerOrderIdException(id)));
  }

  public List<CustomerOrderDTO> all() {
    return orders.findAll(Sort.by("id")).stream().map(CustomerOrderDTO::generateDTO).toList();
  }

  @Transactional
  public CustomerOrderDTO createCustomerOrder(CustomerOrderRequestDTO request) {
    var order = new CustomerOrder();
    order.setCreator(users.require(request.userId()));
    order.setOrderTimeStamp(LocalDateTime.now());
    var items = buildItems(order, request.itemRequests());
    order.getCustomerOrderItems().addAll(items);
    return CustomerOrderDTO.generateDTO(orders.saveAndFlush(order));
  }

  private List<CustomerOrderItem> buildItems(
      CustomerOrder order, List<CustomerOrderItemDTO> requests) {
    if (requests == null || requests.isEmpty()) throw new EmptyCustomerOrderItemsException();
    Set<Long> seen = new HashSet<>();
    List<CustomerOrderItem> items = new ArrayList<>();
    for (var request :
        requests.stream().sorted(Comparator.comparing(CustomerOrderItemDTO::productId)).toList()) {
      if (!seen.add(request.productId()))
        throw new DuplicateProductInOrderException(request.productId());
      var product = products.requireLocked(request.productId());
      if (product.getStatus() == ProductAndStockStatus.DISCONTINUED)
        throw new ConflictException("Discontinued products cannot be ordered");
      var item = new CustomerOrderItem();
      item.setProduct(product);
      item.setCustomerOrder(order);
      item.setQuantity(request.quantity());
      item.setUnitPrice(product.getRetailPrice());
      items.add(item);
    }
    return items;
  }

  @Transactional
  public CustomerOrderDTO update(Long id, CustomerOrderRequestDTO request) {
    var order = locked(id);
    if (order.getStatus() != CustomerOrderStatus.NEW)
      throw new ConflictException("Only new orders can be edited");
    if (!order.getCreator().getId().equals(request.userId()))
      throw new ConflictException("Order creator cannot be changed");
    var items = buildItems(order, request.itemRequests());
    order.getCustomerOrderItems().clear();
    orders.flush();
    order.getCustomerOrderItems().addAll(items);
    return CustomerOrderDTO.generateDTO(orders.saveAndFlush(order));
  }

  private CustomerOrder locked(Long id) {
    return orders.findLockedById(id).orElseThrow(() -> new NoCustomerOrderIdException(id));
  }

  @Transactional
  public CustomerOrderDTO transition(Long id, CustomerOrderStatus target) {
    var order = locked(id);
    var from = order.getStatus();
    if (from == target) return CustomerOrderDTO.generateDTO(order);
    boolean allowed =
        switch (target) {
          case STARTED -> from == CustomerOrderStatus.NEW;
          case COMPLETED -> from == CustomerOrderStatus.STARTED;
          case CANCELLED -> from == CustomerOrderStatus.NEW || from == CustomerOrderStatus.STARTED;
          case DELETED -> from == CustomerOrderStatus.NEW || from == CustomerOrderStatus.CANCELLED;
          case NEW -> false;
        };
    if (!allowed) throw new ConflictException("Cannot change order from " + from + " to " + target);
    order.setStatus(target);
    return CustomerOrderDTO.generateDTO(orders.saveAndFlush(order));
  }
}
