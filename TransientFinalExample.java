///usr/bin/env jbang "$0" "$@" ; exit $?
//RUNTIME_OPTIONS -ea
//DEPS org.projectlombok:lombok:LATEST
//DEPS com.flowlogix:flowlogix-jee:LATEST

// uncomment the below line when using JDK < 21
//COMPILE_OPTIONS -proc:full

import java.io.*;
import com.flowlogix.util.*;
import lombok.*;
import lombok.extern.java.*;

@Log
@Builder(toBuilder = true)
public class TransientFinalExample implements Serializable {
    static class NonSerializableField {
        final Integer intValue;

        public NonSerializableField(Integer intValue) {
            this.intValue = intValue;
        }
    }
    final Integer intValue = 5;
    final transient NonSerializableField nonSerializableField = new NonSerializableField(6);

    Object readResolve() {
        // return new TransientFinalExample();
        return toBuilder().build();
    }

    public static void main(String... args) throws IOException, ClassNotFoundException {
        log.info("Checking Serialization ...");
        var original = new TransientFinalExample();
        TransientFinalExample deserialized = SerializeTester.serializeAndDeserialize(original);

        assert original.nonSerializableField.intValue.equals(deserialized.nonSerializableField.intValue);
        log.info("Done");
    }
}
