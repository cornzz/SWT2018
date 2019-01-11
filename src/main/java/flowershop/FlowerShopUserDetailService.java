package flowershop;

import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Custom {@link UserDetailsService} implementation using the {@link UserAccountManager} to obtain user information for
 * login. This implementation allows login via email as well as username.
 *
 * @author Cornelius Kummer
 */
@Service
@Primary
class FlowerShopUserDetailService implements UserDetailsService {

	@NonNull
	private final UserAccountManager userAccountManager;

	public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
		String username = this.userAccountManager.findAll().get().filter(u -> u.getEmail().equals(name)).
				findFirst().map(UserAccount::getUsername).orElse(name);
		Optional<UserAccount> candidate = this.userAccountManager.findByUsername(username);
		return new UserAccountDetails(candidate.orElseThrow(() -> new UsernameNotFoundException("Useraccount: " + name + "not found")));
	}

	public FlowerShopUserDetailService(@NonNull final UserAccountManager userAccountManager) {
			this.userAccountManager = userAccountManager;
	}

	static class UserAccountDetails implements UserDetails {
		private final String username;
		private final String password;
		private final boolean isEnabled;
		private final Collection<? extends GrantedAuthority> authorities;

		public UserAccountDetails(UserAccount userAccount) {
			this.username = userAccount.getUsername();
			this.password = userAccount.getPassword().toString();
			this.isEnabled = userAccount.isEnabled();
			this.authorities = userAccount.getRoles().stream().
					map((role) -> new SimpleGrantedAuthority(role.getName())).
					collect(Collectors.toList());
		}

		public boolean isAccountNonExpired() {
			return true;
		}

		public boolean isAccountNonLocked() {
			return true;
		}

		public boolean isCredentialsNonExpired() {
			return true;
		}

		public String getUsername() {
			return this.username;
		}

		public String getPassword() {
			return this.password;
		}

		public boolean isEnabled() {
			return this.isEnabled;
		}

		public Collection<? extends GrantedAuthority> getAuthorities() {
			return this.authorities;
		}

		public String toString() {
			return "UserAccountDetailService.UserAccountDetails(username=" + this.getUsername() + ", password=" + this.getPassword() + ", isEnabled=" + this.isEnabled() + ", authorities=" + this.getAuthorities() + ")";
		}

		protected boolean canEqual(final Object other) {
			return other instanceof FlowerShopUserDetailService.UserAccountDetails;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			UserAccountDetails that = (UserAccountDetails) o;
			return getUsername().equals(that.getUsername()) &&
					getPassword().equals(that.getPassword()) &&
					getAuthorities().equals(that.getAuthorities());
		}

		@Override
		public int hashCode() {
			return Objects.hash(getUsername(), getPassword(), getAuthorities());
		}
	}
}
