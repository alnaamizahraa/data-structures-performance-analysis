package assignment2;

/**
 *hash Table using Separate Chaining for collision resolution.
 * each bucket is a hand-rolled singly linked list (no java.util.* collections).
 * hash function: key % tableSize  (modulo division)
 */
public class HashTableChaining {
    // Inner Node for linked-list chains
	private static class Node {
        int  key;
        Node next;
 
        Node(int key) {
            this.key = key;
        }
    }
    // Fields
	private final Node[] table;   // array of chain heads
    private final int    size;    // number of buckets
    private       int    count;   // total elements stored
    
    // Constructor
    /**
     * Creates a hash table with the specified number of buckets.
     * @param tableSize number of buckets (should be prime for best distribution)
     */
    public HashTableChaining(int tableSize) {
        this.size  = tableSize;
        this.table = new Node[tableSize];   // all entries are null by default
        this.count = 0;
    }
    // Hash function
    /**
     * Modulo-division hash function.
     * Math.abs guards against negative keys (Java's % can return negatives).
     */
    private int hash(int key) {
        return Math.abs(key) % size;
    }
    
    //INSERT
    /**
     * Inserts a key into the hash table.
     * Duplicate keys are ignored (set semantics).
     */
    public void insert(int key) {
        int  idx  = hash(key);
        Node curr = table[idx];
 
        // Walk chain to check for duplicate
        while (curr != null) {
            if (curr.key == key) return;  // duplicate – ignore
            curr = curr.next;
        }
 
        // Prepend new node to chain (O(1))
        Node newNode = new Node(key);
        newNode.next = table[idx];
        table[idx]   = newNode;
        count++;
    }

    //SEARCH
    /** Returns true if key exists in the table. */
    public boolean search(int key) {
        int  idx  = hash(key);
        Node curr = table[idx];
 
        while (curr != null) {
            if (curr.key == key) return true;
            curr = curr.next;
        }
        return false;
    }
    // Verification: getChain(int index)
    /**
     * Prints all keys in the chain (bucket) at the given index.
     * Required verification method for graders.
     */
    public void getChain(int index) {
        if (index < 0 || index >= size) {
            System.out.println("Index " + index + " is out of range [0, " + (size - 1) + "].");
            return;
        }
 
        System.out.print("Chain at index " + index + ": ");
        Node curr = table[index];
        if (curr == null) {
            System.out.println("(empty)");
            return;
        }
 
        while (curr != null) {
            System.out.print(curr.key);
            if (curr.next != null) System.out.print(" -> ");
            curr = curr.next;
        }
        System.out.println();
    }
    // Utility
    /** Returns total number of elements stored. */
    public int count() { return count; }
 
    /** Returns the table (bucket) size. */
    public int tableSize() { return size; }
}
    
   
