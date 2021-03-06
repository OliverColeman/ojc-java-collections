/**
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA
 */

package com.ojcoleman.collections;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.UnaryOperator;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;


/**
 * <p>A {link ListSet} backed by a <a href="http://google.github.io/guava/releases/19.0/api/docs/com/google/com.ojcoleman.collections/collect/HashBiMap.html">HashBiMap</a>
 * Addition (appending to end of list), contains, and index-of operations 
 * take O(1) time. Insertion and removal take O(n) time (excepting at the 
 * end of the list, in which case it is O(1)).</p>
 * 
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access an instance concurrently,
 * and at least one of the threads modifies it structurally, it
 * <i>must</i> be synchronized externally.  (A structural modification is
 * any operation that adds or deletes one or more elements, or explicitly
 * resizes the backing array; merely setting the value of an element is not
 * a structural modification.)  This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the list.</p>
 *
 * <p>If no such object exists, the list should be "wrapped" using the
 * {@link Collections#synchronizedList}
 * method.  This is best done at creation time, to prevent accidental
 * unsynchronized access to the list:<pre>  List list = Collections.synchronizedList(new ArrayList(...));</pre>
 *
 * <p>
 * The iterators returned by this class's {@link #iterator()} and
 * {@link #listIterator(int)} methods are <em>fail-fast</em>:
 * if the list is structurally modified at any time after the iterator is
 * created, in any way except through the iterator's own
 * {@link ListIterator#remove()} or
 * {@link ListIterator#add(Object)} methods, the iterator will throw a
 * {@link ConcurrentModificationException}.  Thus, in the face of
 * concurrent modification, the iterator fails quickly and cleanly, rather
 * than risking arbitrary, non-deterministic behaviour at an undetermined
 * time in the future.</p>
 *
 * <p>Note that the fail-fast behaviour of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw {@code ConcurrentModificationException} on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness:  <i>the fail-fast behaviour of iterators
 * should be used only to detect bugs.</i></p>
 * 
 * @author O. J. Coleman
 */
public class BMListSet<T> extends AbstractList<T> implements ListSet<T> {
	BiMap<Integer, T> map;
	
	/**
	 * Creates an empty BiMapListSet.
	 */
	public BMListSet() {
		map = HashBiMap.create();
	}
	
