import com.sun.javafx.iio.ios.IosDescriptor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Concurrency extends Thread{

    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private int character;
    private String header;
    private ZipOutputStream Zos;
    private List<Integer> intList;

    boolean ascii, zip, gzip;

    public Concurrency(Socket accept){

        System.out.println("Thread created");
        socket = accept;
        System.out.println("Socket Accepted");
        ascii = false;
        zip = false;
        gzip = false;
        character = 0;
        header = "";
        intList = new ArrayList<Integer>();
    }

    public void run(){
        server();
    }

    public void server() {

        try {
            is = this.socket.getInputStream();
            os = this.socket.getOutputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            //Store http request into a string
            String http_request = reader.readLine();
            System.out.println("HTTP Request: " + http_request);
            String file;
            String extension;

            if (http_request.contains("?")) {
                // The following lines of code are used to get the filename and his extension (file.extension)
                file = http_request.substring((http_request.indexOf('/') + 1));
                file = file.substring(0, file.indexOf(' '));

                String fileparts[] = file.split("\\?"); //URL will always have parameters even set to false?
                file = fileparts[0];
                System.out.println("\nThe filename is: " + file); //If the file is has spaces it will show as %20

                // This lines will take the extension of the file
                extension = file.substring(file.indexOf('.') + 1);
                System.out.println("The filename extension is: " + extension);

                // Store of parameters
                    String[] parameters = fileparts[1].split("\\&");
                for (String parameter : parameters) {
                    System.out.println(parameter);

                    if (parameter.equals("asc=true")) {
                        ascii = true;
                    }

                    if (parameter.contains("zip=true")) {

                        if (parameter.equals("gzip=true")) {
                            gzip = true;
                        }

                        if (parameter.equals("zip=true")){
                            zip = true;
                        }
                    }
                }
            } else {
                file = http_request.substring((http_request).indexOf('/') + 1);
                file = file.substring(0, file.indexOf(' '));
                System.out.println("\nThe filename is: " + file); //If the file is has spaces it will show as %20

                // This lines will take the extension of the file
                extension = file.substring(file.indexOf('.') + 1);
                System.out.println("The filename extension is: " + extension);
            }

            try{
                is = new FileInputStream(file);

                switch (extension){

                    case "txt":
                        if (gzip || zip){

                            gzip(file);

                            zip(file);

                            //TODO
                            /*if (gzip && zip){

                            }*/
                        }

                        else {
                            header = "HTTP/1.1 200 OK\n" + "Content-Type: text/plain \n\n"; //If plain is changed to txt it will download the file
                            System.out.println("Request answer: " + header);
                            os.write(header.getBytes());
                            this.out();
                        }
                        break;

                    case "html":
                        if (ascii || gzip || zip) {
                            if (ascii) {

                                intList = asciiOut();

                                if (gzip || zip) {

                                    if (gzip && !zip) {
                                        header = "HTTP/1.1 200 OK\n" + "Content-Type: application/x-gzip\n" +
                                                "Content-Disposition: filename=\"" + file + ".gz\"\n\n";
                                        System.out.println("Request answer: " + header);
                                        try {
                                            os.write(header.getBytes());
                                        } catch (IOException IOErr) {
                                            System.out.println("An error has occurred. Stack Trace: ");
                                            IOErr.printStackTrace();
                                        }

                                        this.gzipOut(intList);
                                    }

                                    if (!gzip && zip) {
                                        header = "HTTP/1.1 200 OK\n" + "Content-Type: application/zip\n" +
                                                "Content-Disposition: filename=\"" + file + ".zip\"\n\n";
                                        System.out.println("Request answer: " + header);
                                        try {
                                            os.write(header.getBytes());
                                        } catch (IOException IOErr) {
                                            System.out.println("An error has occurred. Stack Trace: ");
                                            IOErr.printStackTrace();
                                        }

                                        this.zipOut(file, intList);
                                    }

                                    //TODO
                                /*if (gzip && zip){

                                }*/
                                } else {
                                    out(intList);
                                }
                            }

                            if (gzip || zip){

                                gzip(file);

                                zip(file);

                                //TODO
                            /*if (gzip && zip){

                            }*/
                            }
                        }

                        else{
                            header = "HTTP/1.1 200 OK\n" + "Content-Type: text/html \n\n";
                            System.out.println("Request answer: " + header);
                            os.write(header.getBytes());
                            out();
                        }
                        break;

                    case "png":
                    case "jpeg":
                    case "gif":
                        if (gzip || zip){

                            gzip(file);

                            zip(file);

                            //TODO
                            /*if (gzip && zip){

                            }*/
                        }

                        else {
                            header = "HTTP/1.1 200 OK\n" + "Content-Type: image/" + extension + " \n\n";
                            System.out.println("Request answer: " + header);
                            os.write(header.getBytes());
                            this.out();
                        }

                        break;

                    default: //Not extension or extension not in list -> Return 404
                        System.out.println("File not found. Returning 404.");
                        header = "HTTP/1.1 404 Not Found\n\n";
                        System.out.println("Request answer: " + header);
                        os.write(header.getBytes());

                        break;
                }
            } catch (FileNotFoundException FErr) {
                System.out.println("File not found");
                header = "HTTP/1.1 404 Not Found\n\n"; //Return of the header if the file doesn't exists
                System.out.println("Request answer: " + header);
                os.write(header.getBytes());
            }

            os.flush();
            os.close();
            is.close();

        } catch (IOException IOErr){
            System.out.println("An error has ocurred. Stack Trace: ");
            IOErr.printStackTrace();
          }

    }

    private void zip(String file) {
        if(!gzip && zip){
            header = "HTTP/1.1 200 OK\n" + "Content-Type: application/zip\n" +
                    "Content-Disposition: filename=\"" + file + ".zip\"\n\n";
            System.out.println("Request answer: " + header);
            try {
                os.write(header.getBytes());
            } catch (IOException IOErr){
                System.out.println("An error has occurred. Stack Trace: ");
                IOErr.printStackTrace();
            }

            this.zipOut(file);
        }
    }

    private void gzip(String file) {
        if(gzip && !zip){
            header = "HTTP/1.1 200 OK\n" + "Content-Type: application/x-gzip\n" +
                    "Content-Disposition: filename=\"" + file + ".gz\"\n\n";
            System.out.println("Request answer: " + header);
            try {
                os.write(header.getBytes());
            } catch (IOException IOErr){
                System.out.println("An error has occurred. Stack Trace: ");
                IOErr.printStackTrace();
            }

            this.gzipOut();
        }
    }

    public void out(){
        try {
            while ((character = is.read()) != -1) {
                os.write(character);
            }
        } catch (IOException IOErr){
            System.out.println("An error has occurred. Stack Trace: ");
            IOErr.printStackTrace();
        }
    }

    public void out(List<Integer> list){
        try{
            for (Integer c : list){
                os.write(c);
            }

        } catch (IOException IOErr){
            System.out.println("An error has ocurred. Stack Trace: ");
            IOErr.printStackTrace();
        }
    }

    public List<Integer> asciiOut(){

        List<Integer> tmpList = new ArrayList<Integer>();
        is = new AsciiInputStream(is);

        try {
            while ((character = is.read()) != -1){
                tmpList.add(character); }

        } catch (IOException IOErr){
            System.out.println("An error has ocurred. Stack Trace: ");
            IOErr.printStackTrace();
        }

        return tmpList;
    }

    public void gzipOut(){
        try {
            os = new GZIPOutputStream(os);

            while ((character = is.read()) != -1){
                os.write(character);
            }

        } catch (IOException IOErr){
            System.out.println("An error has occurred. Stack Trace: ");
            IOErr.printStackTrace();
        }
    }

    public void gzipOut(List<Integer> list){
        try{
            os = new GZIPOutputStream(os);

            for (Integer c : list){
                os.write(c);
            }
        } catch (IOException IOErr){
            System.out.println("An error has ocurred. Stack Trace: ");
            IOErr.printStackTrace();
        }
    }

    public void zipOut(String file){
        try {
            Zos = new ZipOutputStream(os);
            Zos.putNextEntry(new ZipEntry(file));

            os = Zos;

            while ((character = is.read()) != -1){
                os.write(character);
            }

        } catch (IOException IOErr){
            System.out.println("An error has occurred. Stack Trace: ");
            IOErr.printStackTrace();
        }
    }

    public void zipOut(String file, List<Integer> list){
        try {
            Zos = new ZipOutputStream(os);
            Zos.putNextEntry(new ZipEntry(file));
            os = Zos;

            for (Integer c : list){
                os.write(c);
            }
        } catch (IOException IOErr){
            System.out.println("An error has occurred. Stack Trace: ");
            IOErr.printStackTrace();
        }
    }

}
