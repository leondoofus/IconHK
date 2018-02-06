package iconHK.experiment.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


import iconHK.experiment.model.ResultDescriptor;

public class XPLogger {
	public static BufferedWriter writer; 
	
	public XPLogger(int user){
		 writer = null;
	        try {
	            //create a temporary file for it
	            String timeLog = System.currentTimeMillis()+"";
	            File logFile = new File(user+"-"+timeLog+".csv");

	            // This will output the full path where the file will be written to...
	            System.out.println(logFile.getCanonicalPath());

	            writer = new BufferedWriter(new FileWriter(logFile));
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                // Close the writer regardless of what happens...
	               
	            } catch (Exception e) {
	            }
	        }
	}
	
	
	
	public  void writeSelection(ResultDescriptor result){
		try {
			writer.write(result.toLine()+"\n");
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
