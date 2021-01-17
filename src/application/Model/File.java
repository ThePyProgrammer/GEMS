package application.Model;

import javafx.stage.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class File extends java.io.File {
    public PrintWriter out;
    public Scanner in;
    public FileOutputStream outstream;
    private char type;
    private static FileChooser fc = new FileChooser();
    private static DirectoryChooser dc = new DirectoryChooser();

    public static FileChooser.ExtensionFilter PNG = new FileChooser.ExtensionFilter("Portable Newtwork Graphics (PNG) Files", "*.png"),
            ICO = new FileChooser.ExtensionFilter("ICO Files", "*.ico"),
            JPG = new FileChooser.ExtensionFilter("JPEG Files", "*.jpg", "*.jpeg"),
            GIF = new FileChooser.ExtensionFilter("Graphics Interchange Format (GIF) Files", "*.gif"),
            JFIF = new FileChooser.ExtensionFilter("JPEG File Interchange Format (JFIF) Files", "*.jfif"),
            PDF = new FileChooser.ExtensionFilter("Portable Document Format (PDF) Files", "*.pdf"),
            HTML = new FileChooser.ExtensionFilter("HyperText Markup Language (HTML) Files", "*.htm", "*.html"),
            XHTML = new FileChooser.ExtensionFilter("Extensible HyperText Markup Language (XHTML) Files", "*.xhtml"),
            PHTML = new FileChooser.ExtensionFilter("PHP HyperText Markup Language (PHTML) Files", "*.phtml"),
            XML = new FileChooser.ExtensionFilter("Extensible Markup Language (XML) Files", "*.xml"),
            PHP = new FileChooser.ExtensionFilter("PHP Files", "*.php", "*.php5"),
            CSS = new FileChooser.ExtensionFilter("Cascading Style Sheets (CSS) Files", "*.css"),
            JS = new FileChooser.ExtensionFilter("JavaScript (JS) Files", "*.js", "*.jsm"),
            JSON = new FileChooser.ExtensionFilter("JSON Files", "*.json"),
            JAVA = new FileChooser.ExtensionFilter("Java Files", "*.java"),
            KOTLIN = new FileChooser.ExtensionFilter("Kotlin Files", "*.kt"),
            SCALA = new FileChooser.ExtensionFilter("Scala Files", "*.scala"),
            PYTHON = new FileChooser.ExtensionFilter("Python Files", "*.py", "*.pyc", "*.pyw"),
            RUBY = new FileChooser.ExtensionFilter("Ruby Files", "*.rb", "*.rbw", "*.rake", "*.rbx"),
            C = new FileChooser.ExtensionFilter("C Files", "*.c", "*.h", "*.idc"),
            CPP = new FileChooser.ExtensionFilter("C++ Files", "*.cpp", "*.hpp", "*.c++", "*.h++", "*.cc", "*.hh", "*.cxx", "*.hxx", "*.C", "*.H", "*.cp", "*.cpp"),
            INO = new FileChooser.ExtensionFilter("Arduino Files", "*.ino"),
            VB = new FileChooser.ExtensionFilter("Visual Basic Files", "*.vbs"),
            GO = new FileChooser.ExtensionFilter("Golang Files", "*.go"),
            RUST = new FileChooser.ExtensionFilter("Rust Files", "*.rs"),
            SWIFT = new FileChooser.ExtensionFilter("Swift Files", "*.swift"),
            TEXT = new FileChooser.ExtensionFilter("Text Files", "*.txt", "*.text"),
            MD = new FileChooser.ExtensionFilter("Markdown (MD) Files", "*.md"),
            README = new FileChooser.ExtensionFilter("README Files", "*.README"),
            BIBTEX = new FileChooser.ExtensionFilter("BibTeX Files", "*.bib"),
            LATEX = new FileChooser.ExtensionFilter("LaTeX Files", "*.tex", "*.aux", "*.toc"),
            SQL = new FileChooser.ExtensionFilter("Structured Query Language (SQL) Files", "*.sql");

    //
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
        fc.setInitialDirectory(new java.io.File(System.getProperty("user.dir")));
        return convert(fc.showOpenDialog(stage))[0];
    }

    public static File[] getFiles(Stage stage) {
        fc.setInitialDirectory(new File(System.getProperty("user.dir")));
        return convert(fc.showOpenMultipleDialog(stage));
    }

    public static File getFile(Stage stage, FileChooser.ExtensionFilter...extensions) {
        fc.getExtensionFilters().addAll(Arrays.asList(extensions));
        File file = getFile(stage);
        fc.getExtensionFilters().clear();
        return file;
    }

    public static File[] getFiles(Stage stage, FileChooser.ExtensionFilter...extensions) {
        fc.getExtensionFilters().addAll(Arrays.asList(extensions));
        File[] files = getFiles(stage);
        fc.getExtensionFilters().clear();
        return files;
    }

    public static File getPNG(Stage stage) {
        return getFile(stage, PNG);
    }

    public static File[] getPNGs(Stage stage) {
        return getFiles(stage, PNG);
    }

    public static File getJPG(Stage stage) {
        return getFile(stage, JPG);
    }

    public static File[] getJPGs(Stage stage) {
        return getFiles(stage, JPG);
    }

    public static File getICO(Stage stage) {
        return getFile(stage, ICO);
    }

    public static File[] getICOs(Stage stage) {
        return getFiles(stage, ICO);
    }

    public static File getGIF(Stage stage) {
        return getFile(stage, GIF);
    }

    public static File[] getGIFs(Stage stage) {
        return getFiles(stage, GIF);
    }

    public static File getJFIF(Stage stage) {
        return getFile(stage, JFIF);
    }

    public static File[] getJFIFs(Stage stage) {
        return getFiles(stage, JFIF);
    }

    public static File getPDF(Stage stage) { return getFile(stage, PDF); }
    public static File[] getPDFs(Stage stage) { return getFiles(stage, PDF);}


    public static File getHTML(Stage stage) { return getFile(stage, HTML); }
    public static File[] getHTMLs(Stage stage) { return getFiles(stage, HTML);}


    public static File getXHTML(Stage stage) { return getFile(stage, XHTML); }
    public static File[] getXHTMLs(Stage stage) { return getFiles(stage, XHTML);}


    public static File getPHTML(Stage stage) { return getFile(stage, PHTML); }
    public static File[] getPHTMLs(Stage stage) { return getFiles(stage, PHTML);}


    public static File getXML(Stage stage) { return getFile(stage, XML); }
    public static File[] getXMLs(Stage stage) { return getFiles(stage, XML);}


    public static File getPHP(Stage stage) { return getFile(stage, PHP); }
    public static File[] getPHPs(Stage stage) { return getFiles(stage, PHP);}


    public static File getCSS(Stage stage) { return getFile(stage, CSS); }
    public static File[] getCSSs(Stage stage) { return getFiles(stage, CSS);}


    public static File getJS(Stage stage) { return getFile(stage, JS); }
    public static File[] getJSs(Stage stage) { return getFiles(stage, JS);}


    public static File getJSON(Stage stage) { return getFile(stage, JSON); }
    public static File[] getJSONs(Stage stage) { return getFiles(stage, JSON);}


    public static File getJAVA(Stage stage) { return getFile(stage, JAVA); }
    public static File[] getJAVAs(Stage stage) { return getFiles(stage, JAVA);}


    public static File getKOTLIN(Stage stage) { return getFile(stage, KOTLIN); }
    public static File[] getKOTLINs(Stage stage) { return getFiles(stage, KOTLIN);}


    public static File getSCALA(Stage stage) { return getFile(stage, SCALA); }
    public static File[] getSCALAs(Stage stage) { return getFiles(stage, SCALA);}


    public static File getPYTHON(Stage stage) { return getFile(stage, PYTHON); }
    public static File[] getPYTHONs(Stage stage) { return getFiles(stage, PYTHON);}


    public static File getRUBY(Stage stage) { return getFile(stage, RUBY); }
    public static File[] getRUBYs(Stage stage) { return getFiles(stage, RUBY);}


    public static File getC(Stage stage) { return getFile(stage, C); }
    public static File[] getCs(Stage stage) { return getFiles(stage, C);}


    public static File getCPP(Stage stage) { return getFile(stage, CPP); }
    public static File[] getCPPs(Stage stage) { return getFiles(stage, CPP);}


    public static File getINO(Stage stage) { return getFile(stage, INO); }
    public static File[] getINOs(Stage stage) { return getFiles(stage, INO);}


    public static File getVB(Stage stage) { return getFile(stage, VB); }
    public static File[] getVBs(Stage stage) { return getFiles(stage, VB);}


    public static File getGO(Stage stage) { return getFile(stage, GO); }
    public static File[] getGOs(Stage stage) { return getFiles(stage, GO);}


    public static File getRUST(Stage stage) { return getFile(stage, RUST); }
    public static File[] getRUSTs(Stage stage) { return getFiles(stage, RUST);}


    public static File getSWIFT(Stage stage) { return getFile(stage, SWIFT); }
    public static File[] getSWIFTs(Stage stage) { return getFiles(stage, SWIFT);}


    public static File getTEXT(Stage stage) { return getFile(stage, TEXT); }
    public static File[] getTEXTs(Stage stage) { return getFiles(stage, TEXT);}


    public static File getMD(Stage stage) { return getFile(stage, MD); }
    public static File[] getMDs(Stage stage) { return getFiles(stage, MD);}


    public static File getREADME(Stage stage) { return getFile(stage, README); }
    public static File[] getREADMEs(Stage stage) { return getFiles(stage, README);}


    public static File getBIBTEX(Stage stage) { return getFile(stage, BIBTEX); }
    public static File[] getBIBTEXs(Stage stage) { return getFiles(stage, BIBTEX);}


    public static File getLATEX(Stage stage) { return getFile(stage, LATEX); }
    public static File[] getLATEXs(Stage stage) { return getFiles(stage, LATEX);}


    public static File getSQL(Stage stage) { return getFile(stage, SQL); }
    public static File[] getSQLs(Stage stage) { return getFiles(stage, SQL);}

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