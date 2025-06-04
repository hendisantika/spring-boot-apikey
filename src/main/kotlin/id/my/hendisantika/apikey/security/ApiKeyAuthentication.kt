package id.my.hendisantika.apikey.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

/**
 * Created by IntelliJ IDEA.
 * Project : apikey
 * User: hendisantika
 * Link: s.id/hendisantika
 * Email: hendisantika@yahoo.co.id
 * Telegram : @hendisantika34
 * Date: 04/06/25
 * Time: 14.37
 * To change this template use File | Settings | File Templates.
 */

/**
 * Authentication token for API key-based authentication.
 */
class ApiKeyAuthentication(
    private val apiKey: String,
    authorities: Collection<GrantedAuthority>
) : AbstractAuthenticationToken(authorities) {

    init {
        isAuthenticated = true
    }

    override fun getCredentials(): Any {
        return apiKey
    }

    override fun getPrincipal(): Any {
        return apiKey
    }
}