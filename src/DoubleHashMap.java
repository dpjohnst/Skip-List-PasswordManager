import java.util.ArrayList;
import java.util.List;

public class DoubleHashMap < K extends Comparable < K > , V > {
	private int multiplier;
	private int modulus;
	private int secondaryModulus;
	private HashMapNode[] nodeCache;
	private int nodeCount;
	private int putCollisions = 0;
	private int totalCollisions = 0;
	private int maxCollisions = 0;
	private int putFailures;

	// construct a DoubleHashMap with 4000 places and given hash parameters
	public DoubleHashMap(int multiplier, int modulus, int secondaryModulus) {
		nodeCache = new HashMapNode[4000];
		this.multiplier = multiplier;
		this.modulus = modulus;
		this.secondaryModulus = secondaryModulus;
	}

	// construct a DoubleHashMap with given capacity and given hash parameters
	public DoubleHashMap(int hashMapSize, int multiplier, int modulus, int secondaryModulus) {
		if (hashMapSize < 0) {
			throw new NegativeArraySizeException();
		} else {
			nodeCache = new HashMapNode[hashMapSize];
			this.multiplier = multiplier;
			this.modulus = modulus;
			this.secondaryModulus = secondaryModulus;
		}
	}

	//Primary hash function
	public int hash(K key) {
		return Math.abs(multiplier * key.hashCode()) % modulus;
	}

	// Secondary hash function
	public int secondaryHash(K key) {
		return secondaryModulus - Math.abs(key.hashCode()) % secondaryModulus;
	}

	// Checks if a key is contained within the hashmap
	public boolean containsKey(K key) {
		if (key == null) {
			throw new IllegalArgumentException();
		}
		if (findCell(key, false) == -1) {
			return false;
		} else {
			return true;
		}
	}

	// Returns position in hashmap under the following conditions:
	// Insert parameter determines if the method is looking to place a node into the hashmap, or if it is looking for a specific node
	// Returns -1 if no valid position can be found
	// Returns position of node in hashmap with key key, if it already exists
	// Returns empty position determined by hash function otherwise
	public int findCell(K key, boolean insert) {
		int pos;
		int counter = 0;
		int hash = hash(key);
		int secHash = secondaryHash(key);
		boolean defunctsDetected = false;
		int startPos = (hash + counter * secHash) % nodeCache.length;
		List < Integer > defuncts = new ArrayList <Integer>();

		// Loop until either a valid position is found, or all positions have been exhausted
		while (true) {
			pos = (hash + counter * secHash) % nodeCache.length;

			// If the position arrives back to where it started or goes out of bounds, a complete failure may have occurred
			if (pos >= nodeCache.length || (pos == startPos && counter != 0)) {
				if (insert == true) {
					// If there were defunct nodes detected when searching, return the position of the first defunct detected to be used.
					if (defunctsDetected) {
						if (maxCollisions < counter) {
							maxCollisions = counter;
						}
						return defuncts.get(0);
					}
					putFailures++;
				}
				pos= -1;
				break;
			}

			// If a node is being inserted via the put method
			if (insert == true) {
				// If a defunct node has been found, mark it's position and continue searching only for the keys existence
				if (nodeCache[pos] != null && nodeCache[pos].isDefunct()) {
					defunctsDetected = true;
					defuncts.add(pos);

				// If an empty position has been found and defuncts haven't been detected, or a node with the key being inserted has been found:
				// Update statistics and return pos
				} else if ((nodeCache[pos] == null  && !defunctsDetected) || nodeCache[pos].getKey().equals(key)) {
					if (maxCollisions < counter) {
						maxCollisions = counter;
					}
					break;
				}
			// If node is being searched for
			} else {
				// If an empty position is found before the node with key key, it doesn't exist in the map
				if (nodeCache[pos] == null) {
					return -1;
				} else if (!nodeCache[pos].isDefunct() && nodeCache[pos].getKey().equals(key)) {
					break;
				}
			}

			if (counter == 0 && insert) {
				putCollisions++;
			}
			if (insert){totalCollisions++;}
			counter++;
		}
		return pos;
	}

	// size (return the number of nodes currently stored in the map)
	public int size() {
		return nodeCount;

	}

	// Returns if the map is empty
	public boolean isEmpty() {
		return nodeCount == 0;

	}

	// interface methods
	// Return all non-duplicate nodes with key=key in the hash-map
	@SuppressWarnings("unchecked")
	public List < K > keys() {
		List < K > keys = new ArrayList < K > ();
		for (int i = 0; i < nodeCache.length; i++) {
			// If the nodes aren't null or defunct, add them to the list
			if (nodeCache[i] != null && !nodeCache[i].isDefunct()) {
				K key = (K) nodeCache[i].getKey();
				keys.add(key);
			}
		}
		return keys;

	}

	// Puts a node with key key, and value value into the doublehashmap if it doesn't already exist
	// If it already exists, update the node with the new value
	@SuppressWarnings("unchecked")
	public V put(K key, V value) {
		if (key == null) {
			return null;
		}

		// Set insert position to the first valid cell
		int insertPos = findCell(key, true);

		// -1 indicates a total failure to find a cell
		if (insertPos == -1) {
			throw new RuntimeException("Double Hashing failed to find a free position");
		}

		V oldVal = null;
		
		// If there was already a proper node here, get it's value to return later
		if (nodeCache[insertPos] != null && !nodeCache[insertPos].isDefunct()) {
			oldVal = (V) nodeCache[insertPos].getValue();
		} else {
			nodeCount++;
		}

		HashMapNode < K, V > node = new HashMapNode < K, V > (key, value);
		nodeCache[insertPos] = node;
		return oldVal;
	}

	// Finds and returns the value of node with key key inside the map.
	// Returns null if no such node exists
	@SuppressWarnings("unchecked")
	public V get(K key) {
		if (key == null) {
			return null;
		}

		int insertPos = findCell(key, false);

		if (insertPos == -1) {
			return null;
		} else {		
			V value = (V) nodeCache[insertPos].getValue();
			return value;
		}
	}

	// Removes the node with key key inside the map, and returns the value.
	// Returns null if no such node exists
	@SuppressWarnings("unchecked")
	public V remove(K key) {
		if (key == null) {
			return null;
		}
		
		// Find if the node exists, -1 represents the node unable to be found
		int insertPos = findCell(key, false);
		
		if (insertPos == -1) {
			return null;
		} else {
			HashMapNode < K, V > node = nodeCache[insertPos];
			V value = (V) node.getValue();
			node.setDefunct();
			nodeCount--;
			return value;
		}
	}


	// collision statistics
	public int putCollisions() {
		return putCollisions;
	}

	public int totalCollisions() {
		return totalCollisions;
	}

	public int maxCollisions() {
		return maxCollisions;
	}

	public void resetStatistics() {
		putCollisions = 0;
		totalCollisions = 0;
		maxCollisions = 0;
		putFailures = 0;
	}

	public int putFailures() {
		return putFailures;
	}

}