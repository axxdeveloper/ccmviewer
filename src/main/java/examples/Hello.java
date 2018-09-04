package examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Hello {

    public String hello() throws IOException {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("hello.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder s = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            s.append(line);
        }
        return s.toString();
    }
    
}
