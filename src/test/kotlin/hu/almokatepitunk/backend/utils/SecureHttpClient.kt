package hu.almokatepitunk.backend.utils

import org.springframework.boot.test.web.client.*
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.context.WebApplicationContext


private const val CsrfTokenHeaderName = "X-XSRF-TOKEN"
private const val CookieHeaderName = "Cookie"
private const val SetCookieHeaderName = "set-cookie"

class SecureHttpClient(applicationContext: WebApplicationContext, username:String = "user",
                       password:String = "password") {

    private val testRestTemplate: TestRestTemplate = TestRestTemplate(TestRestTemplate.HttpClientOption.ENABLE_COOKIES)

    private val headers = HttpHeaders()

    private val entity: HttpEntity<Void>
        get() = HttpEntity<Void>(headers)

    init {
        val uriHandler = LocalHostUriTemplateHandler(applicationContext.environment,"http")
        testRestTemplate.setUriTemplateHandler(uriHandler)

        val getResponse = testRestTemplate.getForEntity<String>("/login")
        val csrfToken = getCsrfToken(getResponse)
        val cookies = getResponse.headers.getFirst(SetCookieHeaderName)

        val loginData = LinkedMultiValueMap<String, String>().apply {
            add("username",username)
            add("password", password)
            add("_csrf",csrfToken)
        }

        headers.add(CsrfTokenHeaderName,csrfToken)
        headers.add(CookieHeaderName,cookies)
        val postResponse = testRestTemplate.postForEntity<String>("/login",
            HttpEntity(loginData,headers)
        )
        headers.set(CookieHeaderName,postResponse.headers.getFirst(SetCookieHeaderName))
    }

    private fun getCsrfToken(response: ResponseEntity<*>) =
        response.headers.getFirst(SetCookieHeaderName)?.split(Regex("(;\\s\\w+)*="))
            ?.get(1) ?: throw CsrfNotProvidedException()

    private class CsrfNotProvidedException : RuntimeException()
    companion object {
        fun TestRestTemplate.getForEntitySecure(secureHttpClient: SecureHttpClient, uri: String, type:Class<*>):
                ResponseEntity<out Any> {
            val response = this.exchange(uri, HttpMethod.GET, secureHttpClient.entity,type)
            val csrfToken = secureHttpClient.getCsrfToken(response)
            secureHttpClient.headers.set(CsrfTokenHeaderName,csrfToken)
            return response
        }
    }
}

