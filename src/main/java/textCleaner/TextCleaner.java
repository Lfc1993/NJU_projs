package textCleaner;

import ie.Bootstrapping;
import ie.MyTerm;
import ie.Pattern;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.lucene.queries.function.docvalues.BoolDocValues;
import org.apdplat.word.vector.T;
import util.DBHelper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashSet;
import java.util.List;

/**
 * Created by lfc on 2016/4/9.
 */
public class TextCleaner {


    public static void main(String[] args) {

        String s1 = "秦朝定都于咸阳";
        String s2 = "秦朝定都于西安";
        String s3 = "明朝都城北京";
        List<Term> parse3=NlpAnalysis.parse(s3);
        System.out.println(parse3);
//        List<Term> parse1 = NlpAnalysis.parse(s1);
//        List<Term> parse2 = NlpAnalysis.parse(s2);
//        Pattern p1 = new Pattern(new MyTerm(parse1.get(0)), new MyTerm(parse1.get(1)), new MyTerm(parse1.get(2)));
//        Pattern p2 = new Pattern(new MyTerm(parse2.get(0)), new MyTerm(parse2.get(1)), new MyTerm(parse2.get(2)));
//        System.out.println(p1.equals(p2));
    }


}
