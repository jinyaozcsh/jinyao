package cn.swao.jinyao.hanlp;

import java.util.*;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.dictionary.CoreDictionary.Attribute;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.CRF.CRFSegment;
import com.hankcs.hanlp.seg.NShort.NShortSegment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.*;

public class HanlpSearchService {
    /*
     * 外高桥2路什么时候来 松梅专线什么时候来 松青线 松莘线B线 松青线还有多长时间 松青线还有多长
     */
    public String getStr() {
        return "（原标题：中国楼市开年遇冷 首周各线城市成交回落）  \n 中新社北京1月10日电 在官方严控房地产市场的影响下，2017年首周，中国多地楼市成交量明显回落。\n 中国指数研究院10日发布的数据显示，新年首周，该机构监测的中国32个主要城市整体成交量环比下降17%，一二三线代表城市成交同环比全线回落。其中，一线城市环比下降1%，同比下滑10.6%；二线城市环比回落22.2%，同比降4.2%；三线城市环比下降13.2%，同比降17.2%。\n 32个重点城市中，重庆、武汉和上海成交面积位居前三。从同比数据来看，除北京、重庆和成都同比上升外，其它城市均呈下降态势，苏州同比降幅较为明显，达78.35%，其次是杭州，同比降低近七成。\n \n   \n  \n   \n    \n     \n   \n \n 尽管北京同环比均有所上升，但机构统计数据显示，实际上剔除自住型商品住房之后，北京商品住宅成交面积环比下滑两成，并创下近两个月新低。\n 亚豪机构副总经理任启鑫表示，自去年“9·30”新政开始，房地产调控开始进入收紧周期，各地也都在这一新政基础上“查漏补缺”。由此可以看出，2017年政策风向仍将以收紧为主。\n 例如，北京新增“现房一房一价”，上海将商办类产品纳入监管，海南披露1352家未备案的房地产中介将被暂停网上签约，重庆严控商品房预售等。\n 易居房地产市场研究院也预测称，2017年30个代表性城市的住宅成交量将出现环比下跌10%的幅度。 \n  \n  \n   本文来源：中国新闻网 作者：庞无忌 \n  \n  责任编辑：胡淑丽_MN7479";
    }

    // 标准分词
    public void test1(String content) {
        List<Term> termList = StandardTokenizer.segment(content);
        System.out.println(termList);
    }

    // NLP分词NLPTokenizer会执行全部命名实体识别和词性标注。
    public void test2(String content) {
        List<Term> termList = NLPTokenizer.segment(content);
        System.out.println(termList);
    }

    // 索引分词IndexTokenizer是面向搜索引擎的分词器，能够对长词全切分，另外通过term.offset可以获取单词在文本中的偏移量。
    public void test3(String content) {
        List<Term> termList = IndexTokenizer.segment(content);
        for (Term term : termList) {
            System.out.println(term + " [" + term.offset + ":" + (term.offset + term.word.length()) + "]");
        }
    }

    // N-最短路径分词，N最短路分词器NShortSegment比最短路分词器慢，但是效果稍微好一些，对命名实体识别能力更强。
    public void test4(String content) {
        Segment nShortSegment = new NShortSegment().enableCustomDictionary(true).enablePlaceRecognize(true).enableOrganizationRecognize(true).enablePartOfSpeechTagging(true);
        // Segment shortestSegment = new DijkstraSegment().enableCustomDictionary(false).enablePlaceRecognize(true).enableOrganizationRecognize(true);
        System.out.println("N-最短分词：" + nShortSegment.seg(content));
        // System.out.println("\n最短路分词：" + shortestSegment.seg(content));

    }

    // CRF分词
    public void test5(String content) {
        Segment segment = new CRFSegment();
        segment.enableCustomDictionary(true).enablePlaceRecognize(true).enableOrganizationRecognize(true).enablePartOfSpeechTagging(true);
        List<Term> termList = segment.seg(content);
        System.out.println("CRF分词" + termList);
        /*
         * for (Term term : termList) { if (term.nature == null) { System.out.println("识别到新词：" + term.word); } }
         */
    }

    // 极速词典分词
    public void test6(String content) {
        String text = "江西鄱阳湖干枯，中国最大淡水湖变成大草原";
        System.out.println(SpeedTokenizer.segment(content));
        long start = System.currentTimeMillis();
        int pressure = 1000000;
        for (int i = 0; i < pressure; ++i) {
            SpeedTokenizer.segment(text);
        }
        double costTime = (System.currentTimeMillis() - start) / (double) 1000;
        System.out.printf("分词速度：%.2f字每秒", text.length() * pressure / costTime);
    }

