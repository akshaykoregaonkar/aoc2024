package org.aoc.day;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Day15 {
    private char[][] warehouseGrid;
    private int rows, cols;
    List<int[]> instructions = new ArrayList<>();
    private static Map<Character, int[]> DIRECTION_MAP = new HashMap<>();
    static {
        DIRECTION_MAP.put('<', new int[] {0, -1});
        DIRECTION_MAP.put('v', new int[] {1, 0});
        DIRECTION_MAP.put('^', new int[] {-1, 0});
        DIRECTION_MAP.put('>', new int[] {0, 1});
    }

    public Day15(){
        processFile("aoc2024/src/main/resources/day15.txt");
    }

    public long part1() {
        char[][] warehouse = deepCopyWarehouse(warehouseGrid);
        completeRobotMoves(warehouse);
        return getGpsSum(warehouse);
    }

    public long part2() {
        char[][] expandedWarehouse = expandWarehouse();
        completeRobotMoves(expandedWarehouse);
        return getGpsSum(expandedWarehouse);
    }

    private char[][] deepCopyWarehouse(char[][] warehouseGrid) {
        char[][] copy = new char[rows][];
        for (int i = 0; i < rows; i++) {
            copy[i] = warehouseGrid[i].clone();
        }
        return copy;
    }

    private void completeRobotMoves(char[][] warehouse) {
        int[] robotPosition = findRobot(warehouse, rows, cols);

        for(int[] nextDirection: instructions){
            int nextRow = nextDirection[0] + robotPosition[0];
            int nextCol = nextDirection[1] + robotPosition[1];

            char nextTile = warehouse[nextRow][nextCol];

            if(nextTile == '.'){
                moveRobotToEmptyTile(warehouse, robotPosition, nextRow, nextCol);
                robotPosition = new int[] { nextRow, nextCol };
            } else if((nextTile == 'O' || isHorizontalPushForSquares(nextTile, nextDirection))){
                int[] availableSpace = getNextAvailableSpace(warehouse, robotPosition, nextDirection);
                if (availableSpace != null){
                    moveCirclesAndHorizontalSquares(warehouse, robotPosition, nextDirection, availableSpace);
                    robotPosition = new int[] { nextRow, nextCol };
                }
            } else if(isVerticalPushForSquares(nextTile, nextDirection)){
                if(moveVerticalSquares(warehouse, robotPosition, nextDirection)){
                    robotPosition = new int[] { nextRow, nextCol };
                }
            }
        }
    }

    private static void moveRobotToEmptyTile(char[][] warehouse, int[] robotPosition, int nextRow, int nextCol) {
        warehouse[nextRow][nextCol] = '@';
        warehouse[robotPosition[0]][robotPosition[1]] = '.';
    }

    private long getGpsSum(char[][] warehouseCopy) {
        long gpsSum = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(warehouseCopy[i][j] == 'O' || warehouseCopy[i][j] == '['){
                    gpsSum += (i * 100) + j;
                }
            }
        }
        return gpsSum;
    }

    private boolean moveVerticalSquares(char[][] grid, int[] currentPosition, int[] direction) {
        List<int[]> squarePositions = new ArrayList<>();
        squarePositions.add(currentPosition);

        int rowDir = direction[0];
        int colDir = direction[1];

        for (int i = 0; i < squarePositions.size(); i++) {
            int[] current = squarePositions.get(i);
            int nextRow = current[0] + rowDir;
            int nextCol = current[1] + colDir;

            if (squarePositions.stream().anyMatch(block -> block[0] == nextRow && block[1] == nextCol)) {
                continue;
            }

            switch (grid[nextRow][nextCol]) {
                case '#':
                    return false; // stop as we've hit a wall
                case '[':
                    squarePositions.add(new int[] { nextRow, nextCol });
                    squarePositions.add(new int[] { nextRow, nextCol + 1 });
                    break;
                case ']':
                    squarePositions.add(new int[] { nextRow, nextCol });
                    squarePositions.add(new int[] { nextRow, nextCol - 1 });
                    break;
            }
        }
        updateGridWithMovePositions(grid, squarePositions, rowDir, colDir);
        return true;
    }

    private void updateGridWithMovePositions(char[][] grid, List<int[]> squarePositions, int rowDir, int colDir) {
        // easier to copy original grid, clear it and place new points
        char[][] gridCopy = deepCopyWarehouse(grid);
        clearGrid(grid, squarePositions);

        for (int[] pos : squarePositions) {
            int newRow = pos[0] + rowDir;
            int newCol = pos[1] + colDir;
            grid[newRow][newCol] = gridCopy[pos[0]][pos[1]];
        }
    }

    private void clearGrid(char[][] grid, List<int[]> toMove) {
        for (int i = 0; i < toMove.size(); i++) {
            int[] block = toMove.get(i);
            grid[block[0]][block[1]] = '.';
        }
    }

    private boolean isHorizontalPushForSquares(char nextTile, int[] nextDirection) {
        return (nextTile == ']' || nextTile == '[') && nextDirection[1] != 0;
    }

    private boolean isVerticalPushForSquares(char nextTile, int[] nextDirection) {
        return (nextTile == ']' || nextTile == '[') && nextDirection[0] != 0;
    }

    private void moveCirclesAndHorizontalSquares(char[][] grid, int[] currentPosition, int[] direction, int[] targetDotPosition) {
        int currentRow = currentPosition[0];
        int currentCol = currentPosition[1];

        int tempRow = targetDotPosition[0];
        int tempCol = targetDotPosition[1];

        while (tempRow != currentRow || tempCol != currentCol) {
            int prevRow = tempRow - direction[0];
            int prevCol = tempCol - direction[1];

            grid[tempRow][tempCol] = grid[prevRow][prevCol];
            tempRow = prevRow;
            tempCol = prevCol;
        }

        grid[currentRow][currentCol] = '.';
    }

    private int[] getNextAvailableSpace(char[][] grid, int[] position, int[] direction) {
        int currentRow = position[0];
        int currentCol = position[1];

        while (currentRow >= 0 && currentRow < rows && currentCol >= 0 && currentCol < cols) {
            currentRow += direction[0];
            currentCol += direction[1];

            if (grid[currentRow][currentCol] == '#') {
                break;
            }

            if (grid[currentRow][currentCol] == '.') {
                return new int[] { currentRow, currentCol };
            }
        }

        // null indicates we hit a wall
        return null;
    }

    private int[] findRobot(char[][] warehouseCopy, int rows, int cols) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (warehouseCopy[i][j] == '@') {
                    return new int[]{i, j};
                }
            }
        }
        throw new IllegalArgumentException("Attention! Our Robot has gone missing!");
    }

    private char[][] expandWarehouse() {
        List<char[]> warehouseList = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            String row = new String(warehouseGrid[i]);
            row = row.replace("#", "##")
                    .replace("O", "[]")
                    .replace(".", "..")
                    .replace("@", "@.");
            warehouseList.add(row.toCharArray());
        }
        rows = warehouseList.size();
        cols = warehouseList.get(0).length;
        return warehouseList.toArray(new char[rows][cols]);
    }

    private void processFile(String filepath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            List<char[]> warehouseList = new ArrayList<>();
            boolean isWarehouse = true;
            while ((line = reader.readLine()) != null) {
                if(line.isEmpty()){
                    isWarehouse = false;
                } else if (isWarehouse) {
                    warehouseList.add(line.toCharArray());
                } else{
                    instructions.addAll(line.chars()
                            .mapToObj(c -> DIRECTION_MAP.get((char)c))
                            .collect(Collectors.toList()));
                }
            }
            rows = warehouseList.size();
            cols = warehouseList.get(0).length;
            warehouseGrid = warehouseList.toArray(new char[rows][cols]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
