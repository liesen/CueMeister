package cue;

/**
 * 
 */
public class Flag {
  public static final Flag DCP = new Flag("DCP");

  public static final Flag FOUR_CH = new Flag("4CH");

  public static final Flag PRE = new Flag("PRE");

  public static final Flag SCMS = new Flag("SCMS");

  public static final Flag DATA = new Flag("DATA");

  /** Flag */
  private final String flag;

  private Flag(String flag) {
    this.flag = flag;
  }

  @Override
  public boolean equals(Object o) {
    return flag.equals(o.toString());
  }

  @Override
  public int hashCode() {
    return flag.hashCode();
  }

  @Override
  public String toString() {
    return flag;
  }
}
