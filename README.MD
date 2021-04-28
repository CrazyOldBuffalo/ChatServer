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
    <li> Encryption - The Password of the login class is encrypted when the credentials are checked and is also checked with a hardcoded password created when the the class is launched, which is also Encrypted.</li>
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

If there are issues just run in the IDE
<hr />

#### Commands/Help

To begin using the application you must first login in the Login.java class, Entering your credentials:

These have been hardcoded for demonstration purposes and can be seen below:
<ul>
    <li> Harry: password</li>
    <li> Jack: testpass1</li>
    <li> Kate: HiGuyz</li>
    <li> Sammy: StonkPassword1</li>
</ul>

Once Logged in you can send messages to General Chat, Create and Subscribe to Rooms using the specified buttons and unsubscribe from rooms you're subscribed to.

Images can also be sent through to the server and are stored in a specified collection for images and can be read using the corresponding buttons (Image, GetImages).

Searching can also be done by clicking the Search button, this allows a user to search through a list of posted messages for any messages that have been posted with the search criteria.

The Theme can be changed using the dropdown at the top of the application to either a Dark or Light Mode.

Quitting the App closes it down.
<hr />
&copy; Tomasz Boberek - 2021 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; NSD - Assignment 3 - GUI