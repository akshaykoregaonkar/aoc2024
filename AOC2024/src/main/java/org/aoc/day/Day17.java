package org.aoc.day;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day17 {
    long registerA, registerB = 0, registerC = 0;
    private List<Long> instructions;

    public Day17(){
        processFile("aoc2024/src/main/resources/day17.txt");
    }

    public String part1(){
        return runProgram(registerA, instructions).stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(","));
    }

    public long part2(){
        return findLowestValidValue(instructions, 0, 0);
    }

    private List<Long> runProgram(long registerA, List<Long> program) {
        int pointer = 0;
        List<Long> output = new ArrayList<>();
        while (pointer >= 0 && pointer < program.size()) {
            long opcode = program.get(pointer);
            long operand = program.get(pointer + 1);
            long combo = resolveOperand(operand, registerA, registerB, registerC);
            switch ((int) opcode) {
                case 0: registerA = divideByPowerOf2(registerA, combo); pointer += 2; break;
                case 1: registerB ^= operand; pointer += 2; break;
                case 2: registerB = combo % 8; pointer += 2; break;
                case 3: pointer = (registerA != 0) ? (int) operand : pointer + 2; break;
                case 4: registerB ^= registerC; pointer += 2; break;
                case 5: output.add(combo % 8); pointer += 2; break;
                case 6: registerB = divideByPowerOf2(registerA, combo); pointer += 2; break;
                case 7: registerC = divideByPowerOf2(registerA, combo); pointer += 2; break;
            }
        }

        return output;
    }

    private long findLowestValidValue(List<Long> instructionList, long currentInstruction, int index) {
        for (int i = 0; i < 8; i++) {
            long nextNum = (currentInstruction << 3) + i;
            List<Long> output = runProgram(nextNum, instructionList);

            if (!isOutputPostShiftEqual(output, instructionList.subList(instructionList.size() - index - 1, instructionList.size()))) {
                continue;
            }
            if (isOutputPostShiftEqual(output, instructionList)) {
                return nextNum;
            }

            long result = findLowestValidValue(instructionList, nextNum, index + 1);
            if (result != -1) {
                return result;
            }
        }
        return -1;
    }

    private static boolean isOutputPostShiftEqual(List<Long> outputPostShift, List<Long> instructionList) {
        return outputPostShift.equals(instructionList);
    }

    private long divideByPowerOf2(long value, long power) {
        return (long) (value / Math.pow(2, power));
    }

    private long resolveOperand(long operand, long registerA, long registerB, long registerC) {
        if (operand < 4) {
            return operand;
        }
        switch ((int) operand) {
            case 4: return registerA;
            case 5: return registerB;
            case 6: return registerC;
            default: return operand;
        }
    }

    private void processFile(String filepath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            registerA = parseRegister(reader.readLine());
            skipLines(reader, 3);
            instructions = parseProgram(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long parseRegister(String line) {
        return Long.parseLong(line.split(":")[1].trim());
    }

    private void skipLines(BufferedReader reader, int linesToSkip) throws IOException {
        for (int i = 0; i < linesToSkip; i++) {
            reader.readLine();
        }
    }

    private List<Long> parseProgram(String line) {
        return Arrays.asList(line.split(":")[1].trim().split(","))
                .stream()
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}
