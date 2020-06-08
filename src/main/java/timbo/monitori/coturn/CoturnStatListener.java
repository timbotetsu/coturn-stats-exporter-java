package timbo.monitori.coturn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

@Component
public class CoturnStatListener implements MessageListener {

  private final Logger logger = LoggerFactory.getLogger(CoturnStatListener.class);

  @Override
  public void onMessage(Message message, byte[] pattern) {
    String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
    String[] channelParts = channel.split("/");

    if (channelParts.length < 8 || StringUtils.isEmpty(channelParts[4]) || StringUtils.isEmpty(channelParts[6])) {
      return; // just ignore illegal access
    }

    String msgBody = new String(message.getBody(), StandardCharsets.UTF_8);
    process(construct(channelParts, msgBody));
  }

  private void process(CoturnStat stat) {
    // TODO:
  }

  private CoturnStat construct(String[] channelParts, String msgBody) {
    CoturnStat coturnStat = new CoturnStat();
    coturnStat.setRealm(channelParts[2]);
    coturnStat.setUser(channelParts[4]);
    coturnStat.setAllocation(channelParts[6]);

    if (channelParts[7].startsWith("status")) {
      if (msgBody.startsWith("new")) {
        coturnStat.setStatus(CoturnStat.Status.NEW);
      } else if (msgBody.startsWith("refreshed")) {
        coturnStat.setStatus(CoturnStat.Status.REFRESHED);
      } else if (msgBody.startsWith("deleted")) {
        coturnStat.setStatus(CoturnStat.Status.DELETED);
      }
    } else if (channelParts[7].startsWith("traffic") || channelParts[7].startsWith("total_traffic")) { // "traffic/peer" will be count on with "traffic"
      fillTraffic(coturnStat, msgBody);
    }
    return coturnStat;
  }

  private void fillTraffic(CoturnStat coturnStat, String msgBody) {
    String[] bodyParts = msgBody.split(",");
    for (String s : bodyParts) {
      String[] tmpParts = s.trim().split("=");
      switch (tmpParts[0]) {
        case "rcvp":
          coturnStat.setRcvp(Long.parseLong(tmpParts[1]));
          break;
        case "rcvb":
          coturnStat.setRcvb(Long.parseLong(tmpParts[1]));
          break;
        case "sentp":
          coturnStat.setSentp(Long.parseLong(tmpParts[1]));
          break;
        case "sentb":
          coturnStat.setSentb(Long.parseLong(tmpParts[1]));
          break;
      }
    }
  }

}
