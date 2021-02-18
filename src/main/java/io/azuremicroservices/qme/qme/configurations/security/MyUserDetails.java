package io.azuremicroservices.qme.qme.configurations.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.models.User.Role;

public class MyUserDetails implements UserDetails {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5507119240147437442L;
	private User user;

    public MyUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public Long getId() {
    	return user.getId();
    }
    
    public String getPerspective() {
    	return user.getPerspective().getDisplayValue();
    }
    
    public void setPerspective(Role perspective) {
    	user.setPerspective(perspective);
    }    
    
    public LinkedHashMap<String, Integer> getRolePerspectives() {
    	return user.getRolePerspectives();
    }
    
    public String getFullName() {
    	return user.getFirstName() + " " + user.getLastName();
    }
    
    public String getRole() {
    	return user.getRole().getDisplayValue();
    }   

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.isBlocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User getUser(){ return user;}
}
