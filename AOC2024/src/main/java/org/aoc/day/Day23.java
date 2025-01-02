package org.aoc.day;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day23 {
    List<String> connections = new ArrayList<>();
    Map<String, Set<String>> graph = new HashMap<>();
    public Day23(){
        processFile("aoc2024/src/main/resources/day23.txt");
    }

    private void processFile(String filepath) {
        try {
            connections = Files.readAllLines(Paths.get(filepath));
            graph = buildGraph();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long part1(){
        return chiefsLanPartyCandidateCount();
    }

    public String part2(){
        return findLargestClique();
    }

    private long chiefsLanPartyCandidateCount() {
        Set<Set<String>> triplets = new HashSet<>();
        for (String computer : graph.keySet()) {
            List<String> neighbours = graph.get(computer).stream().collect(Collectors.toList());

            for (int i = 0; i < neighbours.size(); i++) {
                for (int j = i + 1; j < neighbours.size(); j++) {
                    String neighbor1 = neighbours.get(i);
                    String neighbor2 = neighbours.get(j);

                    if (potentiallyChiefsParty(computer, neighbor1, neighbor2) && graph.get(neighbor1).contains(neighbor2)) {
                        Set<String> triplet = new TreeSet<>(Arrays.asList(computer, neighbor1, neighbor2));
                        triplets.add(triplet);
                    }
                }
            }
        }
        return triplets.size();
    }

    private boolean potentiallyChiefsParty(String computer1, String computer2, String computer3) {
        return Stream.of(computer1, computer2, computer3).anyMatch(c -> c.startsWith("t"));
    }

    private String findLargestClique() {
        Set<String> largestClique = new TreeSet<>(Comparator.comparing(Object::toString));
        bronKerbosch(new HashSet<>(), new HashSet<>(graph.keySet()), new HashSet<>(), largestClique);
        return String.join(",", largestClique);
    }

    private void bronKerbosch(Set<String> currentClique, Set<String> potentialNodes, Set<String> excludedNodes, Set<String> largestClique) {
        if (potentialNodes.isEmpty() && excludedNodes.isEmpty()) {
            updateLargestClique(currentClique, largestClique);
            return;
        }

        String pivot = potentialNodes.stream().findFirst().orElse(null);

        Set<String> candidates = new HashSet<>(potentialNodes);
        if (pivot != null) {
            candidates.removeAll(graph.getOrDefault(pivot, Collections.emptySet()));
        }

        for (String node : candidates) {
            Set<String> newClique = createNewClique(currentClique, node);

            Set<String> neighbours = graph.get(node);
            Set<String> newPotentialNodes = intersect(potentialNodes, neighbours);
            Set<String> newExcluded = intersect(excludedNodes, neighbours);

            bronKerbosch(newClique, newPotentialNodes, newExcluded, largestClique);

            potentialNodes.remove(node);
            excludedNodes.add(node);
        }
    }

    private Set<String> intersect(Set<String> set1, Set<String> set2) {
        Set<String> newSet = new HashSet<>(set1);
        newSet.retainAll(set2);
        return newSet;
    }

    private static Set<String> createNewClique(Set<String> currentClique, String node) {
        Set<String> newClique = new HashSet<>(currentClique);
        newClique.add(node);
        return newClique;
    }

    private static void updateLargestClique(Set<String> currentClique, Set<String> largestClique) {
        if (currentClique.size() > largestClique.size()) {
            largestClique.clear();
            largestClique.addAll(currentClique);
        }
    }

    private Map<String, Set<String>> buildGraph() {
        Map<String, Set<String>> graph = new HashMap<>();

        for (String connection : connections) {
            String[] parts = connection.split("-");
            String computer1 = parts[0];
            String computer2 = parts[1];

            graph.computeIfAbsent(computer1, k -> new HashSet<>()).add(computer2);
            graph.computeIfAbsent(computer2, k -> new HashSet<>()).add(computer1);
        }

        return graph;
    }
}
