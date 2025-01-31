package com.project.popupmarket.entity

import com.project.popupmarket.enums.AuthProvider
import com.project.popupmarket.enums.Role
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    var id: Long? = null,

    @Column(name = "email", nullable = false, unique = true)
    var email: String,

    @Column(name = "password", nullable = true)
    private var password: String?,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "social", nullable = false)
    @Enumerated(EnumType.STRING)
    var social: AuthProvider,

    @Column(name = "brand", nullable = false)
    var brand: String,

    @Column(name = "tel", nullable = false)
    var tel: String,

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    var role: Role,

    @Column(nullable = false)
    var registeredAt: LocalDateTime = LocalDateTime.now()
) : UserDetails {

    // JPA를 위한 기본 생성자
    protected constructor() : this(
        id = null,
        email = "",
        password = null,
        name = "",
        social = AuthProvider.EMAIL,
        brand = "",
        tel = "",
        role = Role.CUSTOMER,
        registeredAt = LocalDateTime.now()
    )

    @PrePersist
    protected fun onCreate() {
        registeredAt = LocalDateTime.now()
    }

    fun update(nickname: String): User {
        this.name = nickname
        return this
    }
    fun setPassword(password: String) {
        this.password = password
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("user"))
    }

    override fun getUsername(): String = email

    override fun getPassword(): String? = password

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}

