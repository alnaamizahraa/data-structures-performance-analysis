package assignment2;

import java.io.*;
import java.util.*;

/**
 * Main.java
 * Driver class for DSA Assignment 2.
 *
 * Reads insert/search key files, runs all four data structures,
 * measures time and memory, prints results in the required table format,
 **/
public class Main {



    // Configuration
  

    // relative path prefix for key files (change if files are elsewhere)
    private static final String FILE_PATH = "";

    // Number of JVM warm-up runs before timed measurement
    private static final int WARMUP_RUNS = 3;

    // iteration labels of diff sizes 
    private static final String[] ITR_LABEL = {"Small (1,000)", "Medium (10,000)", "Large (~76,000)"};

    // table sizes as specified in the assignment
    private static final int[] CHAIN_SIZES    = {928,   8_329,   83_329};
    private static final int[] QUADRATIC_SIZES = {2_003, 20_011, 200_003};

    // Entry point
    public static void main(String[] args) throws Exception {

        System.out.println("=== DSA Assignment 2 – Performance Benchmark ===\n");

        // Result storage [structure][iteration]
        // 0=AVL  1=Splay  2=Chain  3=Quadratic
        double[][] insertTime  = new double[4][3];
        double[][] searchTime  = new double[4][3];
        long[][]   insertMem   = new long[4][3];
        long[][]   searchMem   = new long[4][3];

        //running all three iterations 
        for (int itr = 0; itr < 3; itr++) {
            int iterNum = itr + 1;
            System.out.println("─── Iteration " + iterNum + " : " + ITR_LABEL[itr] + " ───");

            // Load keys from files
            int[] insertKeys = loadKeys(FILE_PATH + "iter" + iterNum + "_insert_keys.txt");
            int[] searchKeys = loadKeys(FILE_PATH + "iter" + iterNum + "_search_keys.txt");

            if (insertKeys == null || searchKeys == null) {
                System.out.println("  [ERROR] Could not load key files for iteration " + iterNum
                    + ". Skipping.\n");
                continue;
            }
            System.out.println("  Loaded " + insertKeys.length + " insert keys and "
                + searchKeys.length + " search keys.\n");

            // AVL Tree
            System.out.println("  [AVL Tree]");
            warmupAVL(insertKeys, searchKeys);  // JVM warm-up

            // Insert timing + memory
            long[] avlIns = benchmarkAVLInsert(insertKeys);
            insertTime[0][itr] = avlIns[0] / 1_000_000.0;   // ns -> ms
            insertMem [0][itr] = avlIns[1];

            // Build final tree for search test (fresh, so search doesn't get warm data)
            AVLTree avlTree = new AVLTree();
            for (int k : insertKeys) avlTree.insert(k);

            long[] avlSrc = benchmarkAVLSearch(avlTree, searchKeys);
            searchTime[0][itr] = avlSrc[0] / 1_000_000.0;
            searchMem [0][itr] = avlSrc[1];

            // splay Tree 
            System.out.println("  [Splay Tree]");
            warmupSplay(insertKeys, searchKeys);

            long[] splayIns = benchmarkSplayInsert(insertKeys);
            insertTime[1][itr] = splayIns[0] / 1_000_000.0;
            insertMem [1][itr] = splayIns[1];

            SplayTree splayTree = new SplayTree();
            for (int k : insertKeys) splayTree.insert(k);

            long[] splaySrc = benchmarkSplaySearch(splayTree, searchKeys);
            searchTime[1][itr] = splaySrc[0] / 1_000_000.0;
            searchMem [1][itr] = splaySrc[1];

            // Hash Table , Chaining
            System.out.println("  [Hash Table – Chaining]");
            warmupChain(insertKeys, searchKeys, CHAIN_SIZES[itr]);

            long[] chainIns = benchmarkChainInsert(insertKeys, CHAIN_SIZES[itr]);
            insertTime[2][itr] = chainIns[0] / 1_000_000.0;
            insertMem [2][itr] = chainIns[1];

            HashTableChaining chainTable = new HashTableChaining(CHAIN_SIZES[itr]);
            for (int k : insertKeys) chainTable.insert(k);

            long[] chainSrc = benchmarkChainSearch(chainTable, searchKeys);
            searchTime[2][itr] = chainSrc[0] / 1_000_000.0;
            searchMem [2][itr] = chainSrc[1];

            // hash table, Quadratic Probing
            System.out.println("  [Hash Table – Quadratic Probing]");
            warmupQuad(insertKeys, searchKeys, QUADRATIC_SIZES[itr]);

            long[] quadIns = benchmarkQuadInsert(insertKeys, QUADRATIC_SIZES[itr]);
            insertTime[3][itr] = quadIns[0] / 1_000_000.0;
            insertMem [3][itr] = quadIns[1];

            HashTableQuadratic quadTable = new HashTableQuadratic(QUADRATIC_SIZES[itr]);
            for (int k : insertKeys) quadTable.insert(k);

            long[] quadSrc = benchmarkQuadSearch(quadTable, searchKeys);
            searchTime[3][itr] = quadSrc[0] / 1_000_000.0;
            searchMem [3][itr] = quadSrc[1];

            System.out.println();
        }

        // Print summary tables
        printTable("Insertion Performance (Time in ms)",
                   insertTime, new String[]{"1,000","10,000","100,000"});
        printTable("Search Performance (Time in ms)",
                   searchTime, new String[]{"1,000","10,000","100,000"});
        printTable("Insertion Memory (bytes)",
                   toDouble(insertMem), new String[]{"1,000","10,000","100,000"});
        printTable("Search Memory (bytes)",
                   toDouble(searchMem), new String[]{"1,000","10,000","100,000"});

    }

