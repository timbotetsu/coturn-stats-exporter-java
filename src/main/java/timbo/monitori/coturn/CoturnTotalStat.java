package timbo.monitori.coturn;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Getter
@Setter
@Component
public class CoturnTotalStat {
  private final AtomicLong totalRcvp = new AtomicLong(0);
  private final AtomicLong totalRcvb = new AtomicLong(0);
  private final AtomicLong totalSentp = new AtomicLong(0);
  private final AtomicLong totalSentb = new AtomicLong(0);
}
