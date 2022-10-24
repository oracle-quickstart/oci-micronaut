package api;

import io.micronaut.core.annotation.TypeHint;
import io.micronaut.runtime.Micronaut;
import io.micronaut.security.authentication.ServerAuthentication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.micronaut.http.server.netty.encoders.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.micronaut.http.netty.stream.HttpStreamsServerHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.micronaut.http.server.netty.SmartHttpContentCompressor;
import io.netty.handler.codec.http.HttpServerKeepAliveHandler;
import io.netty.handler.flow.FlowControlHandler;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.channel.DefaultChannelPipeline;

@OpenAPIDefinition(
        info = @Info(
                title = "mushop-api",
                version = "1.0",
                description = "Micronaut MuShop API"
        )
)

@TypeHint(
        typeNames = {
                "io.netty.channel.DefaultChannelPipeline$HeadContext",
                "io.netty.channel.DefaultChannelPipeline$TailContext",
                "io.micronaut.security.authentication.ServerAuthentication",
                "io.micronaut.http.server.netty.encoders.HttpResponseEncoder",
                "io.netty.handler.stream.ChunkedWriteHandler",
                "io.micronaut.http.netty.stream.HttpStreamsServerHandler",
                "io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler",
                "io.micronaut.http.server.netty.SmartHttpContentCompressor",
                "com.github.benmanes.caffeine.cache.StripedBuffer",
                "io.netty.handler.codec.http.HttpServerKeepAliveHandler",
                "io.netty.handler.flow.FlowControlHandler",
                "io.netty.handler.codec.http.HttpServerCodec",
                "io.netty.handler.timeout.IdleStateHandler",
                "io.netty.channel.DefaultChannelPipeline"},
        accessType = {
                TypeHint.AccessType.ALL_PUBLIC,
                TypeHint.AccessType.ALL_PUBLIC_CONSTRUCTORS,
                TypeHint.AccessType.ALL_DECLARED_CONSTRUCTORS,
                TypeHint.AccessType.ALL_DECLARED_FIELDS
        }
)
@SecurityScheme(type = SecuritySchemeType.HTTP, name = Application.BASIC_AUTH, scheme = "basic")
@SecurityScheme(type = SecuritySchemeType.APIKEY, name = Application.COOKIE_AUTH, in = SecuritySchemeIn.COOKIE, paramName = "SESSION")
public class Application {

    public static final String BASIC_AUTH = "BasicAuth";
    public static final String COOKIE_AUTH = "CookieAuth";

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
