package org.aoc.day;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Day24 {

    Map<String, Integer> wireMap = new HashMap<>();
    List<String> unprocessedGates = new ArrayList<>();

    public Day24(){
        processFile("aoc2024/src/main/resources/day24.txt");
    }

    private void processFile(String filepath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            boolean isStartingValue = true;
            while ((line = reader.readLine()) != null) {
                if(line.isEmpty()){
                    isStartingValue = false;
                } else if (isStartingValue) {
                    String[] input = line.replaceAll("\\s+", "").split(":");
                    wireMap.put(input[0], Integer.parseInt(input[1]));
                } else{
                    unprocessedGates.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long part1(){
        simulateGates();
        return Long.parseLong(getZBinary(), 2);
    }

    public String part2(){
        return findFaultyGates();
    }

    private String findFaultyGates() {
        List<Gate> gates = unprocessedGates.stream()
                .map(this::parseGate)
                .collect(Collectors.toList());

        return gates.stream()
                .filter(g-> isFaulty(g, gates))
                .map(g -> g.output)
                .sorted()
                .collect(Collectors.joining(","));
    }

    private Gate parseGate(String gate) {
        String[] gateParams = gate.split(" -> ");
        String[] inputs = gateParams[0].split(" ");
        return new Gate(inputs[0], inputs[1], inputs[2], gateParams[1]);
    }

    /**
     * In a ripple carry adder, the XOR gates are primarily used for sum calculation,
     * AND gates for carry generation, and OR gates assist in carry propagation.
     * So:
     * Valid XOR must contain one of x, y, z since this is used to sum inputs
     * An output gate used in an AND operation shouldn't be used in an XOR anywhere else as this is used for carry generation
     * An output gate used in an XOR operation shouldn't be used in an OR anywhere else as this is used in carry propagation
     * All gates that output z should have an XOR operation unless its z45 (i.e. last output)
     * **/
    private boolean isFaulty(Gate gate, List<Gate> gates) {
        return (gate.op.equals("XOR") && isInvalidXOR(gate.input1, gate.input2, gate.output)) ||
                (gate.op.equals("AND") && !isHalfAdder(gate) && invalidConnectedOperation(gate.output, "XOR", gates)) ||
                (gate.op.equals("XOR") && !isHalfAdder(gate) && invalidConnectedOperation(gate.output, "OR", gates)) ||
                (!gate.op.equals("XOR") && gate.output.startsWith("z") && !gate.output.equals("z45"));
    }

    private boolean isHalfAdder(Gate gate) {
        return gate.input1.equals("x00");
    }

    private boolean invalidConnectedOperation(String currOutput, String operation, List<Gate> gates) {
        return gates.stream()
                .anyMatch(g -> g.op.equals(operation) && (g.output.equals(currOutput) || Arrays.asList(g.input1, g.input2).contains(currOutput)));
    }

    private boolean isInvalidXOR(String... gates) {
        return Arrays.stream(gates).noneMatch(var -> var.startsWith("x") || var.startsWith("y") || var.startsWith("z"));
    }

    private String getZBinary() {
        return wireMap.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("z"))
                .sorted(Map.Entry.<String, Integer>comparingByKey().reversed())
                .map(entry -> entry.getValue().toString())
                .collect(Collectors.joining());
    }

    private void simulateGates() {
        Queue<String> gates = new ArrayDeque<>(unprocessedGates);
        while(!gates.isEmpty()){
            String gate = gates.poll();
            String[] gateParams = gate.split(" -> ");
            String output = gateParams[1];
            String[] inputs = gateParams[0].split(" ");
            String input1 = inputs[0];
            String operation = inputs[1];
            String input2 = inputs[2];

            gates.remove(gate);
            if(wireMap.containsKey(input1) && wireMap.containsKey(input2)){
                resolveGate(input1, input2, operation, output);
            } else{
                gates.add(gate); // if unable to resolve then requeue
            }
        }
    }

    private void resolveGate(String input1, String input2, String operation, String output) {
        int input1Val = wireMap.get(input1);
        int input2Val = wireMap.get(input2);
        switch (operation){
            case "XOR":
                wireMap.put(output, input1Val ^ input2Val);
                break;
            case "OR":
                wireMap.put(output, input1Val | input2Val);
                break;
            case "AND":
                wireMap.put(output, input1Val & input2Val);
                break;
            default:
                throw new IllegalArgumentException("invalid operation detected");
        }
    }

    private class Gate {
        String input1, op, input2, output;

        Gate(String input1, String op, String input2, String output) {
            this.input1 = input1;
            this.op = op;
            this.input2 = input2;
            this.output = output;
        }

        @Override
        public String toString() {
            return input1 + " " + op + " " + input2 + " -> " + output;
        }
    }
}
