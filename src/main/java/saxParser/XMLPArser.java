package saxParser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import util.DBHelper;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * XML parser extract text from wiki xml to store in mysql DB
 */
public class XMLPArser extends DefaultHandler {
    private List<Text> textList;
    private Text text = null;
    private String preTag = null;//作用是记录解析时的上一个节点名称
    private int count;
    private int time;
    private long startTime;
    private long endTime;
    private int length = 99;
    private boolean isId = false;
    private StringBuilder textStr = null;

    public void getText(InputStream xmlStream) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        XMLPArser handler = new XMLPArser();
        parser.parse(xmlStream, handler);
    }

    @Override
    public void startDocument() throws SAXException {
        textList = new ArrayList<Text>(100);
        count = 0;
        System.out.println("------------------开始解析文档--------------------");
        startTime = System.nanoTime();

    }

    @Override
    public void endDocument() throws SAXException {
        if (!textList.isEmpty()) {
        }
        textList = null;
        endTime = System.nanoTime();
        textList = null;
        count = 0;
        time = 0;
        System.out.println("-----------------解析文档结束---------------------");
        System.out.println("共用" + (endTime - startTime) + "纳秒");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("page".equals(qName)) {
            text = new Text();
        } else if ("text".equals(qName)) {
            textStr = new StringBuilder();
        }
        preTag = qName;//将正在解析的节点名称赋给preTag
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if ("page".equals(qName)) {
            count++;
            if ((count % length) != 0) {
                if (null != text.getTitle()) {
                    textList.add(text);
                } else {
                    count--;
                }
            } else {
                BatchInsertThread bt = new BatchInsertThread(textList);
                bt.start();
                count = 0;
                time++;
                textList = new ArrayList<Text>(100);
            }
            System.out.println("------------" + text.getTitle() + " : " + text.getId() + "----------------");

            text = null;
            isId = false;
//            while (true) ;
        } else if ("text".equals(qName)) {
            text.setText(textStr.toString());
            textStr = null;
        }
        preTag = null;/**当解析结束时置为空。这里很重要，例如，当图中画3的位置结束后，会调用这个方法
         ，如果这里不把preTag置为null，根据startElement(....)方法，preTag的值还是book，当文档顺序读到图
         中标记4的位置时，会执行characters(char[] ch, int start, int length)这个方法，而characters(....)方
         法判断preTag!=null，会执行if判断的代码，这样就会把空值赋值给book，这不是我们想要的。*/
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        if (preTag != null) {
            String content = new String(ch, start, length);
            if (null != text) {
                if ("redirect".equals(preTag)) {
                    text = null;
                } else if ("title".equals(preTag)) {
                    text.setTitle(content);
                } else if ("id".equals(preTag) && (!isId)) {
                    text.setId(Integer.parseInt(content));
                    isId = true;
                } else if ("text".equals(preTag)) {
                    textStr.append(content);
                }
            }
        }
    }


    public class BatchInsertThread extends Thread {
        private List<Text> list;
        private String sql;

        public BatchInsertThread(List<Text> textList) {
            list = textList;
        }

        public void run() {
            try {
                sql = "insert into text values(?,?,?)";
                Connection conn = null;
                while (null == conn) {
                    conn = DBHelper.getConn();
                    conn.setAutoCommit(false);
                }
                PreparedStatement pst = conn.prepareStatement(sql);
                for (Text text : list) {

                    if (null != text) {
                        pst.setInt(1, text.getId());
                        pst.setString(2, text.getTitle());
                        pst.setBinaryStream(3, new ByteArrayInputStream(text.getText().getBytes()));
//                        pst.addBatch();
                        try {
                            pst.executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
//                pst.executeBatch();
                conn.commit();
                pst.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}

//public class InsertThread extends Thread {
//    private Text text;
//    private String sql;
//
//    public InsertThread(Text t) {
//        text = t;
//    }
//
//    public void run() {
//        sql = "insert into text values(?,?)";
//        if (null != text) {
//            try {
//                Connection conn = DBHelper.getConn();
//                PreparedStatement pst = conn.prepareStatement(sql);
//                pst.setString(1, text.getTitle());
//                pst.setBinaryStream(2, new ByteArrayInputStream(text.getText().getBytes()));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
//}
//}
