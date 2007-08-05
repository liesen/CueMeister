package cue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class File {
    private String path;

    private Type type;

    public File(String path) {
        this(path, Type.MP3);
    }

    public File(String path, Type type) {
        this.path = path;
        this.type = type;
    }

    @Override
    public String toString() {
        return "FILE \"" + path + "\" " + type;
    }

    /**
     * File types
     * 
     * @author johan
     */
    public static class Type {
        public static final Type AIFF = new Type("AIFF");

        public static final Type BINARY = new Type("BINARY");

        public static final Type MOTOROLA = new Type("MOTOROLA");

        public static final Type MP3 = new Type("MP3");

        public static final Type WAVE = new Type("WAVE");

        /** Type */
        private final String type;

        private Type(String type) {
            this.type = type;
        }

        public static Set<Type> getTypes() {
            return new HashSet<Type>(Arrays.asList(AIFF, BINARY, MOTOROLA, MP3,
                    WAVE));
        }

        public static Type getType(String str) {
            Type test = new Type(str);

            for (Type t : getTypes()) {
                if (t.equals(test)) {
                    return t;
                }
            }

            return null;
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof Type) && type.equals(((Type) o).type);
        }

        @Override
        public int hashCode() {
            return type.hashCode();
        }

        @Override
        public String toString() {
            return type;
        }
    }
}
