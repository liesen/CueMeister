package cuemeister;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.AbstractTagFrame;
import org.jaudiotagger.tag.id3.ID3v24Frames;
import org.jaudiotagger.tag.id3.framebody.AbstractFrameBodyTextInfo;

/**
 * Utility functions for dealing with jaudiotaggers ID3 functions
 * 
 * @author johan
 */
public abstract class ID3Helper {
  /** Identifiers that might correspond to a track's artist */
  public static final String[] ARTIST_FRAME_IDENTIFIERS =
      {ID3v24Frames.FRAME_ID_ARTIST, ID3v24Frames.FRAME_ID_COMPOSER,
          ID3v24Frames.FRAME_ID_ORIGARTIST, ID3v24Frames.FRAME_ID_LYRICIST,
          ID3v24Frames.FRAME_ID_ORIG_LYRICIST};

  /** Identifiers that might correspond to a track's title */
  public static final String[] TITLE_FRAME_IDENTIFIERS =
      {ID3v24Frames.FRAME_ID_TITLE, ID3v24Frames.FRAME_ID_ORIG_TITLE};

  /**
   * Returns the first value (body) found in a ID3v2 tag
   * 
   * @param tag
   * @param identifiers
   * @return
   */
  public static String extractTagValue(AbstractID3v2Tag tag, String[] identifiers) {
    for (String identifier : identifiers) {
      if (tag.hasFrameAndBody(identifier)) {
        AbstractTagFrame frame = (AbstractTagFrame) tag.getFrame(identifier);
        AbstractFrameBodyTextInfo body = (AbstractFrameBodyTextInfo) frame.getBody();

        String value = body.getText();

        if (value != null && value.trim().length() > 0) {
          return value.trim();
        }
      }
    }

    return null;
  }

  /**
   * Returns the artist of an MP3 file
   * 
   * @param mp3
   * @return
   */
  public static String getArtist(MP3File mp3) {
    String artist = null;

    if (mp3.hasID3v1Tag()) {
      String s = mp3.getID3v1Tag().getFirstArtist();

      if (s != null) {
        artist = s;
      }
    }

    if (mp3.hasID3v2Tag()) {
      String s = getArtist(mp3.getID3v2TagAsv24());

      if (s != null) {
        artist = s;
      }
    }

    return artist;
  }

  /**
   * Returns the title of an MP3 file
   * 
   * @param mp3
   * @return
   */
  public static String getTitle(MP3File mp3) {
    String title = null;

    if (mp3.hasID3v1Tag()) {
      String s = mp3.getID3v1Tag().getFirstTitle();

      if (s != null) {
        title = s;
      }
    }

    if (mp3.hasID3v2Tag()) {
      String s = getTitle(mp3.getID3v2TagAsv24());

      if (s != null) {
        title = s;
      }
    }

    return title;
  }

  /**
   * @param tag
   * @return
   */
  public static String getArtist(AbstractID3v2Tag tag) {
    return extractTagValue(tag, ARTIST_FRAME_IDENTIFIERS);
  }

  /**
   * @param tag
   * @return
   */
  public static String getTitle(AbstractID3v2Tag tag) {
    return extractTagValue(tag, TITLE_FRAME_IDENTIFIERS);
  }
}
