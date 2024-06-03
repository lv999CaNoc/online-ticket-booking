package com.actvn.cinema.model;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(min = 4, message = "Username tối thiểu 4 ký tự")
    @Size(max = 20, message = "Username tối đa 10 ký tự")
    private String username;

    @Column(nullable = false)
    @Size(min = 6, message = "Mật khẩu phải dài ít nhất 6 ký tự")
    private String password;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    @NotBlank(message = "Hãy nhập địa chỉ email của bạn")
    @Email(message = "Vui lòng nhập địa chỉ email hợp lệ")
    private String email;

    @Pattern(regexp = "\\d{10}", message = "Vui lòng nhập 10 chữ số.")
    private String phoneNumber;

    @Size(max = 10, message = "Nhập tối đa 10 ký tự")
    @NotBlank(message = "Hãy nhập tên của bạn")
    private String firstName;

    @Size(max = 10, message = "Nhập tối đa 10 ký tự")
    @NotBlank(message = "Hãy nhập họ của bạn")
    private String lastName;

    @Column(name = "enabled", nullable = false)
    private boolean isEnabled;

    @Column(name = "locked", nullable = false)
    private boolean isLocked;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Manager manager;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

}