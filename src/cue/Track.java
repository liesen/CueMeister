package cue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author johan
 */
public class Track extends CueItem implements Comparable<Track> {
    /** Track mode */
    private Mode mode;

    /** Number (0-99) */
    private int number;

    /** Indices */
    private List<Index> indices;

    /** Pre-gap index */
    private Index preGap;

    /** Post-gap index */
    private Index postGap;

    /** List of flags */
    private Set<Flag> flags;

    /** International Standard Recording Code */
    private String isrc;

    /**
     * @return
     */
    public String getIsrc() {
        return isrc;
    }

    /**
     * Set the track's "International Standard Recording Code" (ISRC).
     * The ISRC must be 12 characters in length. The first five
     * characters are alphanumeric, but the last seven are numeric only.
     * 
     * @param isrc
     */
    public void setIsrc(String isrc) throws IllegalArgumentException {
        if (!isrc.matches("^\\W{5}\\d{7}$")) {
            throw new IllegalArgumentException(
                    "The first five characters of the ISRC must be alphanumeric, and the last seven numeric.");
        }

        this.isrc = isrc.toUpperCase();
    }

    /**
     * 
     */
    public Track() {
        this(1, Mode.AUDIO);
    }

    /**
     * @param number
     */
    public Track(int number) {
        this(number, Mode.AUDIO);
    }

    /**
     * @param number
     * @param mode
     */
    public Track(int trackNumber, Mode mode) throws IllegalArgumentException {
        super(null);

        if (trackNumber > 99) {
            throw new IllegalArgumentException(
                    "Track number can not be higher than 99.");
        } else if (trackNumber < 1) {
            throw new IllegalArgumentException(
                    "Track number must be equal to, or greater than, 1.");
        }

        this.number = trackNumber;
        this.mode = mode;

        indices = new ArrayList<Index>(1);
        flags = new HashSet<Flag>();
    }

    public Track(int number, Index index, Mode mode)
            throws IllegalArgumentException {
        this(number, mode);

        indices.add(index);
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public List<Index> getIndices() {
        return indices;
    }

    public void setIndices(List<Index> indices) {
        this.indices = indices;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Index getPostGapIndex() {
        return postGap;
    }

    public void setPostGapIndex(Index postGap) {
        this.postGap = postGap;
    }

    public Index getPreGapIndex() {
        return preGap;
    }

    public void setPreGapIndex(Index preGap) {
        this.preGap = preGap;
    }

    public Set<Flag> getFlags() {
        return flags;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Track) {
            return ((Track) o).number == number;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return String.format("%02d", number).hashCode();
    }

    @Override
    public int compareTo(Track o) {
        if (number != o.number) {
            return number - o.number;
        }

        List<Index> i1 = getIndices(), i2 = o.getIndices();

        if (i1.size() == 0) {
            return -1;
        } else if (i2.size() == 0) {
            return 1;
        }

        return i1.get(0).compareTo(i2.get(0));
    }
}
