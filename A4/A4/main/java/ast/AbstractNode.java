package ast;

import java.util.ArrayList;
import java.util.List;

import parse.TokenType;

public abstract class AbstractNode implements Node {

    protected List<Node> children;
    protected Node parent;
    protected TokenType value;

    public AbstractNode() {
        children = new ArrayList<Node>();
    }

    public AbstractNode(TokenType value) {
        this();
        this.value = value;
    }

    /**
     * Constructs an AbstractNode from two children and a TokenType
     * 
     * @param left  the left node
     * @param value the symbol this node holds
     * @param right the right node
     */
    public AbstractNode(Node left, TokenType value, Node right) {
        this(value);
        children.add(left);
        left.setParent(this);
        children.add(right);
        right.setParent(this);
    }

    public int size() {
        int size = 1;
        for (Node child : children) {
            size += child.size();
        }
        return size;
    }

    @Override
    public Node nodeAt(int index) throws IndexOutOfBoundsException {
        List<Node> list = new ArrayList<Node>();
        InOrderTraversal(this, list);
        return list.get(index);
    }

    /**
     * Traverse the tree from n and put the nodes into list inorder
     * 
     * @param n
     * @param list
     */
    private void InOrderTraversal(Node n, List<Node> list) {
        list.add(n);
        for (Node child : n.getChildren()) {
            InOrderTraversal(child, list);
        }
    }

    @Override
    public abstract StringBuilder prettyPrint(StringBuilder sb);

    @Override
    public Node clone() {
        // creates a shallow clone
        Node shallow = this.shallowClone();
        // add clones of children to the shallow clone
        for (Node child : children) {
            Node childClone = child.clone();
            childClone.setParent(shallow);
            shallow.getChildren().add(childClone);
        }
        return shallow;
    }

    /**
     * Implementation left for subclass for dynamic dispatch
     * 
     * @return a copy of this Node with same value but empty children
     */
    public abstract Node shallowClone();

    @Override
    public List<Node> getChildren() {
        return children;
    }

    public TokenType value() {
        return value;
    }

    @Override
    public Node getParent() {
        return parent;
    }

    public void setParent(Node p) {
        this.parent = p;
    }

    public String toString() {
        return prettyPrint(new StringBuilder()).toString();
    }
}
