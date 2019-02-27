//@author Andrew Dybka	10104087
//Host sending and receiving with bother client and server


package assignment1;

import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class Host {
	private DatagramSocket SRSocket, RSocket, SSocket;
	private DatagramPacket RPacket, SPacket;
	private int port = 23;
	private int tempPort;
	private InetAddress tempAddress;
	private boolean keepRunning;

	public Host() {
		//init sockets 
		try {
			this.SRSocket = new DatagramSocket();
			this.RSocket = new DatagramSocket(this.port);
			this.SSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
			this.keepRunning = false;
			System.exit(1);
		}
	}

	public void clientToHostToServer() {
		//prepare to receive packet from client
		byte[] data = new byte[100];
		this.RPacket = new DatagramPacket(data, data.length);
		System.out.println("Host: Waiting for packet from Client.\n");
		//receive packet
		try {
			this.RSocket.receive(this.RPacket);
		} catch (IOException e) {
			e.printStackTrace();
			this.keepRunning = false;
			this.RSocket.close();
			this.SRSocket.close();
			System.exit(1);
		}
		//print received packet
		this.tempPort = this.RPacket.getPort();
		this.tempAddress = this.RPacket.getAddress();
		System.out.println("---Host: Packet received from Client---");
		System.out.println("From host: " + this.RPacket.getAddress());
		System.out.println("Host port: " + this.RPacket.getPort());
		System.out.print("Containing: ");
		//information in packet
		System.out.println(new String(this.RPacket.getData()));
		System.out.println(this.RPacket.getData() + "\n");
		/*
		try {
			Thread.sleep(23L);
		} catch (InterruptedException e) {
			e.printStackTrace();
			this.keepRunning = false;
			this.RSocket.close();
			this.SRSocket.close();
			System.exit(1);
		}
		*/
		//setting up packet to send to server
		String received = new String(this.RPacket.getData(), 0, this.RPacket.getLength());
		try {
			this.SPacket = new DatagramPacket(received.getBytes(), this.RPacket.getLength(), this.RPacket.getAddress(), 69);
			this.SRSocket.send(this.SPacket);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			this.keepRunning = false;
			this.RSocket.close();
			this.SRSocket.close();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			this.keepRunning = false;
			this.RSocket.close();
			this.SRSocket.close();
			System.exit(1);
		}
		
		//output what is sent to server
		System.out.println("---Host: Sending packet to Server---");
		System.out.println("To host: " + this.SPacket.getAddress());
		System.out.println("Destination host port: " + this.SPacket.getPort());
		System.out.print("Containing: ");
		System.out.println(new String(this.SPacket.getData(), 0, this.SPacket.getLength()));
		System.out.println(this.SPacket.getData() + "\n");
		System.out.println("Host: Packet sent.");
		System.out.println("Host: Waiting for packet from Server\n");

		//receive data from server
		byte[] data2 = new byte[4];
		this.RPacket = new DatagramPacket(data2, data2.length);
		try {
			this.SRSocket.receive(this.RPacket);
		} catch (IOException e) {
			e.printStackTrace();
			this.RSocket.close();
			this.SRSocket.close();
			System.exit(1);
		}
		
		//output what is received
		System.out.println("---Host: Packet received from Server---");
		System.out.println("From host: " + this.RPacket.getAddress());
		System.out.println("Host port: " + this.RPacket.getPort());
		System.out.print("Containing: ");
		System.out.println(this.RPacket.getData().toString());
		System.out.println(new String(this.RPacket.getData())+"\n");
		
		//packet to send back to client
		this.SPacket = new DatagramPacket(this.RPacket.getData(), this.RPacket.getLength(),
				this.tempAddress, this.tempPort);

		//print what is sent to client
		System.out.println("---Host: Sending packet to Client---");
		System.out.println("To host: " + this.SPacket.getAddress());
		System.out.println("Destination host port: " + this.SPacket.getPort());
		System.out.print("Containing: ");
		System.out.println(Arrays.toString(this.RPacket.getData()));
		System.out.println(this.SPacket.getData() + "\n");
		System.out.println("Host: Packet sent.");
		try {
			this.SSocket.send(this.SPacket);
		} catch (IOException e) {
			e.printStackTrace();
			this.keepRunning = false;
			this.RSocket.close();
			this.SRSocket.close();
			System.exit(1);
		}
	}

	//keep system run forever
	public void foreverSystem() {
		keepRunning = true;
		while (this.keepRunning) {
			clientToHostToServer();
		}
		this.RSocket.close();
		this.SRSocket.close();
		System.exit(1);
	}

	public static void main(String[] args) {
		Host h = new Host();
		h.foreverSystem();
	}
}
