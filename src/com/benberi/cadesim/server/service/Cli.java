package com.benberi.cadesim.server.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Cli {
	private static final Logger log = Logger.getLogger(Cli.class.getName());
	private String[] args = null;
	private Options options = new Options();

	public Cli(String[] args) {

		this.args = args;

		options.addOption("h", "help", false, "Show overview of functions available");
		options.addOption("a", "amount", true, "Set amount of allowed players");
		options.addOption("p", "port", true, "Set port for server");
		options.addOption("m", "map", true, "Set type of map");
		
	}

	public void parse() throws NumberFormatException, InterruptedException {
		CommandLineParser parser = new BasicParser();

		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);

			if (cmd.hasOption("h"))
				help();

			if (!cmd.hasOption("a")) {
				log.log(Level.SEVERE, "Missing amount option");
				help();
			}

			if (!cmd.hasOption("p")) {
				log.log(Level.SEVERE, "Missing port option");
				help();
			}
			
			if (!cmd.hasOption("m")) {
				log.log(Level.SEVERE, "Missing map type option");
				help();
			}
			
			if(cmd.hasOption("p") && cmd.hasOption("a")) {
				GameServerBootstrap.initiateServerStart(Integer.parseInt(cmd.getOptionValue("a")), Integer.parseInt(cmd.getOptionValue("m")), Integer.parseInt(cmd.getOptionValue("p")));
			}

		} catch (ParseException e) {
			log.log(Level.SEVERE, "Failed to parse comand line properties", e);
			help();
		}
	}

	private void help() {
		// This prints out some help
		HelpFormatter formater = new HelpFormatter();

		formater.printHelp("Main", options);
		System.exit(0);
	}
}