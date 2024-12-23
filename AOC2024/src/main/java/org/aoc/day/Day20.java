package org.aoc.day;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiPredicate;

public class Day20 {
    public static final int TIME_TO_SAVE = 100;
    Map<Point, Integer> dist = new HashMap<>();
    Map<Point, Character> grid = new HashMap<>();

    public Day20() {
        Point start = processFile("aoc2024/src/main/resources/day20.txt");
        markPathDistances(start);
    }

    public long part1() {
        return calculateDistances((d, diff) -> d == 2 && diff >= TIME_TO_SAVE);
    }

    public long part2() {
        return calculateDistances((d, diff) -> d < 21 && diff >= TIME_TO_SAVE);
    }

    private long calculateDistances(BiPredicate<Integer, Integer> condition) {
        int count = 0;
        List<Map.Entry<Point, Integer>> entries = new ArrayList<>(dist.entrySet());

        for (int i = 0; i < entries.size(); i++) {
            for (int j = i + 1; j < entries.size(); j++) {
                Point p = entries.get(i).getKey();
                Point q = entries.get(j).getKey();
                int rowDist = entries.get(i).getValue();
                int colDist = entries.get(j).getValue();

                int d = Math.abs(p.row - q.row) + Math.abs(p.col - q.col);
                int diff = Math.abs(colDist - rowDist) - d;

                if (condition.test(d, diff)) {
                    count++;
                }
            }
        }

        return count;
    }

    private Point processFile(String filepath) {
        Point start = null;
        try {
            List<String> lines = Files.readAllLines(Paths.get(filepath));
            for (int i = 0; i < lines.size(); i++) {
                String row = lines.get(i);
                for (int j = 0; j < row.length(); j++) {
                    char c = row.charAt(j);
                    if (c != '#') {
                        Point point = new Point(i, j);
                        grid.put(point, c);
                        if (c == 'S') {
                            start = point;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return start;
    }

    private void markPathDistances(Point start) {
        Queue<Point> todo = new LinkedList<>();
        dist.put(start, 0);
        todo.add(start);

        while (!todo.isEmpty()) {
            Point pos = todo.poll();
            List<Point> directions = Arrays.asList( new Point(-1, 0), new Point(1, 0),
                                                    new Point(0, -1), new Point(0, 1));
            for (Point direction : directions) {
                Point newPos = pos.add(direction);
                if (grid.containsKey(newPos) && !dist.containsKey(newPos)) {
                    dist.put(newPos, dist.get(pos) + 1);
                    todo.add(newPos);
                }
            }
        }
    }

    class Point {
        int row;
        int col;

        public Point(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public Point add(Point other) {
            return new Point(this.row + other.row, this.col + other.col);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return row == point.row && col == point.col;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }
    }
}