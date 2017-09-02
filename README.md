# ojc-java-collections

Some really useful Java collection extensions.

## com.ojcoleman.collections.ListMap 

**extends Map (and effectively List)**

A List of mappings from keys to values. Or a Map in which
entries are associated with contiguous, predictable indices. Iterators and
returned Sets and Collections iterate over the elements in the order defined
by the ListMap.

### ListMap Implementations:

#### com.ojcoleman.collections.LSListMap

A ListMap backed by ListSets
Getting, putting and containsKey operations take O(1) time. 
Removal and containsValue take O(n) time.


## com.ojcoleman.collections.ListSet 

**extends List and Set**

Interface for a List that enforces uniqueness amongst its members. 
Or a Set in which items are associated with contiguous, predictable indices.

### ListSet Implementations:

### com.ojcoleman.collections.BMListSet

A ListSet backed by a guava HashBiMap
Append, contains, and index-of operations take O(1) time. 
Insertion and removal take O(n) time (excepting 'push' and 'pop' operations which take O(1) time). 

### com.ojcoleman.collections.AListSet

A ListSet backed by an ArrayList.
Append and contains operations take O(1) time. 
Insertion and removal take O(n) time (excepting 'push' and 'pop' operations which take O(1) time). 
Index-of operations take O(n) time.
