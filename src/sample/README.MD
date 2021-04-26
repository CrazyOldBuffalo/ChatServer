# CHATSERVER

<hr />

### This is a ChatServer based application that allows users to communicate with other clients sending messages and opening channels.

<hr />

#### Archive Contents:

<ul>
    <li> Server.java - The Server file that handles communication between users, Server will continually accept clients. </li>
    <li> Client.java - The Base client that loads the FX for the user, deals with basic communication between the server and the user (like all messages). </li>
    <li> Login.java - The Login JavaFX for the user to login to, uses Encryption to secure passwords. Users must login to access the client. </li>
    <li> Board.java - Board stores information on each user created room, such as each subscribed user or the messages for each board.</li>
    <li> ClientHandler.java - Handles the Requests from the user, such as dealing with user input and output back to the user/client.</li>
    <li> Message.java - JSON parsing file, used to parse the messages into JSON and read them back to the user on a READ request.</li>
    <li> README.MD - This file, obviously</li>
</ul>

<hr />

#### Extensions:

2 Extensions added to the application, both are listed below:

<ol>
    <li> Search Fuctionality - Allows a user to search for a string of text within a message, it will return all items containing the search criteria to the client, or a message stating that nothing was found. </li>
    <li> Encryption - The Password of the login class is encrypted when the credentials are checked and is also checked with a hardcoded password created when the the class is launched.</li>
</ol>

<hr />

#### Compliation:
To Complile the application follow the below steps:
<ol>
    <li> Navigate to the directory where this is stored in the console/terminal (done with cd *file path here*) </li>
    <li> Run the following command: </li>
</ol>

    javac -cp /lib/*.jar *.java

This Will Compile all items in the folder with the required files

To Run the application just run the server:

     java Server

And then run the Client with:

    java Login

<hr />
&copy; Tomasz Boberek - 2021 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; NSD - Assignment 3 - GUI