	/**
	 * Creates a BiMapListSet containing the elements in the specified
     * collection, in the order they are returned by the collection's
     * iterator. If any items are duplicated, according to their equals
     * method, then only the first item is included.
     *
     * @param c the collection whose elements are to be placed into this list
     * @throws NullPointerException if the specified collection is null
	 */
	public BMListSet(Collection<? extends T> c) {
		map = HashBiMap.create();
    	this.addAll(c);
    }
	
	
	/**
	 * Creates a BiMapListSet containing the elements in the specified
     * array in the same order. If any items are duplicated, according to their equals
     * method, then only the first item is included.
     *
     * @param a the elements to be placed into this list
     * @throws NullPointerException if the specified collection is null
	 */
	public BMListSet(T[] a) {
		map = HashBiMap.create();
		for (T e : a) {
			this.add(e);
		}
    }
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(T e) {
		if (!map.containsValue(e)) {
			map.put(map.size(), e);
			modCount++;
			return true;
		}
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(int index, T element) {
		rangeCheckForAdd(index);
		checkNotContains(element);
		for (int newIndex = map.size(); newIndex > index; newIndex--) {
			T e = map.remove(newIndex-1);
			map.put(newIndex, e);
		}
		map.put(index, element);
		modCount++;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean changed = false;
		for (T e : c) {
			if (!map.containsValue(e)) {
				map.put(map.size(), e);
				if (!changed) {
					modCount++;
					changed = true;
				}
			}
		}
		return changed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		rangeCheckForAdd(index);
		ArrayList<T> toAdd = new ArrayList<>(c.size());
		for (T e : c) {
			if (!map.containsValue(e)) {
				toAdd.add(e);
			}
		}
		if (!toAdd.isEmpty()) {
			modCount++;
			for (int oldIndex = map.size()-1, newIndex = oldIndex+toAdd.size(); oldIndex >= index; oldIndex--, newIndex--) {
				T e = map.remove(oldIndex);
				map.put(newIndex, e);
				
			}
			for (T e : toAdd) {
				map.put(index++, e);
			}
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		modCount++;
		map.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(Object o) {
		return map.containsValue(o);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		return map.values().containsAll(c);
	}

	/**
	 * Returns the element at the specified index.
	 */
	/**
	 * {@inheritDoc}
	 */
	@Override
	public T get(int index) {
		this.rangeCheck(index);
		return map.get(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int indexOf(Object o) {
		if (!map.containsValue(o)) {
			return -1;
		}
		return map.inverse().get(o);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int lastIndexOf(Object o) {
		return indexOf(o);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean remove(Object o) {
		if (!map.containsValue(o)) {
			return false;
		}
		modCount++;
		int index = map.inverse().get(o);
		map.remove(index);
		
		int newLastIndex = size();
		for (int newIndex = index; newIndex < newLastIndex; newIndex++) {
			T e = map.remove(newIndex+1);
			map.put(newIndex, e);
		}
		
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T remove(int index) {
		rangeCheck(index);
		modCount++;
		T oldEl = map.remove(index);
		
		int newLastIndex = size();
		for (int newIndex = index; newIndex < newLastIndex; newIndex++) {
			T e = map.remove(newIndex+1);
			map.put(newIndex, e);
		}
		
		return oldEl;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (Object e : c) {
			changed |= remove(e);
		}
		return changed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		boolean[] keep = new boolean[size()];
		for (Object o : c) {
			int index = indexOf(o);
			if (index != -1) {
				keep[index] = contains(o);
			}
		}
		boolean changed = false;
		for (int i = size()-1; i >= 0; i--) {
			if (!keep[i]) {
				remove(i);
				changed = true;
			}
		}
		return changed;
	}
	
	 /**
     * Removes from this list set all of the elements whose index is between
     * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive.
     * Shifts any succeeding elements to the left (reduces their index).
     * This call shortens the list by {@code (toIndex - fromIndex)} elements.
     * (If {@code toIndex==fromIndex}, this operation has no effect.)
     *
     * @param fromIndex index of first element to be removed
     * @param toIndex index after last element to be removed
     */
	@Override
	public void removeRange(int fromIndex, int toIndex) {
		modCount++;
		int lastIndex = size() - 1;
		for (int newIndex = fromIndex, oldIndex = toIndex; newIndex < toIndex; newIndex++, oldIndex++) {
			// Remove element in range to be removed.
			map.remove(newIndex);
			
			if (oldIndex <= lastIndex) {
				// Assign new contiguous index to elements after the range to remove.
				map.put(newIndex, map.remove(oldIndex));
			}
		}
    }
	
    /**
	 * {@inheritDoc}
	 */
	@Override
	public void replaceAll(UnaryOperator<T> operator) {
		ListIterator<T> li = listIterator();
		while (li.hasNext()) {
			li.set(operator.apply(li.next()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T set(int index, T element) {
		rangeCheck(index);
		T current = get(index);
		if (element == null ? current == null : element.equals(current)) {
			return current;
		}
		checkNotContains(element);
		return map.forcePut(index, element);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return map.size();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] toArray() {
		Object[] a = new Object[size()];
		for (int i = 0; i < a.length; i++) {
			a[i] = map.get(i);
		}
		return a;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		if (a.length < size()) {
			a = (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size());
		}
		for (int i = 0; i < a.length; i++) {
			a[i] = (T) map.get(i);
		}
		if (a.length > size()) {
			a[size()] = null;
		}
		return a;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<T> asList() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<T> asSet() {
		return new SetFace<T>(this);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof List))
            return false;
        
        // Use iterators because they'll fail fast in the face of 
        // structural modifications while we're checking for equality.
        ListIterator<T> e1 = listIterator();
        ListIterator e2 = ((List) o).listIterator();
        while (e1.hasNext() && e2.hasNext()) {
            T o1 = e1.next();
            Object o2 = e2.next();
            if (!(o1==null ? o2==null : o1.equals(o2)))
                return false;
        }
        return !(e1.hasNext() || e2.hasNext());
    }
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sort(Comparator<? super T> c) {
		T[] values = (T[]) this.toArray();
		Arrays.sort(values, c);
		map.clear();
		for (int i = 0; i < values.length; i++) {
			map.put(i, values[i]);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Spliterator<T> spliterator() {
		return map.values().spliterator();
	}
	
	
	private void rangeCheck(int index) {
		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException("Index is out of range: " + index);
		}
	}

	private void rangeCheckForAdd(int index) {
		if (index < 0 || index > size()) {
			throw new IndexOutOfBoundsException("Index is out of range: " + index);
		}
	}

	/**
	 * Throws an IllegalArgumentException if this list set contains the given item.
	 */
	private void checkNotContains(T e) {
		if (map.containsValue(e)) {
			throw new IllegalArgumentException("Value already present: " + e);
		}
	}

	@Override
	public ListSet<T> unmodifiableView() {
		return new UnmodifiableListSet<>(this);
	}
}
