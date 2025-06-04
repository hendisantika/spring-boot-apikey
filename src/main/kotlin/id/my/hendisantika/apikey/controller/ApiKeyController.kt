package id.my.hendisantika.apikey.controller

import id.my.hendisantika.apikey.service.ApiKeyService
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
 * Time: 14.35
 * To change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping("/admin/api-keys")
class ApiKeyController(private val apiKeyService: ApiKeyService)