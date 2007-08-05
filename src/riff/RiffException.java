package riff;

import java.io.IOException;

@SuppressWarnings("serial")
public class RiffException extends IOException {
    public RiffException(String message) {
        super(message);
    }
}
