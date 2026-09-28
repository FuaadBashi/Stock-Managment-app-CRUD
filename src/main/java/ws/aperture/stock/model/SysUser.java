package ws.aperture.stock.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import ws.aperture.stock.enums.Role;

@Entity
@Table(name = "sysuser")
@Getter
@Setter
public class SysUser {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;

  @Column(nullable = false, length = 100)
  private String firstName;

  @Column(nullable = false, length = 100)
  private String lastName;

  @Column(nullable = false, unique = true, length = 220)
  private String userName;

  @Column(nullable = false, unique = true, length = 254)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role = Role.EMPLOYEE;

  @Column(nullable = false)
  private LocalDate registrationDate;
}
