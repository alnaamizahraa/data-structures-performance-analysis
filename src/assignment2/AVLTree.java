package assignment2;

//Self balancing Binary Search Tree (AVL Tree) implementation 
// 3/25/26

public class AVLTree {
	
	// inner node class 
	
	private static class Node {
      int key;
      int height; 
      Node left, right;
      
      Node(int key){
    	  this.key = key;
    	  this.height = 1; // a new leaf will have a height 1
      }
		
	}
	
	// fields
	 private Node root;
	// Height helpers
	 
   // return height of a node (0 if null)
	 private int height(Node n) {
	        return (n == null) ? 0 : n.height;
	    }
	 // Recalculates and sets the height of a node based on its children
	 private void updateHeight(Node n) {
	        n.height = 1 + Math.max(height(n.left), height(n.right));
	    }
	 
	// balance factor = height(left) - height(right)
	 private int balanceFactor(Node n) {
	        return (n == null) ? 0 : height(n.left) - height(n.right);
	    }
	 // Rotations, right rotation around node y
	 private Node rotateRight(Node y) {
	        Node x  = y.left;
	        Node T2 = x.right;
	 
	        x.right = y;
	        y.left  = T2;
	 
	        updateHeight(y);
	        updateHeight(x);
	        return x;  // x is new root of this subtree
	    }
	 
	 // left rotation around node  x
	 private Node rotateLeft(Node x) {
	        Node y  = x.right;
	        Node T2 = y.left;
	 
	        y.left  = x;
	        x.right = T2;
	 
	        updateHeight(x);
	        updateHeight(y);
	        return y;  // y is new root of this subtree
	    }
	// balance (applying rotations based on BF)
	 
	 
	 /**
	     * checking balance factor and applies tothe appropriate rotation
	     * Four cases: LL, LR, RR, RL.
	     */
	    private Node balance(Node n) {
	        updateHeight(n);
	        int bf = balanceFactor(n);
	 
	        // left-heavy
	        if (bf > 1) {
	            if (balanceFactor(n.left) < 0)       // LR case
	                n.left = rotateLeft(n.left);
	            return rotateRight(n);               // LL case
	        }
	 
	        // right-heavy
	        if (bf < -1) {
	            if (balanceFactor(n.right) > 0)      // RL case
	                n.right = rotateRight(n.right);
	            return rotateLeft(n);                // RR case
	        }
	 
	        return n;  // already balanced
	    }
	    
	    //INSERT 
	    /** Inserts key into the AVL tree. Duplicate keys are ignored. */
	    public void insert(int key) {
	        root = insert(root, key);
	    }
	 
	    private Node insert(Node node, int key) {
	        // Standard BST insert
	        if (node == null) return new Node(key);
	 
	        if      (key < node.key) node.left  = insert(node.left,  key);
	        else if (key > node.key) node.right = insert(node.right, key);
	        else                     return node;  // duplicate – ignore
	 
	        // Rebalance on the way back up
	        return balance(node);
	    }
	 
	    //SEARCH
	    /** Returns true if key exists in the tree. */
	    public boolean search(int key) {
	        return search(root, key);
	    }
	 
	    private boolean search(Node node, int key) {
	        if (node == null)       return false;
	        if (key == node.key)    return true;
	        if (key < node.key)     return search(node.left,  key);
	        return                         search(node.right, key);
	    }
	    
	    //DELETE
	    public void delete(int key) {
	        root = delete(root, key);
	    }
	 
	    private Node delete(Node node, int key) {
	        if (node == null) return null;  // key not found
	 
	        if      (key < node.key) node.left  = delete(node.left,  key);
	        else if (key > node.key) node.right = delete(node.right, key);
	        else {
	            // Node to delete found
	            if (node.left == null)  return node.right;
	            if (node.right == null) return node.left;
	 
	            // Replace with in-order successor (smallest in right subtree)
	            Node successor = minNode(node.right);
	            node.key   = successor.key;
	            node.right = delete(node.right, successor.key);
	        }
	 
	        return balance(node);
	    }
	    
	    /** Returns the node with the minimum key in a subtree. */
	    private Node minNode(Node node) {
	        while (node.left != null) node = node.left;
	        return node;
	    }
	    
	    // verification: getAVLKeyHeight(int key)

	    /**
	     * Prints the height of the node with the given key.
	     * Required verification method for graders.
	     */
	    public void getAVLKeyHeight(int key) {
	        Node node = findNode(root, key);
	        if (node == null) {
	            System.out.println("Key " + key + " not found in AVL Tree.");
	        } else {
	            System.out.println("Height of node with key " + key + ": " + node.height);
	        }
	    }
	 
	    private Node findNode(Node node, int key) {
	        if (node == null)     return null;
	        if (key == node.key)  return node;
	        if (key < node.key)   return findNode(node.left,  key);
	        return                       findNode(node.right, key);
	    }
	    
	    // Utility: size
	    /** Returns the number of nodes in the tree. */
	    public int size() {
	        return size(root);
	    }
	 
	    private int size(Node node) {
	        if (node == null) return 0;
	        return 1 + size(node.left) + size(node.right);
	    }
	}
	    
 
