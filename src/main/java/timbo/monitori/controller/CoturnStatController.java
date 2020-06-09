package timbo.monitori.controller;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import timbo.monitori.coturn.CoturnStat;
import timbo.monitori.coturn.CoturnTotalStat;

import java.util.HashMap;
import java.util.Objects;

@RestController
@RequestMapping("stat")
public class CoturnStatController {

  private final Cache<String, CoturnStat> coturnStatCache;
  private final CoturnTotalStat coturnTotalStat;

  public CoturnStatController(CacheManager cacheManager, CoturnTotalStat coturnTotalStat) {
    this.coturnStatCache = (Cache<String, CoturnStat>) Objects.requireNonNull(cacheManager.getCache("coturnStat")).getNativeCache();
    this.coturnTotalStat = coturnTotalStat;
  }

  @GetMapping
  public HashMap<String, Object> stat() {
    return new HashMap<String, Object>() {{
      put("rcvp", coturnTotalStat.getTotalRcvp().longValue());
      put("rcvb", coturnTotalStat.getTotalRcvb().longValue());
      put("sentp", coturnTotalStat.getTotalSentp().longValue());
      put("sentb", coturnTotalStat.getTotalSentb().longValue());
      put("stat", new HashMap<String, Long>() {{
        put("hitCount", coturnStatCache.stats().hitCount());
        put("missCount", coturnStatCache.stats().missCount());
        put("loadSuccessCount", coturnStatCache.stats().loadSuccessCount());
        put("loadFailureCount", coturnStatCache.stats().loadFailureCount());
        put("totalLoadTime", coturnStatCache.stats().totalLoadTime());
        put("evictionCount", coturnStatCache.stats().evictionCount());
        put("evictionWeight", coturnStatCache.stats().evictionWeight());
      }});
    }};
  }

}
