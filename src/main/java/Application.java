import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import serializer.ObjectDeserializer;

public class Application {
    public static class ASD {
        private final Optional<FieldField> field;

        public ASD(Optional<FieldField> field) {
            this.field = field;
        }

        @Override
        public String toString() {
            return "ASD{" +
                    "field=" + field +
                    '}';
        }
    }

    public static void main(String[] args) throws NoSuchMethodException {
        var d1 = new ObjectDeserializer(ASD.class);
        var node = d1.serialize(ASD.class, new ASD(Optional.of(new FieldField(516, 'C', "a string!"))));
        System.out.println(node);
        System.out.println(d1.deserialize(ASD.class, node));
    }

    public static class AClass {
        public final Optional<FieldField> optionalField;
        public final int pInt;
        private final Integer wInteger;
        private final boolean pBoolean;
        private final Boolean wBoolean;
        private final byte pByte;
        private final Byte wByte;
        private final char pChar;
        private final Character wChar;

        public AClass(Optional<FieldField> optionalField, int pInt, Integer wInteger, boolean pBoolean, Boolean wBoolean, byte pByte, Byte wByte, char pChar, Character wChar) {
            this.optionalField = optionalField;
            this.pInt = pInt;
            this.wInteger = wInteger;
            this.pBoolean = pBoolean;
            this.wBoolean = wBoolean;
            this.pByte = pByte;
            this.wByte = wByte;
            this.pChar = pChar;
            this.wChar = wChar;
        }

        @Override
        public String toString() {
            return "AClass{" +
                    "optionalField=" + optionalField +
                    ", pInt=" + pInt +
                    ", wInteger=" + wInteger +
                    ", pBoolean=" + pBoolean +
                    ", wBoolean=" + wBoolean +
                    ", pByte=" + pByte +
                    ", wByte=" + wByte +
                    ", pChar=" + pChar +
                    ", wChar=" + wChar +
                    '}';
        }
    }

    public static class FieldField {
        private final Integer anInt;
        private final char aChar;
        private final String aString;

        public FieldField(Integer anInt, char aChar, String aString) {
            this.anInt = anInt;
            this.aChar = aChar;
            this.aString = aString;
        }

        @Override
        public String toString() {
            return "FieldField{" +
                    "anInt=" + anInt +
                    ", aChar=" + aChar +
                    ", aString='" + aString + '\'' +
                    '}';
        }
    }

    public static List<AClass> get(int n) {
        List<AClass> list = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            list.add(new AClass((i % 2 == 0 ? Optional.empty() : Optional.of(new FieldField(123, 'c', "this is a string"))), 42,
            24, false, true, Byte.parseByte("41"), Byte.parseByte("14"),'a', 'A'));
        }

        return list;
    }
}
