package propets.accounting.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AccountDto {
	String email;
	String name;
	String avatar;
	String phone;
	Set<String> roles;
	
}
