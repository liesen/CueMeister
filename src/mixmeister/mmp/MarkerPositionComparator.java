package mixmeister.mmp;

import java.util.Comparator;

/**
 * Comparator for marker position's
 *
 * @author johan
 */
public class MarkerPositionComparator implements Comparator<Marker> {
    public int compare(Marker o1, Marker o2) {
        return o1.getPosition() - o2.getPosition();
    }
}
