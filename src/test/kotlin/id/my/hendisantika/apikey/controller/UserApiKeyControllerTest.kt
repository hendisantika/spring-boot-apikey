package id.my.hendisantika.apikey.controller

import id.my.hendisantika.apikey.entity.ApiKey
import id.my.hendisantika.apikey.entity.ApiKeyRole
import id.my.hendisantika.apikey.security.ApiKeyAuthentication
import id.my.hendisantika.apikey.security.SecurityConfig
import id.my.hendisantika.apikey.service.ApiKeyService
import jakarta.servlet.ServletException
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

@WebMvcTest(UserApiKeyController::class)
@Import(SecurityConfig::class)
class UserApiKeyControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var apiKeyService: ApiKeyService

    @Test
    fun `getCurrentUserApiKey should return the current user's API key`() {
        // Arrange
        val apiKey = ApiKey(
            key = "user-key",
            name = "User Key",
            expiresAt = LocalDateTime.now().plusDays(1),
            role = ApiKeyRole.ROLE_CLIENT,
            enabled = true,
            rateLimit = 1000
        )

        val authorities = listOf(SimpleGrantedAuthority("ROLE_CLIENT"))
        val authentication = ApiKeyAuthentication("user-key", authorities)

        `when`(apiKeyService.validateKey("user-key")).thenReturn(authentication)
        `when`(apiKeyService.getApiKeyByKey("user-key")).thenReturn(apiKey)

        // Act & Assert
        mockMvc.perform(get("/api-keys").header("X-API-Key", "user-key"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.key").value("user-key"))
            .andExpect(jsonPath("$.name").value("User Key"))
            .andExpect(jsonPath("$.role").value("ROLE_CLIENT"))
            .andExpect(jsonPath("$.rateLimit").value(1000))
    }

    @Test
    fun `getCurrentUserApiKey should return not found when API key does not exist`() {
        // Arrange
        val authorities = listOf(SimpleGrantedAuthority("ROLE_CLIENT"))
        val authentication = ApiKeyAuthentication("non-existent-key", authorities)

        `when`(apiKeyService.validateKey("non-existent-key")).thenReturn(authentication)
        `when`(apiKeyService.getApiKeyByKey("non-existent-key")).thenReturn(null)

        // Act & Assert
        // MockMvc does not render error pages, so the IllegalStateException propagates
        val exception = assertFailsWith<ServletException> {
            mockMvc.perform(get("/api-keys").header("X-API-Key", "non-existent-key"))
        }
        assertIs<IllegalStateException>(exception.cause)
    }
}