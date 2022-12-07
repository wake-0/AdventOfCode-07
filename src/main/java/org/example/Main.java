package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException {
        Main main = new Main();
        main.run();
    }

    record SimpleFile(String name, Long size) {}

    final String path = "TODO";
    Map<String, List<SimpleFile>> filesInDir = new HashMap<>();

    void run() throws IOException {
        this.readFiles();
        this.printSize();
    }

    void readFiles() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(this.path + "input.txt"))) {

            String dir = "";
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals("$ cd /") || line.equals("$ ls") || line.startsWith("dir")) { continue; }

                if (line.equals("$ cd ..")) {
                    final int lastSlash = dir.lastIndexOf("/");
                    dir = dir.substring(0, lastSlash);
                    continue;
                }

                if (line.startsWith("$ cd")) {
                    final String directory = line.split(" ")[2];
                    dir = String.format("%s/%s", dir, directory);
                    continue;
                }

                try {
                    final String[] values = line.split(" ");
                    final String fileName = values[1];
                    final Long possibleSize = Long.parseLong(values[0]);
                    SimpleFile simpleFile = new SimpleFile(fileName, possibleSize);

                    String subDir = dir;
                    do {
                        final List<SimpleFile> files;
                        if (this.filesInDir.containsKey(subDir)) {
                            files = this.filesInDir.get(subDir);
                        } else {
                            files = new ArrayList<>();
                            this.filesInDir.put(subDir, files);
                        }
                        files.add(simpleFile);

                        int lastSlash = subDir.lastIndexOf("/");
                        if (lastSlash == -1) { break; }
                        subDir = subDir.substring(0, lastSlash);
                    } while(true);
                } catch (NumberFormatException ex) { /* no file */ }
            }
        }
    }

    void printSize() {
        Long sum = this.filesInDir.keySet()
                .stream().map(dir -> {
                    Long size = filesInDir.get(dir).stream().map(SimpleFile::size).reduce(Long::sum).get();
                    System.out.printf("%s: %s\n", dir, size);
                    return size;
                })
                .filter(size -> size <= 100000)
                .reduce(Long::sum).get();
        System.out.println(sum);
    }
}