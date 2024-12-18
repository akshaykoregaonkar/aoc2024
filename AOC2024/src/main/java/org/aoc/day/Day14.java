package org.aoc.day;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;

public class Day14 {
    private static final int COLS = 101;
    private static final int ROWS = 103;
    List<Robot> robots = new ArrayList<>();
    public Day14(){
        processFile("aoc2024/src/main/resources/day14.txt");
    }

    public long part1(){
        moveRobots(100);
        int[] quadrantCounts = getQuadrantCounts();
        return calculateSafetyFactor(quadrantCounts);
    }

    public long part2() {
        return IntStream.range(1, 10001)
                .filter(i -> {
                    moveRobots(1);
                    return areAllPositionsUnique();
                })
                .findFirst()
                .orElse(0);
    }

    // Not random if all positions are at unique points given its 500 points
    private boolean areAllPositionsUnique() {
        Set<String> positionSet = new HashSet<>();
        for (Robot robot : robots) {
            positionSet.add(robot.position[0] + "" + robot.position[1]);
        }
        return positionSet.size() == robots.size();
    }

    private static long calculateSafetyFactor(int[] quadrantCounts) {
        long safetyFactor = 1;
        for (int count : quadrantCounts) {
            safetyFactor *= count;
        }
        return safetyFactor;
    }

    private int[] getQuadrantCounts() {
        int[] quadrantCounts = new int[4];
        Arrays.fill(quadrantCounts, 0);
        for (Robot robot : robots) {
            int quadrant = getQuadrant(robot.position);
            if (quadrant != -1) {
                quadrantCounts[quadrant]++;
            }
        }
        return quadrantCounts;
    }

    private void moveRobots(int seconds) {
        for (int i = 0; i < seconds; i++) {
            for(Robot robot : robots){
                robot.position = reposition(robot);
            }
        }
    }

    public static int getQuadrant(int[] position) {
        int col = position[0];
        int row = position[1];

        int midCol = COLS / 2;
        int midRow = ROWS / 2;

        if (col > midCol && row < midRow) {
            return 0;
        } else if (col < midCol && row < midRow) {
            return 1;
        } else if (col < midCol && row > midRow) {
            return 2;
        } else if (col > midCol && row > midRow) {
            return 3;
        }
        return -1;
    }

    private int[] reposition(Robot robot) {
        int next_col = (robot.position[0] + robot.velocity[0]) % COLS;
        int next_row = (robot.position[1] + robot.velocity[1]) % ROWS;

        if (next_col < 0) next_col += COLS;
        if (next_row < 0) next_row += ROWS;

        return new int[]{next_col, next_row};
    }

    private void processFile(String filepath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                int[] position = parseCoordinates(parts[0].substring(2));
                int[] velocity = parseCoordinates(parts[1].substring(2));

                robots.add(new Robot(position, velocity));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int[] parseCoordinates(String coordinates) {
        String[] values = coordinates.split(",");
        return new int[] { Integer.parseInt(values[0]), Integer.parseInt(values[1]) };
    }

    private class Robot{
        int [] position;
        int [] velocity;

        public Robot(int[] position, int[] velocity) {
            this.position = position;
            this.velocity = velocity;
        }
    }
}
