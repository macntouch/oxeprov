import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import br.eng.etech.oxeprov.Config;
import br.eng.etech.oxeprov.FolderMonitor;

/**
 * The main application class.
 * 
 * @author eduardo@etech.eng.br
 *
 */
public class OxeProv {
	
	private static String myVersion = "3.0b build v8.1";

	/**
	 * The main application.
	 * 
	 * @param args the command line arguments.
	 * @throws IOException reading config file error.
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws IOException {
		System.out.println("OxeProv " + myVersion + " (c) 2011-2012 eduardo@etech.eng.br");
		
		Options options = new Options();
		options.addOption(OptionBuilder.isRequired().withLongOpt("config").hasArgs(1).withArgName("ARQ").create('c'));
		
		CommandLineParser parser = new PosixParser();
		
		CommandLine line; 
		try {
			line = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.toString());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("OxeProv", options);
			return;
		}
		
		String config = line.getOptionValue("c");
		
		try {
			Config.global = new Config(new File(config));
		} catch (FileNotFoundException e) {
			System.err.println(e.getLocalizedMessage());
			return;
		}
		
		FolderMonitor.run();
	}

}
