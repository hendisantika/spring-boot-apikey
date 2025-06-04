package id.my.hendisantika.apikey.exception

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
/**
 * Exception thrown when an API key is invalid, disabled, or expired.
 */
class InvalidApiKeyException(message: String) : RuntimeException(message)

/**
 * Exception thrown when an API key exceeds its rate limit.
 */
class RateLimitExceededException(message: String) : RuntimeException(message)