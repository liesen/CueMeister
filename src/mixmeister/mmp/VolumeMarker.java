package mixmeister.mmp;

public class VolumeMarker extends Marker {
    public VolumeMarker(int type, int position, int value, boolean isChanged) {
        super(type, position, value, isChanged);
    }

    public double getVolume() {
        return getValue() / 100.0;
    }
}
