package io.github.smiley4.ktorplus.data

import io.ktor.http.HttpStatusCode

interface HandledResponseData

data class HandledResponseBody(
    val value: Any?
) : HandledResponseData

data class HandledStatusCode(
    val value: HttpStatusCode
) : HandledResponseData
