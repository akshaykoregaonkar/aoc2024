package org.aoc.day;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day4 {
    private char [][] grid;
    private int rows, cols;
    
    public Day4(){
        processFile("aoc2024/src/main/resources/day4.txt");
    }

    private char[][] processFile(String filepath) {
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

        grid = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            grid[i] = gridList.get(i);
        }
        return grid;
    }

    public int part1(){
        int count = 0;
        String word = "XMAS";
        int[][] directions = {
                {0, 1}, {1, 0}, {1, 1,}, {1, -1},
                {0, -1}, {-1, 0}, {-1, -1,}, {-1, 1}
        };
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                for(int[] dir: directions){
                    if(dfs(grid, i, j, 0, word, new int[]{dir[0], dir[1]})){
                        count++; 
                    }
                }
            }
            
        }
        return count; 
    }

    public int part2(){
        int count = 0;
        String word = "MAS";

        /* Scenarios:
            MAS top left to bottom Right     |         MAS is Bottom right to top left
            M.S		M.M                      |          S.S		S.M
            .A.		.A.                      |          .A.		.A.
            M.S		S.S                      |          M.M 	S.M
        */
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(dfs(grid, i, j, 0, word, new int[]{1, 1})){
                    if(checkCross(i + 2, j, word, i, j + 2)){
                        count++;
                    }
                }
                if(dfs(grid, i, j, 0, word, new int[]{-1, -1})){
                    if(checkCross(i, j - 2, word, i - 2, j)){
                        count++;
                    }
                }
            }
        }

        return count;
    }

    private boolean checkCross(int i, int j, String word, int i_rev, int j_rev) {
        return dfs(grid, i, j, 0, word, new int[]{-1, 1}) ||
                dfs(grid, i_rev, j_rev, 0, word, new int[]{1, -1});
    }

    private boolean dfs(char[][] grid, int x, int y, int index, String word, int [] next_direction) {
        int x_dir = next_direction[0];
        int y_dir = next_direction[1];

        if(index == word.length()){
            return true;
        }

        if(x < 0 || x >= rows || y < 0 || y >= cols || grid[x][y] != word.charAt(index)){
            return false;
        }

        return dfs(grid, x + x_dir, y + y_dir, index + 1, word, next_direction);
    }

}
