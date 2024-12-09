package org.aoc.day;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Day5 {

    public Day5(){
        processFile("aoc2024/src/main/resources/day5.txt");
    }

    Map<Integer, Set<Integer>> prevNumberRule = new HashMap<>();
    List<int[]> pageNumbers = new ArrayList<>();

    private void processFile(String filepath){
        try(BufferedReader reader = new BufferedReader(new FileReader(filepath))){
            String line;
            while((line = reader.readLine()) != null){
                if(line.contains("|")) {
                    String[] columns = line.trim().split("\\|");
                    int before = Integer.parseInt(columns[0]);
                    int after = Integer.parseInt(columns[1]);
                    prevNumberRule.computeIfAbsent(before, k -> new HashSet<>()).add(after);
                }

                if(line.contains(",")){
                    int [] numbers = Arrays.stream(line.trim().split(","))
                                                .mapToInt(Integer::parseInt)
                                                .toArray();
                    pageNumbers.add(numbers);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long part1(){
        long total = 0;
        for(int[] nums: pageNumbers){
            if(checkOrder(nums)){
                total += nums[nums.length / 2];
            }
        }
        return total;
    }

    public long part2(){
        long total = 0;
        for(int[] nums: pageNumbers){
            if(!checkOrder(nums)){
                nums = sortArrayWithRules(nums, prevNumberRule);
                total += nums[nums.length / 2];
            }
        }
        return total;
    }

    private boolean checkOrder(int[] nums){
        Set<Integer> prev = new HashSet<>();
        for(int num: nums){
            prev.add(num);
            if(hasCommonElements(prev, prevNumberRule.getOrDefault(num, Collections.emptySet()))){
                return false;
            }
        }
        return true;
    }

    private boolean hasCommonElements(Set<Integer> previousPages, Set<Integer> rules) {
        // check if any previous pages that have passed break the rules
        Set<Integer> commonElements = new HashSet<>(previousPages);
        commonElements.retainAll(rules);
        return !commonElements.isEmpty();
    }

    private int[] sortArrayWithRules(int[] nums, Map<Integer, Set<Integer>> rules) {
        return Arrays.stream(nums)
                .boxed()
                .sorted((a,b) -> rules.getOrDefault(a, Collections.emptySet()).contains(b) ? -1 : 1)
                .mapToInt(Integer::intValue)
                .toArray();
    }

}
