package wcas.freedom1b2830.globalleaks;

import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;

public class AtomicCopyOnWriteArrayList<E> extends CopyOnWriteArrayList<E> {

	private static final long serialVersionUID = 6374961895481233260L;

	public E next() throws NoSuchElementException {
		if (size() == 0) {
			throw new NoSuchElementException("size==0");
		}

		var element = get(0);
		remove(element);
		return element;
	}
}
