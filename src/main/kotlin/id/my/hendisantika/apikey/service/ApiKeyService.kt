package id.my.hendisantika.apikey.service

import id.my.hendisantika.apikey.entity.ApiKey
import id.my.hendisantika.apikey.entity.ApiKeyRole
import id.my.hendisantika.apikey.exception.InvalidApiKeyException
import id.my.hendisantika.apikey.exception.RateLimitExceededException
import id.my.hendisantika.apikey.repository.ApiKeyRepository
import id.my.hendisantika.apikey.security.ApiKeyAuthentication
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

@Service
class ApiKeyService(private val apiKeyRepository: ApiKeyRepository) {
    // Simple rate limiter: key -> (count, reset time)
    private val rateLimiters = ConcurrentHashMap<String, Pair<Int, LocalDateTime>>()

    fun getAllApiKeys(): List<ApiKey> {
        return apiKeyRepository.findAll()
    }

    fun getApiKeyByKey(key: String): ApiKey? {
        return apiKeyRepository.findByKey(key)
    }

    fun validateKey(key: String): Authentication {
        val apiKey = apiKeyRepository.findByKey(key)
            ?: throw InvalidApiKeyException("Invalid API key")

        if (!apiKey.enabled) {
            throw InvalidApiKeyException("API key is disabled")
        }

        if (apiKey.expiresAt.isBefore(LocalDateTime.now())) {
            throw InvalidApiKeyException("API key has expired")
        }

        // Check rate limit
        val now = LocalDateTime.now()
        rateLimiters.compute(key) { _, value ->
            if (value == null || value.second.isBefore(now)) {
                // First request or reset time has passed
                Pair(1, now.plusHours(1))
            } else if (value.first >= apiKey.rateLimit) {
                // Rate limit exceeded
                throw RateLimitExceededException("Rate limit exceeded")
            } else {
                // Increment count
                Pair(value.first + 1, value.second)
            }
        }

        // Create authentication token
        val authorities = listOf(SimpleGrantedAuthority(apiKey.role.name))
        return ApiKeyAuthentication(key, authorities)
    }

    fun createApiKey(name: String, role: ApiKeyRole, rateLimit: Int): ApiKey {
        val apiKey = ApiKey(
            name = name,
            role = role,
            rateLimit = rateLimit,
            expiresAt = LocalDateTime.now().plusYears(1)
        )
        return apiKeyRepository.save(apiKey)
    }
}
