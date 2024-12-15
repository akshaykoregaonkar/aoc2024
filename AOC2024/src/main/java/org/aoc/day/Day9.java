package org.aoc.day;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Day9 {
    Map<Integer, int[]> fileMap = new HashMap<>();

    public Day9() {
        processFile("aoc2024/src/main/resources/day9.txt");
    }

    public long part1() {
        List<Integer> defragmentedList = defragmentWithFiles();
        return calcCheckSum(defragmentedList);
    }

    public long part2() {
        List<Integer> defragmentedList = defragmentWithFileBlocks();
        return calcCheckSum(defragmentedList);
    }

    private void processFile(String filepath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line = reader.readLine();
            String[] numbers = line.split("");
            for (int i = 0; i < numbers.length; i += 2) {
                int fileCount = Integer.parseInt(numbers[i]);
                int freeSpace = (i + 1 < numbers.length) ? Integer.parseInt(numbers[i + 1]) : 0;
                fileMap.put(i / 2, new int[]{fileCount, freeSpace});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Integer> defragmentWithFiles() {
        Map<Integer, int[]> fileMapPart1 = deepCopyFileMap(fileMap);
        List<Integer> defragmentedList = new ArrayList<>();
        int blockAtEndPointer = fileMapPart1.size() - 1;
        for (Map.Entry<Integer, int[]> entry : fileMapPart1.entrySet()) {
            int fileId = entry.getKey();
            int fileCount = entry.getValue()[0];
            int freeSpace = entry.getValue()[1];

            addFilesToList(defragmentedList, fileId, fileCount);

            if (blockAtEndPointer == fileId) {
                break;
            }

            blockAtEndPointer = fillFreeSpace(fileMapPart1, defragmentedList, blockAtEndPointer, freeSpace);
        }
        return defragmentedList;
    }

    private int fillFreeSpace(Map<Integer, int[]> fileMapPart1, List<Integer> defragmentedList, int blockAtEndPointer, int freeSpace) {
        while (freeSpace > 0) {
            int[] blockData = fileMapPart1.get(blockAtEndPointer);

            if (blockData[0] == 0) {
                blockAtEndPointer--;
                continue;
            }

            defragmentedList.add(blockAtEndPointer);
            blockData[0]--;
            freeSpace--;
        }
        if (fileMapPart1.get(blockAtEndPointer)[0] == 0) {
            blockAtEndPointer--;
        }
        return blockAtEndPointer;
    }

    private List<Integer> defragmentWithFileBlocks() {
        Map<Integer, int[]> fileMapPart2 = deepCopyFileMap(fileMap);
        Set<Integer> processed = new HashSet<>();
        int blockAtEndPointer = fileMapPart2.size() - 1;
        List<Integer> defragmented = new ArrayList<>();

        for (Map.Entry<Integer, int[]> entry : fileMapPart2.entrySet()) {
            int fileId = entry.getKey();
            int fileCount = entry.getValue()[0];
            int freeSpace = entry.getValue()[1];

            addFilesToList(defragmented, !processed.contains(fileId) ? fileId : 0, fileCount);

            if (blockAtEndPointer == fileId) {
                break;
            }

            freeSpace = backfillBlocks(fileMapPart2, defragmented, blockAtEndPointer, freeSpace, fileId, processed);
            if (freeSpace > 0) {
                addFilesToList(defragmented, 0, freeSpace);
            }
        }
        return defragmented;
    }

    private int backfillBlocks(Map<Integer, int[]> fileMapPart2, List<Integer> defragmentedList, int blockAtEndPointer, int freeSpace, int current, Set<Integer> processed) {
        while (freeSpace > 0 && blockAtEndPointer >= current) {
            int[] blockData = fileMapPart2.get(blockAtEndPointer);
            int blockSize = blockData[0];

            // Skip blocks that cannot fit or are already processed
            if (blockSize <= freeSpace && !processed.contains(blockAtEndPointer)) {
                addFilesToList(defragmentedList, blockAtEndPointer, blockSize);
                processed.add(blockAtEndPointer);
                freeSpace -= blockSize;
            }

            if (blockAtEndPointer == current) {
                break;
            }
            blockAtEndPointer--;
        }
        return freeSpace;
    }

    private static void addFilesToList(List<Integer> defragmentedList, int fileId, int fileCount) {
        defragmentedList.addAll(Collections.nCopies(fileCount, fileId));
    }

    private static long calcCheckSum(List<Integer> defragmentedList) {
        long checkSum = 0;
        for (int i = 0; i < defragmentedList.size(); i++) {
            checkSum += (long) i * defragmentedList.get(i);
        }
        return checkSum;
    }

    private Map<Integer, int[]> deepCopyFileMap(Map<Integer, int[]> original) {
        return original.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> Arrays.copyOf(entry.getValue(), entry.getValue().length)
                ));
    }
}
