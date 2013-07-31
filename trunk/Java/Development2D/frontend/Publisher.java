package frontend;

import java.util.LinkedList;
import java.util.List;

public abstract class Publisher<E> {
	private List<Subscriber<E>> subscribers;
	
	public Publisher(){
		subscribers = new LinkedList<Subscriber<E>>();
	}
	
	public void addSubscriber( Subscriber<E> sub ){
		subscribers.add(sub);
	}
	
	public void removeSubscriber( Subscriber<E> sub ){
		subscribers.remove(sub);
	}
			
	public void updateSubscribers(){
		for(Subscriber<E> sub : subscribers){
			sub.update( getPublishableData() );
		}
	}
	
	public abstract E getPublishableData();
}