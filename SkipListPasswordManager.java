import java.util.List;


public class SkipListPasswordManager {
	private SkipList<String, User> usrStore;
	
	 public SkipListPasswordManager(){
		 usrStore = new SkipList<String, User>();
	 }

	// returns djb2 hash representation of given password string
	 public Long hash(String password){
		 long hash = 5381;
		 
		if (password == null){
			throw new IllegalArgumentException();
		}
		
		// djb2 password hash
		String pwd = new String(password);
		
		for(int i = 0; i < pwd.length(); i++){
			hash = ((hash << 5) + hash) + pwd.charAt(i);
		}

		return hash;
		 
	 }
	 // userbase methods
	 // return a list of all usernames currently stored
	 public List<String> listUsers(){
		return usrStore.keys();
		 
	 }
	 
	 // Returns number of users in the password manager
	 public int numberUsers(){
		return usrStore.size();
		 
	 }
	 
	 // Adds to the password manager a new user with username username, and password password
	 public String addNewUser(String username, String password){
		if (username == null || password == null){
			throw new IllegalArgumentException();
		}

		if (usrStore.containsKey(username)){
			return "User already exists.";
		}
		
		User usr = new User(username);
		usr.setUserPassword(hash(password));
		usrStore.put(username, usr);
		return username;
		 
	 }
	 
	 // Deletes from the password manager user with username username, and password password	 
	 public String deleteUser(String username, String password){
		if (username == null || password == null){
			throw new IllegalArgumentException();
		}
		
		// Stores the authentication reply from the authenticate method
		String auth = authenticate(username,password);

		if (auth.equals(username)){
			usrStore.remove(username);
			return username;
		}

		switch(auth){
			case "No such user exists.":
				return "No such user exists.";
				
			case "Failed to authenticate user.":
				return "Failed to authenticate user.";
		}
		
		return "Failed to authenticate user.";
	 }
	 
	 // interface methods
	 public String authenticate(String username, String password){
		 return authenticate(username, password, "null", true);
	 }
	 
	 public String authenticate(String username, String password, String appName){
		 return authenticate(username, password, appName, false);
	 }
	 
	 private String authenticate(String username, String password, String appName, boolean internal){
			if (username == null || password == null || appName == null){
				throw new IllegalArgumentException();
			}
			
			Long givenPwdHash = hash(password);
			
			// Attempts to find user in password manager
			User usr = usrStore.get(username);
			
			if (usr == null){
				return "No such user exists.";
			} else{
				Long usrPwdHash;
				
				if (internal){
					usrPwdHash = usr.getUserPassword();
				} else{
					usrPwdHash = usr.getPassword(appName);
				}

				if (usrPwdHash== null){
					return "No password found.";
				}

				if (!givenPwdHash.equals(usrPwdHash)){
					return "Failed to authenticate user.";
				} else{
					return usr.getUsername();
				}
			}
	 }
	 
	 public String resetPassword(String username, String oldPassword, String newPassword){
		 return resetPassword(username, oldPassword, newPassword, "null", true);
	 }
	 
	 public String resetPassword(String username, String oldPassword, String newPassword, String appName){
		return resetPassword(username, oldPassword, newPassword, appName, false);
		 
	 }
	 
	 // Resets password for given user
	 // boolean internal to signify whether the users internal password is being reset or an app password is being reset
	 private String resetPassword(String username, String oldPassword, String newPassword, String appName, boolean internal){
			if (username == null || oldPassword == null || newPassword==null){
				throw new IllegalArgumentException();
			}
			
			// Stores the authentication reply from the authenticate method
			String auth;
			
			if (internal){
				auth = authenticate(username,oldPassword);
			} else{
				auth = authenticate(username,oldPassword, appName);
			}
			
			switch(auth){
				case "No such user exists.":
					return "No such user exists.";
					
				case "No password found.":
					return "No password found.";
					
				case "Failed to authenticate user.":
					return "Failed to authenticate user.";
			}
			
			// Attempts to find user in password manager
			User usr = usrStore.get(username);

			if (internal){
				usr.setUserPassword(hash(newPassword));
			} else{
				usr.setPassword(appName,hash(newPassword));
			}
			
			return username;
	 }
	 
	 // Adds a new app password to the given users password storage
	 public String newAppPassword(String username, String usrPassword, String appPassword, String appName){
		
		if (username == null || usrPassword == null || appPassword == null || appName == null){
			throw new IllegalArgumentException();
		}
		
		String auth = authenticate(username,usrPassword);
		
		switch(auth){
			case "No such user exists.":
				return "No such user exists.";
				
			case "Failed to authenticate user.":
				return "Failed to authenticate user.";
		}
		
		User usr = usrStore.get(username);

		if (usr.containsApp(appName)){
			return "Password already set up.";
		}

		usr.setPassword(appName, hash(appPassword));
		return username;
	 }
}
