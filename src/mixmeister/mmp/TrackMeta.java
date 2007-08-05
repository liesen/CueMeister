package mixmeister.mmp;

public class TrackMeta extends Marker {    
    public static final int INTRO_RANGE              =        0x1;
    public static final int OUTRO_RANGE              =        0x2;
    public static final int FIRST_VOLUME_MARKER      =       0x10;
    public static final int INTRO_RANGE_BEGIN_VOLUME =       0x20;
    public static final int INTRO_RANGE_END_VOLUME   =       0x40;
    public static final int OUTRO_RANGE_BEGIN_VOLUME =      0x200;
    public static final int OUTRO_RANGE_END_VOLUME   =      0x400;
    public static final int LAST_VOLUME_MARKER       =     0x1000;
    public static final int USER_VOLUME_MARKER       =     0x8000;
    public static final int USER_TREBLE_MARKER       =    0x10000;
    public static final int USER_BASS_MARKER         =    0x40000;
    public static final int INTRO_RANGE_BEGIN_TEMPO  =   0x100000;
    public static final int INTRO_RANGE_END_TEMPO    =   0x200000;
    public static final int OUTRO_RANGE_BEGIN_TEMPO  =   0x400000;
    public static final int OUTRO_RANGE_END_TEMPO    =   0x800000;
    public static final int USER_LABEL_MARKER        =  0x8000000;
    public static final int MEASURE_MARKER           = 0x10000000;
    
    /**
     * @param type
     * @param position
     * @param changeFlag
     * @param volume
     */
    public TrackMeta(int type, int position, int changeFlag, int value) {
        super(type, position, value, changeFlag != 0);
    }
}
