package timbo.monitori.coturn;

import lombok.Data;

import java.util.Date;

@Data
public class CoturnUserStat {
  private String rruid;
  private String did;
  private String channel;
  private long rcvp;
  private long rcvb;
  private long sentp;
  private long sentb;
  private Date createTime;
  private Date updateTime;
}
