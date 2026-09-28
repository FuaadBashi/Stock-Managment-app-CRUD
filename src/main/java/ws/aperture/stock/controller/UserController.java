package ws.aperture.stock.controller;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ws.aperture.stock.dto.SysUserDTO;
import ws.aperture.stock.dto.UserIdDTO;
import ws.aperture.stock.exceptions.DuplicateEmailException;
import ws.aperture.stock.exceptions.NoUserWithIdException;
import ws.aperture.stock.exceptions.UnfilledRegistrationFieldsException;
import ws.aperture.stock.model.SysUser;
import ws.aperture.stock.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    private static final String welcomeTemplate = "Welcome %s. You are visitor no: %d\n";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/welcome")
    public String greeting(@RequestParam(value = "name", defaultValue = "") String name) {
        return String.format(welcomeTemplate, name, counter.incrementAndGet());
    }

    @GetMapping("")
    public List<SysUserDTO> all() {
        return userService.all();
    }

    @PostMapping("")
    public SysUserDTO registerUser(@RequestBody SysUser newUser)
            throws UnfilledRegistrationFieldsException, DuplicateEmailException {

        if (checkUserFields(newUser)) {
            return userService.registerUser(newUser);

        } else {
            throw new UnfilledRegistrationFieldsException();
        }
    }

    @GetMapping("/{userId}")
    public SysUserDTO getById(@PathVariable(value = "userId", required = true) Long id)
            throws NoUserWithIdException {
        return userService.getById(id);
    }

    @DeleteMapping("/{userId}")
    public SysUserDTO deleteById(@PathVariable(value = "userId", required = true) Long id)
            throws NoUserWithIdException {
        return userService.deleteById(id);
    }

    @GetMapping("/ids")
    public List<UserIdDTO> getIDs() {
        return userService.getIDs();
    }

    @GetMapping("/info/{userName}")
    public SysUserDTO getInfoByUsername(
            @PathVariable(value = "userName", required = true) String userName) {
        return userService.getInfoByUsername(userName);
    }

    private boolean checkUserFields(SysUser inputUser) {

        String firstName = inputUser.getFirstName();
        String lastName = inputUser.getLastName();
        String email = inputUser.getEmail();

        return (firstName != null
                && lastName != null
                && email != null
                && firstName.length() > 0
                && lastName.length() > 0
                && email.length() > 6);
    }
}
