package timbo.monitori.coturn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class CoturnStatListener implements MessageListener {

  private final Logger logger = LoggerFactory.getLogger(CoturnStatListener.class);

  @Override
  public void onMessage(Message message, byte[] pattern) {
    // TODO: handle message
    logger.info("here comes message {}", message);
  }

}
