package ws.aperture.stock.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDate;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import ws.aperture.stock.enums.Role;

@Entity
@Getter
@Setter
@Table(name = "sysuser")
public class SysUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String userName;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = false, nullable = true)
    private String occupation;

    @Column(unique = false, nullable = true)
    private String salutation;

    @Column(unique = false, nullable = true, columnDefinition = "DATE")
    private LocalDate startDate;

    @Column(unique = false, nullable = true, columnDefinition = "DATE")
    private LocalDate registrationDate;

    @Column
    @Transient
    public String fullName() {
        return firstName + " " + lastName;
    }

    @OneToMany(mappedBy = "creator", cascade = CascadeType.REMOVE, orphanRemoval = true)
    public Set<CustomerOrder> customerOrders;
}
