public class User {
	
 // construct a new User with given username and empty password store
 // store should have size 20, and use multiplier=1 modulus=23 secondaryModulus=11
	DoubleHashMap<String, Long> pwdStore;
	private String usr;
	private Long usrPwd;
	
 public User(String username){
	 this.usr = username;
	 pwdStore = new DoubleHashMap<String, Long>(20, 1, 23, 11);
 }
 
 // get methods
 public String getUsername(){
	 return usr;
 }
 
 // Returns password hash for given app
 public Long getPassword(String appName){
	 return pwdStore.get(appName);
 }
 
 // Sets the users password hash to the given password hash
 public void setUserPassword(Long newPwd){
	 this.usrPwd = newPwd;
 }
 
 // Returns password hash for user
 public Long getUserPassword(){
	 return usrPwd;
 }
 
 // Returns if the given app exists
 public boolean containsApp(String appName){
	 if (pwdStore.containsKey(appName)){
		 return true;
	 } else{
		 return false;
	 }
 }
 
 // Sets the password for a given app
 public void setPassword(String appName, Long passwordHash){
	 pwdStore.put(appName, passwordHash);
 }
}
