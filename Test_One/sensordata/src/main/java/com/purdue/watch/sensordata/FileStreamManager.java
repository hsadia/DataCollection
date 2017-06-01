package com.purdue.watch.sensordata;

/**
 * Created by Haleema on 01/06/2017.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileStreamManager {
    public List<String> Read(String filename)
    {
        File SDFile = android.os.Environment.getExternalStorageDirectory();
        //Read text from file
        List<String> text = new ArrayList<String>();

		try {
		    BufferedReader br = new BufferedReader(new FileReader(SDFile.getAbsolutePath()+File.separator+filename));
		    String line;

		    while ((line = br.readLine()) != null) {
		        text.add(line);
		    }
		}
		catch (IOException e) {
		   System.err.println("Error: "+ e.getMessage());
		}
        return text;
    }

    public void Output(String filename,String content)
    {
        try{
            // Create file
            File SDFile = android.os.Environment.getExternalStorageDirectory();

            FileWriter fstream = new FileWriter(SDFile.getAbsolutePath()+File.separator+filename,true);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(content);
            out.write("\r\n");
            //Close the output stream
            out.close();
        }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void OutputList(String filename, List<String> content)
    {
        try{
            // Create file
            File SDFile = android.os.Environment.getExternalStorageDirectory();

            FileWriter fstream = new FileWriter(SDFile.getAbsolutePath()+File.separator+filename,true);
            BufferedWriter out = new BufferedWriter(fstream);
            for(int i=0;i<content.size();i++)
            {
                out.write(content.get(i));
                out.write("\r\n");
            }
            //Close the output stream
            out.close();
        }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
}