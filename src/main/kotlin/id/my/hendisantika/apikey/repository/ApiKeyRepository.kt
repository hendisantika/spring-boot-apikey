package id.my.hendisantika.apikey.repository

import id.my.hendisantika.apikey.entity.ApiKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Created by IntelliJ IDEA.
 * Project : apikey
 * User: hendisantika
 * Link: s.id/hendisantika
 * Email: hendisantika@yahoo.co.id
 * Telegram : @hendisantika34
 * Date: 04/06/25
 * Time: 14.36
 * To change this template use File | Settings | File Templates.
 */

@Repository
interface ApiKeyRepository : JpaRepository<ApiKey, String> {
    fun findByKey(key: String): ApiKey?
}
