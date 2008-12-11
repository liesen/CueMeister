import org.eclipse.jface.viewers.ICellEditorValidator;


public class CueIndexValidator implements ICellEditorValidator {
  public static final String FORMAT_ERROR_MESSAGE = "Must be in mm:ss:ff format";

  @Override
  public String isValid(Object value) {
    if (!(value instanceof String) || ((String) value).length() == 0) {
      return FORMAT_ERROR_MESSAGE;
    }

    String[] parts = ((String) value).split(":");

    if (parts.length > 3) {
      return FORMAT_ERROR_MESSAGE;
    }

    for (String s : parts) {
      if (s.length() == 0) {
        continue;
      }

      try {
        Integer.parseInt(s);
      } catch (NumberFormatException e) {
        return e.getMessage();
      }
    }

    return null;
  }
}
