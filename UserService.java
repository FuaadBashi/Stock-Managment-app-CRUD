package ws.aperture.stock.service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ws.aperture.stock.dto.SysUserDTO;
import ws.aperture.stock.dto.UserIdDTO;
import ws.aperture.stock.enums.Role;
import ws.aperture.stock.exceptions.DuplicateEmailException;
import ws.aperture.stock.exceptions.NoUserWithIdException;
import ws.aperture.stock.exceptions.NoUserwithUserNameException;
import ws.aperture.stock.model.SysUser;
import ws.aperture.stock.repository.UserRepository;




@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    UserService( UserRepository userRepository ) {
        this.userRepository = userRepository;
    }

    @Transactional
    public List<SysUserDTO> all() {
        return userRepository.findAll()
               .stream()
               .map( user -> SysUserDTO.generateDTO( user ))
               .collect( Collectors.toList()) ;
    }

    @Transactional
    private String generateNickName(String firstName, String lastName) {
        char firstChar = firstName.charAt(0);
        long duplicateNameCount = userRepository.countByFirstNameStartingWithAndLastName("" + firstChar, lastName);
        String numSuffix = (duplicateNameCount > 0) ? Long.toString(duplicateNameCount + 1) : "";

        return Character.toLowerCase(firstChar) + lastName.toLowerCase() + numSuffix;
    }

    private String cleanNameCaps(String name) {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
    }

    @Transactional
    public SysUserDTO registerUser( SysUser newUser ) throws DuplicateEmailException {
        String fmtdFirstName = cleanNameCaps(newUser.getFirstName());
        String fmtdLastName = cleanNameCaps(newUser.getLastName());
        String userName = generateNickName(fmtdFirstName, fmtdLastName);
        SysUser found = userRepository.findByEmail(newUser.getEmail()); // check this email is not already registered
        if (found != null) {
            throw new DuplicateEmailException(newUser.getEmail());
        } else {
            newUser.setUserName(userName);
            newUser.setFirstName(fmtdFirstName);
            newUser.setLastName(fmtdLastName);
            newUser.setRole( Role.EMPLOYEE );
            newUser.setRegistrationDate( LocalDate.now() );
            SysUser registeredUser = userRepository.saveAndFlush(newUser);
            SysUserDTO registeredUserDTO = SysUserDTO.generateDTO(registeredUser);
            return registeredUserDTO;
        }
    }

    @Transactional
    public List<UserIdDTO> getIDs() {
        return userRepository.findAll()
               .stream()
               .map( user -> UserIdDTO.generateDTO( user ))
               .collect( Collectors.toList()) ;
    }

    @Transactional
    public SysUserDTO deleteById(Long id) throws NoUserWithIdException {
        Optional<SysUser> found = userRepository.findById(id);
        if (found.isPresent()) {
            SysUserDTO foundDTO = SysUserDTO.generateDTO(found.get()) ;
            userRepository.deleteById(id);
            return foundDTO;
        } else {
            throw new NoUserWithIdException(id);
        }
    }

 
    @Transactional
    public SysUserDTO getInfoByUsername(String userName) throws NoUserwithUserNameException {
        SysUser found = userRepository.findByUserName(userName);
        if (found == null) {
            throw new NoUserwithUserNameException(userName);
        } else {
            SysUserDTO foundDTO = SysUserDTO.generateDTO(found);
            return foundDTO;
        }
    }

    @Transactional
    public SysUserDTO getById(Long id) throws NoUserWithIdException {

        Optional<SysUser> found = userRepository.findById(id);
        if (found.isPresent()) {
            SysUserDTO foundDTO = SysUserDTO.generateDTO(found.get());
            return foundDTO;
        } else {
            throw new NoUserWithIdException(id);
        }
    }

    @Transactional
    public boolean userExistWithId(Long id) {
       return userRepository.existsById( id );
    }

    @Transactional
    public SysUser getReferenceById(Long id){
       return userRepository.getReferenceById(id);
    } 

}
