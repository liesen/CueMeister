package cue;

import java.util.Comparator;

public class TrackNumberComparator implements Comparator<Track> {
    public int compare(Track o1, Track o2) {
        return o1.getNumber() - o2.getNumber();
    }
}
