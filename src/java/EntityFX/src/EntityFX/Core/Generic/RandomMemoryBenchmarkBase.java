package EntityFX.Core.Generic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import javax.xml.crypto.dsig.keyinfo.KeyValue;

import EntityFX.Core.Writer;

public abstract class RandomMemoryBenchmarkBase<TResult> extends BenchmarkBase<TResult> {

    public RandomMemoryBenchmarkBase(Writer writer, boolean printToConsole) throws FileNotFoundException {
        super(writer, printToConsole);
        Iterrations = 500000;
        Ratio = 2;
    }

    public MemoryBenchmarkResult BenchRandomMemory() throws IOException {
        SimpleEntry<Double, Integer>  int4k = MeasureArrayRandomRead(1024);
        output.writeLine("Random int 4k: %.2f MB/s", int4k.getKey());
        SimpleEntry<Double, Integer>  int512k = MeasureArrayRandomRead(131072);
        output.writeLine("Random int 512k: %.2f MB/s", int512k.getKey());
        SimpleEntry<Double, Integer>  int8m = MeasureArrayRandomRead(2097152);
        output.writeLine("Random int 8M: %.2f MB/s", int8m.getKey());

        SimpleEntry<Double, Long>  long4k = MeasureArrayRandomLongRead(1024);
        output.writeLine("Random long 4k: %.2f MB/s", long4k.getKey());
        SimpleEntry<Double, Long> long512k = MeasureArrayRandomLongRead(131072);
        output.writeLine("Random long 512k: %.2f MB/s", long512k.getKey());
        SimpleEntry<Double, Long> long8m = MeasureArrayRandomLongRead(2097152);
        output.writeLine("Random long 8M: %.2f MB/s", long8m.getKey());

        double avg = Arrays.stream(new double[] { int4k.getKey(), int512k.getKey(), int8m.getKey(), 
                long4k.getKey(), long512k.getKey(), long8m.getKey(), }).average().orElse(-1);

        output.writeLine("Average: %.2f MB/s", avg);

        return new MemoryBenchmarkResult() {
            {
                Average = avg;
                Output = output.Output;
            }
        };
    }

    protected SimpleEntry<Double, Integer> MeasureArrayRandomRead(int size) {
        int I = 0;
        int[] array = randomIntArray(size, Integer.MAX_VALUE);

        int end = array.length - 1;
        int[] indexes = randomIntArray(end, end);
        int k1 = (size / 1024) == 0 ? 1 : (size / 1024);
        int iterInternal = Iterrations / k1;
        for (int idx = 0; idx < end; idx++) {
            I = array[idx];
        }
        long start = System.currentTimeMillis();
        for (int i = 0; i < iterInternal; i++) {
            for (int idx : indexes) {
                I = array[idx];
            }
        }
        long elapsed = System.currentTimeMillis() - start;

        return new SimpleEntry<Double, Integer>(
                (double) iterInternal * array.length * 4 / (elapsed / 1000.0) / 1024 / 1024, I);
    }

    protected SimpleEntry<Double, Long> MeasureArrayRandomLongRead(int size) {
        long L = 0;
        long[] array = randomLongArray(size);

        int end = array.length - 1;
        int[] indexes = randomIntArray(end, end);
        int k1 = (size / 1024) == 0 ? 1 : (size / 1024);
        int iterInternal = Iterrations / k1;
        for (int idx = 0; idx < end; idx++) {
            L = array[idx];
        }
        long start = System.currentTimeMillis();
        for (int i = 0; i < iterInternal; i++) {
            for (int idx : indexes) {
                L = array[idx];
            }
        }
        long elapsed = System.currentTimeMillis() - start;

        return new SimpleEntry<Double, Long>(
                (double) iterInternal * array.length * 8 / (elapsed / 1000.0) / 1024 / 1024, L);
    }

}