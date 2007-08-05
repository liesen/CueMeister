import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import cue.Track;


public class CueSheetLabelProvider implements ITableLabelProvider {
    @Override
    public void addListener(ILabelProviderListener listener) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isLabelProperty(Object element, String property) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
        Track track = (Track) element;
        
        switch (columnIndex) {
        case 0:
            return track.getPerformer();
            
        case 1:
            return track.getTitle();
            
        case 2:
            return (track.getIndices().size() > 0) ? track.getIndices().get(0).toString() : "";
        }
        
        return null;
    }

}
