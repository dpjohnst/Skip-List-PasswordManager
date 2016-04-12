import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SkipList<K extends Comparable<K>, V>{
	private int size;

	// Stores the lowest left sentinel node
	private SkipListNode<K,V> begin;
	// Stores the lowest right sentinel node
	private SkipListNode<K,V> end;

	private boolean alreadyExisted;

	// Report usage only
	protected Integer numberOfSteps;
	
	public SkipList(){
		size = 0;
		begin = new SkipListNode<K,V>(true);
		end = new SkipListNode<K,V>(true);
		begin.setNext(end);
		end.setPrev(begin);
	}
	
	public int size() {
		return size;
	}
	
	// Returns the left sentinel node at the top of the skiplist
	private SkipListNode<K, V> toTop(){
		SkipListNode<K,V> current = begin;
		
		while(current.getAbove() != null){
			current = current.getAbove();
		}

		return current;
		
	}

	// Returns whether the skiplist is empty
	public boolean isEmpty() { return size == 0; }

	// Returns all keys stored in the skiplist
	public List<K> keys() {
		List<K> keys = new ArrayList<K>();
		SkipListNode<K,V> current = begin.getNext();
		
		while (current != end){
			keys.add(current.getKey());
			current = current.getNext();
		}
		
		return keys;
	}
	
	// returns whether a node with key key exists in the skip-list
	public boolean containsKey(K key){
		
		if (key == null){
			throw new IllegalArgumentException();
		}
		
		// Start looking from the next node after the lowest left sentinel node
		SkipListNode<K,V> current = begin.getNext();
		
		// Keep checking to the right until either the node is found or a sentinel node is reached. 
		while (true){
			if (current == null){
				return false;
			} else{
				if (current.isSentinel()){
					return false;
				}
				
				if (current.getKey().equals(key)){
					return true;
				} else{
					current = current.getNext();
				}
			}
		}
	}

	// Searches for node with key key
	// Returns the node if it is found, and null otherwise
	public SkipListNode<K, V> search(K key) {
		numberOfSteps = 0;

		// If the node to the right of the lowest left sentinel, is the lowest right sentinel, return the lowest left sentinel as no other nodes exist
		if (begin.getNext() == end){
			return begin;
		}
		
		// Start at the top left of the skiplist, and work way down
		SkipListNode<K, V> currentNode = toTop();

		while(currentNode.getNext() != null){
			SkipListNode<K, V> nextNode = currentNode.getNext();
			// If the next node is a sentinel node, go down if possible, otherwise return the current node.
			if (nextNode.isSentinel()){
				if (currentNode.getBelow() != null){
					currentNode = currentNode.getBelow();
					continue;
				} else{
					return currentNode;
				}
			}
			numberOfSteps += 1;
			
			/* Compare the next node with the key being looked for
			*  If it is equal, return that node
			*  If the next node is greater than the key being looked for, go down if possible, otherwise return the current node
			*  If the next node is less than the key being looked for, move across */
			int comparison = nextNode.getKey().compareTo(key);
			
			if (comparison == 0){
				return nextNode;
			} else if (comparison < 0){
				currentNode = nextNode;
			} else{
				if (currentNode.getBelow() != null){
					currentNode = currentNode.getBelow();
				} else{
					return currentNode;
				}
			}
		}

		return null;
	}
	
	// Finds and returns the node most right of the given node
	private SkipListNode<K,V> mostRight(SkipListNode<K,V> start){
		SkipListNode<K,V> current = start;

		while(current.getNext() != null){
			current = current.getNext();
		}

		return current;
	}
	
	public int getHeight(){
		SkipListNode<K,V> start = begin;
		int height = 1;

		while (begin.getAbove() != null){
			begin = begin.getAbove();
			height++;
		}

		return height;
	}

	// Puts a node into the skiplist with key key and value value
	public V put(K key, V value) {
		alreadyExisted = false;
		Random rand = new Random();
		rand.setSeed(5);
		SkipListNode<K,V> current = begin;
		int height = 1;

		if (key == null){
			throw new IllegalArgumentException();
		}
		
		
		V oldValue = get(key);
		SkipListNode<K,V> lastInserted = putRow(current, key, value);
		boolean nodeAbove;

		// If the node doesn't already exist in the skiplist, increase the skiplist size count
		// Don't bother with coin flips
		if (!alreadyExisted){
			size++;
			nodeAbove = rand.nextBoolean();
		}
		else{
			nodeAbove = true;
		}
		
		while (nodeAbove || alreadyExisted){
			height++;

			if (!alreadyExisted){
				nodeAbove = rand.nextBoolean();
			}
			
			if (current.getAbove() == null){
				if (alreadyExisted){
					break;
				}

				SkipListNode<K,V> oldCurrent = current;
				current = expand(current);
				current.setBelow(oldCurrent);
				lastInserted.setAbove(current);
				SkipListNode<K,V> end = mostRight(oldCurrent);
				SkipListNode<K,V> endUp = mostRight(current);
				endUp.setBelow(end);
				end.setAbove(endUp);
				
			} else{
				current = current.getAbove();
			}

			SkipListNode<K,V> newNode = putRow(current, key, value);
			newNode.setBelow(lastInserted);
			lastInserted.setAbove(newNode);
			lastInserted = newNode;
		}
		if (alreadyExisted){
			return oldValue;
		} else{
			return null;
		}
		
	}
	
	/* Attempts to find the appropriate position for a new node with key key and value value to be inserted
	*  Begins search for appropriate position from the position of the supplied sentinel node
	*  If the node already exists, return the position of that node and mark it as already existing
	*  Else insert it in the list at the appropriate position and fix node pointers */
	private SkipListNode<K, V> putRow(SkipListNode<K, V> sentinel, K key, V value){
		SkipListNode<K, V> current = findInsertPos(sentinel, key);

		if (!current.isSentinel() && current.getKey().equals(key)){
			current.setValue(value);
			alreadyExisted = true;
			return current;
		} else{
		// Fix node pointers
		SkipListNode<K, V> after = current.getNext();
		SkipListNode<K, V> newNode = new SkipListNode<K,V>(key, value);
		current.setNext(newNode);
		after.setPrev(newNode);
		newNode.setNext(after);
		newNode.setPrev(current);
		return newNode;
		}

	}
	
	// Find the correct position to insert a node with key key, 
	// starts the search from the supplied sentinel node
	private SkipListNode<K, V> findInsertPos(SkipListNode<K, V> sentinel, K key){
		SkipListNode<K, V> current = sentinel;
		
		// Keep looking at the next nodes until the correct node is found, or is deemed impossible to find
		while(true){
			SkipListNode<K, V> nextNode = current.getNext();

			if (nextNode.isSentinel()){
				return current;
			}

			int comparison = nextNode.getKey().compareTo(key);

			if (comparison == 0){
				return nextNode;
			} else if (comparison < 0){
				current = nextNode;
			} else{
					return current;
			}
		}
	}

	
	// Search for node with key key
	public V get(K key) {
		if (key == null){
			throw new IllegalArgumentException();
		}
		
		SkipListNode<K, V> node = search(key);
		
		if (node.isSentinel()){
			return null;
		} else if (!node.getKey().equals(key)){
			return null;
		} else{
			return node.getValue();
		}
	}

	// Remove node from skiplist with key key
	public V remove(K key) {

		if (key == null){
			throw new IllegalArgumentException();
		}
		
		V value = null;
		
		// Search for node to remove
		SkipListNode<K, V> nodeFound = search(key);
		
		if (nodeFound.isSentinel()){
			return null;
		} else if (!nodeFound.getKey().equals(key)){
			return null;
		} else{
			value = remove(nodeFound);
			this.size -= 1;
		}
		
		// Remove any unnecessary layers in the skip-list
		SkipListNode<K, V> topNode = begin;

		while (topNode.getAbove() != null){
			topNode = topNode.getAbove();
		}

		while (topNode.getNext().isSentinel() && topNode.getBelow() != null){
			topNode = compress(topNode);
		}

		return value;
	}
	
	// Helper method to remove a given node
	private V remove (SkipListNode<K, V> node){
		SkipListNode<K, V> prevNode;
		
		while (node.getBelow() != null){
			node = node.getBelow();
		}

		prevNode = node;

		while (node != null){
			node = removeCurrentNode(node);
		}

		return prevNode.getValue();
	}
	
	private SkipListNode<K, V> removeCurrentNode(SkipListNode<K, V> node){
		SkipListNode<K,V> above = node.getAbove();
		SkipListNode<K,V> left = node.getPrev();
		SkipListNode<K,V> right = node.getNext();
		
		left.setNext(right);
		right.setPrev(left);
		if (above != null){above.setBelow(null);}
		node.setNext(null);
		node.setPrev(null);
		node.setBelow(null);
		node.setAbove(null);
		return above;
		
	}
	
	// Method for testing, used to check if a nodes parents have the same key as the node itself
	public boolean checkKeyIntegrity(K key){
		SkipListNode<K, V> node = search(key);
		
		if (node.getKey().equals(key)){
			while (node.getBelow() != null){
				node = node.getBelow();
			}
			
			while (node.getAbove() != null){
				SkipListNode<K, V> above = node.getAbove();

				if (!node.getValue().equals(above.getValue())){
					return false;
				}

				node = above;
			}
		} else{
			return true;
		}
		
		return true;
	}
	
	// Adds a new layer to the skip-list, one layer above the given left sentinel node
	// Returns the left sentinel node in the new layer
	private SkipListNode<K,V> expand(SkipListNode<K,V> left){
		// Initialized to left, will be corrected in next block
		SkipListNode<K,V> right;
		
		// Correct right-most node to correct node
		right = mostRight(left);
		
		// Add nodes in layer and fix pointers
		SkipListNode<K,V> leftUp = new SkipListNode<K, V>(true);
		SkipListNode<K,V> rightUp = new SkipListNode<K, V>(true);
		leftUp.setNext(rightUp);
		rightUp.setPrev(leftUp);
		left.setAbove(leftUp);
		right.setAbove(leftUp);
		return leftUp;
	}
	
	// Removes the layer from the skip-list containing the given left-sentinel node
	// (Reverse of expand)
	// Returns the left sentinel node in the layer below
	private SkipListNode<K,V> compress(SkipListNode<K,V> left){
		// Initialized to left, will be corrected in next block
		SkipListNode<K,V> right;
		
		// Correct right-most node to correct node
		right = mostRight(left);
		
		// Remove nodes in layer and fix pointers
		SkipListNode<K,V> below = left.getBelow();
		left.getBelow().setAbove(null);
		right.getBelow().setAbove(null);
		left.setBelow(null);
		right.setBelow(null);
		return below;
	}

}
