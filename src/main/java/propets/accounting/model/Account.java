package propets.accounting.model;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
@EqualsAndHashCode(of = { "email" })
@Document(collection = "accounts")
public class Account implements Serializable {

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
	Map<String, Set<String>> favourites = new HashMap<>();
	Map<String, Set<String>> activities = new HashMap<>();
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

	public void addFavorite(String postId, String serviceName) {
		if (!favourites.containsKey(serviceName)) {
			favourites.put(serviceName, new HashSet<String>());
		}
		favourites.get(serviceName).add(postId);
	}

	public void removeFavorite(String postId, String serviceName) {
		if (favourites.containsKey(serviceName)) {
			favourites.get(serviceName).remove(postId);
		}
	}

	public void addActivity(String postId, String serviceName) {
		if (!activities.containsKey(serviceName)) {
			activities.put(serviceName, new HashSet<String>());
		}
		activities.get(serviceName).add(postId);
	}

	public void removeActivity(String postId, String serviceName) {
		if (activities.containsKey(serviceName)) {
			activities.get(serviceName).remove(postId);
		}
	}

	public boolean blockAccount(String blockStatus) {
		if ("true".equalsIgnoreCase(blockStatus)) {
			flBlocked = true;
			timeUnblock = Instant.now().plus(blockPeriod, ChronoUnit.DAYS).toEpochMilli();
			return true;
		} else if ("false".equalsIgnoreCase(blockStatus)) {
			flBlocked = false;
			timeUnblock = 0;
			return true;
		}
		return false;
	}

}
