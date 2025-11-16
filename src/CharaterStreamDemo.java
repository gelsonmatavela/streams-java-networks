import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
public class CharaterStreamDemo {
    public static void main(String[] args) throws IOException {
      FileReader characterReaderStream = null;
      FileWriter characterWriterStream = null;

      try{
         characterReaderStream = new FileReader("src/characterSource.txt");
         characterWriterStream = new FileWriter("src/characterDest.txt");

        // Reading source file and wrinting content to target file character by character
        
        int content;
        while((content = characterReaderStream.read()) != -1){
            characterWriterStream.append((char) content);
        }
      }finally{
        if (characterReaderStream != null){
            characterReaderStream.close();
        }
        if(characterWriterStream != null){
            characterWriterStream.close();
        }
      }
    }
}