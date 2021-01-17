package application.resources;

import javafx.stage.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.List;
import java.util.Scanner;


public class File extends java.io.File {
    public PrintWriter out;
    public Scanner in;
    public FileOutputStream outstream;
    private char type;
    private static FileChooser fc = new FileChooser();
    private static DirectoryChooser dc = new DirectoryChooser();

    static {
        fc.setInitialDirectory(new java.io.File(System.getProperty("user.dir")));
        dc.setInitialDirectory(new java.io.File(System.getProperty("user.dir")));
    }

    public File(String filename) {
        this(filename, 'r');
    }

    public File(String filename, char type) {
        super(filename);
        this.type = type;
        if(!isDirectory()) {
            if (type == 'r' || type == 'w' || type == 'a') {
                try {
                    in = new Scanner(this);
                } catch (IOException e) {
                    in = null;
                }
            }
            if (type == 'r') {
                outstream = null;
                out = null;
            } else if (type == 'w') {
                try {
                    outstream = new FileOutputStream(filename);
                    out = new PrintWriter(outstream);
                } catch (IOException e) {
                    out = null;
                }
            } else if (type == 'a') {
                try {
                    outstream = new FileOutputStream(filename, true);
                    out = new PrintWriter(outstream);
                } catch (IOException e) {
                    out = null;
                }
            }
        } else {
            in = null;
            out = null;
            outstream = null;
        }
    }

    public File() {
        this('r');
    }

    public File(char type) {
        this(getFile(new Stage()), type);
    }

    public File(File file) {
        this(file.getAbsolutePath());
    }

    public File(java.io.File file) {
        this(file.getAbsolutePath());
    }

    public File(File file, char type) {
        this(file.getAbsolutePath(), type);
    }

    public File(java.io.File file, char type) {
        this(file.getAbsolutePath(), type);
    }


    public String getName() { return getAbsolutePath(); }

    public boolean rename(File dest) { return renameTo(dest); }

    public File[] listFiles() {
        return convert(listFiles());
    }

    public static File[] convert(java.io.File ...files) {
        File[] rearr = new File[files.length];
        for(int i = 0; i < files.length; i++) {
            rearr[i] = new File(files[i]);
        }
        return rearr;
    }

    public static File[] convert(List<java.io.File> files) {
        File[] rearr = new File[files.size()];
        for(int i = 0; i < files.size(); i++) {
            rearr[i] = new File(files.get(i));
        }
        return rearr;
    }

    public static File getFile(Stage stage) {
        return convert(fc.showOpenDialog(stage))[0];
    }

    public static File[] getFiles(Stage stage) {
        return convert(fc.showOpenMultipleDialog(stage));
    }

    public static File getPNG(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Portable Newtwork Graphics (PNG) Files", "*.png")
        );
        chooser.setInitialDirectory(new java.io.File(System.getProperty("user.dir")));
        return convert(chooser.showOpenDialog(stage))[0];
    }

    public static File getDirectory(Stage stage) {
        return convert(dc.showDialog(stage))[0];
    }

    public boolean hasNext() {
        return in.hasNext();
    }

    public int count(String substring) {
        String input = read();
        String[] words = input.split(substring);
        return words.length-1;
    }

    public int count(char substring) {
        return count(substring+"");
    }

    public int count(int substring) {
        return count(substring+"");
    }

    public int count(double substring) {
        return count(substring+"");
    }

    public int count(float substring) {
        return count(substring+"");
    }

    public String[] split(String delimeter) {
        String input = read();
        String[] words = input.split(delimeter);
        return words;
    }

    public String read() {
        if (this.in != null) {
            Scanner inp;
            try {
                inp = new Scanner(this);
                String res = "";
                while (inp.hasNext()) {
                    res += inp.nextLine()+'\n';
                }
                return res;
            } catch (IOException e) {
                return "";
            }
        }
        else return "";
    }

    public String readLine() {
        if (in != null) {
            if (!hasNext()) {
                try {
                    in = new Scanner(this);
                } catch (IOException e) {
                    in = null;
                }
            }
            return in.nextLine();
        }
        else return "";
    }



    public String[] readLines() {
        if (in != null) return read().split("\n");
        else return new String[0];
    }

    public int[] readInts(String delimeter) {
        String[] s;
        if (delimeter == "\n") s = readLines();
        else s = readLine().split(delimeter);
        int[] rearr = new int[s.length];
        for(int i = 0; i < s.length; i++) {
            rearr[i] = Integer.parseInt(s[i].trim());
        }
        return rearr;
    }
    public int[] readInts(char delimeter) throws IOException {
        try {
            String[] s;
            if (delimeter == '\n') s = readLines();
            else s = readLine().split("" + delimeter);
            int[] rearr = new int[s.length];
            for (int i = 0; i < s.length; i++) {
                rearr[i] = Integer.parseInt(s[i].trim());
            }
            return rearr;
        } catch (Exception ex) {
            throw new IOException();
        }
    }
    public int[] readInts() throws IOException {
        try {
            String[] s = readLines();
            int[] rearr = new int[s.length];
            for (int i = 0; i < s.length; i++) {
                rearr[i] = Integer.parseInt(s[i].trim());
            }
            return rearr;
        }
        catch (Exception ex) {
            throw new IOException();
        }
    }

    public void close() {
        try { in.close(); } catch(NullPointerException ex) {}
        try { out.close(); } catch(NullPointerException ex) {}
    }

    public URI relativize(File file) { return toURI().relativize(file.toURI()); }
}

