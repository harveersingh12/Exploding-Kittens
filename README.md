# Exploding ~~Kittens~~ Brains
A coding nightmare that almost blew our brains off.



## Table of contents
- [Preface](#preface)
- [Starting the game](#starting-the-game)
- [Notes after successfully starting your first game](#notes-after-successfully-starting-your-first-game)

## Preface
In this section, we have written some recommendations and notes before someone starts our game.
- The user input accepted by the server respects the M2 Team's protocol. Even though all the commands accepted by the server
are shown to the Client when they type "HELLO", we decided to include the file `Utils.M2 Protocol.pdf`. We recommend
reading the file before starting a game, since it explains the use of all the commands in a clear and concise fashion.
- We implemented the flags 0 - chat, 1 - party_pack, 3 - lobby, 4 - combos, 5 - extension. Lobby is always active,
but with the rest, the client is given the option to activate. 
- NOTE! Computer Players are not available in a game with the flags 1, 4, or 5 enabled. 
- To make two computer players play against each other you have to connect a client with the server, add two computers 
and then type "REQUEST_GAME~3". The human player will automatically be removed.
- NOTE! The cat combos are not enabled in the base game (no flags). They can only be activated along with the special combos,
meaning flag 4.
- We have provided the rules of the Party Pack and Streaking Kittens variations in the folder `src.Utils.Instructions` so that
their extra rules are easier to grasp for anyone playing the game.
- We thought of as many edge cases or wrong inputs as we could and handled them appropriately. We would like to kindly request to not stray
from the path provided below, in the "Help Menu" and in the "M2 Protocol", because you might have a broader imagination
than we do.

## Starting the game
<span style = "color: lightgreen">Please read this whole chapter before trying to start a game. 
It contains valuable information that you must know before running a game.</span>
1. Run the class: `src.Controller.ClientServer.Server`.
2. Run the class: `src.Controller.ClientServer.Client` between 1 to 10 times. `Client`
3. In each client's console, please make sure that the first command you type is the connection according to 
our protocol. Example: `CONNECT~TA_NAME~1,4,5`. The third argument represents the flags contained in the server.
The first client who uses the above-mentioned command sets the flags,<span style = "color: lightgreen">
and all the players connecting afterward need to connect with the same flags, otherwise they will receive an error message.</span>
4. After completing the steps above, you can use the Help Menu (which is printed every time a client 
connects) to guide you through the game.<span style = "color: lightgreen"> Please remember that commands are case-sensitive!</span><br>

<span style = "color: #ff6242">***NOTE! We submitted the project with the hardcoded IP address `127.0.0.1` and
port number `8811`. If a connection with these values cannot be established on you computer, you can make the 
following changes:<br>1. Edit line `201`(IP Address) and line `202`(Port) in the class `src.Controller.ClientServer.Server`
<br>2. Edit line `108`(IP Address) and line `109`(Port) in the class `src.Controller.ClientServer.Client`
<br>3. Edit line `60`(IP Address) and line `61`(Port) in the class `src.Controller.ClientServer.ComputerClient`
<br> If you want to test with user-inputed values, in `Server.java` follow the comment from the line `204`
,and in `Client.java` the comment from line `111`.***</span>

## Notes after successfully starting your first game
- If a player disconnects during a game while it's not their turn, the game's current player just has to finish their turn 
and the playerList will be updated.
- If a player disconnects during a game while it is not their turn, the server will still wait for that player's input,
so the server must be restarted.
- If there is nothing being displayed on one the console, check all players to see who is supposed to be playing at the time.
- The infinite loop "Waiting for player input" in the server is not an error. That is exactly how we designed it.