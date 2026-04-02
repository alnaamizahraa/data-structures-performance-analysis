# data-structures-performance-analysis
---

## System Configuration
- Java Version: OpenJDK 21  
- Operating System: macOS 
- CPU: Apple M3

---

## Project Description
This project implements and evaluates the performance of four data structures in handling large datasets:

1. AVL Tree (self-balancing binary search tree)  
2. Splay Tree (self-adjusting binary search tree)  
3. Hash Table (Chaining) using linked lists  
4. Hash Table (Quadratic Probing) using open addressing  

Each data structure supports:
- Insertion of unique keys  
- Search (lookup) operations  

Performance is measured across three dataset sizes:
- Small (1,000 elements)  
- Medium (10,000 elements)  
- Large (~76,000 elements)  

---

## Implementation Details

### AVL Tree
- Maintains balance using rotations (LL, RR, LR, RL)  
- Guarantees O(log n) time for insert and search  
- Stores height at each node  

### Splay Tree
- Self-adjusting tree  
- Frequently accessed elements move closer to root  
- Uses zig, zig-zig, zig-zag rotations  

### Hash Table (Chaining)
- Uses array of linked lists  
- Handles collisions via separate chaining  
- Hash function: key % tableSize  

### Hash Table (Quadratic Probing)
- Uses open addressing  
- Collision resolution:  
  index = (hash + i^2) % tableSize  
- Reduces clustering compared to linear probing  

---

## Performance Results

### Insertion Time (ms)

| Data Structure           | 1,000 | 10,000 | ~76,000 |
|------------------------|------|--------|----------|
| AVL Tree               | 0.13 | 1.71   | 15.73    |
| Splay Tree             | 0.22 | 3.17   | 29.07    |
| Hash Table (Chaining)  | 0.09 | 0.60   | 2.78     |
| Hash Table (Quadratic) | 0.15 | 0.72   | 2.73     |

---

### Search Time (ms)

| Data Structure           | 1,000 | 10,000 | ~76,000 |
|------------------------|------|--------|----------|
| AVL Tree               | 0.14 | 1.81   | 13.18    |
| Splay Tree             | 0.32 | 4.89   | 43.95    |
| Hash Table (Chaining)  | 0.10 | 0.94   | 4.79     |
| Hash Table (Quadratic) | 0.13 | 1.05   | 5.88     |

---

## Memory Usage

### Insertion Memory (bytes)

| Data Structure           | 1,000 | 10,000 | ~76,000 |
|------------------------|--------|---------|-----------|
| AVL Tree               | 0      | 372,992 | 2,621,440 |
| Splay Tree             | 62,928 | 372,992 | 2,510,296 |
| Hash Table (Chaining)  | 62,936 | 310,800 | 2,232,496 |
| Hash Table (Quadratic) | 62,936 | 121,512 | 1,572,864 |

---

### Search Memory (bytes)

| Data Structure           | 1,000 | 10,000 | ~76,000 |
|------------------------|--------|---------|-----------|
| AVL Tree               | 0      | 0       | 0         |
| Splay Tree             | 0      | 0       | 0         |
| Hash Table (Chaining)  | 0      | 0       | 0         |
| Hash Table (Quadratic) | 0      | 0       | 0         |

Note: Memory measurements may be affected by Java garbage collection and may not be perfectly precise.

---

## Discussion

### Performance Observations

- AVL Tree:
  - Showed consistent and predictable performance across all dataset sizes  
  - Insertion time increased steadily due to the cost of maintaining balance  
  - Search remained efficient and stable due to O(log n) complexity  

- Splay Tree:
  - Slower than AVL for both insertion and search  
  - Performance degraded significantly at larger dataset sizes  
  - Limited benefit observed due to lack of repeated access patterns  

- Hash Table (Chaining):
  - Achieved the fastest insertion and search times overall  
  - Scaled efficiently with increasing input size  
  - Collisions handled effectively using linked lists  

- Hash Table (Quadratic Probing):
  - Slightly slower than chaining due to probing  
  - Maintained strong performance overall  
  - Minor slowdown observed at larger sizes due to clustering  

---

### Trade-offs

| Structure | Pros | Cons |
|----------|------|------|
| AVL Tree | Balanced, consistent performance | Overhead from rotations |
| Splay Tree | Adapts to access patterns | Slower for random data |
| Chaining | Fast and simple | Uses extra memory |
| Quadratic | Cache-friendly | Clustering and probing cost |

---

### Observations

- Hash tables significantly outperformed tree-based structures in both insertion and search  
- AVL trees provided stable and reliable performance regardless of input  
- Splay trees performed the worst due to lack of repeated access patterns  
- Quadratic probing used less memory than chaining but had slightly slower performance  

---

## Conclusion

This experiment compared the performance of AVL Trees, Splay Trees, and Hash Tables under varying dataset sizes.

Hash tables demonstrated the best overall performance, with chaining achieving the fastest results. Quadratic probing also performed well, though slightly slower due to probing overhead.

AVL trees maintained consistent and reliable performance but were slower due to balancing operations.

Splay trees performed the worst in this experiment, especially at larger sizes, due to the overhead of restructuring and lack of repeated access benefits.

Overall, hash tables are the best choice for fast lookups, while AVL trees are preferable when consistent performance and ordered data are required.

---

## Files Included
- AVLTree.java  
- SplayTree.java  
- HashTableChaining.java  
- HashTableQuadratic.java  
- Main.java  
- README.md  
