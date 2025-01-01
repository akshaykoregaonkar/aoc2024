package org.aoc.day;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Day22 {
    public static final int SIMULATION_COUNT = 2000;
    private List<Long> secretNumbers = new ArrayList<>();

    public Day22(){
        processFile("aoc2024/src/main/resources/day22.txt");
    }

    private void processFile(String filepath) {
        try {
            secretNumbers = Files.lines(Paths.get(filepath))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long part1(){
        return secretNumbers.stream()
                .mapToLong(num -> getSecretNumber(num, 2000))
                .sum();
    }

    public long part2() {
        Map<Long, Map<List<Long>, Long>> sequenceToPriceMap = getSequenceToPriceMap();
        return calculateMaxBananas(sequenceToPriceMap);
    }

    private Map<Long, Map<List<Long>, Long>> getSequenceToPriceMap() {
        return secretNumbers.stream()
                .collect(Collectors.toMap(num -> num, this::getSequenceToPriceMap));
    }

    private long getSecretNumber(long number, int generation) {
        for (int i = 0; i < generation; i++) {
            number = getPsuedoNumber(number);
        }
        return number;
    }

    private long getPsuedoNumber(long number) {
        number = processNumber(number, number * 64);
        number = processNumber(number, number / 32);
        number = processNumber(number, number * 2048);
        return number;
    }

    private long processNumber(long number, long result) {
        return (number ^ result) % 16777216;
    }

    private Map<List<Long>, Long> getSequenceToPriceMap(long number) {
        Map<List<Long>, Long> sequenceToPriceMap = new HashMap<>();
        Deque<Long> sequence = new ArrayDeque<>(4);
        long previousPrice = number % 10;

        for (int i = 0; i < SIMULATION_COUNT; i++) {
            number = getPsuedoNumber(number);
            long newPrice = number % 10;
            long priceChange = newPrice - previousPrice;

            sequence.add(priceChange);
            // sliding window
            if (sequence.size() == 4) {
                sequenceToPriceMap.putIfAbsent(new ArrayList<>(sequence), newPrice);
                sequence.removeFirst();
            }
            previousPrice = newPrice;
        }

        return sequenceToPriceMap;
    }

    private long calculateMaxBananas(Map<Long, Map<List<Long>, Long>> sequences) {
        Map<List<Long>, Long> sequenceToBananasMap = new HashMap<>();

        for (Map<List<Long>, Long> sequenceMap : sequences.values()) {
            for (Map.Entry<List<Long>, Long> entry : sequenceMap.entrySet()) {
                // build a map of number of bananas we can get per sequence
                sequenceToBananasMap.merge(entry.getKey(), entry.getValue(), Long::sum);
            }
        }

        // return max bananas
        return sequenceToBananasMap.values().stream().mapToLong(Long::longValue).max().orElse(0);
    }
}
