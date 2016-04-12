# Skip-List Password Manager
***
Written by: Dylan Johnston
## Description
***
A password manager class that uses a custom skip-list class to store and authenticate users access to accounts and applications.
## Usage
***

##### Initialisation
Initialise a Skip-List Password Manager by calling the constructor for the class SkipListPasswordManager with no arguments. 

##### Methods
**hash(String password):**  
Returns the djb2 hash representation of the given password.

List **listUsers():**  
**Returns** a list of all usernames currently stored.

int **numberUsers():**  
**Returns** the number of usernames currently stored.

String **addNewUser(String username, String password):**  
Adds a new user to the password manager with the specified username and password.  
Password will be automatically hashed.  
**Returns** the username of the user if it does not already exists.  
Otherwise "User already exists." if the user is already stored in the password manager.

String **deleteUser(String username, String password):**  
Deletes the user from the password manager with the specified username and password.  
**Returns** the username on success.

String **authenticate(String username, String password):**  
**Returns** whether the given password matches the stored value for the given username.

String **authenticate(String username, String password, String appName):**  
**Returns** whether the given password matches the stored value for the given username and appName.

String **resetPassword(String username, String oldPassword, String newPassword):**  
Resets the given users account password.  
**Returns** username on success.

String **resetPassword(String username, String oldPassword, String newPassword, String appName):**  
Resets the given apps password to the newPassword, given the appName, username, and oldPassword.  
**Returns** username on success.

String **newAppPassword(String username, String usrPassword, String appPassword, String appName)**:  
Adds a new application password to the password manager.  
**Returns** username on success.