
public class SkipListNode<K extends Comparable<K>, V>{
	private SkipListNode<K,V> nodeLeft;
	private SkipListNode<K,V> nodeRight;
	private SkipListNode<K,V> nodeUp;
	private SkipListNode<K,V> nodeDown;
	private V value;
	private K key;
	private boolean isSentinel;
	
	// Constructor for standard node
	public SkipListNode(K key, V value){
		this.key = key;
		this.value = value;
		this.isSentinel = false;
	}
	
	// Constructs for sentinel node, uses boolean: sentinel to force declaration of a sentinel node
	public SkipListNode(boolean sentinel){
		if (sentinel){
			this.isSentinel = true;
		} else{
			throw new IllegalArgumentException("Did not properly declare as sentinel node");
		}
		
	}
	
	public boolean isSentinel(){
		return isSentinel;
	}
	

	public K getKey() {
		return key; 
	}


	public V getValue() {
		return value;
	}


	public SkipListNode<K, V> getPrev() {
		return nodeLeft;
	}


	public SkipListNode<K, V> getNext() {
		return nodeRight;
	}


	public SkipListNode<K, V> getAbove() {
		return nodeUp;
	}


	public SkipListNode<K, V> getBelow() {
		return nodeDown;
	}


	public void setValue(V newValue) {
		this.value = newValue;
		
	}


	public void setPrev(SkipListNode<K, V> prev) {
		this.nodeLeft = prev;
		
	}


	public void setNext(SkipListNode<K, V> next) {
		this.nodeRight = next;
		
	}


	public void setAbove(SkipListNode<K, V> above) {
		this.nodeUp = above;
		
	}


	public void setBelow(SkipListNode<K, V> below) {
		this.nodeDown = below;
		
	}

}
