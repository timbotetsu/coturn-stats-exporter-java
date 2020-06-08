package timbo.monitori.coturn;

import lombok.Data;

@Data
public class CoturnStat {
  private String realm;
  private String user;
  private String allocation;
  private Status status;
  private long rcvp;
  private long rcvb;
  private long sentp;
  private long sentb;

  public enum Status {
    NEW,
    REFRESHED,
    DELETED;
  }
}
