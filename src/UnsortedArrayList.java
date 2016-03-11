import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Noah on 2015-05-16.
 *
 * Has no sence of sorting at all. Requires objects that knows their position in the list.
 *
 * Can't contain copies or null
 */
public class UnsortedArrayList<E extends UnsortedObject> extends ArrayList<E> {

    public UnsortedArrayList(){
        super();
    }

    public UnsortedArrayList(int initialCapacity) {
        super(initialCapacity);
    }


    public UnsortedArrayList(Collection<? extends E> c) {
        super(c);
        for(int i = 0; i < size(); i++)
            get(i).setUnsortedListPosition(i);

    }

    //Appends all of the elements in the specified collection to the end of this list, in the order that they are returned by the specified collection's Iterator.
    @Override
    public boolean addAll(Collection<? extends E> c){
        boolean bool = super.addAll(c);
        for(int i = 0; i < size(); i++)
            get(i).setUnsortedListPosition(i);
        return bool;
    }
    //Inserts all of the elements in the specified collection into this list, starting at the specified position.
    public boolean addAll(int index, Collection<? extends E> c) {
        boolean bool = super.addAll(index, c);
        for(int i = index; i < size(); i++)
            get(i).setUnsortedListPosition(i);
        return bool;
    }




    //Returns the index of the first occurrence of the specified element in this list, or -1 if this list does not contain the element.
    @Override
    public int indexOf(Object o) {
        if (o == null)
            return -1;
        else if (((UnsortedObject)o).getUnsortedListPosition() >= 0 && ((UnsortedObject)o).getUnsortedListPosition() < size() && ((UnsortedObject)o).equals(get(((UnsortedObject)o).getUnsortedListPosition())))
            return ((UnsortedObject)o).getUnsortedListPosition();
        else
            return -1;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }
    /*  //not usable for this
    @Override
    public int lastIndexOf(Object o) {
        return indexOf(o);
    }
    */

    //overrides the current element on index with element if and only if element isn't already in the array. Returns the the previous element or null
    @Override
    public E set(int index, E element) {
        if(contains(element))
            return null;
        E returnValue = super.set(index,element);
        element.setUnsortedListPosition(index);
        return returnValue;
    }

    //overrides i1 with i2 useful since set can't do it.
    //returns the ovverided value.
    public E override(int i1, int i2) {
        E returnValue = super.set(i1,get(i2));
        returnValue.setUnsortedListPosition(-1);
        get(i1).setUnsortedListPosition(i1);
        remove(i2);
        return returnValue;
    }
    private void ovverideWithLast(int i){
        get(i).setUnsortedListPosition(-1);
        super.set(i,get(size()-1));
        get(i).setUnsortedListPosition(i);
        super.remove(size()-1);
    }

    @Override
    public boolean add(E e) {
        if(contains(e))
            return false;
        e.setUnsortedListPosition(size());
        super.add(e);
        return true;
    }

    //Inserts the specified element at the specified position in this list.
    @Override
    public void add(int index, E element){
        if(contains(element))
            return;
        super.add(index,element);
        element.setUnsortedListPosition(index);
    }

    //Moves the last element to index
    @Override
    public E remove(int index) {
        E oldValue = get(index);
        ovverideWithLast(index);
        return oldValue;
    }

    public boolean remove(E o) {
        if(o.getUnsortedListPosition() >= 0 && o.getUnsortedListPosition() < size() && o.equals(get(o.getUnsortedListPosition()))) {
            remove(o.getUnsortedListPosition());
            return true;
        }
        else
            return false;
    }

    public E removeLast(){
        return remove(size()-1);
    }

    //Removes all of the elements from this list.
    public void clear() {
        for(int i = 0; i < size(); i++)
            get(i).setUnsortedListPosition(-1);
        super.clear();
    }

    public E peekFirst() {
        if(size() > 0)
            return get(0);
        else
            return null;
    }
    public E peekLast() {
        if(size() > 0)
            return get(size()-1);
        else
            return null;
    }

    public E pollFirst() {
        return remove(0);
    }
    public E pollLast() {
        return remove(size()-1);
    }


    /* // todo iterators

    //Returns an iterator over the elements in this list in proper sequence.
    Iterator<E>	iterator()

    //Returns the index of the last occurrence of the specified element in this list, or -1 if this list does not contain the element.
    int	lastIndexOf(Object o)

    //Returns a list iterator over the elements in this list (in proper sequence).
    ListIterator<E>	listIterator()


    //Returns a list iterator over the elements in this list (in proper sequence), starting at the specified position in the list.
    ListIterator<E>	listIterator(int index)


    //Removes from this list all of its elements that are contained in the specified collection.
    boolean	removeAll(Collection<?> c)

    //Removes from this list all of the elements whose index is between fromIndex, inclusive, and toIndex, exclusive.
    protected void	removeRange(int fromIndex, int toIndex)

    //Retains only the elements in this list that are contained in the specified collection.
    boolean	retainAll(Collection<?> c)

    //Replaces the element at the specified position in this list with the specified element.
    E	set(int index, E element)


*/

}
