package ie;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.apache.log4j.Logger;
import util.DBHelper;

import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * Created by lfc on 2016/5/15.
 */
public class Bootstrapping {

    private static HashSet<Pattern> seedPattern;
    private static HashMap<Pattern, Double> candidatePattrn = new HashMap<Pattern, Double>();
    private static HashMap<Pattern, Double> candidateTerms = new HashMap<Pattern, Double>();
    private static HashMap<Pattern, Integer> extractSeed = new HashMap<Pattern, Integer>();
    private static int iterNum = 10;
    private static double PatternThreshold = 0.05;
    private static double TermThreshold = 2.0;
    private static int onceAmount = 200;
    private static ArrayList<String> textList = new ArrayList<String>(onceAmount);
    private static int current = 0;
    private static int curId = 1;
    private static String path = "F:\\id.txt";
    private static Logger logger = Logger.getLogger(Bootstrapping.class);
    private static Logger scoreLogger = Logger.getLogger("scoreLogger");

    public static void setCurId(int curId) {
        Bootstrapping.curId = curId;
    }

    public Bootstrapping(HashSet<Pattern> seed, int iter, double patternThreshold, double termThreshold) {
        this.seedPattern = seed;
        this.iterNum = iter;
        this.PatternThreshold = patternThreshold;
        this.TermThreshold = termThreshold;
        initId();
    }

    public HashSet<Pattern> bootstrapping() {
//自扩展算法主要逻辑

        String s;
        int count = 0;
        long totalStart = System.currentTimeMillis();

        for (int i = 0; i < this.iterNum; i++) {
            long everyStart = System.currentTimeMillis();
            long everyEnd;
            //产生候选集
            do {
                long getTime = System.currentTimeMillis();
                s = this.getNextText();
                long getEnd = System.currentTimeMillis();
                long time = getEnd - getTime;
                if (time > 1000) {
                    System.out.println("----------------------------------------------------");
                    System.out.println("get text time : " + (getEnd - getTime));
                }
                long pcTime = System.currentTimeMillis();
                this.processText(s);
                long pcEnd = System.currentTimeMillis();
                time = pcEnd - pcTime;
                if (time > 1000) {
                    System.out.println("----------------------------------------------------");
                    System.out.println("process text time : " + (pcEnd - pcTime));
                }
            } while (null != s);

            printCandidate();

            //开始对候选集打分
            score();

            //筛选候选集，加入种子集
            Iterator iter = candidatePattrn.entrySet().iterator();
            Map.Entry entry;
            while (iter.hasNext()) {
                entry = (Map.Entry) iter.next();
                Double d = (Double) entry.getValue();

                Pattern p = (Pattern) entry.getKey();
                scoreLogger.debug(p.getSubject().getTerm().getName()
                        + p.getPredicate().getTerm().getName()
                        + p.getObject().getTerm().getName() + " score : " + d);
                if (d >= PatternThreshold) {
                    seedPattern.add((Pattern) entry.getKey());
                }
            }
            iter = candidateTerms.entrySet().iterator();
            while (iter.hasNext()) {
                entry = (Map.Entry) iter.next();
                Double d = (Double) entry.getValue();

                Pattern p = (Pattern) entry.getKey();
                scoreLogger.debug(p.getSubject().getTerm().getName()
                        + p.getPredicate().getTerm().getName()
                        + p.getObject().getTerm().getName() + " score : " + d);

                if (d > TermThreshold) {
                    seedPattern.add((Pattern) entry.getKey());
                }
            }

            if (candidatePattrn.isEmpty() && (candidateTerms.isEmpty())) {
                System.out.println("--------------------------------------------");
                System.out.println("no more candidate add");

                break;
            }
            //TODO 可能需要持久化抽取出的种子集
            everyEnd = System.currentTimeMillis();
            candidatePattrn.clear();
            candidateTerms.clear();
            System.out.println("--------------------------------------------");
            System.out.println("cost for the " + i + " iteration : " + (everyEnd - everyStart));
        }

        long totalEnd = System.currentTimeMillis();
        System.out.println("--------------------------------------------");
        System.out.println("total cost : " + (totalEnd - totalStart));

        return this.seedPattern;
    }

