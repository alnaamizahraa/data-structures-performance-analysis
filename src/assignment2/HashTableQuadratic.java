package assignment2;
/**
* HashTableQuadratic.java
* Hash Table using Quadratic Probing (open addressing) for collision resolution.
* Probe sequence: (hash(key) + i*i) % tableSize  for i = 0, 1, 2, …
* Hash function: key % tableSize  (modulo division)
* No use of java.util.* collections.
*
* note to myself: Quadratic probing is guaranteed to find an empty slot if the table
* is at most half full AND the table size is prime.
*/
public class HashTableQuadratic {
    // Special sentinel for deleted slots
	private static final int EMPTY   = Integer.MIN_VALUE;
    private static final int DELETED = Integer.MIN_VALUE + 1;
    
    //fields
    private final int[] table;   // the flat array
    private final int   size;    // total slots
    private       int   count;   // elements currently stored
 
 // constructor 
    /**
     * Creates a hash table with the specified number of slots.
     * @param tableSize must be prime and at least 2× expected element count
     */
    public HashTableQuadratic(int tableSize) {
        this.size  = tableSize;
        this.table = new int[tableSize];
        this.count = 0;
 
        // marks every slot as empt
        for (int i = 0; i < tableSize; i++) {
            table[i] = EMPTY;
        }
    }
    // Hash function
    /**
     * Modulo-division hash.
     * Math.abs guards against negative Java % results.
     */
    private int hash(int key) {
        return Math.abs(key) % size;
    }
    //INSERT
    /**
     * Inserts a key using quadratic probing.
     * Duplicate keys are ignored.
     * Throws RuntimeException if the table is full (should not happen with correct sizing).
     */
    public void insert(int key) {
        int startIdx = hash(key);
        int firstDeleted = -1;   // first DELETED slot we passed over
 
        for (int i = 0; i < size; i++) {
            int idx = (startIdx + i * i) % size;
 
            if (table[idx] == EMPTY) {
                // Use first deleted slot if we passed one; otherwise use this empty slot
                int insertAt = (firstDeleted != -1) ? firstDeleted : idx;
                table[insertAt] = key;
                count++;
                return;
 
            } else if (table[idx] == DELETED) {
                if (firstDeleted == -1) firstDeleted = idx;  // remember first deleted
 
            } else if (table[idx] == key) {
                return;  // duplicate – ignore
            }
        }
        // If we only passed deleted slots and never found EMPTY, use firstDeleted
        if (firstDeleted != -1) {
            table[firstDeleted] = key;
            count++;
            return;
        }
 
        throw new RuntimeException("Hash table is full – cannot insert key " + key);
    }
    //SEARCH
    //returns true if key exists in the table
    public boolean search(int key) {
        return findIndex(key) != -1;
    }
    /**
     * Returns the array index where the key is stored, or -1 if not found.
     * Uses the same quadratic probe sequence as insert.
     */
    private int findIndex(int key) {
        int startIdx = hash(key);
 
        for (int i = 0; i < size; i++) {
            int idx = (startIdx + i * i) % size;
 
            if (table[idx] == EMPTY) {
                return -1;          // cluster ended – key definitely not here
            } else if (table[idx] == DELETED) {
                continue;           // skip deleted slots and keep probing
            } else if (table[idx] == key) {
                return idx;         // found
            }
        }
        return -1;
    }
    // verification: getQuadraticIndex(int key)
    /**
     * Prints the array index where the specified key is stored.
     * Required verification method for graders.
     */
    public void getQuadraticIndex(int key) {
        int idx = findIndex(key);
        if (idx == -1) {
            System.out.println("Key " + key + " not found in Quadratic Probing table.");
        } else {
            System.out.println("Key " + key + " is stored at index " + idx + ".");
        }
    }
    // Utility
    // returns number of elements currently stored
    public int count() { return count; }
 
    // returns the total number of slots in the table
    public int tableSize() { return size; }
}

        
  
