package assignment2;

//self-adjusting BST
//accessed node is moved to the root via a sequence of rotations ("splaying")


public class SplayTree {
    // Inner Node class
	private static class Node {
        int  key;
        Node left, right, parent;
 
        Node(int key) {
            this.key = key;
        }
    }
    // Field
    private Node root;

    // Rotation helpers
    // Replaces child pointer in parent (or updates root)
    
    private void setParent(Node child, Node parent) {
        if (child != null) child.parent = parent;
    }
    
    //Makes node `newChild` take the place of `oldChild` in its parent. 
    private void replaceNode(Node oldChild, Node newChild) {
        if (oldChild.parent == null) {
            root = newChild;                           // oldChild was root
        } else if (oldChild == oldChild.parent.left) {
            oldChild.parent.left  = newChild;
        } else {
            oldChild.parent.right = newChild;
        }
        setParent(newChild, oldChild.parent);
    }
    //Zig (right rotation) around node x whose left child is x
    private void rotateRight(Node p) {
        Node x = p.left;
        p.left = x.right;
        setParent(x.right, p);
        replaceNode(p, x);
        x.right = p;
        setParent(p, x);
    }
 //Zag (left rotation) around node x whose right child is x.
    private void rotateLeft(Node p) {
        Node x = p.right;
        p.right = x.left;
        setParent(x.left, p);
        replaceNode(p, x);
        x.left = p;
        setParent(p, x);
    }
    // Splay operation
   //Splays node x to the root using zig, zig-zig, and zig-zag steps
    private void splay(Node x) {
        while (x.parent != null) {
            Node p = x.parent;    // parent
            Node g = p.parent;    // grandparent (may be null)
 
            if (g == null) {
                // Zig step: p is root
                if (x == p.left) rotateRight(p);
                else             rotateLeft(p);
 
            } else if (x == p.left && p == g.left) {
                // Zig-zig (left-left): rotate parent first, then x
                rotateRight(g);
                rotateRight(p);
 
            } else if (x == p.right && p == g.right) {
                // Zig-zig (right-right)
                rotateLeft(g);
                rotateLeft(p);
 
            } else if (x == p.right && p == g.left) {
                // Zig-zag (left-right)
                rotateLeft(p);
                rotateRight(g);
 
            } else {
                // Zig-zag (right-left)
                rotateRight(p);
                rotateLeft(g);
            }
        }
        // x is now the root
    }
    
    //INSERT
    //Inserts a key. Duplicates are ignored. Inserted node is splayed to root. 
    public void insert(int key) {
        if (root == null) {
            root = new Node(key);
            return;
        }
 
        Node curr   = root;
        Node parent = null;
 
        // Standard BST walk to find insertion point
        while (curr != null) {
            parent = curr;
            if      (key < curr.key) curr = curr.left;
            else if (key > curr.key) curr = curr.right;
            else {
                // Duplicate: splay the existing node to root and return
                splay(curr);
                return;
            }
        }
 
        // Create and link new node
        Node newNode = new Node(key);
        newNode.parent = parent;
        if (key < parent.key) parent.left  = newNode;
        else                  parent.right = newNode;
 
        // Splay new node to root
        splay(newNode);
    }
    //SEARCH
    /**
     * Searches for a key.
     * If found, splays the node to the root and returns true.
     * If not found, splays the last visited node to root and returns false.
     */
    public boolean search(int key) {
        Node curr = root;
        Node last = null;
 
        while (curr != null) {
            last = curr;
            if      (key == curr.key) { splay(curr); return true; }
            else if (key <  curr.key)   curr = curr.left;
            else                        curr = curr.right;
        }
 
        // Splay last visited node (keeps tree amortised-balanced)
        if (last != null) splay(last);
        return false;
    }
    // Verification: DFSSplayTree()
    /**
     * Prints all keys using Depth-First Search (pre-order) traversal.
     * Required verification method for graders.
     */
    public void DFSSplayTree() {
        System.out.print("Splay Tree DFS (pre-order): ");
        dfsPreOrder(root);
        System.out.println();
    }
 
    /** Recursive pre-order DFS: root → left → right */
    private void dfsPreOrder(Node node) {
        if (node == null) return;
        System.out.print(node.key + " ");
        dfsPreOrder(node.left);
        dfsPreOrder(node.right);
    }
    
 // Utility: size
 //Returns the number of nodes in the tree. */
    public int size() {
        return size(root);
    }
 
    private int size(Node node) {
        if (node == null) return 0;
        return 1 + size(node.left) + size(node.right);
    }
}
