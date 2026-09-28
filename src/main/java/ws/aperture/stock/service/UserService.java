package ws.aperture.stock.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ws.aperture.stock.dto.SysUserDTO;
import ws.aperture.stock.dto.UserIdDTO;
import ws.aperture.stock.dto.UserRequestDTO;
import ws.aperture.stock.exceptions.ConflictException;
import ws.aperture.stock.exceptions.DuplicateEmailException;
import ws.aperture.stock.exceptions.NoUserWithIdException;
import ws.aperture.stock.exceptions.NoUserwithUserNameException;
import ws.aperture.stock.model.SysUser;
import ws.aperture.stock.repository.CustomerOrderRepository;
import ws.aperture.stock.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserService {
  private final UserRepository users;
  private final CustomerOrderRepository orders;

  public UserService(UserRepository users, CustomerOrderRepository orders) {
    this.users = users;
    this.orders = orders;
  }

  public List<SysUserDTO> all() {
    return users.findAll(Sort.by("id")).stream().map(SysUserDTO::generateDTO).toList();
  }

  public List<UserIdDTO> getIDs() {
    return users.findAll(Sort.by("id")).stream().map(UserIdDTO::generateDTO).toList();
  }

  public SysUserDTO getById(Long id) {
    return SysUserDTO.generateDTO(require(id));
  }

  public SysUser require(Long id) {
    return users.findById(id).orElseThrow(() -> new NoUserWithIdException(id));
  }

  public SysUserDTO getInfoByUsername(String name) {
    var user = users.findByUserName(name);
    if (user == null) throw new NoUserwithUserNameException(name);
    return SysUserDTO.generateDTO(user);
  }

  @Transactional
  public SysUserDTO registerUser(UserRequestDTO request) {
    String email = request.email().strip().toLowerCase(Locale.ROOT);
    if (users.findByEmail(email) != null) throw new DuplicateEmailException(email);
    var user = new SysUser();
    user.setFirstName(request.firstName().strip());
    user.setLastName(request.lastName().strip());
    user.setEmail(email);
    user.setUserName("user-" + UUID.randomUUID());
    user.setRegistrationDate(LocalDate.now());
    users.saveAndFlush(user);
    user.setUserName("staff-" + user.getId());
    return SysUserDTO.generateDTO(users.saveAndFlush(user));
  }

  @Transactional
  public SysUserDTO update(Long id, UserRequestDTO request) {
    var user = require(id);
    String email = request.email().strip().toLowerCase(Locale.ROOT);
    var duplicate = users.findByEmail(email);
    if (duplicate != null && !duplicate.getId().equals(id))
      throw new DuplicateEmailException(email);
    user.setFirstName(request.firstName().strip());
    user.setLastName(request.lastName().strip());
    user.setEmail(email);
    return SysUserDTO.generateDTO(users.saveAndFlush(user));
  }

  @Transactional
  public SysUserDTO deleteById(Long id) {
    var user = require(id);
    if (orders.existsByCreatorId(id))
      throw new ConflictException("Staff with order history cannot be deleted");
    var dto = SysUserDTO.generateDTO(user);
    users.delete(user);
    users.flush();
    return dto;
  }
}
