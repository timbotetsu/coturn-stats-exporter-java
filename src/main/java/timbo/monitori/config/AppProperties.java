package timbo.monitori.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("monitori")
@PropertySources({
  @PropertySource(value = "classpath:appconfig.properties", ignoreResourceNotFound = true),
  @PropertySource(value = "file://${CONFIG_HOME}/appconfig.properties", ignoreResourceNotFound = true)
})
public class AppProperties {

  private RedisProperties redis;

  @Getter
  @Setter
  public static class RedisProperties {
    private String host;
    private int port;
    private int database;
    private String password;
  }


}
