#### Project 0: Bank Application
# Overview
This project is the only application that will be run on the console.
The purpose of this project is to simulate a banking service, where users are able to log into their User accounts in order to deposit/withdraw from their various Bank Accounts.
For some of the technologies used in this project, Java Eclipse and Oracle SQL were primarily used to develop the code.

# Note For Those Who Want To Clone This
There is an additional folder required that is missing: src/main/resources.  The resources folder contains 2 files that deal with pre-authenticating login information to my Oracle SQL Database.  Please reach out to me if you want this folder with the two files.  

# Application Features
- A registered user can login with their username and password
- An unregistered user can register by creating a username and password 
- A superuser can view, create, update, and delete all users.
- A user can view their own existing accounts and balances. 
- A user can create an account.
- A user can delete an account if it is empty.
- A user can add to or withdraw from an account. 
- A user can execute multiple deposits or withdrawals in a session. 
- A user can logout. 
- Use sequences to generate USER_ID and BANK_ACCOUNT_ID. 
- Provide validation messages through the console for all user actions. 
- Use the DAO design pattern. 
- Store database connection information in a properties file. 
- PL/SQL with at least one stored procedure, JDBC with prepared and callable statements,
- Scanner for user input.

# Additional Features
- A user's transactions are recorded.
- A user may view transaction history. 
- JUnit tests on DAO methods.

# Database Organization
There are three tables in the database: Users, Accounts, and Transactions.
- Each User has a unique id, a full name, a user name, and a password, in addition to a list of Accounts and Transactions.
- Each (Bank) Account has a unique id, a name, a balance, and a user id that directly references which User has this Account.
- Each Transaction has a unique id, a message, and a user id that directly references which User has this transaction.

All in all, the Users table is the main table in which it has two different one-to-many relationships with the other tables.

![](images/ERD0.png?raw=true)

# Notable Assumptions
- There is only one superuser, and they are either created upon launching of the application or are already added to the database.
- A superuser has the same features as a regular user, as well as five additional features that will be describled in the Program Flow.
- Superuser transactions on another user's account will NOT be shown as a transaction on the affected user.



# Program Flow
- There are 3 different main menus, depending on the state of the application
    - No user is logged in (3 prompts are shown)
      1. Log in
      2. Sign up
      3. Exit the program  <br/>
    - The user that is logged in is a regular user (6 prompts are shown):
      1. View all Bank Accounts
      2. View the 10 most recent transactions made
      3. Create a new Bank Account
      4. Delete a Bank Account (or the User itself if there are no more Bank Accounts)
      5. Deposit onto a Bank Account
      6. Withdraw from a Bank Account
      7. Log out  <br/>
    - The user that is logged in is a superuser (alongside the 7 prompts of the regular user, 5 more are shown):
      1. View a specific User's balance
      2. View a specific User's 10 most recent transactions
      3. Create a brand new User
      4. Update a specific User's bank account(s) via deposit or withdraw
      5. Delete a specific User's bank account(s) or the User itself
