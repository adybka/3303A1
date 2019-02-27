//@author Andrew Dybka 101041087
//Server sending and receiveing to Host

package assignment1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server {
	private DatagramSocket SSocket, RSocket;
	private DatagramPacket SPacket, RPacket;
	private int port = 69;
	private boolean keepRunning;

	public Server() {
		try {
			this.SSocket = new DatagramSocket();
			this.RSocket = new DatagramSocket(this.port);
		} catch (SocketException se) {
			se.printStackTrace();
			this.keepRunning = false;
			this.RSocket.close();
			this.SSocket.close();
			System.exit(1);
		}
	}

	public void receiveAndEcho() {
		//set up to receive packet
		byte[] data = new byte[100];
		this.RPacket = new DatagramPacket(data, data.length);
		System.out.println("Server: Waiting for packet from Host.\n");
		//try to get packet
		try {
			this.RSocket.receive(this.RPacket);
		} catch (IOException e) {
			e.printStackTrace();
			this.keepRunning = false;
			this.RSocket.close();
			this.SSocket.close();
			System.exit(1);
		}
		
		String received = new String(data, 0, this.RPacket.getLength());
		//print info from packet
		System.out.println("---Server: Packet received from Host---");
		System.out.println("From host: " + this.RPacket.getAddress());
		System.out.println("Host port: " + this.RPacket.getPort());
		System.out.print("Containing: ");
		System.out.println(received);
		System.out.println(this.RPacket.getData() + "\n");
		/*
		try {
			Thread.sleep(69L);
		} catch (InterruptedException e) {
			e.printStackTrace();
			this.keepRunning = false;
			this.RSocket.close();
			this.SSocket.close();
			System.exit(1);
		}
		*/
		
		//set up packet to send back
		byte[] serverResponse = Parse(this.RPacket.getData());
		this.SPacket = new DatagramPacket(serverResponse, serverResponse.length, this.RPacket.getAddress(), this.RPacket.getPort());

		//print what is sent
		System.out.println("---Server: Sending packet to Host---");
		System.out.println("To host: " + this.SPacket.getAddress());
		System.out.println("Destination host port: " + this.SPacket.getPort());
		System.out.print("Containing: ");
		System.out.println(serverResponse.toString());
		System.out.println(this.SPacket.getData() + "\n");
		System.out.println("Server: Packet sent.");
		try {
			this.SSocket.send(this.SPacket);
		} catch (IOException e) {
			e.printStackTrace();
			this.keepRunning = false;
			this.RSocket.close();
			this.SSocket.close();
			System.exit(1);
		}
	}
	
	private boolean checkValid(byte[]arr) {
		int curr=0;
		//check first 3 bytes are 0, 1 or 2, and not 0
		if(arr[0]==0 && (arr[1]==1||arr[1]==2) && arr[2]!=0) {
			//there is ending to first set of text and only one 0 at the end
			for(int i=2; i<arr.length; i++) {
				if(arr[i]==0) curr=i+1;
			}
			//check there is more text 
			for(int i=curr; i<arr.length; i++) {
				if(arr[i]==0) curr=i+1;
			}
			//check there are only 0s after the second part of the text
			for(int i=curr; i<arr.length; i++) {
				if(arr[i]!=0) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private byte[] Parse(byte[] arr) {
		byte[] invalid = {0};
		if(checkValid(arr)) {
			if (arr[0] == 0 & arr[1] == 1) {
				System.out.println("---Parsing---");
				System.out.println("'Packet is a Read Request' \n");
				
				byte[] read = { 0, 3, 0, 1 };
				return read;
			}
			//writing
			else if (arr[0] == 0 & arr[1] == 2) {
				System.out.println("---Parsing---");
				System.out.println("'Packet is a Write Request' \n");
				byte[] write = { 0, 4, 0, 0 };
				return write;
			}
		}
		else {
			//invalid request to server
			System.out.println("---Parsing---");
			System.out.println("'Invalid Request'");
			this.keepRunning = false;
			return invalid;
		}
		return invalid;
	}

	public void foreverSystem() {
		this.keepRunning = true;
		while (this.keepRunning) {
			receiveAndEcho();
		}
		this.SSocket.close();
		this.RSocket.close();
	}

	public static void main(String[] args) {
		Server s = new Server();
		s.foreverSystem();
	}
}
