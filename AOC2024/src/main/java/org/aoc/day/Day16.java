package org.aoc.day;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Day16 {

    private int rows, cols;
    static final int[][] DIRECTIONS = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // reindeer points east
    char[][] grid;

    public Day16(){
        processFile("aoc2024/src/main/resources/day16.txt");
    }

    public int part1(){
        List<Node> optimalPaths = findAllOptimalPaths();
        return optimalPaths.get(0).cost;
    }

    public int part2(){
        List<Node> optimalPaths = findAllOptimalPaths();
        return countBestPathTiles(optimalPaths);
    }

    private int countBestPathTiles(List<Node> optimalPaths) {
        Set<String> distinctTiles = new HashSet<>();
        for(Node node: optimalPaths){
            while(node != null){
                distinctTiles.add(node.row + ", " + node.col);
                node = node.parent;
            }
        }
        return distinctTiles.size();
    }

    public List<Node> findAllOptimalPaths() {
        int rows = grid.length, cols = grid[0].length;
        int[] start = new int[]{rows - 2, 1};
        int[] end = new int[]{1, cols - 2};
        int minCost = Integer.MAX_VALUE;

        // Djikstra - priority queue with a cost comparator
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(n -> n.cost));
        queue.add(new Node(start[0], start[1], -1, 0, null));

        int[][][] cost = new int[rows][cols][4];
        for (int[][] row : cost) {
            for (int[] cell : row) Arrays.fill(cell, Integer.MAX_VALUE);
        }

        for (int d = 0; d < 4; d++) {
            cost[start[0]][start[1]][d] = 0;
        }

        List<Node> optimalPaths = new ArrayList<>();
        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (current.row == end[0] && current.col == end[1]) {
                minCost = Math.min(minCost, current.cost);
                if(current.cost <= minCost){
                    optimalPaths.add(current);
                }
            }

            for (int d = 0; d < 4; d++) {
                int newRow = current.row + DIRECTIONS[d][0];
                int newCol = current.col + DIRECTIONS[d][1];
                int newCost = current.cost + 1;

                if (current.direction != d) {
                    newCost += 1000;
                }

                if (grid[newRow][newCol] != '#' && newCost <= cost[newRow][newCol][d]) {
                    cost[newRow][newCol][d] = newCost;
                    queue.add(new Node(newRow, newCol, d, newCost, current));
                }
            }
        }

        return optimalPaths;
    }

    private void processFile(String filepath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            List<char[]> gridList = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                gridList.add(line.toCharArray());
            }
            rows = gridList.size();
            cols = gridList.get(0).length;
            grid = gridList.toArray(new char[rows][cols]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Node {
        int row, col, direction, cost;
        Node parent;

        Node(int row, int col, int direction, int cost, Node parent) {
            this.row = row;
            this.col = col;
            this.direction = direction;
            this.cost = cost;
            this.parent = parent;
        }
    }
}
