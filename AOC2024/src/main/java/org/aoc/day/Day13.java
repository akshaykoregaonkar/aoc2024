package org.aoc.day;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day13 {
    List<Equation> equations = new ArrayList<>();
    public Day13() {
        processFile("aoc2024/src/main/resources/day13.txt");
    }

    public long part1(){
        return calculateTotal(false);
    }

    public long part2(){
        return calculateTotal(true);
    }

    /**
     * solve for
     * x1A + x2B = P1 (e.g. 80*94 + 40*22 = 8400)
     * y1A + y2B = P2 (e.g. 80*34 + 40*67 = 5400)
     * x1 and y1 in Button A, x2 and y2 in Button B
     * Using M . v = P
     * v = Minv.P
     * |x1 x2| |A| = |P1|
     * |y1 y2| |B|   |P2|
     *
     * |A| = 1/det(M) |y2   -x2| |P1|
     * |B|            |-y1   x1| |P2|
     *
     * det(M) = (x1 * y1) - (x2 * y2)
     *
     * A = (P1 y2) - (P2 x2) / det
     * B = (x1 P2) - (y1 P1) / det
     */

    private long calculateTotal(boolean goBig) {
        long total = 0;
        for(Equation equation: equations){
            double det = (equation.x1 * equation.y2) - (equation.x2 * equation.y1);
            if(goBig){
                equation.P1 = 10000000000000L + equation.P1;
                equation.P2 = 10000000000000L + equation.P2;
            }
            double A = (equation.P1 * equation.y2 - equation.P2 * equation.x2) / det;
            double B = (equation.x1 * equation.P2 - equation.y1 * equation.P1) / det;
            if(A % 1 == 0 && B % 1 == 0){
                total += 3 * A + B;
            }
        }
        return total;
    }

    private void processFile(String filepath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            List<String> lines = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                if(!line.trim().isEmpty()){
                    lines.add(line.trim());
                    if(lines.size() == 3){
                        equations.add(parseEquation(lines));
                        lines.clear();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Equation parseEquation(List<String> lines) {
        Pattern buttonPattern = Pattern.compile("X\\+(\\d+).*Y\\+(\\d+)");
        Pattern prizePattern = Pattern.compile("X=(\\d+).*Y=(\\d+)");

        long x1 = 0, y1 = 0, x2 = 0, y2 = 0, P1 = 0, P2 = 0;

        Matcher matcher;

        matcher = buttonPattern.matcher(lines.get(0));
        if (matcher.find()) {
            x1 = Long.parseLong(matcher.group(1));
            y1 = Long.parseLong(matcher.group(2));
        }

        matcher = buttonPattern.matcher(lines.get(1));
        if (matcher.find()) {
            x2 = Long.parseLong(matcher.group(1));
            y2 = Long.parseLong(matcher.group(2));
        }

        matcher = prizePattern.matcher(lines.get(2));
        if (matcher.find()) {
            P1 = Long.parseLong(matcher.group(1));
            P2 = Long.parseLong(matcher.group(2));
        }

        return new Equation(x1, y1, x2, y2, P1, P2);
    }

    class Equation{
        long x1, y1, x2, y2, P1, P2;

        public Equation(long x1, long y1, long x2, long y2, long p1, long p2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.P1 = p1;
            this.P2 = p2;
        }
    }
}
