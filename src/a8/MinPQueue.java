package a8;

import java.util.ArrayList;

/**
 * A min priority queue of distinct elements of type `KeyType` associated with (extrinsic) double
 * priorities. Supports updating the priorities of elements currently in the queue, and guarantees 
 * O(log N) performance for all modifying operations, where N is the queue size.
 */
public class MinPQueue<KeyType> {

    /**
     * Pairs an element `key` with its associated priority `priority`.
     */
    private record Entry<KeyType>(KeyType key, double priority) { }

    /**
     * ArrayList representing a binary min-heap of element-priority pairs.  Satisfies
     * `heap.get(i).priority() >= heap.get((i-1)/2).priority()` for all `i` in `[1..heap.size())`.
     */
    private final ArrayList<Entry<KeyType>> heap;

    /**
     * Associates each element in the queue with its index in `heap`.  Satisfies
     * `heap.get(index.get(e)).key().equals(e)` if `e` is an element in the queue. 
     * Only maps elements that are in the queue (`index.size() == heap.size()`).
     */
    private final PacMap<KeyType, Integer> index;


    /**
     * Create an empty queue.
     */
    public MinPQueue() {
        index = new ProbingPacMap<>();
        heap = new ArrayList<>();
    }

    /**
     * Return whether this queue contains no elements.
     */
    public boolean isEmpty() {
        return heap.isEmpty();
    }

    /**
     * Return the number of elements contained in this queue.
     */
    public int size() {
        return heap.size();
    }

    /**
     * Return an element associated with the smallest priority in this queue.  This is the same
     * element that would be removed by a call to `remove()` (assuming no mutations in between).
     * Throws a `NoSuchElementException` if this queue is empty.
     */
    public KeyType peek() {
        // Propagate exception from `List::getFirst()` if empty.
        return heap.getFirst().key();
    }

    /**
     * Return the minimum priority associated with an element in this queue.  Throws
     * a `NoSuchElementException` if this queue is empty.
     */
    public double minPriority() {
        return heap.getFirst().priority();
    }

    /**
     * Swap the `Entry`s at indices `i` and `j` in `heap`, updating `index` accordingly.  Requires
     * `0 <= i,j < heap.size()`.
     */
    private void swap(int i, int j) {
        // TODO 6a: Implement this method according to its specification
        Entry<KeyType> iPos = heap.get(i);
        Entry<KeyType> jPos = heap.get(j);
        heap.set(i, jPos);
        heap.set(j, iPos);
        index.put(iPos.key(), j);
        index.put(jPos.key(), i);

    }

    // TODO 6b: Implement private helper methods for bubbling entries up and down in the heap.
    //  Their interfaces are up to you, but you must write precise specifications.
    /**
     * NEEDS SPEC
     */
    private void bubbleUp(int i) {
        if (i == 0) {
            return;
        }
        int parentPos = (i-1)/2;
        if (heap.get(i).priority() < heap.get(parentPos).priority()) {
            swap(i, parentPos);
            bubbleUp(parentPos);
        }
    }

    /**
     * NEEDS SPEC
     */
    private void bubbleDown(int i) {
        // Find the smaller child. Check if bigger than that child -> swap with child then bubbleDown on new pos.
        // find smaller child:
        int smallChildPos = 2 * i + 1;
        int rightChildPos = 2 * i + 2;
        if (smallChildPos >= heap.size()){
            return;
        } // Check if the right child is smaller than the left
        if (rightChildPos < heap.size() && heap.get(rightChildPos).priority() < heap.get(smallChildPos).priority()) {
            smallChildPos += 1;
        }
        if (heap.get(i).priority() > heap.get(smallChildPos).priority()){
            swap(i,smallChildPos);
            bubbleDown(smallChildPos);
        }
    }

    /**
     * Add element `key` to this queue, associated with priority `priority`.  Requires `key` is not
     * contained in this queue.
     */
    private void add(KeyType key, double priority) {
        // TODO 6c: Implement this method according to its specification
        heap.add(new Entry<>(key,priority));
        index.put(key, heap.size() - 1);
        bubbleUp(heap.size() - 1);
    }

    /**
     * Change the priority associated with element `key` to `priority`.  Requires that `key` is
     * contained in this queue.
     */
    private void update(KeyType key, double priority) {
        assert index.containsKey(key);

        // TODO 6d: Implement this method according to its specification
        int originalPos = index.get(key);
        heap.set(originalPos, new Entry<>(key, priority));
        bubbleUp(originalPos);
        bubbleDown(originalPos);

//        System.out.println(heap.get(originalPos));
//        swap(originalPos, heap.size()-1);
//        System.out.println(heap.getLast());
//        heap.removeLast();
//        bubbleDown(originalPos);
//        bubbleUp(originalPos);
//        heap.add(new Entry<>(key,priority));
//        index.put(key, heap.size() - 1);
//        bubbleUp(heap.size()-1);
    }

    /**
     * If `key` is already contained in this queue, change its associated priority to `priority`.
     * Otherwise, add it to this queue with that priority.
     */
    public void addOrUpdate(KeyType key, double priority) {
        if (!index.containsKey(key)) {
            add(key, priority);
            System.out.println("add called");
        } else {
            System.out.println("update called");
            update(key, priority);
        }
    }

    /**
     * Remove and return the element associated with the smallest priority in this queue.  If
     * multiple elements are tied for the smallest priority, an arbitrary one will be removed.
     * Throws NoSuchElementException if this queue is empty.
     */
    public KeyType remove() {
        // TODO 6e: Implement this method according to its specification
        swap(0,heap.size()-1);
        Entry<KeyType> value = heap.removeLast();
        bubbleDown(0);
        return value.key();
    }
}