    // 提取关键字
    public void test7(String content) {
        List<String> keywordList = HanLP.extractKeyword(content, 10);
        System.out.println(keywordList);
    }

    // 地名分词
    public void test8() {
        String[] testCase = new String[] { "武胜县新学乡政府大楼门前锣鼓喧天", "蓝翔给宁夏固原市彭阳县红河镇黑牛沟村捐赠了挖掘机", };
        Segment segment = HanLP.newSegment().enablePlaceRecognize(true);
        for (String sentence : testCase) {
            List<Term> termList = segment.seg(sentence);
            System.out.println(termList);
        }
    }

    public void test9(String content) {
        Segment segment = HanLP.newSegment().enablePlaceRecognize(true);
        List<Term> termList = segment.seg(content);
        System.out.println(termList);
    }

    public void test10(String content) {
        Segment segment = HanLP.newSegment().enableOrganizationRecognize(true);
        List<Term> termList = segment.seg(content);
        System.out.println(termList);
    }

    public static void main(String[] args) {
        HanlpSearchService hanlp = new HanlpSearchService();
        /*
         * hanlp.test1(hanlp.getStr()); hanlp.test2(hanlp.getStr()); hanlp.test3(hanlp.getStr()); hanlp.test6(hanlp.getStr());
         */
        // hanlp.test7(hanlp.getStr());
        // hanlp.test4(hanlp.getStr());
        // hanlp.test5(hanlp.getStr());
        // hanlp.test4(hanlp.getStr());
       // hanlp.test4(hanlp.getStr());
        //hanlp.test5(hanlp.getStr());
        hanlp.test7(hanlp.getStr());
        //nAndcrf(hanlp.getStr());
        // Attribute attribute = CustomDictionary.get("社区黄页");
         System.out.println(nAndcrf(hanlp.getStr()));
        // hanlp.test8();
    }

    // N-最短路径分词，N最短路分词器NShortSegment比最短路分词器慢，但是效果稍微好一些，对命名实体识别能力更强。
    public  static List<String> Nsort(String content) {
        Segment nShortSegment = new NShortSegment().enableCustomDictionary(true).enablePlaceRecognize(true).enableOrganizationRecognize(true).enablePartOfSpeechTagging(true);
        // Segment shortestSegment = new DijkstraSegment().enableCustomDictionary(false).enablePlaceRecognize(true).enableOrganizationRecognize(true);
        // System.out.println("\n最短路分词：" + shortestSegment.seg(content));
        List<String> word = new ArrayList<String>();
        for (Term term : nShortSegment.seg(content)) {
            word.add(term.word);
        }
        return word;
    }

    // CRF分词
    public static List<String> CRF(String content) {
        Segment segment = new CRFSegment();
        segment.enableCustomDictionary(true).enablePlaceRecognize(true).enableOrganizationRecognize(true).enablePartOfSpeechTagging(true);
        List<String> word = new ArrayList<String>();

        for (Term term : segment.seg(content)) {
            word.add(term.word);
        }
        return word;
    }

    public static List<String> nAndcrf(String content) {
        List<String> nsort = Nsort(content);
        List<String> crf = CRF(content);

        List<Boolean> nb = new ArrayList<Boolean>();
        List<Boolean> cb = new ArrayList<Boolean>();

        for (String n : nsort) {
            nb.add(crf.contains(n));
        }
        for (String c : crf) {
            cb.add(nsort.contains(c));
        }
        Map<String, String> nmap = new HashMap<String, String>();
        for (int i = 0; i < nsort.size(); i++) {
            if (!nb.get(i) && i + 1 < nsort.size() && !nb.get(i + 1)) {
                for (int j = 0; j < cb.size(); j++) {
                    if (!cb.get(j)) {
                        String str = nsort.get(i) + nsort.get(i + 1);
                        String key = i + "," + (i + 1);
                        if (str.equals(crf.get(j))) {
                            nmap.put(key, str);
                        } else {
                            int z = i + 1;
                            while (true) {
                                z++;
                                if (z >= nsort.size() || nb.get(z)) {
                                    break;
                                }
                                key += "," + z;
                                str += nsort.get(z);
                                if (str.equals(crf.get(j))) {
                                    nmap.put(key, str);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        for (String key : nmap.keySet()) {
            String[] split = key.split(",");
            for (int i = 0; i < split.length; i++) {
                int index = Integer.parseInt(split[i]);
                if (i == 0) {
                    nsort.set(index, nmap.get(key));
                } else {
                    nsort.set(index, null);
                }
            }

        }
        while (true) {
            if (nsort.contains(null)) {
                nsort.remove(null);
            } else {
                break;
            }
        }
        return nsort;
    }

}
