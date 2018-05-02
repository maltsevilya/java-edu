import java.util.AbstractQueue;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class BoundedBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E> {

    private final int capacity;
    private final ArrayDeque<E> store;

    public BoundedBlockingQueue(int capacity) {
        store = new ArrayDeque<>(capacity);
        this.capacity = capacity;
    }

    @Override
    public Iterator<E> iterator() {
        return new InternalIterator<>();
    }

    @Override
    public int size() {
        return store.size();
    }

    @Override
    public synchronized void put(E e) throws InterruptedException {
        while (remainingCapacity() == 0) {
            wait();
        }
        store.addLast(e);
        notifyAll();
    }

    @Override
    public synchronized boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        long remainingTime = unit.toMillis(timeout);
        while (remainingCapacity() == 0) {
            long start = System.nanoTime();
            wait(remainingTime);
            long end = System.nanoTime();
            remainingTime -= TimeUnit.NANOSECONDS.toMillis(end - start);
            if (remainingTime < 0) {
                notifyAll();
                return false;
            }
        }
        store.addLast(e);
        notifyAll();
        return true;
    }

    @Override
    public synchronized E take() throws InterruptedException {
        E head;
        while ((head = store.poll()) == null) {
            wait();
        }
        notifyAll();
        return head;
    }

    @Override
    public synchronized E poll(long timeout, TimeUnit unit) throws InterruptedException {
        long remainingTime = unit.toMillis(timeout);
        E head;
        while ((head = store.poll()) == null) {
            long start = System.nanoTime();
            wait(remainingTime);
            long end = System.nanoTime();
            remainingTime -= TimeUnit.NANOSECONDS.toMillis(end - start);
            if (remainingTime < 0) {
                notifyAll();
                return null;
            }
        }
        notifyAll();
        return head;
    }

    @Override
    public int remainingCapacity() {
        return capacity - store.size();
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        return drainTo(c, Integer.MAX_VALUE);
    }

    @Override
    public synchronized int drainTo(Collection<? super E> c, int maxElements) {
        int size = size();
        c.addAll(store);
        store.clear();
        notifyAll();
        return size;
    }

    @Override
    public synchronized boolean offer(E e) {
        if (remainingCapacity() == 0) {
            return false;
        }
        store.add(e);
        notifyAll();
        return true;
    }

    @Override
    public synchronized E poll() {
        if (size() == 0) {
            return null;
        }
        E result = store.removeFirst();
        notifyAll();
        return result;
    }

    @Override
    public synchronized E peek() {
        if (size() == 0) {
            return null;
        }
        return store.getFirst();
    }

    private class InternalIterator<E> implements Iterator<E> {
        private final Iterator<E> delegate;

        InternalIterator() {
            synchronized (BoundedBlockingQueue.this) {
                delegate = (Iterator<E>) store.iterator();
            }
        }

        @Override
        public boolean hasNext() {
            synchronized (BoundedBlockingQueue.this) {
                return delegate.hasNext();
            }
        }

        @Override
        public E next() {
            synchronized (BoundedBlockingQueue.this) {
                return delegate.next();
            }
        }
    }
}
