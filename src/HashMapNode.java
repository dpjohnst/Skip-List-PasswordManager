public class HashMapNode<K extends Comparable<K>, V> {
	private K key;
	private V value;
	
 // construction
 public HashMapNode(K key, V value){
	 this.key = key;
	 this.value = value;
 }

 // get methods
 public K getKey(){
	return this.key;
	 
 }
 
 public V getValue(){
	return this.value;
	 
 }
 
 // set method
 public void setValue(V newValue){
	 this.value = newValue;
 }
 
 // Sets the node to a defunct node
 public void setDefunct(){
	 this.key = null;
	 this.value = null;
 }
 
 // Returns true if node is defunct, false otherwise
 public boolean isDefunct(){
	 return this.key == null && this.value == null;
 }
}