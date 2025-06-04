package id.my.hendisantika.apikey.service

import id.my.hendisantika.apikey.entity.ApiKey
import id.my.hendisantika.apikey.entity.ApiKeyRole
import id.my.hendisantika.apikey.exception.InvalidApiKeyException
import id.my.hendisantika.apikey.repository.ApiKeyRepository
import id.my.hendisantika.apikey.security.ApiKeyAuthentication
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.time.LocalDateTime

class ApiKeyServiceTest {

    private lateinit var apiKeyRepository: ApiKeyRepository
    private lateinit var apiKeyService: ApiKeyService

    @BeforeEach
    fun setUp() {
        apiKeyRepository = mock(ApiKeyRepository::class.java)
        apiKeyService = ApiKeyService(apiKeyRepository)
    }

    @Test
    fun `validateKey should return authentication when key is valid`() {
        // Arrange
        val apiKey = ApiKey(
            key = "test-key",
            name = "Test Key",
            expiresAt = LocalDateTime.now().plusDays(1),
            role = ApiKeyRole.ROLE_CLIENT,
            enabled = true,
            rateLimit = 1000
        )

        `when`(apiKeyRepository.findByKey("test-key")).thenReturn(apiKey)

        // Act
        val authentication = apiKeyService.validateKey("test-key")

        // Assert
        assertNotNull(authentication)
        assertTrue(authentication is ApiKeyAuthentication)
        assertEquals("test-key", authentication.credentials)
        assertTrue(authentication.authorities.contains(SimpleGrantedAuthority("ROLE_CLIENT")))
    }

    @Test
    fun `validateKey should throw InvalidApiKeyException when key is not found`() {
        // Arrange
        `when`(apiKeyRepository.findByKey("invalid-key")).thenReturn(null)

        // Act & Assert
        val exception = assertThrows<InvalidApiKeyException> {
            apiKeyService.validateKey("invalid-key")
        }
        assertEquals("Invalid API key", exception.message)
    }

    @Test
    fun `validateKey should throw InvalidApiKeyException when key is disabled`() {
        // Arrange
        val apiKey = ApiKey(
            key = "disabled-key",
            name = "Disabled Key",
            expiresAt = LocalDateTime.now().plusDays(1),
            role = ApiKeyRole.ROLE_CLIENT,
            enabled = false,
            rateLimit = 1000
        )

        `when`(apiKeyRepository.findByKey("disabled-key")).thenReturn(apiKey)

        // Act & Assert
        val exception = assertThrows<InvalidApiKeyException> {
            apiKeyService.validateKey("disabled-key")
        }
        assertEquals("API key is disabled", exception.message)
    }

    @Test
    fun `validateKey should throw InvalidApiKeyException when key is expired`() {
        // Arrange
        val apiKey = ApiKey(
            key = "expired-key",
            name = "Expired Key",
            expiresAt = LocalDateTime.now().minusDays(1),
            role = ApiKeyRole.ROLE_CLIENT,
            enabled = true,
            rateLimit = 1000
        )

        `when`(apiKeyRepository.findByKey("expired-key")).thenReturn(apiKey)

        // Act & Assert
        val exception = assertThrows<InvalidApiKeyException> {
            apiKeyService.validateKey("expired-key")
        }
        assertEquals("API key has expired", exception.message)
    }

    @Test
    fun `createApiKey should create and save a new API key`() {
        // Arrange
        val name = "New Key"
        val role = ApiKeyRole.ROLE_ADMIN
        val rateLimit = 500

        val savedApiKey = ApiKey(
            key = "generated-key",
            name = name,
            role = role,
            rateLimit = rateLimit,
            expiresAt = LocalDateTime.now().plusYears(1)
        )

        `when`(apiKeyRepository.save(any(ApiKey::class.java))).thenReturn(savedApiKey)

        // Act
        val result = apiKeyService.createApiKey(name, role, rateLimit)

        // Assert
        assertNotNull(result)
        assertEquals(name, result.name)
        assertEquals(role, result.role)
        assertEquals(rateLimit, result.rateLimit)
        verify(apiKeyRepository).save(any(ApiKey::class.java))
    }

    @Test
    fun `getAllApiKeys should return all API keys`() {
        // Arrange
        val apiKey1 = ApiKey(
            key = "key-1",
            name = "Key 1",
            expiresAt = LocalDateTime.now().plusDays(1),
            role = ApiKeyRole.ROLE_CLIENT,
            enabled = true,
            rateLimit = 1000
        )

        val apiKey2 = ApiKey(
            key = "key-2",
            name = "Key 2",
            expiresAt = LocalDateTime.now().plusDays(2),
            role = ApiKeyRole.ROLE_ADMIN,
            enabled = true,
            rateLimit = 2000
        )

        val apiKeys = listOf(apiKey1, apiKey2)
        `when`(apiKeyRepository.findAll()).thenReturn(apiKeys)

        // Act
        val result = apiKeyService.getAllApiKeys()

        // Assert
        assertEquals(2, result.size)
        assertEquals(apiKey1, result[0])
        assertEquals(apiKey2, result[1])
        verify(apiKeyRepository).findAll()
    }

    @Test
    fun `getApiKeyByKey should return API key when it exists`() {
        // Arrange
        val apiKey = ApiKey(
            key = "test-key",
            name = "Test Key",
            expiresAt = LocalDateTime.now().plusDays(1),
            role = ApiKeyRole.ROLE_CLIENT,
            enabled = true,
            rateLimit = 1000
        )

        `when`(apiKeyRepository.findByKey("test-key")).thenReturn(apiKey)

        // Act
        val result = apiKeyService.getApiKeyByKey("test-key")

        // Assert
        assertNotNull(result)
        assertEquals(apiKey, result)
        verify(apiKeyRepository).findByKey("test-key")
    }

    @Test
    fun `getApiKeyByKey should return null when key does not exist`() {
        // Arrange
        `when`(apiKeyRepository.findByKey("non-existent-key")).thenReturn(null)

        // Act
        val result = apiKeyService.getApiKeyByKey("non-existent-key")

        // Assert
        assertNull(result)
        verify(apiKeyRepository).findByKey("non-existent-key")
    }
}
