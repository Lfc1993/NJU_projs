package ie;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.nlpcn.commons.lang.jianfan.JianFan;
import util.DBHelper;

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/** main method for pattern extracting
 * Created by lfc on 2016/5/12.
 */
public class Main {


    public static void main(String[] args) {
//        Item i1 = new Item("n", "qing");
//        Item i2 = new Item("n", "tang");
//        set.add(t1);
//        System.out.println(set.size());
//        set.add(t2);
//        System.out.println(set.size())

//        jianFan();//第一次使用需要先将繁体字转为简体字

        ArrayList<String> seedText = new ArrayList<String>();
        seedText.add("秦朝定都于咸阳");
        seedText.add("西周定都于镐京");
        seedText.add("西汉定都于长安");
        seedText.add("明朝定都于北京");
        seedText.add("明朝首都北京");
        seedText.add("明朝都城北京");
        seedText.add("西周都城镐京");
        seedText.add("秦朝首都咸阳");
        seedText.add("东汉首都洛阳");
        HashSet<Pattern> seedPattern = new HashSet<Pattern>();

        for (String s : seedText) {
            List<Term> parse = NlpAnalysis.parse(s);
            if (parse.size() == 3) {
                MyTerm sub = new MyTerm(parse.get(0));
                MyTerm pre = new MyTerm(parse.get(1));
                MyTerm obj = new MyTerm(parse.get(2));
                Pattern pattern = new Pattern(sub, pre, obj);
                seedPattern.add(pattern);
            }

        }

        int iterNum = 20;
        double patternThreshold = 0.05;
        double termThreshold = 2.0;

        Bootstrapping b = new Bootstrapping(seedPattern, iterNum, patternThreshold, termThreshold);
        try {
            b.bootstrapping();
        } catch (Exception e) {
            e.printStackTrace();
            Bootstrapping.writeId();
        }
//        test();
//        testSelect();
    }

    /**
     * change comflex form to simplified form, all to DB
     */
    public static void jianFan() {
        int count = 0;
        DBHelper db = new DBHelper();
        Connection conn;
        PreparedStatement selectPs;
        PreparedStatement updatePs;
        long totalStart = System.currentTimeMillis();

        try {
            conn = db.getConn();
            int onceAmount = 200;
            int id = 0;
            ResultSet rs;
            HashMap<Integer, String> simpText = new HashMap<Integer, String>(onceAmount);
            String selectSql;
            String updateSql;
            String fan;
            String jian;
            Iterator iterator;
            long selectStart;
            long selecttEnd;
            long updateStart;
            long updateEnd;
            Blob blob;

            Map.Entry map;
            int key;
            String value;

            boolean isNotEnd;

            do {

                selectSql = "select * from text where id>" + id + " limit " + onceAmount;
                updateSql = "update text set text=? where id=?";

                selectPs = conn.prepareStatement(selectSql);
                rs = selectPs.executeQuery();
                selectStart = System.currentTimeMillis();
                while (rs.next()) {
                    id = rs.getInt("id");
                    blob = rs.getBlob("text");
                    fan = new String(blob.getBytes(1, (int) blob.length()));

                    jian = JianFan.f2j(fan);

                    if (!jian.equals(fan)) {
                        count++;
                        System.out.println("------------------------------simplify---------------------------");
                        System.out.println("-------------------------------ID : " + id + "----------------------");
                        simpText.put(id, jian);
                    }
                }
                selecttEnd = System.currentTimeMillis();
                System.out.println("---------------------------------------------------------");
                System.out.println("select time : " + (selecttEnd - selectStart));

                isNotEnd = (rs.last()) && (rs.getRow() == onceAmount);
                rs.close();
                selectPs.close();

                //update
                iterator = simpText.entrySet().iterator();
                updatePs = conn.prepareStatement(updateSql);
                updateStart = System.currentTimeMillis();
                while (iterator.hasNext()) {
                    map = (Map.Entry) iterator.next();
                    key = (Integer) map.getKey();
                    value = (String) map.getValue();

                    updatePs.setBinaryStream(1, new ByteArrayInputStream(value.getBytes()));
                    updatePs.setInt(2, key);
                    updatePs.addBatch();
                }
                updatePs.executeBatch();
                updatePs.close();
                updateEnd = System.currentTimeMillis();

                System.out.println("---------------------------------------------------------");
                System.out.println("update count : " + simpText.size());
                System.out.println("update time : " + (updateEnd - updateStart));
                simpText.clear();

//            System.out.println("first : " + (l2 - l1) + "   id : " + id);
                System.gc();

            } while (isNotEnd);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long totalEnd = System.currentTimeMillis();

        System.out.println("----------------------end----------------------------");
        System.out.println("total time : " + (totalEnd - totalStart));
        System.out.println("total amount : " + count);
    }

    public static void testSelect() {
        DBHelper db = new DBHelper();
        try {
            Connection conn = db.getConn();
            int end = 200;
            int start = 0;
            int id = 0;
            String sql = "select * from text where id>" + id + " order by id asc limit " + start + "," + end;

            long l1 = System.currentTimeMillis();
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            long l2 = System.currentTimeMillis();
            rs.last();
            id = rs.getInt("id");
            System.out.println("first : " + (l2 - l1) + "   id : " + id);
            rs.close();
            preparedStatement.close();

            start = 80000;
            sql = "select * from text limit " + start + "," + end;
            l1 = System.currentTimeMillis();
            PreparedStatement preparedStatement2 = conn.prepareStatement(sql);
            ResultSet rs2 = preparedStatement2.executeQuery();
            l2 = System.currentTimeMillis();
            rs2.last();
            id = rs2.getInt("id");
            System.out.println("first : " + (l2 - l1) + "   id : " + id);
            rs2.close();
            preparedStatement2.close();

            start = 500000;
            sql = "select * from text limit " + start + "," + end;
            l1 = System.currentTimeMillis();
            PreparedStatement preparedStatement3 = conn.prepareStatement(sql);
            ResultSet rs3 = preparedStatement3.executeQuery();
            l2 = System.currentTimeMillis();
            rs3.last();
            id = rs3.getInt("id");
            System.out.println("first : " + (l2 - l1) + "   id : " + id);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
