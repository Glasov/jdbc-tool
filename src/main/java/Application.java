import serializer.ObjectDeserializer;
import serializer.SerializedNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class Application {
    public static void main(String[] args) throws NoSuchMethodException, NoSuchFieldException {
        var d = new ObjectDeserializer(AClass.class);
        System.out.println(AClass.class.getField("integer") == AClass.class.getField("integer"));
        List<AClass> elements = get(1);
        AtomicInteger i = new AtomicInteger();
        long startTime = System.nanoTime();
        elements.stream().forEach(s -> {
            SerializedNode node = d.serialize(AClass.class, s);
            System.out.println(d.deserialize(AClass.class, node));
            System.out.print(i.get() + ": ");
            System.out.print("{");
            while (node != null) {
                        System.out.print((node.value() == null ? "(in null) " : "") + "'" + node.value() + "'" + (node.hasNext() ? ", " : ""));
                        node = node.next();
                    }
            System.out.println("}");
                    i.getAndIncrement();
                });
        long elapsedTime = System.nanoTime() - startTime;
        System.out.println("Total execution time to create 1000K objects in Java in millis: "
                + elapsedTime/1000000);
        boolean identical =
                (Object.class.getMethod("toString") == Object.class.getMethod("toString"));
        System.out.println(identical);
    }

    public static class AClass {
        public final Optional<Integer> integer;

        public AClass(Optional<Integer> integer) {
            this.integer = integer;
        }
    }

    public record FieldField(int anInt, byte aByte, char aChar, String aString) {
    }

    public static List<AClass> get(int n) {
        List<AClass> list = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            list.add(new AClass(i % 2 == 0 ? Optional.empty() : Optional.of(123)));
        }

        return list;
    }
}
