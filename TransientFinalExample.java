///usr/bin/env jbang --ea "$0" "$@" ; exit $?
//DEPS org.projectlombok:lombok:LATEST


import java.io.*;
import lombok.*;

@Builder(toBuilder = true)
public class TransientFinalExample implements Serializable {
    static class NonSerializableFiled {
        final Integer intValue;

        public NonSerializableFiled(Integer intValue) {
            this.intValue = intValue;
        }
    }
    final Integer intValue;
    final transient NonSerializableFiled nonSerializableField;

    public TransientFinalExample(Integer intValue) {
        this(intValue, null);
    }

    public TransientFinalExample(Integer intValue, NonSerializableFiled nonSerializableField) {
        this.intValue = intValue;
        this.nonSerializableField = nonSerializableField == null ? new NonSerializableFiled(intValue + 1) : nonSerializableField;
    }

    Object readResolve() {
        // return new TransientFinalExample(intValue);
        return toBuilder().build();
    }

    public static void main(String... args) throws IOException, ClassNotFoundException {
        new Example().checkSerialization(5);
    }

    // tests and examples below
    public static class Example {
        public void checkSerialization(int value) throws IOException, ClassNotFoundException {
            var original = new TransientFinalExample(value);
            var deserialized = serializeAndDeserialize(original);

            assert original.nonSerializableField.intValue.equals(deserialized.nonSerializableField.intValue);
        }

        public TransientFinalExample serializeAndDeserialize(TransientFinalExample original) throws IOException, ClassNotFoundException {
            // serialize
            var byteArrayOutputStream = new ByteArrayOutputStream();
            try (var outputStream = new ObjectOutputStream(byteArrayOutputStream)) {
                outputStream.writeObject(original);
            }

            // deserialize
            try (var inputStream = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()))) {
                return (TransientFinalExample) inputStream.readObject();
            }
        }
    }
}
