package org.aoc.day;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Day18 {
    int cols = 71, rows = 71;
    private char[][] grid = new char[cols][rows];
    private static final int[] DIR_X = {-1, 1, 0, 0};
    private static final int[] DIR_Y = {0, 0, -1, 1};
    private String filepath = "aoc2024/src/main/resources/day18.txt";

    public long part1(){
        processFile(filepath, 1024);
        return getShortestPath(grid, 0, 0, cols - 1, rows -1);
    }

    public String part2(){
        // skip first 1024 since we know path exists
        for (int i = 1024; i < 3450; i++) {
            processFile(filepath, i);
            if(getShortestPath(grid, 0, 0, cols - 1, rows -1) == -1){
                return getLine(filepath, i);
            }
        }
        return null;
    }

    private int getShortestPath(char[][] grid, int startX, int startY, int endX, int endY) {
        int rows = grid.length;
        int cols = grid[0].length;

        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{startX, startY, 0});

        boolean[][] visited = new boolean[rows][cols];
        visited[startX][startY] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];
            int dist = current[2];

            if (x == endX && y == endY) {
                return dist;
            }

            for (int i = 0; i < 4; i++) {
                int newX = x + DIR_X[i];
                int newY = y + DIR_Y[i];

                if (isValid(grid, newX, newY, visited)) {
                    visited[newX][newY] = true;
                    queue.offer(new int[]{newX, newY, dist + 1});
                }
            }
        }

        return -1;
    }

    private boolean isValid(char[][] grid, int x, int y, boolean[][] visited) {
        return x >= 0 && x < rows && y >= 0 && y < cols && grid[x][y] == '.' && !visited[x][y];
    }

    private void processFile(String filepath, int byteCount) {
        for (int i = 0; i < cols; i++) {
            Arrays.fill(grid[i], '.');
        }
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null && count != byteCount) {
                String[] coordinates = line.split(",");
                int x = Integer.parseInt(coordinates[1]);
                int y = Integer.parseInt(coordinates[0]);
                grid[x][y] = '#';
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getLine(String filepath, int lineNumber) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String currentLine;
            int currentLineNumber = 1;

            while ((currentLine = reader.readLine()) != null) {
                if (currentLineNumber == lineNumber) {
                    return currentLine;
                }
                currentLineNumber++;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

}
