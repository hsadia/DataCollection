package com.purdue.watch.sensordata;

/**
 * Created by Haleema on 01/06/2017.
 */

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class FileStreamManager {

    public void deleteFolder(String folderName, String sDate) {
        File SDFile = android.os.Environment.getExternalStorageDirectory();
        File destDir = new File(SDFile.getAbsolutePath() + "/"+folderName+"/"
                + sDate);
        String[] myFiles;
        if(destDir.isDirectory()){
            myFiles = destDir.list();
            for (int i=0; i<myFiles.length; i++) {
                File myFile = new File(destDir, myFiles[i]);
                myFile.delete();
            }
        }
        destDir.delete();

        File Dir = new File (SDFile.getAbsolutePath() + "/"+folderName);
        myFiles = Dir.list();
        for (int i=0; i<myFiles.length; i++) {
            File myFile = new File(Dir, myFiles[i]);
            myFile.delete();
        }
        Dir.delete();
        Log.d("Delete Folder", "Deleted!");
    }

    public boolean OutputList(String filename, List<String> content, String folderName, String sDate, Context c)
    {
        // ... creating file in external storage

        File SDFile     = Environment.getExternalStorageDirectory();
        String path     = SDFile.getAbsolutePath();
        String path_one = path       + File.separator + folderName;
        String path_two = path_one   + File.separator + sDate;
        String myFile   = path_two   + File.separator + filename;

        boolean check = false;
        File folder, date_folder, file;

        try {
            folder = new File(path_one);
            if(folder.exists() && folder.isDirectory()){
                date_folder = new File (path_two);
                if(date_folder.exists() && date_folder.isDirectory()){
                    //do nothing
                }else{
                    date_folder = new File(path_two);
                    date_folder.mkdirs();
                }
            } else {
                folder.mkdirs();
                Log.d("Created Folder", folder.getName());
                date_folder = new File(path_two);
                date_folder.mkdirs();
                Log.d("Created Folder", date_folder.getName());
            }
            file = new File(path_one+"/"+filename);
            file.createNewFile();
            Log.d("Created File", file.getName());
        }catch (Exception e){
            Log.e("Create File", e.getMessage());
            return false;
        }

        //...if able to create file, write in it
        if(!check) {
            try {

                FileWriter fstream = new FileWriter(myFile);
                BufferedWriter out = new BufferedWriter(fstream);

                for (int i = 0; i < content.size(); i++) {

                    out.write(content.get(i));
                    out.write("\r\n");
                }

                //Close the output stream
                out.close();

            } catch (Exception e) {
                Log.e("Writing File", "Error: " + e.getMessage());
                return false;
            }
      }
        return true;
    }

    public void ReadDir(){


        File SDFile = android.os.Environment.getExternalStorageDirectory();
        String path = SDFile.getAbsolutePath().toString()+"/Data";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
        }

    }


}