package id.my.hendisantika.apikey.service

import id.my.hendisantika.apikey.repository.ApiKeyRepository
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

@Service
class ApiKeyService(private val apiKeyRepository: ApiKeyRepository) {
    private val rateLimiters = ConcurrentHashMap<String, Bucket>()

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
        val bucket = rateLimiters.computeIfAbsent(key) { k ->
            Bucket4j.builder()
                .addLimit(Bandwidth.classic(apiKey.rateLimit, Refill.intervally(apiKey.rateLimit, Duration.ofHours(1))))
                .build()
        }

        if (!bucket.tryConsume(1)) {
            throw RateLimitExceededException("Rate limit exceeded")
        }

        // Create authentication token
        val authorities = listOf(SimpleGrantedAuthority(apiKey.role.name))
        return ApiKeyAuthentication(key, authorities)
    }
}
