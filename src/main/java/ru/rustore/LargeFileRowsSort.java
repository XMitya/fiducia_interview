package ru.rustore;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static ru.rustore.LargeFileRowsSort.Cursor.CURSOR_COMPARATOR;

/**
 * Dumb implementation of sorting rows in a large file. May be more effective if
 * instead of streams, use arrays and loops.
 */
public class LargeFileRowsSort {
    private static final int DEFAULT_BATCH_SIZE = 1000;

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: java LargeFileRowsSort <input file> <output file> [rows per block]");
            System.exit(1);
        }

        final String inputFile = args[0];
        final String outputFile = args[1];
        final int rowsPerBlock = args.length > 2 ? Integer.parseInt(args[2]) : DEFAULT_BATCH_SIZE;

        sortFile(Path.of(inputFile), Path.of(outputFile), rowsPerBlock);
    }

    public static void sortFile(Path inputFile, Path outputFile, int batchSize) throws IOException {
        final List<Path> tempFiles = new ArrayList<>();
        final List<String> batch = new ArrayList<>(batchSize);

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                batch.add(line);
                if (batch.size() == batchSize) {
                    sortAndWriteTempFile(batch, tempFiles);

                    batch.clear();
                }
            }
            if (!batch.isEmpty()) {
                sortAndWriteTempFile(batch, tempFiles);
            }
        }

        mergeSortedFiles(outputFile, tempFiles);

        // Clean up temp files
        for (Path tempFile : tempFiles) {
            Files.delete(tempFile);
        }
    }

    private static void sortAndWriteTempFile(List<String> batch,
                                             List<Path> tempFiles) throws IOException {
        batch.sort(String.CASE_INSENSITIVE_ORDER);
        final Path tempFilePath = File.createTempFile("temp", ".txt").toPath();
        tempFiles.add(tempFilePath);
        try (final BufferedWriter bw = new BufferedWriter(new FileWriter(tempFilePath.toFile()))) {
            for (String line : batch) {
                writeLine(bw, line);
            }
        }
    }

    private static void mergeSortedFiles(Path outputFile,
                                         List<Path> tempFiles) throws IOException {
        List<Cursor> cursors = tempFiles.stream()
                .map(file -> {
                    try {
                        return new BufferedReader(new FileReader(file.toFile()));
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(Cursor::new)
                .toList();

        try (final BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile.toFile()))) {
            while (!cursors.isEmpty()) {
                // Write next ordered line.
                cursors.stream()
                        .min(CURSOR_COMPARATOR)
                        .ifPresent(cursor -> {
                            writeLine(bw, cursor.peekLine());
                            cursor.moveCursor();
                        });
                // Close read files.
                cursors.stream()
                        .filter(Cursor::completed)
                        .forEach(Cursor::quietClose);
                // Remove read files.
                cursors = cursors.stream()
                        .filter(cursor -> !cursor.completed())
                        .toList();
            }
        }
    }

    private static void writeLine(BufferedWriter bw, String line) {
        try {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Cursor implements Closeable, Comparable<Cursor> {
        public static final Comparator<Cursor> CURSOR_COMPARATOR = Comparator.comparing(Cursor::peekLine, String.CASE_INSENSITIVE_ORDER);

        private final BufferedReader reader;
        private String line;

        private Cursor(BufferedReader reader) {
            this.reader = reader;
        }

        public String peekLine() {
            if (line == null) {
                try {
                    line = reader.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return line;
        }

        public void moveCursor() {
            line = null;
            peekLine();
        }

        public boolean completed() {
            return line == null;
        }

        @Override
        public void close() throws IOException {
            reader.close();
        }

        public void quietClose() {
            try {
                reader.close();
            } catch (IOException e) {
                // ignore
            }
        }

        @Override
        public int compareTo(Cursor o) {
            return CURSOR_COMPARATOR.compare(this, o);
        }
    }
}