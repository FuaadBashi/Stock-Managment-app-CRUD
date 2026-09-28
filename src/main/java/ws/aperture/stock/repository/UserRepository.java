package ws.aperture.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ws.aperture.stock.model.SysUser;

public interface UserRepository extends JpaRepository<SysUser, Long> {

    SysUser findByEmail(String email);

    SysUser findByUserName(String userName);

    long countByFirstNameStartingWithAndLastName(String firstNameChar0, String lastName);
}
