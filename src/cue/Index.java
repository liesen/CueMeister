package cue;

/**
 *
 * @author johan
 *
 */
public class Index implements Comparable<Index> {
    public final static int FRAMES_PER_SECOND = 75;
    
    private int minutes;

    private int seconds;

    private int frames;

    /**
     * Creates an index: 00:00:00
     */
    public Index() {
        this(0, 0, 0);
    }

    /**
     * Creates an index based on the number of seconds.
     * 
     * @param seconds
     */
    public Index(int seconds) {
        this(0, seconds, 0);
    }

    /**
     * Creates an index mm:ss:00.
     * 
     * @param minutes
     * @param seconds
     */
    public Index(int minutes, int seconds) {
        this(minutes, seconds, 0);
    }

    /**
     * Automatically converts frames to seconds and seconds to minutes.
     * 
     * Frame: 1/75 second.
     * 
     * @param minutes
     * @param seconds
     * @param frames
     */
    public Index(int minutes, int seconds, int frames) {
        this.frames = frames % FRAMES_PER_SECOND;
        seconds += frames / FRAMES_PER_SECOND;
        this.seconds = seconds % 60;
        this.minutes = minutes + seconds / 60;
    }
    
    /**
     * @param seconds
     */
    public Index(double seconds) {
        this(0, (int) seconds, 0);
        
        this.frames = (int) ((seconds - (int) seconds) * FRAMES_PER_SECOND);
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d", minutes, seconds, frames);
    }

    @Override
    public int compareTo(Index o) {
        if (minutes != o.minutes) {
            return minutes - o.minutes;
        }

        if (seconds != o.seconds) {
            return seconds - o.seconds;
        }

        return frames - o.frames;
    }
}
