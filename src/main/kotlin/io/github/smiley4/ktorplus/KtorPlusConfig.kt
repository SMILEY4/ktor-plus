package io.github.smiley4.ktorplus

import io.github.smiley4.ktorplus.websocketconnection.GenericCallWebSocketConnectionPropertyHandler
import io.github.smiley4.ktorplus.websocketconnection.GenericCookieParameterWebSocketConnectionPropertyHandler
import io.github.smiley4.ktorplus.websocketconnection.GenericHeaderParameterWebSocketConnectionPropertyHandler
import io.github.smiley4.ktorplus.websocketconnection.GenericPathParameterWebSocketConnectionPropertyHandler
import io.github.smiley4.ktorplus.websocketconnection.GenericPrincipalWebSocketConnectionPropertyHandler
import io.github.smiley4.ktorplus.websocketconnection.GenericQueryParameterWebSocketConnectionPropertyHandler
import io.github.smiley4.ktorplus.websocketconnection.GenericSessionWebSocketConnectionPropertyHandler
import io.github.smiley4.ktorplus.websocketconnection.WebSocketConnectionPropertyHandler
import io.github.smiley4.ktorplus.core.GenericParameterTranscoder
import io.github.smiley4.ktorplus.core.ParameterDecoder
import io.github.smiley4.ktorplus.core.ParameterEncoder
import io.github.smiley4.ktorplus.core.PropertyAnalyzer
import io.github.smiley4.ktorplus.request.RequestPropertyHandler
import io.github.smiley4.ktorplus.response.ResponsePropertyHandler
import io.github.smiley4.ktorplus.core.TypeAnalyzer
import io.github.smiley4.ktorplus.typedescriptor.TypeDescriptorEntry
import io.github.smiley4.ktorplus.request.GenericBodyRequestPropertyHandler
import io.github.smiley4.ktorplus.request.GenericCallRequestPropertyHandler
import io.github.smiley4.ktorplus.request.GenericCookieParameterRequestPropertyHandler
import io.github.smiley4.ktorplus.request.GenericHeaderParameterRequestPropertyHandler
import io.github.smiley4.ktorplus.request.GenericPathParameterRequestPropertyHandler
import io.github.smiley4.ktorplus.request.GenericPrincipalRequestPropertyHandler
import io.github.smiley4.ktorplus.request.GenericQueryParameterRequestPropertyHandler
import io.github.smiley4.ktorplus.response.GenericBodyResponseHandler
import io.github.smiley4.ktorplus.response.GenericCookieParameterResponseHandler
import io.github.smiley4.ktorplus.response.GenericHeaderParameterResponseHandler
import io.github.smiley4.ktorplus.response.GenericStatusCodeResponseHandler
import io.github.smiley4.ktorplus.typedescriptor.BodyAnalyzer
import io.github.smiley4.ktorplus.typedescriptor.CallAnalyzer
import io.github.smiley4.ktorplus.typedescriptor.CookieParameterAnalyzer
import io.github.smiley4.ktorplus.typedescriptor.HeaderParameterAnalyzer
import io.github.smiley4.ktorplus.typedescriptor.PathParameterAnalyzer
import io.github.smiley4.ktorplus.typedescriptor.PrincipalAnalyzer
import io.github.smiley4.ktorplus.typedescriptor.QueryParameterAnalyzer
import io.github.smiley4.ktorplus.typedescriptor.RequestAnalyzer
import io.github.smiley4.ktorplus.typedescriptor.ResponseAnalyzer
import io.github.smiley4.ktorplus.typedescriptor.WebSocketConnectionAnalyzer
import io.github.smiley4.ktorplus.typedescriptor.WebSocketSessionAnalyzer
import kotlinx.serialization.json.Json

object KtorPlusConfig {

    var json: Json = Json

    val encoders: MutableList<ParameterEncoder<*>> = mutableListOf(
        GenericParameterTranscoder()
    )

    val decoders: MutableList<ParameterDecoder<*>> = mutableListOf(
        GenericParameterTranscoder()
    )

    val typeAnalyzers: MutableList<TypeAnalyzer<out Annotation, out TypeDescriptorEntry>> = mutableListOf(
        RequestAnalyzer(),
        ResponseAnalyzer(),
        WebSocketConnectionAnalyzer()
    )

    val propertyAnalyzers: MutableList<PropertyAnalyzer<out Annotation, out TypeDescriptorEntry>> = mutableListOf(
        BodyAnalyzer(),
        CallAnalyzer(),
        WebSocketSessionAnalyzer(),
        CookieParameterAnalyzer(),
        HeaderParameterAnalyzer(),
        PathParameterAnalyzer(),
        PrincipalAnalyzer(),
        QueryParameterAnalyzer(),
    )

    val requestHandlers: MutableList<RequestPropertyHandler<*>> = mutableListOf(
        GenericCookieParameterRequestPropertyHandler { decoders },
        GenericHeaderParameterRequestPropertyHandler { decoders },
        GenericPathParameterRequestPropertyHandler { decoders },
        GenericQueryParameterRequestPropertyHandler { decoders },
        GenericBodyRequestPropertyHandler(),
        GenericCallRequestPropertyHandler(),
        GenericPrincipalRequestPropertyHandler(),
    )

    val responseHandlers: MutableList<ResponsePropertyHandler<*>> = mutableListOf(
        GenericCookieParameterResponseHandler { encoders },
        GenericHeaderParameterResponseHandler { encoders },
        GenericBodyResponseHandler(),
        GenericStatusCodeResponseHandler(),
    )

    val connectionHandlers: MutableList<WebSocketConnectionPropertyHandler<*>> = mutableListOf(
        GenericCookieParameterWebSocketConnectionPropertyHandler { decoders },
        GenericHeaderParameterWebSocketConnectionPropertyHandler { decoders },
        GenericPathParameterWebSocketConnectionPropertyHandler { decoders },
        GenericQueryParameterWebSocketConnectionPropertyHandler { decoders },
        GenericCallWebSocketConnectionPropertyHandler(),
        GenericSessionWebSocketConnectionPropertyHandler(),
        GenericPrincipalWebSocketConnectionPropertyHandler(),
    )

}
