package mixmeister.mmp;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A Mixmeister playlist item/track.
 * 
 * @author johan
 */
public class Track {
  /** Track header */
  private TrackHeader header; // TRKH chunk

  /** Path to file */
  private String fileName; // TRKF chunk

  /** Meta data */
  private Map<Integer, Set<Marker>> metaData; // TKLY chunk

  private Marker stop;

  /**
   * 
   * 
   * @param header
   * @param fileName
   */
  public Track(TrackHeader header, String fileName) {
    this(header, fileName, new LinkedList<Marker>());
  }

  /**
   * 
   * 
   * @param header
   * @param fileName
   * @param meta2
   */
  public Track(TrackHeader header, String fileName, List<Marker> meta2) {
    this.header = header;
    this.fileName = fileName;
    this.metaData = new HashMap<Integer, Set<Marker>>();

    for (Marker meta : meta2) {
      addMarker(meta);
    }
  }

  /**
   * @return track's meta data
   */
  public Collection<Set<Marker>> getMetaData() {
    return metaData.values();
  }

  /**
   * @param type
   * @return markers of given type if any; null otherwise
   */
  public Set<Marker> getMarkers(int type) {
    return metaData.containsKey(type) ? metaData.get(type) : Collections.<Marker>emptySet();
  }

  /**
   * @param metaData
   */
  public void addMarker(Marker metaData) {
    Set<Marker> metaSet;

    if (this.metaData.containsKey(metaData.getType())) {
      metaSet = this.metaData.get(metaData.getType());
    } else {
      metaSet = new TreeSet<Marker>(new MarkerPositionComparator());

      this.metaData.put(metaData.getType(), metaSet);
    }

    metaSet.add(metaData);
  }

  public void setStopMarker(Marker trks) {
    stop = trks;
  }

  public Marker getStopMarker() {
    return stop;
  }

  public String getFileName() {
    return fileName;
  }

  public TrackHeader getHeader() {
    return header;
  }

  public double getLength() {
    return getStopMarker() != null ? -1 : getStopMarker().getValue();
  }

  @Override
  public String toString() {
    StringBuffer str = new StringBuffer("Track[");

    str.append("file: ").append(fileName).append(", header: ").append(header.toString())
        .append("]");

    return str.toString();
  }
}
