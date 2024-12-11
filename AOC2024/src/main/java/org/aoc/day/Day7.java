package org.aoc.day;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Day7 {

    Map<Long, List<Long>> puzzleInput = new HashMap<>();

    public Day7(){
        processFile("aoc2024/src/main/resources/day7.txt");
    }

    private void processFile(String filepath) {
        try(BufferedReader reader = new BufferedReader(new FileReader(filepath))){
            String line;
            while((line = reader.readLine()) != null){
                String[] input = line.split(":");
                long target = Long.parseLong(input[0]);
                List<Long> inputNumbers = Arrays.stream(input[1].trim().split(" "))
                                                    .map(Long::parseLong)
                                                    .collect(Collectors.toList());
                puzzleInput.put(target, inputNumbers);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long part1(){
        long total = 0;
        for(Map.Entry<Long, List<Long>> entry : puzzleInput.entrySet()){
            total += isValidEquation(entry, false) ? entry.getKey() : 0;
        }
        return total;
    }

    public long part2(){
        long total = 0;
        for(Map.Entry<Long, List<Long>> entry : puzzleInput.entrySet()){
            total += isValidEquation(entry, true) ? entry.getKey() : 0;
        }
        return total;
    }

    private boolean isValidEquation(Map.Entry<Long, List<Long>> entry, boolean concat) {
        AtomicBoolean found = new AtomicBoolean(false);
        long target = entry.getKey();
        List<Long> inputNumbers = entry.getValue();
        checkOperations(inputNumbers, target, 0, inputNumbers.get(0), found, concat);
        return found.get();
    }

    private void checkOperations(List<Long> inputNumbers, long target, int index, long currentValue, AtomicBoolean found, boolean concat) {
        if(found.get()) return;

        if(index == inputNumbers.size() - 1) {
            if (currentValue == target) {
                found.set(true);
            }
            return;
        }
        checkOperations(inputNumbers, target, index + 1, currentValue + inputNumbers.get(index + 1), found, concat);
        checkOperations(inputNumbers, target, index + 1, currentValue * inputNumbers.get(index + 1), found, concat);
        if(concat){
            Long concatenatedValue = Long.parseLong(String.valueOf(currentValue) + inputNumbers.get(index + 1));
            checkOperations(inputNumbers, target, index + 1, concatenatedValue, found, true);
        }
    }
}
