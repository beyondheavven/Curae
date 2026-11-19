package com.vention.curae.chat.websocket.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.websocket")
public class WebSocketProperties {

	private List<String> allowedOrigins;

}
