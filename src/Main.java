
import java.lang.String;
import java.util.Arrays;
import java.util.List;
import java.util.stream.*;
import java.util.*;
import java.nio.file.*;
import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException{
//        System.out.println("Hello, World!");

//      1.  Integer Stream
        IntStream
                .range(1, 10)
                .forEach(System.out::print);
        System.out.println();


//        1.  Integer Stream with skip
        IntStream
                .range(1, 10)
                .skip(5)
                .forEach(x -> System.out.println(x));
        System.out.println();
    }
}