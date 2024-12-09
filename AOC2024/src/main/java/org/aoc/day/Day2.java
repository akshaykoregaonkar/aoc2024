package org.aoc.day;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day2 {
    public Day2(){
        processFile("aoc2024/src/main/resources/day2.txt");
    }

    List<List<Integer>> reports = new ArrayList<>();

    private void processFile(String filepath){
        try(BufferedReader reader = new BufferedReader(new FileReader(filepath))){
            String line;
            while((line = reader.readLine()) != null){
                List<Integer> report = new ArrayList<>();
                String[] levels = line.trim().split("\\s+");
                for(String level: levels){
                    report.add(Integer.parseInt(level));
                }
                reports.add(report);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isSafe(List<Integer> report){
        if(report.size() <= 1) return true;

        boolean increasing = report.get(1) > report.get(0);

        for (int i = 1; i < report.size(); i++) {
            if(safetyCheck(increasing, report.get(i) - report.get(i-1))){
                return false;
            }
        }
        return true;
    }

    private boolean isSafeWithTolerance(List<Integer> report){
        for(int i = 0; i < report.size(); i++){
            List<Integer> copy1 = report;
            List<Integer> copy = new ArrayList<>(report);
            copy.remove(i);
            if(isSafe(copy)){
                return true;
            }
        }
        return false;
    }

    private static boolean safetyCheck(boolean increasing, int dist) {
        return (increasing && (dist <= 0 || dist > 3)) ||
                (!increasing && (dist >= 0 || dist < -3));
    }

    public long part1(){
        long total = 0;
        for(List<Integer> report : reports){
            total += isSafe(report) ? 1 : 0;
        }
        return total;
    }

    public long part2(){
        long total = 0;
        for (List<Integer> report: reports) {
            if(isSafe(report) || isSafeWithTolerance(report)){
                total++;
            }
        }
        return total;
    }

}
