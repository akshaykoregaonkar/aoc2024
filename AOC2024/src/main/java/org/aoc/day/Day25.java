package org.aoc.day;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day25 {
    List<List<Integer>> locks = new ArrayList<>();
    List<List<Integer>> keys = new ArrayList<>();

    public Day25(){
        processFile("aoc2024/src/main/resources/day25.txt");
    }

    public long part1(){
        return locks.stream()
                .flatMap(lock -> keys.stream().filter(key -> !isInvalid(lock, key)))
                .count();
    }

    private boolean isInvalid(List<Integer> lock, List<Integer> key) {
        return IntStream.range(0, lock.size())
                .anyMatch(i -> lock.get(i) + key.get(i) > 5);
    }

    private void processFile(String filepath) {
        try{
            List<String> lines = Files.readAllLines(Paths.get(filepath));
            lines.add(""); //so processLine builds last lock/key
            List<String> grid = new ArrayList<>();
            lines.forEach(line -> processLine(grid, line));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processLine(List<String> grid, String line) {
        if(line.isEmpty()){
            buildLockOrKey(grid);
            grid.clear();
        }else{
            grid.add(line);
        }
    }

    private void buildLockOrKey(List<String> grid) {
        List<Integer> heights = IntStream.range(0, 5)
                                    .mapToObj(col -> getHeight(grid, col))
                                    .collect(Collectors.toList());

        if(grid.get(0).startsWith("#")){
            locks.add(heights);
        }else{
            keys.add(heights);
        }
    }

    private int getHeight(List<String> grid, int col) {
        return IntStream.range(1, 6)
                .map(row -> grid.get(row).charAt(col) == '#' ? 1 : 0)
                .sum();
    }
}
