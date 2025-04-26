package adidasCopy.adidasCopyBackEnd.account;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AccountEntity {

	@Id
	private String uniqueId;
	private String username;
	private String email;
	private String firstName;
	private String lastName;
	private String password;
	private String authority;
	private boolean authenticated = false;
}
