package ws.aperture.stock.dto;

import java.time.LocalDate;
import ws.aperture.stock.enums.Role;
import ws.aperture.stock.model.SysUser;

public record SysUserDTO(
        Long id, String firstName, String lastName, String userName, Role role, LocalDate regDate) {
    public static SysUserDTO generateDTO(SysUser sysUser) {
        return new SysUserDTO(
                sysUser.getId(),
                sysUser.getFirstName(),
                sysUser.getLastName(),
                sysUser.getUserName(),
                sysUser.getRole(),
                sysUser.getRegistrationDate());
    }
}
