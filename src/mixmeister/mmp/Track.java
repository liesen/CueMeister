package mixmeister.mmp;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A Mixmeister playlist item/track
 * 
 * @author johan
 */
public class Track {
    /** Track header */
    private TrackHeader header; // TRKH

    /** Path to file */
    private String fileName; // TRKF

    /** Meta data */
    private Map<Integer, Set<TrackMeta>> metaData; // TKLY

    private TrackMeta stop;

    /**
     * 
     * 
     * @param header
     * @param fileName
     */
    public Track(TrackHeader header, String fileName) {
        this(header, fileName, new LinkedList<TrackMeta>());
    }

    /**
     * 
     * 
     * @param header
     * @param fileName
     * @param metaData
     */
    public Track(TrackHeader header, String fileName,
            Collection<TrackMeta> metaData) {
        this.header = header;
        this.fileName = fileName;
        this.metaData = new HashMap<Integer, Set<TrackMeta>>();

        for (TrackMeta meta : metaData) {
            addMetaData(meta);
        }
    }

    /**
     * @return track's meta data
     */
    public Collection<Set<TrackMeta>> getMetaData() {
        return metaData.values();
    }

    /**
     * @param type
     * @return markers of given type if any; null otherwise
     */
    public Set<TrackMeta> getMarkers(int type) {
        return metaData.get(type);
    }

    /**
     * @param metaData
     */
    public void addMetaData(TrackMeta metaData) {
        Set<TrackMeta> metaSet;

        if (this.metaData.containsKey(metaData.getType())) {
            metaSet = this.metaData.get(metaData.getType());
        } else {
            metaSet = new TreeSet<TrackMeta>(new MarkerPositionComparator());

            this.metaData.put(metaData.getType(), metaSet);
        }

        metaSet.add(metaData);
    }

    public void setStopMarker(TrackMeta trks) {
        stop = trks;
    }

    public TrackMeta getStopMarker() {
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

        str.append("file: ").append(fileName).append(", header: ").append(
                header.toString()).append("]");

        return str.toString();
    }
}
