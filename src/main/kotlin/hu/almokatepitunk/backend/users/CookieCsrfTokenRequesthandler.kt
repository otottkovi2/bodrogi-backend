package hu.almokatepitunk.backend.users

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler
import org.springframework.security.web.csrf.CsrfTokenRequestHandler
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler
import org.springframework.util.StringUtils
import java.util.function.Supplier

class CookieCsrfTokenRequesthandler : CsrfTokenRequestHandler {

    private val plainRequesthandler: CsrfTokenRequestHandler = CsrfTokenRequestAttributeHandler()
    private val xorRequestHandler: CsrfTokenRequestHandler = XorCsrfTokenRequestAttributeHandler()


    override fun handle(request: HttpServletRequest?, response: HttpServletResponse?, csrfToken: Supplier<CsrfToken>) {
        xorRequestHandler.handle(request, response, csrfToken)
        csrfToken.get()
    }

    override fun resolveCsrfTokenValue(request: HttpServletRequest, csrfToken: CsrfToken): String? {
        val header = request.getHeader(csrfToken.headerName)
        return if (StringUtils.hasText(header)) {
            plainRequesthandler
        } else {
            xorRequestHandler
        }.resolveCsrfTokenValue(request, csrfToken)
    }

}