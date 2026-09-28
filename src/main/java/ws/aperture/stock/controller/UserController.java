package ws.aperture.stock.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ws.aperture.stock.dto.SysUserDTO;
import ws.aperture.stock.dto.UserIdDTO;
import ws.aperture.stock.dto.UserRequestDTO;
import ws.aperture.stock.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
  private final UserService service;

  public UserController(UserService service) {
    this.service = service;
  }

  @GetMapping
  public List<SysUserDTO> all() {
    return service.all();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public SysUserDTO create(@Valid @RequestBody UserRequestDTO request) {
    return service.registerUser(request);
  }

  @GetMapping("/{id}")
  public SysUserDTO get(@PathVariable Long id) {
    return service.getById(id);
  }

  @PutMapping("/{id}")
  public SysUserDTO update(@PathVariable Long id, @Valid @RequestBody UserRequestDTO request) {
    return service.update(id, request);
  }

  @DeleteMapping("/{id}")
  public SysUserDTO delete(@PathVariable Long id) {
    return service.deleteById(id);
  }

  @GetMapping("/ids")
  public List<UserIdDTO> ids() {
    return service.getIDs();
  }

  @GetMapping("/info/{name}")
  public SysUserDTO find(@PathVariable String name) {
    return service.getInfoByUsername(name);
  }
}
