package timbo.monitori.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import timbo.monitori.coturn.CoturnStatListener;

@Configuration
@ComponentScan("timbo.monitori")
public class AppConfig {

  private final AppProperties.RedisProperties redisProperties;

  public AppConfig(AppProperties appProperties) {
    this.redisProperties = appProperties.getRedis();
  }

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration() {{
      setHostName(redisProperties.getHost());
      setPort(redisProperties.getPort());
      setDatabase(redisProperties.getDatabase());
      setPassword(redisProperties.getPassword());
    }};
    return new LettuceConnectionFactory(config);
  }

  @Bean
  public MessageListenerAdapter messageListenerAdapter(CoturnStatListener coturnStatListener) {
    return new MessageListenerAdapter(coturnStatListener);
  }

  @Bean
  public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory,
                                                                     MessageListenerAdapter messageListenerAdapter) {
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(redisConnectionFactory);
    container.addMessageListener(messageListenerAdapter, new PatternTopic("turn/realm/*"));
    return container;
  }

}
