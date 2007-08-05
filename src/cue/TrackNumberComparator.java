package cue;

import java.io.Serializable;
import java.util.Comparator;

public class TrackNumberComparator implements Comparator<Track>, Serializable {
    /** */
    private static final long serialVersionUID = 1258556463379532205L;

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(Track o1, Track o2) {
        return o1.getNumber() - o2.getNumber();
    }
}
