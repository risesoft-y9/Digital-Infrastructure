package net.risesoft.id.impl;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

import net.risesoft.id.IdGenerator;

public class TimeBasedUuidGenerator implements IdGenerator {

    // different ProcessEngines on the same classloader share one generator.
    protected static volatile TimeBasedGenerator timeBasedGenerator;

    public TimeBasedUuidGenerator() {
        ensureGeneratorInitialized();
    }

    protected void ensureGeneratorInitialized() {
        if (timeBasedGenerator == null) {
            synchronized (TimeBasedUuidGenerator.class) {
                if (timeBasedGenerator == null) {
                    timeBasedGenerator = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
                }
            }
        }
    }

    @Override
    public String getNextId() {
        return timeBasedGenerator.generate().toString();
    }

}
