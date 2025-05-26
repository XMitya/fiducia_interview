package ru.fiducia.interview;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;

public class CountParallelProcesses {
   /*
there are logs from a service, and for each request we have a record like:
getData <unix start time> <unix end time>
find maximum number of parallel requests.

getData 1742129969123 1742139969123
getData 1742229970122 1742239969122
getData 1742129971123 1742229972122


                  -----
       ----------  -------         --
      ----    -------    ------   -----
   ------------------------ ---------------

-----------------------------t------------------------->
*/

    public static void main(String[] args) throws IOException {
        try (final Log log = new Log("process.log")) {
            ParallelProcessCounter counter = new InMemoryParallelProcessCounter();
            final int parallelEvents = counter.maxParallelProcesses(log.events()
                    // Sorted for small unordered streams.
                    // For large files, entries must be sorted beforehand, using 'sort' command, for instance,
                    // or LargeFileRowsSort.
                    .sorted());
            System.out.println(parallelEvents);
        }
    }

    private interface ParallelProcessCounter {
        int maxParallelProcesses(Stream<LogEvent> logEvents);
    }

    private static class InMemoryParallelProcessCounter implements ParallelProcessCounter {
        @SuppressWarnings("ResultOfMethodCallIgnored")
        @Override
        public int maxParallelProcesses(Stream<LogEvent> logEvents) {
            int[] max = {0};
            logEvents.mapToInt(e -> e.startEvent() ? 1 : -1)
                    // Not the best way to use streams, but OK in lack of foldLeft() operation.
                    .reduce(0, (a, b) -> {
                        final int currentParallel = a + b;
                        max[0] = Math.max(currentParallel, max[0]);
                        return currentParallel;
                    });

            return max[0];
        }
    }

    private static class Log implements Closeable {
        private final String logFileName;
        private BufferedReader reader;

        private Log(String logFileName) {
            this.logFileName = logFileName;
        }

        public Stream<LogEvent> events() {
            if (reader != null) {
                throw new IllegalStateException("Already initialized with " + logFileName);
            }

            final InputStream in = Log.class.getClassLoader().getResourceAsStream(logFileName);
            if (in == null) {
                throw new IllegalArgumentException("Log file not found: " + logFileName);
            }

            reader = new BufferedReader(new InputStreamReader(in));
            return reader.lines()
                    .flatMap(LogEvent::fromLine);
        }


        @Override
        public void close() throws IOException {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private record LogEvent(boolean startEvent, long timestamp) implements Comparable<LogEvent> {
        @Override
        public int compareTo(LogEvent o) {
            return Long.compare(timestamp, o.timestamp);
        }

        public static Stream<LogEvent> fromLine(String line) {
            final String[] values = line.split(" ");
            if (values.length != 3) {
                throw new IllegalArgumentException("Invalid log line: " + line);
            }

            return Stream.of(
                    new LogEvent(true, Long.parseLong(values[1])),
                    new LogEvent(false, Long.parseLong(values[2]))
            );
        }
    }
}
