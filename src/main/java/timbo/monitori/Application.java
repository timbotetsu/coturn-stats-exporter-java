package timbo.monitori;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;
import timbo.monitori.config.AppConfig;

@Configuration
@EnableAutoConfiguration
public class Application {

  public static void main(String[] args) {
    new SpringApplicationBuilder(AppConfig.class)
      .bannerMode(Banner.Mode.OFF)
      .run(args)
      .getEnvironment();
  }

}
