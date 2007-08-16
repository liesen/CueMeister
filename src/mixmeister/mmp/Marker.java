package mixmeister.mmp;



/**
 * A track marker represents some occurance on a track, typically a change in 
 * volume or a label.
 * 
 * @author johan
 */
public class Marker {    
    public static final int INTRO_RANGE              =        0x1;
    public static final int OUTRO_RANGE              =        0x2;
    public static final int FIRST_VOLUME_MARKER      =       0x10;
    public static final int INTRO_RANGE_BEGIN_VOLUME =       0x20;
    public static final int INTRO_RANGE_END_VOLUME   =       0x40;
    public static final int VOLUME_MARKER            =      0x100;
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
    
    
    protected int position;
    protected int value;
    protected int type;
    protected int volume;
    protected boolean isChanged;


    /**
     * @param position
     * @param value
     * @param type
     */
    public Marker(int type, int position, int value, int changed, int volume, int dummy1, int dummy2) {
        this(type, position, value, changed != 0, volume, dummy1, dummy2);
    }

    /**
     * @param position
     * @param value
     * @param type
     * @param isChanged
     */
    public Marker(int type, int position, int value, boolean isChanged, int volume, int dummy1, int dummy2) {
        this.position = position;
        this.value = value;
        this.type = type;
        this.isChanged = isChanged;
        this.volume = volume;
    }

    
    /**
     * @return Returns the position.
     */
    public int getPosition() {
        return position;
    }

    /**
     * @param position The position to set.
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * @return Returns the type.
     */
    public int getType() {
        return type;
    }

    /**
     * @param type The type to set.
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return Returns the value.
     */
    public int getValue() {
        return value;
    }

    /**
     * @param value The value to set.
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * @return the volume
     */
    public int getVolume() {
        return volume;
    }

    /**
     * @param volume the volume to set
     */
    public void setVolume(int volume) {
        this.volume = volume;
    }

    /**
     * @return the isChanged
     */
    public boolean isChanged() {
        return isChanged;
    }

    /**
     * @param isChanged the isChanged to set
     */
    public void setChanged(boolean isChanged) {
        this.isChanged = isChanged;
    }    
}
