package propets.accounting.model;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@EqualsAndHashCode(of = {"email"})
@Document(collection = "accounts")
public class Account implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 799573405309893159L;

	@Value("${block.period}")
	int blockPeriod;
	
	@Id
	String email;
	@Setter
	String password;
	@Setter
	String name;
	@Setter
	String avatar;
	@Setter
	String phone;
	Set<String> roles = new HashSet<>();
	Set<String> favorites = new HashSet<>();
	Set<String> activities = new HashSet<>();
	boolean flBlocked;
	long timeUnblock;
	
	public Account(String email, String name) {
		this.email = email;
		this.name = name;
	}
	
	public boolean addRole(String role) {
		return roles.add(role.toUpperCase());
	}
	
	public boolean removeRole(String role) {
		return roles.remove(role.toUpperCase());
	}

	public void addFavorite(String postId) {
		favorites.add(postId);
	}
	
	public void removeFavorite(String postId) {
		favorites.remove(postId);
	}
	
	public void addActivity(String postId) {
		activities.add(postId);
	}
	
	public void removeActivity(String postId) {
		activities.remove(postId);
	}
	
	public boolean blockAccount(String blockStatus) {
		if ("true".equalsIgnoreCase(blockStatus)) {
			flBlocked = true;
			timeUnblock = Instant.now().plus(blockPeriod, ChronoUnit.DAYS).toEpochMilli();
			return true;
		} else if ("false".equalsIgnoreCase(blockStatus)) {
			flBlocked = false;
			return true;
		}
		return false;
	}
	
}
