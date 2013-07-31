package frontend;

public interface Subscriber<E> {
	public void update( E data );
}