    /**
     * score candidate patterns and terms
     */
    private void score() {
        //TODO to test
        // score candidate patterns and terms

        HashMap<Pattern, Double> termPatternScore = new HashMap<Pattern, Double>();
        Iterator iterTerm;

        Map.Entry entryTerm;
        Pattern tempTerm;
        double scoreTerm;

        String fullText;
        //init get next text
        curId = 1;
        current = 201;

        //score candidate pattern
        do {
            fullText = getNextText();
            if (null != fullText) {
                //scan candidate pattern
                scoreNofPattern(candidatePattrn, fullText);

                //scan candidate terms
                iterTerm = candidateTerms.entrySet().iterator();
                while (iterTerm.hasNext()) {
                    entryTerm = (Map.Entry) iterTerm.next();
                    tempTerm = (Pattern) entryTerm.getKey();

                    Pattern pattern = findCandidatePattern(tempTerm, fullText);
                    if (null != pattern) {
                        if (!isSeedPattern(pattern)) {
                            termPatternScore.put(pattern, 0.0);
                        }
                    }
                }
            }
        } while (null != fullText);

        //scan seed pattern
        scorePattern(candidatePattrn);

        //reinit getNextText
        curId = 1;
        current = 201;

        //score candidate terms
        do {
            fullText = getNextText();
            if (null != fullText) {
                scoreNofPattern(termPatternScore, fullText);
            }
        } while (null != fullText);

        scorePattern(termPatternScore);

        iterTerm = candidateTerms.entrySet().iterator();
        while (iterTerm.hasNext()) {
            entryTerm = (Map.Entry) iterTerm.next();
            tempTerm = (Pattern) entryTerm.getKey();

            Iterator iter = termPatternScore.entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Pattern pattern = (Pattern) entry.getKey();
                if ((pattern.getSubject().equals(tempTerm.getSubject()))
                        && (pattern.getObject().equals(tempTerm.getObject()))) {
                    double score = (Double) entry.getValue();
                    entryTerm.setValue((Double) entry.getValue() + 1 + 0.01*score);
                }
            }
        }
    }


    /**
     * find N of candidate patterns
     *
     * @param candidatePattrn
     */
    private void scoreNofPattern(HashMap<Pattern, Double> candidatePattrn, String fullText) {
        Iterator iterPattern = candidatePattrn.entrySet().iterator();
        Map.Entry entryPattern;
        Pattern tempPattern;

        while (iterPattern.hasNext()) {
            entryPattern = (Map.Entry) iterPattern.next();
            tempPattern = (Pattern) entryPattern.getKey();

            Pattern pattern = findCandidateTerm(tempPattern, fullText);
            if (null != pattern) {
                if (!isSeedTerm(pattern)) {
                    entryPattern.setValue((Double) entryPattern.getValue() + 1);
                }
            }

        }
    }

    private void scorePattern(HashMap<Pattern, Double> tempCandidatePattrn) {
        Iterator iterPattern = tempCandidatePattrn.entrySet().iterator();
        Map.Entry entryPattern;
        Pattern tempPattern;
        double scorePattern;

        while (iterPattern.hasNext()) {
            int f = 0;
            entryPattern = (Map.Entry) iterPattern.next();
            tempPattern = (Pattern) entryPattern.getKey();
            double n = (Double) entryPattern.getValue();
            Iterator iter = seedPattern.iterator();

            for (Pattern p : seedPattern) {
                if ((p.getSubject().equals(tempPattern.getSubject())) && (p.getObject().equals(tempPattern.getObject()))) {
                    f += 1;
                }
            }
            if (n > 0) {
                scorePattern = (f / n) * (Math.log(f + 1) / Math.log(2));
                entryPattern.setValue(scorePattern);
            }
            scoreLogger.info(tempPattern.getSubject().getTerm().getName()
                    + tempPattern.getPredicate().getTerm().getName()
                    + tempPattern.getObject().getTerm().getName() + " N : " + n + " f : " + f);

        }
    }

    /**
     * print candidate patterns and terms
     */
    private void printCandidate() {
        Iterator iter = (Iterator) candidatePattrn.entrySet().iterator();
        printIter(iter);
        iter = (Iterator) candidateTerms.entrySet().iterator();
        printIter(iter);
    }

    /**
     * used by printCandidate()
     * @param iter
     */
    private void printIter(Iterator iter) {
        Map.Entry entry;
        Pattern p;
        while (iter.hasNext()) {
            entry = (Map.Entry) iter.next();
            p = (Pattern) entry.getKey();
            logger.info("-----------------------------------------------");
            logger.info(p.getSubject().getTerm().getName()
                    + p.getPredicate().getTerm().getName()
                    + p.getObject().getTerm().getName());

        }
    }

    /**
     * find candidate term from fulltext , matching pattern p
     * @param p
     * @param fullText
     * @return
     */
    private Pattern findCandidateTerm(Pattern p, String fullText) {
        List<Term> parse;
        Pattern termTemp = null;
        if ((null != fullText) && (null != p)) {
            String[] texts = fullText.split("。");
            if (texts != null) {
                for (String text : texts) {
//                System.out.println("-----------------------process text:---------------------");
//                System.out.println(text);
                    if (text.contains(p.getPredicate().getTerm().getName())) {
                        //谓语匹配，看是否发现候选主谓语名词
                        parse = NlpAnalysis.parse(text);
                        int index = findPosition(parse, p.getPredicate().getTerm());
                        if (index >= 0) {
                            Term[] t = findNearestTerms(parse, index, p.getSubject().getTerm().getNatureStr(), p.getObject().getTerm().getNatureStr());
                            if ((null != t) && (t.length > 1)) {
                                termTemp = new Pattern(new MyTerm(t[0]), new MyTerm(parse.get(index)), new MyTerm(t[1]));
                            }
                        }
                    }
                }
            }
        }
        return termTemp;
    }


    /**
     * find candidate pattern from fulltext , matching tern p
     * @param p
     * @param fullText
     * @return
     */
    private Pattern findCandidatePattern(Pattern p, String fullText) {
        List<Term> parse;
        Pattern pattern = null;
        if ((null != fullText) && (null != p)) {
            String[] texts = fullText.split("。");
            if (texts != null) {
                for (String text : texts) {
                    if ((text.contains(p.getSubject().getTerm().getName())
                            && (text.contains(p.getObject().getTerm().getName())))) {
                        //主宾语匹配，看是否发现候选模板
                        parse = NlpAnalysis.parse(text);
                        int subIndex = findPosition(parse, p.getSubject().getTerm());
                        if (subIndex >= 0) {
                            int objIndex = findPosition(parse, p.getObject().getTerm());
                            if (objIndex >= 0) {
                                int preIndex = findPredicatePosition(parse, subIndex, objIndex, p.getPredicate().getTerm().getNatureStr());
                                if (preIndex >= 0) {
                                    pattern = new Pattern(new MyTerm(parse.get(subIndex)), new MyTerm(parse.get(preIndex)), new MyTerm(parse.get(objIndex)));

                                }
                            }
                        }
                    }
                }
            }
        }
        return pattern;
    }

    /**
     * process every text , named-entity recognize , extract candidate patterns and terms
     * @param fullText
     */
    private void processText(String fullText) {
        if (null != fullText) {
            for (Pattern p : seedPattern) {
                Pattern termTemp = findCandidateTerm(p, fullText);
                if (null != termTemp) {
                    if (!isSeedTerm(termTemp)) {
                        Bootstrapping.candidateTerms.put(termTemp, 0.0);
                        System.out.println("-----------------------add candidate---------------------");
                        System.out.println("raw text : " + fullText);
                        logger.debug("raw text : " + fullText);
                        logger.info(termTemp.getSubject().getTerm().getName()
                                + termTemp.getPredicate().getTerm().getName()
                                + termTemp.getObject().getTerm().getName());
                    }
                } else {
                    Pattern pattern = findCandidatePattern(p, fullText);
                    if (null != pattern) {
                        if (!isSeedPattern(pattern)) {
                            Bootstrapping.candidatePattrn.put(pattern, 0.0);
                            System.out.println("-----------------------add candidate---------------------");
                            System.out.println("raw text : " + fullText);
                            logger.debug("raw text : " + fullText);
                            logger.info(pattern.getSubject().getTerm().getName()
                                    + pattern.getPredicate().getTerm().getName()
                                    + pattern.getObject().getTerm().getName());
                        }
                    }
                }
            }
        }

    }

    /**
     * find the position of term t in the named-entity list
     * @param list
     * @param t
     * @return
     */
    private int findPosition(List<Term> list, Term t) {
        int index = -1;
        Term temp;
        for (int i = 0; i < list.size(); i++) {
            temp = list.get(i);
            if ((temp.getNatureStr().equals(t.getNatureStr())) && (temp.getName().equals(t.getName()))) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * 根据主宾的位置，查找谓语的位置
     * @param list
     * @param low
     * @param high
     * @param pos
     * @return
     */
    private int findPredicatePosition(List<Term> list, int low, int high, String pos) {
        int index = -1;
        int size = list.size();
        if ((low >= high) || (low < 0) || (high >= size)) {
            return index;
        }
        for (int i = low + 1; i < high; i++) {
            Term temp = list.get(i);
            if (temp.getNatureStr().equals(pos)) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * 查找最近的术语
     * @param list
     * @param offSet
     * @param subPos
     * @param objPos
     * @return
     */
    public Term[] findNearestTerms(List<Term> list, int offSet, String subPos, String objPos) {
        Term[] res = null;
        for (int i = offSet - 1; i >= 0; i--) {
            if (list.get(i).getNatureStr().equals(subPos)) {
                for (int j = offSet + 1; j < list.size(); j++) {
                    if (list.get(j).getNatureStr().equals(objPos)) {
                        res = new Term[2];
                        res[0] = list.get(i);
                        res[1] = list.get(j);
                        return res;
                    }
                }
            }
        }

        return res;
    }

    /**
     * judge if the term is seed term
     *
     * @param term
     * @return
     */
    private boolean isSeedTerm(Pattern term) {
        boolean isExit = false;
        String termSubName = term.getSubject().getTerm().getName();
        String termObjName = term.getObject().getTerm().getName();
        String termSubNature = term.getSubject().getTerm().getNatureStr();
        String termObjNature = term.getObject().getTerm().getNatureStr();

        for (Pattern p : seedPattern) {
            String subName = p.getSubject().getTerm().getName();
            String objName = p.getObject().getTerm().getName();
            String subNature = p.getSubject().getTerm().getNatureStr();
            String objNature = p.getObject().getTerm().getNatureStr();
            if ((subName.equals(termSubName))
                    && (objName.equals(termObjName))
                    && (subNature.equals(termSubNature))
                    && (objNature.equals(termObjNature))) {
                isExit = true;
                break;
            }
        }
        return isExit;
    }

    /**
     * judge if the pattern is seed pattern
     *
     * @param pattern
     * @return
     */
    private boolean isSeedPattern(Pattern pattern) {
        boolean isExit = false;
        String termName = pattern.getPredicate().getTerm().getName();
        String termNature = pattern.getPredicate().getTerm().getNatureStr();

        for (Pattern p : seedPattern) {
            Term t = p.getPredicate().getTerm();
            if ((termName.equals(t.getName()) && (t.getNatureStr().equals(termNature)))) {
                isExit = true;
                break;
            }
        }
        return isExit;
    }

    /**
     * get next text from DB
     * @return
     */
    private String getNextText() {


        DBHelper db = new DBHelper();
        String sql;
        Connection conn = null;
        String ret = null;

        try {
            //先试用小数据量
            if (curId > 60000) {
                return null;
            }
            //till this line

            if ((current >= onceAmount) || (0 == textList.size())) {
//                System.out.println("------------------------------------------------------");
//                System.out.println("----------------start read from DBs-----------------");
//                System.out.println("------------------------------------------------------");
                System.gc();
                while (null == conn) {
                    conn = db.getConn();
                }
                textList.clear();
                sql = "select * from text where id>=" + curId + " limit " + onceAmount;
                System.out.println("------------------------current id------------------------");
                System.out.println("id : " + curId);
                writeId();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    String temp = this.blob2Str(rs.getBlob("text"));
                    textList.add(temp);
                }
                rs.last();
                curId = rs.getInt("id");
                current = 0;

                rs.close();
                pstmt.close();
                db.close(conn);
//                System.out.println("total count : " + curId);
//                System.out.println("text size : " + textList.size());
//                System.out.println("------------------------------------------------------");
//                System.out.println("----------------end read from DBs-----------------");
//                System.out.println("------------------------------------------------------");
            }
            if ((current < onceAmount) && (textList.size() > current)) {
                ret = textList.get(current);
            } else if ((textList.size() <= current) && (current < onceAmount)) {
                return null;
            } else {
                System.out.println("------------------------------------------------------");
                System.out.println("----------------something wrong occurs-----------------");
                System.out.println("------------------------------------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        current++;
        return ret;
    }

    /**
     * transform blob type in mysql to String object in java
     * @param blob
     * @return
     */
    private String blob2Str(Blob blob) {
        String str = null;
        if (null != blob) {
            try {
                str = new String(blob.getBytes(1, (int) blob.length()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    /**
     * init id from txt file
     * @return
     */
    public static int initId() {
        File file = new File(path);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            String s = null;
            while (null != (s = br.readLine())) {
                curId = Integer.parseInt(s);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return curId;
    }

    /**
     * write id to txt file
     */
    public static void writeId() {
        File file = new File(path);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            bw.write(curId + "");
            System.out.println("curId : " + curId);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

