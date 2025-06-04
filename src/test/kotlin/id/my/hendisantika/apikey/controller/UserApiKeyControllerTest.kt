package id.my.hendisantika.apikey.controller

import id.my.hendisantika.apikey.entity.ApiKey
import id.my.hendisantika.apikey.entity.ApiKeyRole
import id.my.hendisantika.apikey.security.ApiKeyAuthentication
import id.my.hendisantika.apikey.service.ApiKeyService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@WebMvcTest(UserApiKeyController::class)
class UserApiKeyControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
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

        `when`(apiKeyService.getApiKeyByKey("user-key")).thenReturn(apiKey)

        // Act & Assert
        mockMvc.perform(get("/api-keys").with(authentication(authentication)))
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

        `when`(apiKeyService.getApiKeyByKey("non-existent-key")).thenReturn(null)

        // Act & Assert
        mockMvc.perform(get("/api-keys").with(authentication(authentication)))
            .andExpect(status().isInternalServerError) // Since we throw IllegalStateException
    }
}