    // File loader
    //
    // Reads integers (one per line) from a text file
     // Returns null if the file cannot be opened
   
    private static int[] loadKeys(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("  [WARN] File not found: " + filename);
            return null;
        }

        // first pass: countin the lines
        int lineCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.readLine() != null) lineCount++;
        } catch (IOException e) {
            System.out.println("  [WARN] Could not read " + filename + ": " + e.getMessage());
            return null;
        }

        // second pass: filling the array
        int[] keys = new int[lineCount];
        int   idx  = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    keys[idx++] = Integer.parseInt(line);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("  [WARN] Error parsing " + filename + ": " + e.getMessage());
            return null;
        }

        return (idx < lineCount) ? java.util.Arrays.copyOf(keys, idx) : keys;
    }


    // Benchmark helpers, return [timeNs, memBytes]
    private static long[] benchmarkAVLInsert(int[] keys) throws Exception {
        System.gc(); Thread.sleep(50);
        Runtime rt = Runtime.getRuntime();
        long memBefore = rt.totalMemory() - rt.freeMemory();
        long t0 = System.nanoTime();

        AVLTree tree = new AVLTree();
        for (int k : keys) tree.insert(k);

        long elapsed = System.nanoTime() - t0;
        long memAfter = rt.totalMemory() - rt.freeMemory();
        return new long[]{elapsed, Math.max(0, memAfter - memBefore)};
    } 

    private static long[] benchmarkAVLSearch(AVLTree tree, int[] keys) throws Exception {
        System.gc(); Thread.sleep(50);
        Runtime rt = Runtime.getRuntime();
        long memBefore = rt.totalMemory() - rt.freeMemory();
        long t0 = System.nanoTime();

        for (int k : keys) tree.search(k);

        long elapsed = System.nanoTime() - t0;
        long memAfter = rt.totalMemory() - rt.freeMemory();
        return new long[]{elapsed, Math.max(0, memAfter - memBefore)};
    }

    private static long[] benchmarkSplayInsert(int[] keys) throws Exception {
        System.gc(); Thread.sleep(50);
        Runtime rt = Runtime.getRuntime();
        long memBefore = rt.totalMemory() - rt.freeMemory();
        long t0 = System.nanoTime();

        SplayTree tree = new SplayTree();
        for (int k : keys) tree.insert(k);

        long elapsed = System.nanoTime() - t0;
        long memAfter = rt.totalMemory() - rt.freeMemory();
        return new long[]{elapsed, Math.max(0, memAfter - memBefore)};
    }

    private static long[] benchmarkSplaySearch(SplayTree tree, int[] keys) throws Exception {
        System.gc(); Thread.sleep(50);
        Runtime rt = Runtime.getRuntime();
        long memBefore = rt.totalMemory() - rt.freeMemory();
        long t0 = System.nanoTime();

        for (int k : keys) tree.search(k);

        long elapsed = System.nanoTime() - t0;
        long memAfter = rt.totalMemory() - rt.freeMemory();
        return new long[]{elapsed, Math.max(0, memAfter - memBefore)};
    }

    private static long[] benchmarkChainInsert(int[] keys, int tableSize) throws Exception {
        System.gc(); Thread.sleep(50);
        Runtime rt = Runtime.getRuntime();
        long memBefore = rt.totalMemory() - rt.freeMemory();
        long t0 = System.nanoTime();

        HashTableChaining ht = new HashTableChaining(tableSize);
        for (int k : keys) ht.insert(k);

        long elapsed = System.nanoTime() - t0;
        long memAfter = rt.totalMemory() - rt.freeMemory();
        return new long[]{elapsed, Math.max(0, memAfter - memBefore)};
    }

    private static long[] benchmarkChainSearch(HashTableChaining ht, int[] keys) throws Exception {
        System.gc(); Thread.sleep(50);
        Runtime rt = Runtime.getRuntime();
        long memBefore = rt.totalMemory() - rt.freeMemory();
        long t0 = System.nanoTime();

        for (int k : keys) ht.search(k);

        long elapsed = System.nanoTime() - t0;
        long memAfter = rt.totalMemory() - rt.freeMemory();
        return new long[]{elapsed, Math.max(0, memAfter - memBefore)};
    }

    private static long[] benchmarkQuadInsert(int[] keys, int tableSize) throws Exception {
        System.gc(); Thread.sleep(50);
        Runtime rt = Runtime.getRuntime();
        long memBefore = rt.totalMemory() - rt.freeMemory();
        long t0 = System.nanoTime();

        HashTableQuadratic ht = new HashTableQuadratic(tableSize);
        for (int k : keys) ht.insert(k);

        long elapsed = System.nanoTime() - t0;
        long memAfter = rt.totalMemory() - rt.freeMemory();
        return new long[]{elapsed, Math.max(0, memAfter - memBefore)};
    }

    private static long[] benchmarkQuadSearch(HashTableQuadratic ht, int[] keys) throws Exception {
        System.gc(); Thread.sleep(50);
        Runtime rt = Runtime.getRuntime();
        long memBefore = rt.totalMemory() - rt.freeMemory();
        long t0 = System.nanoTime();

        for (int k : keys) ht.search(k);

        long elapsed = System.nanoTime() - t0;
        long memAfter = rt.totalMemory() - rt.freeMemory();
        return new long[]{elapsed, Math.max(0, memAfter - memBefore)};
    }


    // JVM Warm-up runs (discarded)
    private static void warmupAVL(int[] ins, int[] src) {
        for (int r = 0; r < WARMUP_RUNS; r++) {
            AVLTree t = new AVLTree();
            for (int k : ins) t.insert(k);
            for (int k : src) t.search(k);
        }
    }

    private static void warmupSplay(int[] ins, int[] src) {
        for (int r = 0; r < WARMUP_RUNS; r++) {
            SplayTree t = new SplayTree();
            for (int k : ins) t.insert(k);
            for (int k : src) t.search(k);
        }
    }

    private static void warmupChain(int[] ins, int[] src, int sz) {
        for (int r = 0; r < WARMUP_RUNS; r++) {
            HashTableChaining t = new HashTableChaining(sz);
            for (int k : ins) t.insert(k);
            for (int k : src) t.search(k);
        }
    }

    private static void warmupQuad(int[] ins, int[] src, int sz) {
        for (int r = 0; r < WARMUP_RUNS; r++) {
            HashTableQuadratic t = new HashTableQuadratic(sz);
            for (int k : ins) t.insert(k);
            for (int k : src) t.search(k);
        }
    }

  
    // structure table layout
    private static final String[] DS_NAMES = {
        "AVL Tree",
        "Splay Tree",
        "Hash Table (Chaining)",
        "Hash Table (Quadratic)"
    };

    private static void printTable(String title, double[][] data, String[] colHeaders) {
        System.out.println(title);
        System.out.printf("%-26s", "Data Structure");
        for (String h : colHeaders) System.out.printf("%14s", h);
        System.out.println();
        System.out.println("-".repeat(26 + colHeaders.length * 14));
        for (int ds = 0; ds < data.length; ds++) {
            System.out.printf("%-26s", DS_NAMES[ds]);
            for (int itr = 0; itr < colHeaders.length; itr++) {
                System.out.printf("%14.2f", data[ds][itr]);
            }
            System.out.println();
        }
        System.out.println();
    }

    // Converts long[][] to double[][] for the shared printTable method
    private static double[][] toDouble(long[][] src) {
        double[][] dst = new double[src.length][src[0].length];
        for (int i = 0; i < src.length; i++)
            for (int j = 0; j < src[i].length; j++)
                dst[i][j] = src[i][j];
        return dst;
    }
     //HERE
}
