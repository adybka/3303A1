//@author Andrew Dybka 101041087
//Client sending and receiving with host

package assignment1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	private DatagramSocket SRSocket;
	private DatagramPacket SPacket, RPacket;
	private int port = 23;

	public Client() {
		try {
			//init SRSocket
			this.SRSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
			this.SRSocket.close();
			System.exit(1);
		}
	}

	public void sendAndReceive(String filename, String mode, byte readOrWrite) {
		//set up what packet to be sent
		byte[] filenameByte = filename.getBytes();
		byte[] modeByte = mode.getBytes();
		byte[] result = new byte[filenameByte.length + modeByte.length + 4];
		result[0] = 0;
		result[1] = readOrWrite;
		
		System.arraycopy(filenameByte, 0, result, 2, filenameByte.length);
		result[(filenameByte.length + 2)] = 0;
		System.arraycopy(modeByte, 0, result, filenameByte.length + 3, modeByte.length);
		result[(filenameByte.length + modeByte.length + 3)] = 0;
		
		//sending packet
		try {
			this.SPacket = new DatagramPacket(result, result.length, InetAddress.getLocalHost(), this.port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			this.SRSocket.close();
			System.exit(1);
		}
		
		//information about packet being sent
		System.out.println("---Client: Sending packet to Host---");
		System.out.println("To host: " + this.SPacket.getAddress());
		System.out.println("Destination host port: " + this.SPacket.getPort());
		System.out.print("Containing: ");
		System.out.println(new String(this.SPacket.getData(), 0, this.SPacket.getLength()));
		System.out.println(this.SPacket.getData() + "\n");
	


		//request being sent
		try {
			if (readOrWrite == 1) {
				System.out.println("---Read Request Sending--- \n");
			}
			if (readOrWrite == 2) {
				System.out.println("---Write Request Sending--- \n");
			}
			this.SRSocket.send(this.SPacket);
		} catch (IOException e) {
			e.printStackTrace();
			this.SRSocket.close();
			System.exit(1);
		}
		
		//confirmation request was sent
		System.out.println("Client: Packet sent.");
		System.out.println("Client: Waiting for packet from Host\n");

		//receive packet 
		byte[] data = new byte[4];
		this.RPacket = new DatagramPacket(data, data.length);
		try {
			this.SRSocket.receive(this.RPacket);
		} catch (IOException e) {
			e.printStackTrace();
			this.SRSocket.close();
			System.exit(1);
		}
		
		//print packet contents
		System.out.println("---Client: Packet received:---");
		System.out.println("From host: " + this.RPacket.getAddress());
		System.out.println("Host port: " + this.RPacket.getPort());
		System.out.print("Containing: ");
		System.out.println(this.RPacket.getData().toString());
		System.out.println("");
		
	}

	public void startPrint(String filename, String mode) {
		byte readRequest = 1;
		byte writeRequest = 2;
		byte invalidRequest = 3;
		for (int n = 0; n < 10; n++) {
			if (n % 2 == 0) {
				sendAndReceive(filename, mode, readRequest);
			} else {
				sendAndReceive(filename, mode, writeRequest);
			}
		}
		//purposely send invalid request
		sendAndReceive(filename, mode, invalidRequest);
		System.out.println("ERROR: Invalid Request");

		this.SRSocket.close();
		System.exit(1);
	}

	public static void main(String[] args) {
		Client client1 = new Client();
		Scanner sc = new Scanner(System.in);

		System.out.print("Enter the filename (eg. filename.txt): ");
		String filename = sc.next();
		System.out.print("Enter the mode (eg. 'netascii' or 'octet'): ");
		String mode = sc.next();
		System.out.println("");
		System.out.println("filename: " + filename + "   mode: " + mode + "\n");
		client1.startPrint(filename, mode);
		sc.close();
	}
}
