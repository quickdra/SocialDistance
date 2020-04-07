Store.java - contains all the algorithms realted to store - algorithm 1, 3 and 4.

DataBase.java - sets up and declares the tables - Store and StoreStaff.

StoreStaff.java - Contains algorithms related to the store staff - algorithm 2.

Customer.java - Contains algorithms related to customers - aglorithm 5,6,7,8.

GlobalConstants.java - As the name suggest it's an interface that has PplPerM2, MaxTimeInStore, MaxWindow - global constants.

Note - QRCode is an image that can encodes strings, hence instead of images the strings that are encoded are stored in the database.
and the String can be converted to the image whenever required locally.

The InQueue array is stored as a string sentence in DB, and also when making changes to InQueue we use it in string format so after 
making any changes and updating to db, it can be directly done so. Else every fetch or update from db would involve a parsing operation.
