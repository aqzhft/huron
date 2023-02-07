package cc.powind.huron.view;

import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

public class WebsocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Bean
    public WebsocketServer websocketServer() {
        return new WebsocketServer();
    }

    @Bean
    public MetricController metricController() {
        return new MetricController();
    }
}
