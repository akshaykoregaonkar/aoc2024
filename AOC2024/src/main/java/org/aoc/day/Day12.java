package org.aoc.day;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Day12 {
    private static final int[] DX = {0, 1, 0, -1};
    private static final int[] DY = {1, 0, -1, 0};
    private char[][] grid;
    private int rows, cols;

    public Day12() {
        processFile("aoc2024/src/main/resources/day12.txt");
    }

    public long part1() {
        return calculateTotalPrice(garden -> garden.area * garden.perimeter);
    }

    public long part2() {
        return calculateTotalPrice(garden -> garden.area * findSides(garden.points));
    }

    private long calculateTotalPrice(Function<Garden, Long> priceCalculator) {
        boolean[][] visitedGardens = new boolean[rows][cols];
        long total = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!visitedGardens[i][j]) {
                    Garden garden = calculateGarden(i, j, visitedGardens, grid[i][j]);
                    total += priceCalculator.apply(garden);
                }
            }
        }
        return total;
    }

    private int findSides(Set<List<Integer>> region) {
        // we find points along the garden that point outward or inward
        Set<List<Integer>> neighbours = new HashSet<>();
        int outwardEdges = region.stream().mapToInt(point -> getOuterEdgeCount(point, region, neighbours)).sum();
        return outwardEdges + findInwardEdges(region, neighbours);
    }

    private int getOuterEdgeCount(List<Integer> point, Set<List<Integer>> region, Set<List<Integer>> neighbours) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        char gardenType = grid[point.get(0)][point.get(1)];

        List<List<Integer>> boundaryNeighbours = new ArrayList<>();
        for (int[] dir : directions) {
            int neighbourX = point.get(0) + dir[0];
            int neighbourY = point.get(1) + dir[1];
            List<Integer> neighbour = Arrays.asList(neighbourX, neighbourY);

            if (isWithinBounds(neighbourX, neighbourY) && grid[neighbourX][neighbourY] != gardenType) {
                neighbours.add(neighbour);
            }
            if (!region.contains(neighbour)) {
                boundaryNeighbours.add(neighbour);
            }
        }

        return countDiagonalPairs(boundaryNeighbours);
    }

    private boolean isWithinBounds(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    private int findInwardEdges(Set<List<Integer>> region, Set<List<Integer>> neighbours) {
        List<List<Integer>> regionList = new ArrayList<>(region);
        List<List<Integer>> outerPoints = new ArrayList<>();

        for (int i = 0; i < regionList.size(); i++) {
            for (int j = i + 1; j < regionList.size(); j++) {
                List<Integer> p1 = regionList.get(i);
                List<Integer> p2 = regionList.get(j);

                if (isDiagonal(p1, p2)) {
                    List<Integer> outerNeighbour = hasOuterNeighbour(p1, p2, neighbours);
                    if (!outerNeighbour.isEmpty()) {
                        outerPoints.add(outerNeighbour);
                    }
                }
            }
        }

        return outerPoints.size();
    }

    private int countDiagonalPairs(List<List<Integer>> boundaryNeighbours) {
        return (int) IntStream.range(0, boundaryNeighbours.size())
                .flatMap(i -> isDiagonalNeighbour(boundaryNeighbours, i))
                .count();
    }

    private IntStream isDiagonalNeighbour(List<List<Integer>> boundaryNeighbours, int i) {
        return IntStream.range(i + 1, boundaryNeighbours.size())
                .filter(j -> isDiagonal(boundaryNeighbours.get(i), boundaryNeighbours.get(j)));
    }

    private boolean isDiagonal(List<Integer> p1, List<Integer> p2) {
        int dx = Math.abs(p1.get(0) - p2.get(0));
        int dy = Math.abs(p1.get(1) - p2.get(1));

        return dx == 1 && dy == 1;
    }

    private List<Integer> hasOuterNeighbour(List<Integer> p1, List<Integer> p2, Set<List<Integer>> neighbours) {
        List<Integer> neighbour1 = Arrays.asList(p1.get(0), p2.get(1));
        List<Integer> neighbour2 = Arrays.asList(p2.get(0), p1.get(1));

        // edge case where points have two outside neighbours
        if (neighbours.contains(neighbour1) && neighbours.contains(neighbour2)) {
            return Collections.emptyList();
        }
        return neighbours.contains(neighbour1) ? neighbour1 :
                (neighbours.contains(neighbour2) ? neighbour2 : Collections.emptyList());
    }

    private Garden calculateGarden(int row, int col, boolean[][] visitedGardens, char gardenType) {
        if (row < 0 || row >= rows || col < 0 || col >= cols || visitedGardens[row][col] || grid[row][col] != gardenType) {
            return new Garden(0, 0);
        }
        visitedGardens[row][col] = true;

        Garden garden = new Garden(1, getPerimeterForPoint(row, col, gardenType));
        garden.points.add(Arrays.asList(row, col));

        for (int i = 0; i < 4; i++) {
            int newRow = row + DX[i];
            int newCol = col + DY[i];
            Garden neighbour = calculateGarden(newRow, newCol, visitedGardens, gardenType);
            garden.area += neighbour.area;
            garden.perimeter += neighbour.perimeter;
            garden.points.addAll(neighbour.points);
        }

        return garden;
    }

    private long getPerimeterForPoint(int row, int col, char gardenType) {
        int boundary = 0;
        for (int i = 0; i < 4; i++) {
            int newRow = row + DX[i];
            int newCol = col + DY[i];

            if (newRow < 0 || newRow >= rows || newCol < 0 || newCol >= cols || grid[newRow][newCol] != gardenType) {
                boundary++;
            }
        }
        return boundary;
    }

    private void processFile(String filepath) {
        List<char[]> gridList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                gridList.add(line.toCharArray());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        rows = gridList.size();
        cols = gridList.get(0).length;
        grid = gridList.toArray(new char[rows][cols]);
    }

    private class Garden {
        long area;
        long perimeter;
        Set<List<Integer>> points;

        Garden(long area, long perimeter) {
            this.area = area;
            this.perimeter = perimeter;
            this.points = new HashSet<>();
        }
    }
}