import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import cue.CueSheet;


public class CueSheetContentProvider implements IStructuredContentProvider {
    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return ((CueSheet) inputElement).getTracks().toArray();
    }

}
