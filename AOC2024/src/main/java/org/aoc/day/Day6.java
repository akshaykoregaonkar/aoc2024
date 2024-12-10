package org.aoc.day;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Day6 {
    private char [][] grid;
    private int rows, cols;
    private int startRow = -1, startCol = -1;
    public Day6() {
        processFile("aoc2024/src/main/resources/day6.txt");
        setStartingPoint();
    }

    private static final int[][] DIRECTIONS = {
            {-1, 0}, // up
            {0, 1},  // right
            {1, 0},  // down
            {0, -1}  // left
    };

    public long part1(){
        return getPatrolPath().size();
    }

    public long part2(){
        Set<List<Integer>> path = getPatrolPath();
        return countObstructions(path);
    }

    private void processFile(String filepath) {
        List<char[]> gridList = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(filepath))){
            String line;
            while((line = reader.readLine()) != null){
                gridList.add(line.toCharArray());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        rows = gridList.size();
        cols = gridList.get(0).length;
        grid = gridList.toArray(new char[rows][cols]);
    }

    private void setStartingPoint() {
        outer:
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(grid[i][j] == '^'){
                    startRow = i;
                    startCol = j;
                    break outer;
                }
            }
        }
    }

    private Set<List<Integer>> getPatrolPath() {
        Set<List<Integer>> path = new HashSet<>();
        int row = startRow, col = startCol, direction = 0;
        while(isWithinBounds(row, col)){
            path.add(Arrays.asList(row, col));

            int nextRow = row + DIRECTIONS[direction][0];
            int nextCol = col + DIRECTIONS[direction][1];

            if(!isWithinBounds(nextRow, nextCol)){
                break;
            }

            if(grid[nextRow][nextCol] == '#'){
                direction = (direction + 1) % 4;
            }else{
                row = nextRow;
                col = nextCol;
            }
        }
        return path;
    }

    private boolean isWithinBounds(int row, int col) {
        return row >= 0 && col >= 0 && row < rows && col < cols;
    }

    private long countObstructions(Set<List<Integer>> path) {
        long loop = 0;

        for(List<Integer> points: path){
            int i = points.get(0);
            int j = points.get(1);

            if (grid[i][j] != '.' || (i == startRow && j == startCol)) continue;

            // Temporarily place obstruction
            grid[i][j] = '#';

            // Check if this causes a loop
            if (doesGuardGetStuck(grid, startRow, startCol)) {
                // System.out.println("Obstruction at (" + i + ", " + j + ") creates a loop:");
                loop++;
            }

            grid[i][j] = '.';
        }
        return loop;
    }

    private boolean doesGuardGetStuck(char[][] grid, int row, int col) {
        Set<String> visitedStates = new HashSet<>();
        int direction = 0; // Assume guard starts facing "Up"

        while (isWithinBounds(row, col)) {
            String state = row + "," + col + "," + direction;
            if (visitedStates.contains(state)) {
                return true; // Guard revisited the exact same state -> Loop detected
            }
            visitedStates.add(state);

            int nextRow = row + DIRECTIONS[direction][0];
            int nextCol = col + DIRECTIONS[direction][1];

            if(!isWithinBounds(nextRow, nextCol)){
                break;
            }

            if(grid[nextRow][nextCol] == '#'){
                direction = (direction + 1) % 4;
            }else{
                row = nextRow;
                col = nextCol;
            }
        }
        return false;
    }
}
