package cc.powind.huron.core.router;

import cc.powind.huron.core.collect.CollectRecorder;
import cc.powind.huron.core.collect.CollectService;
import cc.powind.huron.core.collect.CollectServiceImpl;
import cc.powind.huron.core.model.Realtime;
import cc.powind.huron.core.model.RealtimeException;
import cc.powind.huron.core.model.RealtimeRegister;
import cc.powind.huron.core.model.RealtimeWrapper;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

public class NettyRouter {

    private final Log log = LogFactory.getLog(getClass());

    private int port = 8090;

    private CollectService collectService;

    private ObjectMapper mapper;

    private RealtimeRegister register;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public CollectService getCollectService() {
        return collectService;
    }

    public void setCollectService(CollectService collectService) {
        this.collectService = collectService;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public RealtimeRegister getRegister() {
        return register;
    }

    public void setRegister(RealtimeRegister register) {
        this.register = register;
    }

    public void start() {

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup());
        // default keepAlive is true
        // serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        serverBootstrap.channel(NioServerSocketChannel .class);

        serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel> () {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {

                // debug
                // ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));

                // http
                ch.pipeline().addLast(new HttpServerCodec());
                ch.pipeline().addLast(new HttpObjectAggregator(10 * 1024 * 1024));

                ch.pipeline().addLast(new ChunkedWriteHandler());
                ch.pipeline().addLast(new WebSocketServerProtocolHandler("/ws"));

                // 只处理指定类型的数据
                ch.pipeline().addLast(new SimpleChannelInboundHandler<FullHttpRequest>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
                        ctx.writeAndFlush(handleRequest(ctx, msg));
                    }

                    @Override
                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("channel closed");
                    }
                });
            }
        });
        serverBootstrap.bind(port);
    }

    protected HttpResponse handleRequest(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        SocketAddress socketAddress = ctx.channel().remoteAddress();
        InetSocketAddress address = (InetSocketAddress) socketAddress;

        HttpMethod method = request.method();
        if ("post".equalsIgnoreCase(method.name())) {
            return postHandleRequest(request, address.getAddress().getHostAddress());
        } else {
            return getHandleRequest(ctx, request);
        }
    }

    private HttpResponse getHandleRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        try {

            String uri = request.uri();

            if (uri.startsWith("/record")) {

                if (collectService instanceof CollectServiceImpl) {
                    CollectRecorder collectRecorder = ((CollectServiceImpl) collectService).getCollectRecorder();
                    byte[] bytes = mapper.writeValueAsBytes(collectRecorder);
                    ByteBuf buffer = ctx.alloc().buffer();
                    buffer.writeBytes(bytes);
                    return RESPONSE_CONTEXT.OK.response(request.protocolVersion(), buffer);
                }

            } else if (uri.startsWith("/metric")) {

            }

            return RESPONSE_CONTEXT.OK.response(request.protocolVersion());
        } catch (Exception e) {
            log.error(e);
            return RESPONSE_CONTEXT.INTERNAL_SERVER_ERROR.response(request.protocolVersion());
        }
    }

    private HttpResponse postHandleRequest(FullHttpRequest request, String ipAddress) {
        try {
            ByteBuf content = request.content();
            String realtimeAlias = getRealtimeAlias(request.uri());
            if (StringUtils.isBlank(realtimeAlias)) {
                return RESPONSE_CONTEXT.BAD_REQUEST.response(request.protocolVersion());
            }

            byte[] bytes = new byte[content.capacity()];

            content.readBytes(bytes);

            JavaType javaType = mapper.getTypeFactory().constructParametricType(RealtimeWrapper.class, register.getClazz(realtimeAlias));
            RealtimeWrapper<Realtime> wrapper = mapper.readValue(bytes, javaType);

            wrapper.setIpAddress(ipAddress);

            try {
                collectService.collect(wrapper, realtimeAlias);
            } catch (RealtimeException e) {
                return RESPONSE_CONTEXT.BAD_REQUEST.response(request.protocolVersion());
            }
            return RESPONSE_CONTEXT.OK.response(request.protocolVersion());
        } catch (Exception e) {
            log.error(e);
            return RESPONSE_CONTEXT.INTERNAL_SERVER_ERROR.response(request.protocolVersion());
        }
    }

    private String getRealtimeAlias(String uri) {

        String substring = uri.substring(uri.indexOf("?") + 1);

        String[] splits = substring.split("&");

        for (String split: splits) {
            String[] target = split.split("=");
            if ("alias".equalsIgnoreCase(target[0])) {
                return target[1];
            }
        }

        return null;
    }

    enum RESPONSE_CONTEXT {

        OK(HttpResponseStatus.OK),

        INTERNAL_SERVER_ERROR(HttpResponseStatus.INTERNAL_SERVER_ERROR),

        BAD_REQUEST(HttpResponseStatus.BAD_REQUEST);

        final HttpResponseStatus status;

        RESPONSE_CONTEXT(HttpResponseStatus status) {
            this.status = status;
        }

        public HttpResponse response(HttpVersion httpVersion) {
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(httpVersion, HttpResponseStatus.OK);
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, 0);
            return response;
        }

        public HttpResponse response(HttpVersion httpVersion, ByteBuf byteBuf) {
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(httpVersion, HttpResponseStatus.OK, byteBuf);
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
            return response;
        }
    }
}
