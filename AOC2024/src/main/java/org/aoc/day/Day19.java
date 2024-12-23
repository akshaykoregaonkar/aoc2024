package org.aoc.day;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Day19 {
    Set<String> towelPatterns = new HashSet<>();
    List<String> designList = new ArrayList<>();

    Map<String, Long> towelMap = new HashMap<>();
    public Day19(){
        processFile("aoc2024/src/main/resources/day19.txt");
    }

    public long part1(){
        int possiblePatterns = 0;
        for(String design: designList){
            possiblePatterns += isPossible(design) ? 1 : 0;
        }
        return possiblePatterns;
    }

    public long part2(){
        long sum = 0;
        for(String design: designList){
            long count = countPatterns(design);
            towelMap.put(design, towelMap.getOrDefault(design, 0L) + count);
            sum += count;
        }
        return sum;
    }

    private boolean isPossible(String design) {
        if(design.isEmpty()){
            return true;
        }

        for(String towel: towelPatterns){
            if(design.startsWith(towel)){
                if(isPossible(design.substring(towel.length()))){
                    return true;
                }
            }
        }
        return false;
    }

    private long countPatterns(String design){
        return countPatternsHelper(design, new long[design.length()]);
    }

    private long countPatternsHelper(String design, long[] memo) {
        if(design.isEmpty()){
            return 1;
        }
        if(memo[design.length()-1] != 0){
            return memo[design.length() - 1];
        }
        long count = 0;
        for(String towel: towelPatterns){
            if(design.startsWith(towel)){
                count += countPatternsHelper(design.substring(towel.length()), memo);
            }
        }
        memo[design.length() - 1] = count;
        return count;
    }


    private void processFile(String filepath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            boolean isTowelPattern = true;
            while ((line = reader.readLine()) != null) {
                if(line.isEmpty()){
                    isTowelPattern = false;
                } else if (isTowelPattern) {
                    String[] patterns = line.replaceAll("\\s+", "").split(",");
                    Collections.addAll(towelPatterns, patterns);
                } else{
                    designList.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
