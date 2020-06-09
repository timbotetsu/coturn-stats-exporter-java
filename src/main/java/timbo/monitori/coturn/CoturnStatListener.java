package timbo.monitori.coturn;

import com.github.benmanes.caffeine.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
public class CoturnStatListener implements MessageListener {

  private final Logger logger = LoggerFactory.getLogger(CoturnStatListener.class);

  private final Cache<String, CoturnStat> coturnStatCache;
  private final CoturnTotalStat coturnTotalStat;

  public CoturnStatListener(CacheManager cacheManager, CoturnTotalStat coturnTotalStat) {
    this.coturnStatCache = (Cache<String, CoturnStat>) Objects.requireNonNull(cacheManager.getCache("coturnStat")).getNativeCache();
    this.coturnTotalStat = coturnTotalStat;
  }

  @Override
  public void onMessage(Message message, byte[] pattern) {
    logger.info("message coming..");
    String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
    String[] channelParts = channel.split("/");

    if (channelParts.length < 8 || StringUtils.isEmpty(channelParts[4]) || StringUtils.isEmpty(channelParts[6])) {
      return; // just ignore illegal access
    }

    String msgBody = new String(message.getBody(), StandardCharsets.UTF_8);
    process(construct(channelParts, msgBody));
  }

  private synchronized void process(CoturnStat stat) {
    // TODO: process status in stat
    logger.info("process message start..");
    String user = stat.getUser();
    CoturnStat coturnStat = coturnStatCache.getIfPresent(user);
    if (Objects.isNull(coturnStat)) {
      coturnStatCache.put(user, stat);
    } else {
      coturnStat.setRcvp(coturnStat.getRcvp() + stat.getRcvp());
      coturnStat.setRcvb(coturnStat.getRcvb() + stat.getRcvb());
      coturnStat.setSentp(coturnStat.getSentp() + stat.getSentp());
      coturnStat.setSentb(coturnStat.getSentb() + stat.getSentb());
      coturnStatCache.put(user, coturnStat);
    }

    coturnTotalStat.getTotalRcvp().addAndGet(stat.getRcvp());
    coturnTotalStat.getTotalRcvb().addAndGet(stat.getRcvb());
    coturnTotalStat.getTotalSentp().addAndGet(stat.getSentp());
    coturnTotalStat.getTotalSentb().addAndGet(stat.getSentb());
    logger.info("process message end.. ");
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
