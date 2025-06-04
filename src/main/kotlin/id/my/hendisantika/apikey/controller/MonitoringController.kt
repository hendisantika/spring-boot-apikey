package id.my.hendisantika.apikey.controller

import id.my.hendisantika.apikey.repository.ApiKeyRepository
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created by IntelliJ IDEA.
 * Project : apikey
 * User: hendisantika
 * Link: s.id/hendisantika
 * Email: hendisantika@yahoo.co.id
 * Telegram : @hendisantika34
 * Date: 04/06/25
 * Time: 14.40
 * To change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping("/admin/monitoring")
class MonitoringController(
    private val apiKeyRepository: ApiKeyRepository,
    private val meterRegistry: MeterRegistry
) {
    @GetMapping("/api-keys/usage")
    fun getApiKeyUsage(): List<ApiKeyUsage> {
        return apiKeyRepository.findAll().map { apiKey ->
            val requests = meterRegistry.get("api.requests")
                .tag("apiKey", apiKey.key)
                .counter()
                .count()

            ApiKeyUsage(
                name = apiKey.name,
                requests = requests.toLong(),
                rateLimit = apiKey.rateLimit
            )
        }
    }

    data class ApiKeyUsage(
        val name: String,
        val requests: Long,
        val rateLimit: Int
    )
}
