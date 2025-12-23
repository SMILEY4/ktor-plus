package io.github.smiley4.ktorplus.data

import io.ktor.http.Cookie
import io.ktor.http.CookieEncoding
import io.ktor.util.date.GMTDate

/**
 * Data for setting a cookie.
 */
data class Cookie(
    val value: String,
    val encoding: CookieEncoding = CookieEncoding.URI_ENCODING,
    val maxAge: Int? = null,
    val expires: GMTDate? = null,
    val domain: String? = null,
    val path: String? = null,
    val paths: Collection<String> = listOf(),
    val secure: Boolean = false,
    val httpOnly: Boolean = false,
    val extensions: Map<String, String?> = emptyMap()
) {

    fun toKtorCookie(name: String) = (paths + path).filterNotNull().map { path ->
        Cookie(
            name = name,
            value = value,
            encoding = encoding,
            maxAge = maxAge,
            expires = expires,
            domain = domain,
            path = path,
            secure = secure,
            httpOnly = httpOnly,
            extensions = extensions,
        )
    }

}
