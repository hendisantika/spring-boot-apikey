package id.my.hendisantika.apikey.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * Project : apikey
 * User: hendisantika
 * Link: s.id/hendisantika
 * Email: hendisantika@yahoo.co.id
 * Telegram : @hendisantika34
 * Date: 04/06/25
 * Time: 14.30
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "api_keys")
data class ApiKey(
    @Id
    @Column(length = 64)
    val key: String = UUID.randomUUID().toString(),

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val expiresAt: LocalDateTime,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: ApiKeyRole = ApiKeyRole.ROLE_CLIENT,

    @Column(nullable = false)
    val enabled: Boolean = true,

    // Rate limit in requests per hour
    @Column(nullable = false)
    val rateLimit: Int = 1000
)

enum class ApiKeyRole {
    ROLE_CLIENT,
    ROLE_ADMIN
}
