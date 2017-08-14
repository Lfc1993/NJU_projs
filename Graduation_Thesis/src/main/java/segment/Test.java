package segment;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.nlpcn.commons.lang.jianfan.JianFan;

import java.util.List;

/**
 * Created by lfc on 2016/4/11.
 */
public class Test {
    public static void main(String[] args) {
        String str2 = "北伐結束後，中華民國正式定都于南京，開啟往後以[[經濟]]建設為主的[[南京十年]]時期。但同時，國民黨與共產黨自1929年起爆發[[國共內戰]]，國民黨先後對中共的[[中国共产党革命根据地|武装根据地]]發動五次圍剿；";
        String str1 = "===\\n1271年，[[成吉思汗]]孙[[忽必烈]]建立[[元朝]]，定都于[[大都]]<sup>（今北京市）</sup>。1279年併[[南宋]]，開啟了“大中國”時代<ref>北大历史系主任张帆：[http://m.thepaper.cn/newsDetail_forward_1341436 ";
        String str3 = "这是一个测试，中华民国正式定都南京";


        str3 = JianFan.f2j(str3);

        System.out.println("-------------------------------jianfanzhuanhuan--------------------------------");
        System.out.println(str3);
        str1 = JianFan.f2j(str1);
        str2 = JianFan.f2j(str2);
        str2 = JianFan.f2j(str2);

//        wordSeg(str1);
        ansjSeg(str1);

//        wordSeg(str2);
        ansjSeg(str2);
    }

//    public static void wordSeg(String s) {
//        List<Word> res = WordSegmenter.segWithStopWords(s);
//        System.out.println("--------------------------------------------------------");
//        System.out.println("------------------------word segment--------------------------------");
//        System.out.println(res);
//        PartOfSpeechTagging.process(res);
//        System.out.println("------------------------word segment--------------------------------");
//        System.out.println(res);
//
//    }

    public static void ansjSeg(String s) {
        List<Term> parse = NlpAnalysis.parse(s);
        System.out.println("---------------------------ansj segment-----------------------------");
        System.out.println(parse);
//        new NatureRecognition(parse).recognition();
        System.out.println("---------------------------ansj segment-----------------------------");
        System.out.println(parse);

    }

}
