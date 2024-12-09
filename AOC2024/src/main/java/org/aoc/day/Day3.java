package org.aoc.day;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day3 {
    List<String> matches = new ArrayList<>();
    String regex;

    public Day3(){
        regex  = "mul\\((\\d+),\\s*(\\d+)\\)";
        processFile("aoc2024/src/main/resources/day3.txt");
        System.out.println(matches);
    }

    private void processFile(String filepath) {
        String regex = "mul\\((\\d+),\\s*(\\d+)\\)|do\\(\\)|don't\\(\\)";

        // Compile the pattern
        Pattern pattern = Pattern.compile(regex);

        try(BufferedReader reader = new BufferedReader(new FileReader(filepath))){
            String line;
            while((line = reader.readLine()) != null){
                Matcher matcher = pattern.matcher(line);
                while(matcher.find()){
                    matches.add(matcher.group());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long part1(){
        long sum = 0;

        Pattern pattern = Pattern.compile(regex);
        for (String match : matches) {
            Matcher matcher = pattern.matcher(match);
            if(matcher.find()) {
                int num1 = Integer.parseInt(matcher.group(1));
                int num2 = Integer.parseInt(matcher.group(2));
                sum += num1 * num2;
            }
        }
        return sum;
    }

    public long part2(){
        long sum = 0;
        boolean multiply = true;
        Pattern pattern = Pattern.compile(regex);
        for (String match : matches) {
            switch (match) {
                case "don't()":
                    multiply = false;
                    break;
                case "do()":
                    multiply = true;
                    break;
                default:
                    if (multiply) {
                        Matcher matcher = pattern.matcher(match);
                        if (matcher.find()) {
                            int num1 = Integer.parseInt(matcher.group(1));
                            int num2 = Integer.parseInt(matcher.group(2));
                            sum += num1 * num2;
                        }
                    }else{
                        System.out.println("skipped:" + match);
                    }
                    break;
            }
        }
        return sum;
    }
}
