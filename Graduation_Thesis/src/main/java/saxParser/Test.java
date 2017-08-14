package saxParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/** main method to start parsing xml to DB
 * Created by lfc on 2016/3/27.
 */
public class Test {

    public static void main(String[] args) {
        XMLPArser sax = new XMLPArser();
        InputStream input = null;
        try {
            input = new FileInputStream(new File("F:\\Download\\zhwiki-latest-pages-articles-multistream.xml"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            sax.getText(input);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
