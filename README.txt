@author Andrew Dybka (101041087)


Launch Programs in Server, Host, Client Order.
Once Running input a text file name and mode (netascii or octet)

The program should run 10 times (5 read requests and 5 read requests) and an 11th time with an invalid request.
The UML class diagram shows how the packets sent between the 3 programs. The squence diagram describes what is sent as
as some extra functions used to validate requests.

Client:
-creates a socket to send and receive
-creates a packet to sent
-sets it to either read or write
-sends the packet to the host
-creates a packet to receive from the host
-receives the packet
-go back to top

Host:
-creates a send and receive socket
-creates receive packet on port 23
-prints contents of packet
-sets packet to server on port 69
-creats a packet to receive from server
-receives packet
-sends packet receives from server to the client on port 23
-continuously waits for packets
-closes on error

Server:
-creates socket to send and receive
-creates packet to receive
-receives packet from host on port 69
-validates packet
-prepare appropraite response to receives packet
-sends packet to host
-continuously waits for packet from host
-closes on error