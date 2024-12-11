package org.aoc.day;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;

public class Day8 {
    char[][] grid;
    private int rows, cols;
    Set<List<Integer>> antiNodePositions = new HashSet<>();
    Map<Character, List<int[]>> antennaMap = new HashMap<>();
    public Day8(){
        processFile("aoc2024/src/main/resources/day8.txt");
        generateAntennaMap();
    }

    public long part1(){
        processAntennaMap(this::markAntinodes);
        return antiNodePositions.size();
    }

    public long part2(){
        antiNodePositions.clear();
        processAntennaMap(this::markTFreqAntinodes);
        // some antennas are antinodes so just mark the antinodes we've found and get count from grid where char != "."
        markAntinodesInGrid();
        return countAllAntinodes();
    }

    private void generateAntennaMap() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(grid[i][j] != '.'){
                    antennaMap.computeIfAbsent(grid[i][j], k -> new ArrayList<>()).add(new int[]{i, j});
                }
            }
        }
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

    private void markAntinodesInGrid() {
        for(List<Integer> pos: antiNodePositions){
            grid[pos.get(0)][pos.get(1)] = '#';
        }
    }

    private long countAllAntinodes() {
        // any non dots are valid antinodes
        return Arrays.stream(grid)
                .flatMapToInt(row -> new String(row).chars())
                .filter(ch -> ch != '.')
                .count();
    }

    private void processAntennaMap(BiConsumer<int[], int[]> antinodeHandler) {
        for (List<int[]> antennaPositions : antennaMap.values()) {
            for (int i = 0; i < antennaPositions.size(); i++) {
                for (int j = i + 1; j < antennaPositions.size(); j++) {
                    antinodeHandler.accept(antennaPositions.get(i), antennaPositions.get(j));
                }
            }
        }
    }

    private void markTFreqAntinodes(int[] pos1, int[] pos2) {
        int pos1Row = pos1[0];
        int pos1Col = pos1[1];
        int pos2Row = pos2[0];
        int pos2Col = pos2[1];
        int rowDiff = Math.abs(pos2Row - pos1Row);
        int colDiff = Math.abs(pos2Col - pos1Col);

        boolean isRightToLeft = pos1Col > pos2Col;

        if (isRightToLeft) {
            findAntinodesInDirection(pos2Row, pos2Col, rowDiff, -colDiff, rows, -1); // Down-left
            findAntinodesInDirection(pos1Row, pos1Col, -rowDiff, colDiff, -1, cols); // Up-right
        } else {
            findAntinodesInDirection(pos1Row, pos1Col, -rowDiff, -colDiff, -1, -1); // Up-left
            findAntinodesInDirection(pos2Row, pos2Col, rowDiff, colDiff, rows, cols); // Down-right
        }
    }

    private void findAntinodesInDirection(int startRow, int startCol, int rowStep, int colStep, int rowBound, int colBound) {
        int currentRow = startRow;
        int currentCol = startCol;

        while ((rowStep > 0 ? currentRow < rowBound : currentRow >= rowBound) &&
                (colStep > 0 ? currentCol < colBound : currentCol >= colBound)) {
            int nextRow = currentRow + rowStep;
            int nextCol = currentCol + colStep;
            addValidAntinode(nextRow, nextCol);
            currentRow = nextRow;
            currentCol = nextCol;
        }
    }

    private void markAntinodes(int[] pos1, int[] pos2) {
        int rowDiff = Math.abs(pos2[0] - pos1[0]);
        int colDiff = Math.abs(pos2[1] - pos1[1]);

        boolean isRightToLeft = pos1[1] > pos2[1];

        // Calculate first antinode position
        int rowPosA1 = isRightToLeft ? pos2[0] + rowDiff : pos1[0] - rowDiff;
        int colPosA1 = isRightToLeft ? pos2[1] - colDiff : pos1[1] - colDiff;
        addValidAntinode(rowPosA1, colPosA1);

        // Calculate second antinode position
        int rowPosA2 = isRightToLeft ? pos1[0] - rowDiff : pos2[0] + rowDiff;
        int colPosA2 = isRightToLeft ? pos1[1] + colDiff : pos2[1] + colDiff;
        addValidAntinode(rowPosA2, colPosA2);
    }

    private void addValidAntinode(int row, int col) {
        if(row >= 0 && col >= 0 && row < rows && col < cols){
            antiNodePositions.add(Arrays.asList(row, col));
        }
    }
}
