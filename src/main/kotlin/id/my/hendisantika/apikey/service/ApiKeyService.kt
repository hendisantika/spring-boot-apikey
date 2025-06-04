package id.my.hendisantika.apikey.service

import id.my.hendisantika.apikey.repository.ApiKeyRepository
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class ApiKeyService(private val apiKeyRepository: ApiKeyRepository) {
    private val rateLimiters = ConcurrentHashMap<String, Bucket>()

}
