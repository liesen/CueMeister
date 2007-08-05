package cue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author johan
 */
public class Mode {
    public static final Mode AUDIO = new Mode("AUDIO");

    public static final Mode CDG = new Mode("CDG");

    public static final Mode MODE1_2048 = new Mode("MODE1/2048");

    public static final Mode MODE1_2352 = new Mode("MODE1/2352");

    public static final Mode MODE2_2336 = new Mode("MODE2/2336");

    public static final Mode MODE2_2352 = new Mode("MODE2/2352");

    public static final Mode CDI_2336 = new Mode("CDI/2336");

    public static final Mode CDI_2352 = new Mode("CDI/2352");

    /** Mode */
    private final String mode;

    private Mode(String mode) {
        this.mode = mode;
    }

    /**
     * @return list of all modes
     */
    public static Set<Mode> getModes() {
        return new HashSet<Mode>(Arrays.asList(AUDIO, CDG, MODE1_2048,
                MODE1_2352, MODE2_2336, MODE2_2352, CDI_2336, CDI_2352));
    }

    /**
     * @param str
     * @return
     */
    public static Mode getMode(String str) {
        Mode test = new Mode(str);

        for (Mode m : getModes()) {
            if (m.equals(test)) {
                return m;
            }
        }

        return null;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Mode) && mode.equals(((Mode) o).mode);
    }

    @Override
    public int hashCode() {
        return mode.hashCode();
    }

    @Override
    public String toString() {
        return mode;
    }
}