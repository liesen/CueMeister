package mixmeister.mmp;

public class TempoMarker extends Marker {
    public TempoMarker(int type, int position, int value) {
        super(type, position, value);
    }
    
    public double getBPM() {
        return getValue() / 100.0;
    }
}
