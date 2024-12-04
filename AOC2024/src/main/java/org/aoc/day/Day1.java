package org.aoc.day;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Day1{

    public Day1(){
        processFile("src/main/resources/day1.txt");
    }

    PriorityQueue<Integer> queue1 = new PriorityQueue<>();
    PriorityQueue<Integer> queue2 = new PriorityQueue<>();
    private void processFile(String filepath){
        try(BufferedReader reader = new BufferedReader(new FileReader(filepath))){
            String line;
            while((line = reader.readLine()) != null){
                String[] columns = line.trim().split("\\s+");
                queue1.add(Integer.parseInt(columns[0]));
                queue2.add(Integer.parseInt(columns[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long part1(){
        long sum = 0;

        while(!queue1.isEmpty() && !queue2.isEmpty()){
            sum += Math.abs(queue1.poll() - queue2.poll());
        }
        return sum;
    }

    public long part2(){
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        long total = 0;
        for(Integer num : queue2){
            frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
        }

        for(Integer num: queue1){
            total += num * frequencyMap.getOrDefault(num, 0);
        }
        return total;
    }

}
