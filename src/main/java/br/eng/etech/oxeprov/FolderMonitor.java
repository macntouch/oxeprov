package br.eng.etech.oxeprov;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

import org.apache.log4j.Logger;

/**
 * Monitors a folder for files to process.
 * 
 * @author eduardo@etech.eng.br
 */
public class FolderMonitor {
	static Logger logger = Logger.getLogger(FolderMonitor.class);
	
	public static void run() {
		boolean oneTime = false;

		do {
			// Open the folder to monitor
			File folder = new File(Config.global.getFolderSearch());
			logger.debug("Folder " + folder + (folder.exists()?" exists":" does not exist"));
			File[] files = folder.listFiles();
			if(null == files || 0 == files.length ) {
				break;
			}
			
			
			// For each file...
			for(File file : files) {
				if(file.isFile()) {
					
					// Tries to open for read and write and lock the file
					RandomAccessFile fd = null;
					FileLock lock = null;
					try {
						fd = new RandomAccessFile(file, "rw");
						lock = fd.getChannel().tryLock();
					} catch (IOException e) {
						logger.error(e + " trying to lock the file " + file);
					} finally {
						try {
							if(null != fd)
								fd.close();
						} catch (IOException e) {
						}
					}
					
					// If could lock
					if(null != lock) {
						Batch batch;
						try {
							batch = new Batch(file);
						} catch (IOException e) {
							logger.error(e + " running batch on file " + file);
							oneTime = true;
							continue;
						}
						
						try {
							batch.run();
						} catch(IOException e) {
							logger.error(e + " writing to output file");
							oneTime = true;
							continue;							
						}
//						try {
//							batch.writeErrors();
//						} catch (IOException e) {
//							logger.error(e + " writing to output file");
//							oneTime = true;
//							continue;
//						}
						
						// Erase the original file
						file.delete();
					} else {
						oneTime = true;
					}
				}
			}
		} while(!oneTime);
	}
}
