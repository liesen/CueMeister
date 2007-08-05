package mixmeister.mmp;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator for marker position's
 *
 * @author johan
 */
public class MarkerPositionComparator implements Comparator<Marker>, Serializable {
    /** */
    private static final long serialVersionUID = -8370545748640985297L;

    @Override
    public int compare(Marker o1, Marker o2) {
        return o1.getPosition() - o2.getPosition();
    }
}
