package main;

import gui.GUI;
import server.Server;

public class Main {

	// the main class that decides whether the client or the
	public static void main(String[] args) {
		if (args.length == 0) {
			GUI.launch(GUI.class, args);
		} else if (args.length == 4) {
			try {
				int port = Integer.parseInt(args[0]);
				Server server = new Server(port, args[1], args[2], args[3]);
				server.run();
			} catch (NumberFormatException n) {
				System.out.print(
						"Unknown command. If you are launching a server, \n"
								+ "please make sure to follow the format of [number][String][String][String]");
			}
		} else {
			System.out.println("unknown command");
		}
	}
}
