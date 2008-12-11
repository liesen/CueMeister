import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.TableItem;

import cue.Index;
import cue.Track;

public class CueTrackModifier implements ICellModifier {
  private final Viewer viewer;

  /**
   * @param viewer
   */
  public CueTrackModifier(Viewer viewer) {
    this.viewer = viewer;
  }

  @Override
  public boolean canModify(Object element, String property) {
    return true;
  }

  @Override
  public Object getValue(Object element, String property) {
    Track track = (Track) element;

    if ("Performer".equals(property)) {
      return track.getPerformer();
    } else if ("Title".equals(property)) {
      return track.getTitle();
    } else if ("Index".equals(property)) {
      return track.getIndices().iterator().next().toString();
    }

    return null;
  }

  @Override
  public void modify(Object element, String property, Object value) {
    if (value == null || !(value instanceof String)) {
      return;
    }

    TableItem item = (TableItem) element;
    Track track = (Track) item.getData();
    String v = (String) value;

    if ("Performer".equals(property)) {
      track.setPerformer(v);
    } else if ("Title".equals(property)) {
      track.setTitle(v);
    } else if ("Index".equals(property)) {
      // Create index from string
      String[] parts = v.split(":");

      try {
        int minutes = parseInt(parts[0]);
        int seconds = (parts.length > 1) ? parseInt(parts[1]) : 0;
        int frames = (parts.length > 2) ? parseInt(parts[2]) : 0;

        track.getIndices().clear();
        track.getIndices().add(new Index(minutes, seconds, frames));
      } catch (NumberFormatException e) {

      }
    }

    viewer.refresh();
  }

  private static final int parseInt(String s) {
    if (s == null || s.length() == 0) {
      return 0;
    }

    return Integer.parseInt(s);
  }
}
