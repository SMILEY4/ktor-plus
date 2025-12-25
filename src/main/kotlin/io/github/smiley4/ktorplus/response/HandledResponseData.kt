package io.github.smiley4.ktorplus.response

import io.ktor.http.HttpStatusCode

interface HandledResponseData

data class HandledResponseBody(
    val value: Any?
) : HandledResponseData

data class HandledStatusCode(
    val value: HttpStatusCode
) : HandledResponseData
