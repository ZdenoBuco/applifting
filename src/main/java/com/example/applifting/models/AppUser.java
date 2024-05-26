package com.example.applifting.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AppUser implements UserDetails {
    @Id
    @GeneratedValue
    private UUID id;
    private String username;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String accessToken;
    @OneToMany(mappedBy = "ownerId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<MonitoredEndpoint> monitoredEndpoints;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return accessToken;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
