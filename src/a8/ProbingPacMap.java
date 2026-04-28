package a8;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A map with keys of type 'K' and values of type `V`, implemented using a hash table with linear
 * probing.
 */
public class ProbingPacMap<K, V> implements PacMap<K, V> {

    /**
     * Represents an association of a key `key` (of type `K`) with a value `value` (of type `V`).
     */
    private record Entry<K, V>(K key, V value) {

    }

    /**
     * Represents a tombstone. If an entry at index `i` is removed, element `i` will be replaced by
     * a reference to this object. Tombstones count toward the load factor, and are cleared when the
     * hash table is resized.
     */
    private static final Entry TOMBSTONE = new Entry<>(null, null);

    /**
     * The initial capacity of the hash table for new instances of `ProbingPacMap`.
     */
    private static final int INITIAL_CAPACITY = 16;

    /**
     * The maximum load factor (inclusive) that is allowed in the `entries` hash table. If the load
     * factor ever exceeds this maximum, then the hash table length must be immediately doubled to
     * reduce the load factor. Must have `0 < MAX_LOAD_FACTOR < 1`.
     */
    public static final double MAX_LOAD_FACTOR = 0.5;

    /**
     * The probing hash table backing this map. Indices (i.e., buckets) that don't currently store
     * an entry (possibly a TOMBSTONE) are `null`. If this map contains an entry with a key whose
     * hash code maps to index `i`, then the (unique) entry containing that key is reachable via
     * linear search starting at index `i` (wrapping around the array if necessary) without
     * encountering `null`.
     */
    private Entry<K, V>[] entries;
    /**
     * The number of keys currently associated with values in this map.
     */
    private int size;

    /**
     * The number of Tombstones in the hash table at any given moment.
     */
    private int numberOfTombstones;

    /**
     * Create a new empty `ProbingPacMap`.
     */
    @SuppressWarnings("unchecked")
    public ProbingPacMap() {
        entries = new Entry[INITIAL_CAPACITY];
        size = 0;
        numberOfTombstones = 0;


    }

    /**
     * Returns the number of keys currently associated with values in this map. Runs in O(1) time.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns the current load factor of the hash table backing this map. Runs in O(1) time.
     */
    private double loadFactor() {
        return (size + numberOfTombstones + 0.0) / entries.length;
    }


    /**
     * If our load factor is greater than 0.5, then it replaces our entries array with an array of
     * doubles its length and rehashes all of its entries leaving tombstones behind
     */
    private void rehash() {
        if (loadFactor() > MAX_LOAD_FACTOR) {
            Entry<K, V>[] oldEntries = entries;
            entries = new Entry[2 * entries.length];
            int savedSize = size;
            for (int i = 0; i < oldEntries.length; i++) {
                if (oldEntries[i] != null && oldEntries[i] != TOMBSTONE) {
                    put(oldEntries[i].key(), oldEntries[i].value());
                }
            }
            size = savedSize;
            numberOfTombstones = 0;
        }
    }

    /**
     * If `key` is a key in this map, return the index in `entries` for this key. Otherwise, returns
     * the first index of a `null` or tombstone entry in the table at or after the index
     * corresponding to the key's hash code (wrapping around).
     */
    private int findEntry(K key) {
        int placement = Math.abs(key.hashCode()) % entries.length;
        if (entries[placement] == null) {
            return placement;
        }
        if (key.equals(entries[placement].key())) {
            return placement;
        }
        int tombstone = -1;
        for (int i = placement + 1; i < placement + entries.length; i++) {
            if (tombstone == -1 && entries[i % entries.length] == TOMBSTONE) {
                tombstone = i % entries.length;
            } else if (entries[i % entries.length] == null) {
                if (tombstone == -1) {
                    return i % entries.length;
                } else {
                    return tombstone;
                }
            } else {
                if (entries[i % entries.length].key() == key) {
                    return i % entries.length;
                }
            }
        }
        return -1;
    }


    @Override
    public boolean containsKey(K key) {
        int i = findEntry(key);
        if (entries[i] == null || entries[i] == TOMBSTONE) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public V get(K key) throws NoSuchElementException {
        int i = findEntry(key);
        if (entries[i] == null || entries[i] == TOMBSTONE) {
            throw new NoSuchElementException();
        } else {
            return entries[i].value();
        }
    }

    @Override
    public void put(K key, V value) {
        int i = findEntry(key);
        if (entries[i] == null) {
            size++;
            rehash();
        } else if (entries[i] == TOMBSTONE) {
            numberOfTombstones--;
            size++;
            rehash();
        }
        entries[i] = new Entry<>(key, value);
    }


    @Override
    @SuppressWarnings("unchecked")
    public V remove(K key) {
        int i = findEntry(key);
        if (entries[i] == null || entries[i] == TOMBSTONE) {
            throw new NoSuchElementException();
        } else {
            Entry<K, V> save = entries[i];
            entries[i] = TOMBSTONE;
            numberOfTombstones++;
            size--;
            return save.value();
        }
    }

    @Override
    public Iterator<K> iterator() {
        return new ProbingPacMapIterator();
    }

    /**
     * An iterator over the keys in this hash table. This map must not be structurally modified
     * while any such iterators are alive.
     */
    private class ProbingPacMapIterator implements Iterator<K> {

        /**
         * The index of the entry in `entries` containing the next value to yield, or
         * `entries.length` if all values have been yielded.
         */
        private int iNext;

        /**
         * Create a new iterator over this dictionary's keys.
         */
        ProbingPacMapIterator() {
            iNext = 0;
            findNext();
        }

        /**
         * Set `iNext` to the first index `i` not less than the current value of `iNext` such that
         * `entries[i] != null` and 'entries[i] != TOMBSTONE', or set it to `entries.length` if
         * there are no remaining non-null and non-tombstone entries.  Note that if `iNext` is
         * already the index of a non-null and non-tombstone entry, then it will not be changed.
         */
        private void findNext() {
            while (iNext < entries.length && (entries[iNext] == null
                    || entries[iNext] == TOMBSTONE)) {
                iNext += 1;
            }
        }

        @Override
        public boolean hasNext() {
            return iNext < entries.length;
        }

        @Override
        public K next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            K ans = entries[iNext].key;
            iNext += 1;
            findNext();
            return ans;
        }
    }
}