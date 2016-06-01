package textCleaner;

import ie.Pattern;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by lfc on 2016/5/22.
 */
public class Test {
    private static int id = 1;

    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        Test.id = id;
    }

    public void test() throws Exception {
        id += 100;
        throw new Exception("debug");
    }

    public void backup() {

//        Iterator iterPattern = candidatePattrn.entrySet().iterator();
//        Iterator iterTerm;
//        Iterator iterExtrcSeed;
//
//        Map.Entry entryPattern;
//        Pattern tempPattern;
//        double scorePattern;
//
//        Map.Entry entrySeed;
//        Pattern extrcSeedPattern;
//
//        Map.Entry entryTerm;
//        Pattern tempTerm;
//        double scoreTerm;
//
//        //init get next text
//        curId = 1;
//        current = 201;
//
//        //score candidate pattern
//        while (iterPattern.hasNext()) {
//
//
//            entryPattern = (Map.Entry) iterPattern.next();
//            tempPattern = (Pattern) entryPattern.getKey();
//            scorePattern = (Double) entryPattern.getValue();
//            int f = 0;
//            int n = 0;
//
//            iterExtrcSeed = extractSeed.entrySet().iterator();
//            while (iterExtrcSeed.hasNext()) {
//                entrySeed = (Map.Entry) iterExtrcSeed.next();
//                extrcSeedPattern = (Pattern) entrySeed.getKey();
//
//                if (extrcSeedPattern.equals(tempPattern)) {
//                    f = f + (Integer) entrySeed.getValue();
//                    iterExtrcSeed.remove();
//                }
//            }
//
//            iterTerm = candidateTerms.entrySet().iterator();
//            while (iterTerm.hasNext()) {
//                entryTerm = (Map.Entry) iterTerm.next();
//                tempTerm = (Pattern) entryTerm.getKey();
//
//                if (tempTerm.getPredicate().equals(tempPattern)) {
//                    n = n + 1;
//                }
//            }
//            n = n + f;
//
//            if (0 != n) {
//                scorePattern = (f / n) * (Math.log(f + 1) / Math.log(2));
//                candidatePattrn.put(tempPattern, scorePattern);
//            }
//            scoreLogger.debug("pattern : " + tempPattern.getSubject().getTerm().getName()
//                    + tempPattern.getPredicate().getTerm().getName()
//                    + tempPattern.getObject().getTerm().getName()
//                    + " score : " + scorePattern);
//        }
//
//        //score candidate terms
//        iterTerm = candidateTerms.entrySet().
//
//                iterator();
//
//        while (iterTerm.hasNext())
//
//        {
//            entryTerm = (Map.Entry) iterTerm.next();
//            tempTerm = (Pattern) entryTerm.getKey();
//            scoreTerm = (Double) entryTerm.getValue();
//
//            iterPattern = candidatePattrn.entrySet().iterator();
//            while (iterPattern.hasNext()) {
//                entryPattern = (Map.Entry) iterPattern.next();
//                tempPattern = (Pattern) entryPattern.getKey();
//                if ((tempPattern.getSubject().equals(tempTerm.getSubject()))
//                        && (tempPattern.getObject().equals(tempTerm.getObject()))) {
//                    scoreTerm = scoreTerm + 1 + (Double) entryPattern.getValue();
//                }
//            }
//            candidateTerms.put(tempTerm, scoreTerm);
//            scoreLogger.debug("term : " + tempTerm.getSubject().getTerm().getName()
//                    + tempTerm.getPredicate().getTerm().getName()
//                    + tempTerm.getObject().getTerm().getName()
//                    + " score : " + scoreTerm);
//        }
//
    }
}
