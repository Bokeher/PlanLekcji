package com.example.planlekcji;

import com.example.planlekcji.MainApp.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileReadWriter {

    public void writeToFile(String fileName, String content) {
        File path = MainActivity.getContext().getFilesDir();

        try {
            File f = new File(path, fileName);
            FileOutputStream writer = new FileOutputStream(f);
            writer.write(content.getBytes());
            writer.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String readFromFile(String fileName) {
        File path = MainActivity.getContext().getFilesDir();
        File readFrom = new File(path, fileName);
        byte[] content = new byte[(int) readFrom.length()];

        try {
            FileInputStream stream = new FileInputStream(readFrom);
            stream.read(content);
            stream.close();
            return new String(content);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
