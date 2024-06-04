package bstmap;



import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable, V> implements Map61B {

    private Node root;

    private class Node{
        private K key;
        private V val;
        private Node left, right;
        private int size;

        public Node(K key, V val, int size) {
            this.key = key;
            this.val = val;
            this.size = size;
        }
    }


    @Override
    public void clear() {

    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public Object get(Object key) {
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(Object key, Object value) {

    }

    @Override
    public Set keySet() {
        return null;
    }

    @Override
    public Object remove(Object key) {
        return null;
    }

    @Override
    public Object remove(Object key, Object value) {
        return null;
    }

    @Override
    public Iterator iterator() {
        return null;
    }


    public static void printInOrder(){

    }
}
