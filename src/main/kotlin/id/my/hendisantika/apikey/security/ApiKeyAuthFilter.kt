package id.my.hendisantika.apikey.security

import id.my.hendisantika.apikey.exception.InvalidApiKeyException
import id.my.hendisantika.apikey.exception.RateLimitExceededException
import id.my.hendisantika.apikey.service.ApiKeyService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Created by IntelliJ IDEA.
 * Project : apikey
 * User: hendisantika
 * Link: s.id/hendisantika
 * Email: hendisantika@yahoo.co.id
 * Telegram : @hendisantika34
 * Date: 04/06/25
 * Time: 14.31
 * To change this template use File | Settings | File Templates.
 */
@Component
class ApiKeyAuthFilter(
    private val apiKeyService: ApiKeyService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Skip authentication for certain paths
        if (shouldNotFilter(request)) {
            filterChain.doFilter(request, response)
            return
        }

        val apiKey = request.getHeader("X-API-Key")

        if (apiKey == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No API key provided")
            return
        }

        try {
            val authentication = apiKeyService.validateKey(apiKey)
            SecurityContextHolder.getContext().authentication = authentication
            filterChain.doFilter(request, response)
        } catch (e: InvalidApiKeyException) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.message)
        } catch (e: RateLimitExceededException) {
            response.sendError(429, e.message) // 429 is TOO_MANY_REQUESTS
        }
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.servletPath
        return path.startsWith("/public/") || path == "/error"
    }
}
