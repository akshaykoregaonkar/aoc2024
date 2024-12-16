package org.aoc.day;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Day11 {
    List<Long> startingStones = new ArrayList<>();
    public Day11() {
        processFile("aoc2024/src/main/resources/day11.txt");
    }

    public long part1(){
        return transformation(25);
    }

    public long part2(){
        return transformation(75);
    }

    private long transformation(int blinks) {
        Map<Long, Long> stoneMap = new HashMap<>();

        for (Long stone : startingStones) {
            stoneMap.put(stone, 1L);
        }

        for (int i = 0; i < blinks; i++) {
            Map<Long, Long> blinkedStones = new HashMap<>();

            for (Map.Entry<Long, Long> entry : stoneMap.entrySet()) {
                List<Long> blinkedList = applyRule(entry.getKey());
                for (Long newStone : blinkedList) {
                    blinkedStones.put(newStone, blinkedStones.getOrDefault(newStone, 0L) + entry.getValue());
                }
            }

            stoneMap = blinkedStones;
        }

        return stoneMap
                .values()
                .stream()
                .mapToLong(Long::longValue)
                .sum();
    }

    private List<Long> applyRule(Long stone) {
        String stoneStr = String.valueOf(stone);
        if (stone == 0L) {
            return Arrays.asList(1L);
        } else if (stoneStr.length() % 2  == 0) {
            int mid = stoneStr.length() / 2;
            Long firstNum = Long.parseLong(stoneStr.substring(0, mid));
            Long secondNum = Long.parseLong(stoneStr.substring(mid));
            return Arrays.asList(firstNum, secondNum);
        } else {
            return Arrays.asList(stone * 2024);
        }
    }

    private void processFile(String filepath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String[] line = reader.readLine().split("\\s+");
            for (String numStr: line) {
                startingStones.add(Long.parseLong(numStr));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
