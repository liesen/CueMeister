package mixmeister.mmp;



/**
 * A track marker represents some occurance on a track, typically a change in 
 * volume or a label.
 * 
 * @author johan
 */
public class Marker {    
    protected int position;
    protected int value;
    protected int type;
    protected boolean isChanged;


    /**
     * @param position
     * @param value
     * @param type
     */
    public Marker(int type, int position, int value) {
        this(type, position, value, false);
    }

    /**
     * @param position
     * @param value
     * @param type
     * @param isChanged
     */
    public Marker(int type, int position, int value, boolean isChanged) {
        this.position = position;
        this.value = value;
        this.type = type;
        this.isChanged = isChanged;
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
}
