package ws.aperture.stock.dto;

import ws.aperture.stock.model.SysUser;

public record UserIdDTO( Long id, String firstName, String lastName, String userName ) {
    public static UserIdDTO generateDTO( SysUser user ) {
        return new UserIdDTO( user.getId(), user.getFirstName(), user.getLastName(), user.getUserName() );
    } 
    
}
