package id.my.hendisantika.apikey.controller

import id.my.hendisantika.apikey.entity.ApiKeyRole
import id.my.hendisantika.apikey.service.ApiKeyService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

/**
 * Created by IntelliJ IDEA.
 * Project : apikey
 * User: hendisantika
 * Link: s.id/hendisantika
 * Email: hendisantika@yahoo.co.id
 * Telegram : @hendisantika34
 * Date: 04/06/25
 * Time: 14.35
 * To change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping("/api-keys")
class UserApiKeyController(private val apiKeyService: ApiKeyService) {

    @GetMapping
    fun getCurrentUserApiKey(): ApiKeyResponse {
        val authentication = SecurityContextHolder.getContext().authentication
        val apiKey = authentication.credentials.toString()

        val apiKeyEntity = apiKeyService.getApiKeyByKey(apiKey)
            ?: throw IllegalStateException("API key not found")

        return ApiKeyResponse(
            key = apiKeyEntity.key,
            name = apiKeyEntity.name,
            expiresAt = apiKeyEntity.expiresAt,
            role = apiKeyEntity.role,
            rateLimit = apiKeyEntity.rateLimit
        )
    }

    data class ApiKeyResponse(
        val key: String,
        val name: String,
        val expiresAt: LocalDateTime,
        val role: ApiKeyRole,
        val rateLimit: Int
    )
}