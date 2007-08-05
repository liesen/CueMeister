package cue;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public class TrackIndexComparator implements Comparator<Track>, Serializable {
    /** */
    private static final long serialVersionUID = -206931938413172940L;

    @Override
    public int compare(Track o1, Track o2) {
        List<Index> i1 = o1.getIndices(), i2 = o2.getIndices();

        if (i1.size() == 0) {
            return -1;
        } else if (i2.size() == 0) {
            return 1;
        }

        return i1.get(0).compareTo(i2.get(0));
    }

}
