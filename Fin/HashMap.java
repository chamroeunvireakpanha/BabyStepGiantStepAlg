package Fin;
import java.util.Objects;

/**
 * A simple custom implementation of a HashMap-like data structure.
 * Supports put, get, remove, and containsKey operations.
 */
public class HashMap<K, V> {
    // Node representing each key-value pair in a bucket
    private static class Entry<K, V> {
        final K key;
        V value;
        Entry<K, V> next;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private Entry<K, V>[] buckets;
    private int capacity = 16;             // initial number of buckets
    private int size = 0;                  // number of key-value pairs
    private final float loadFactor = 0.75f;

    @SuppressWarnings("unchecked")
    public HashMap() {
        buckets = (Entry<K, V>[]) new Entry[capacity];
    }

    private int indexFor(K key) {
        // Spread bits and ensure non-negative
        return (key == null ? 0 : (Objects.hashCode(key) & 0x7fffffff) % capacity);
    }

    /**
     * Associates the specified value with the specified key.
     */
    public void put(K key, V value) {
        int index = indexFor(key);
        Entry<K, V> head = buckets[index];

        // Check if key exists and update
        for (Entry<K, V> e = head; e != null; e = e.next) {
            if (Objects.equals(e.key, key)) {
                e.value = value;
                return;
            }
        }

        // Insert new entry at head of bucket
        Entry<K, V> newEntry = new Entry<>(key, value);
        newEntry.next = head;
        buckets[index] = newEntry;
        size++;

        if ((float) size / capacity >= loadFactor) {
            resize();
        }
    }

    /**
     * Returns the value to which the specified key is mapped, or null if none.
     */
    public V get(K key) {
        int index = indexFor(key);
        for (Entry<K, V> e = buckets[index]; e != null; e = e.next) {
            if (Objects.equals(e.key, key)) {
                return e.value;
            }
        }
        return null;
    }

    /**
     * Removes the mapping for the specified key if present.
     * Returns the previous value, or null if none.
     */
    public V remove(K key) {
        int index = indexFor(key);
        Entry<K, V> prev = null;
        for (Entry<K, V> e = buckets[index]; e != null; prev = e, e = e.next) {
            if (Objects.equals(e.key, key)) {
                if (prev == null) {
                    buckets[index] = e.next;
                } else {
                    prev.next = e.next;
                }
                size--;
                return e.value;
            }
        }
        return null;
    }

    /**
     * Returns true if this map contains a mapping for the specified key.
     */
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        int newCapacity = capacity * 2;
        Entry<K, V>[] newBuckets = (Entry<K, V>[]) new Entry[newCapacity];

        // Rehash entries
        for (Entry<K, V> head : buckets) {
            while (head != null) {
                Entry<K, V> next = head.next;
                int newIndex = (head.key == null ? 0
                        : (Objects.hashCode(head.key) & 0x7fffffff) % newCapacity);
                head.next = newBuckets[newIndex];
                newBuckets[newIndex] = head;
                head = next;
            }
        }

        buckets = newBuckets;
        capacity = newCapacity;
    }

    /**
     * Returns the number of key-value mappings.
     */
    public int size() {
        return size;
    }
}
