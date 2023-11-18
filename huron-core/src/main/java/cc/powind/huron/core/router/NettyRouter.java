package cc.powind.huron.core.router;

import cc.powind.huron.core.collect.CollectService;
import cc.powind.huron.core.model.Realtime;
import cc.powind.huron.core.model.RealtimeRegister;
import cc.powind.huron.core.model.RealtimeWrapper;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
                        ctx.writeAndFlush(handleRequest(msg));
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

    protected HttpResponse handleRequest(FullHttpRequest request) throws Exception {

        try {
            ByteBuf content = request.content();

            String uri = request.uri();

            String substring = uri.substring(uri.indexOf("?") + 1);

            String[] splits = substring.split("&");

            Map<String, String> map = new HashMap<>();
            for (String split: splits) {
                String[] target = split.split("=");
                map.put(target[0], target[1]);
            }

            byte[] bytes = new byte[content.capacity()];

            content.readBytes(bytes);

            JavaType javaType = mapper.getTypeFactory().constructParametricType(RealtimeWrapper.class, register.getClazz(map.get("alias")));
            RealtimeWrapper<Realtime> wrapper = mapper.readValue(bytes, javaType);

            // convert realtime
            wrapper.getRealtimeList().forEach(realtime -> collectService.collect(realtime));
            return RESPONSE_CONTEXT.OK.response(request.protocolVersion());
        } catch (Exception e) {
            log.error(e);
            return RESPONSE_CONTEXT.INTERNAL_SERVER_ERROR.response(request.protocolVersion());
        }
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
    }
}
