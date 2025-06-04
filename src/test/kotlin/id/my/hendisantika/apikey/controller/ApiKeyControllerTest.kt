package id.my.hendisantika.apikey.controller

import id.my.hendisantika.apikey.entity.ApiKey
import id.my.hendisantika.apikey.entity.ApiKeyRole
import id.my.hendisantika.apikey.service.ApiKeyService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@WebMvcTest(ApiKeyController::class)
class ApiKeyControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var apiKeyService: ApiKeyService

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `getAllApiKeys should return all API keys for admin users`() {
        // Arrange
        val now = LocalDateTime.now()
        val apiKey1 = ApiKey(
            key = "key-1",
            name = "Key 1",
            expiresAt = now.plusDays(1),
            role = ApiKeyRole.ROLE_CLIENT,
            enabled = true,
            rateLimit = 1000
        )

        val apiKey2 = ApiKey(
            key = "key-2",
            name = "Key 2",
            expiresAt = now.plusDays(2),
            role = ApiKeyRole.ROLE_ADMIN,
            enabled = true,
            rateLimit = 2000
        )

        `when`(apiKeyService.getAllApiKeys()).thenReturn(listOf(apiKey1, apiKey2))

        // Act & Assert
        mockMvc.perform(get("/admin/api-keys"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].key").value("key-1"))
            .andExpect(jsonPath("$[0].name").value("Key 1"))
            .andExpect(jsonPath("$[0].role").value("ROLE_CLIENT"))
            .andExpect(jsonPath("$[0].rateLimit").value(1000))
            .andExpect(jsonPath("$[1].key").value("key-2"))
            .andExpect(jsonPath("$[1].name").value("Key 2"))
            .andExpect(jsonPath("$[1].role").value("ROLE_ADMIN"))
            .andExpect(jsonPath("$[1].rateLimit").value(2000))
    }

    @Test
    @WithMockUser(roles = ["CLIENT"])
    fun `getAllApiKeys should return forbidden for non-admin users`() {
        // Act & Assert
        mockMvc.perform(get("/admin/api-keys"))
            .andExpect(status().isForbidden)
    }
}
