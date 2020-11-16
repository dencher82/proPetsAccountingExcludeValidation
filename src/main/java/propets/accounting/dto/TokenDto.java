package propets.accounting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TokenDto {
	String email;
	String name;
	String avatar;
	String phone;
	String roles;
	boolean flBlocked;
	long timeUnblock;
	
}
