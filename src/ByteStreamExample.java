import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

class ByteStreamExample {
    public static void main(String[] ar) throws IOException {
        FileInputStream inStream = null;
        FileOutputStream outStream = null;

        try {
            inStream = new FileInputStream("src/source.txt");
            outStream = new FileOutputStream("src/dest.txt");
            
            // faz leitura de byte a cada tempo, se atingiu o final do ficheiro retorna -1
            int content;

            while ((content = inStream.read()) != -1){
                outStream.write((byte) content);
            }

        } finally {
            if(inStream != null){
                inStream.close();
            }
            if(outStream != null){
                outStream.close();
            }
        }
    }
}