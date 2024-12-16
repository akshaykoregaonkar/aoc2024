package org.aoc.day;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class Day10 {
    int[][] grid;
    int rows, cols;
    private static final int[][] DIRECTIONS = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} };


    public Day10() {
        processFile("aoc2024/src/main/resources/day10.txt");
    }

    public long part1(){
        return sumBy((row, col) -> trailHeadScore(row, col));
    }

    public long part2(){
        return sumBy((row, col) -> trailHeadRating(row, col));
    }

    private long sumBy(BiFunction<Integer, Integer, Long> operation) {
        long total = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(grid[i][j] == 0){
                    total += operation.apply(i, j);
                }
            }
        }
        return total;
    }

    private long trailHeadScore(int row, int col) {
        Set<List<Integer>> reachableNines = new HashSet<>();
        bfs(row, col, (r, c) -> reachableNines.add(Arrays.asList(r, c)));
        return reachableNines.size();
    }

    private long trailHeadRating(int row, int col) {
        final long[] trailRating = {0};
        bfs(row, col, (r, c) -> trailRating[0]++);
        return trailRating[0];
    }

    private void bfs(int row, int col, BiConsumer<Integer, Integer> scoringMechanism) {
        final int target = 9;
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{row, col, grid[row][col]});

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int currRow = current[0], currCol = current[1], value = current[2];

            if (grid[currRow][currCol] == target) {
                scoringMechanism.accept(currRow, currCol);
                continue;
            }

            for (int[] direction : DIRECTIONS) {
                int nextRow = currRow + direction[0];
                int nextCol = currCol + direction[1];

                if (isValid(nextRow, nextCol, value)) {
                    queue.add(new int[]{nextRow, nextCol, value + 1});
                }
            }
        }
    }

    private boolean isValid(int x, int y, int currentVal) {
        return (x >= 0 && x < rows && y >= 0 && y < cols && grid[x][y] == (currentVal + 1));
    }

    private void processFile(String filepath) {
        List<int[]> gridList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while((line = reader.readLine()) != null) {
                int[] numbers = Arrays.stream(line.split(""))
                                        .mapToInt(Integer::parseInt)
                                        .toArray();
                gridList.add(numbers);
            }
            rows = gridList.size();
            cols = gridList.get(0).length;
            grid = gridList.toArray(new int[rows][cols]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
