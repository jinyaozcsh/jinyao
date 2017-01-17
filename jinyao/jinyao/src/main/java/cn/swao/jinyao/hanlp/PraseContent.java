package cn.swao.jinyao.hanlp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.regex.*;

import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.CRF.CRFSegment;
import com.hankcs.hanlp.seg.NShort.NShortSegment;
import com.hankcs.hanlp.seg.common.Term;

import cn.swao.jinyao.cash.WebCash;

public class PraseContent {

    /**
     * 最短路分词
     * 
     * @param content
     * @return
     */
    public List<Term> getNShortSegment(String content) {
        Segment nShortSegment = new NShortSegment().enableCustomDictionary(false).enablePlaceRecognize(true).enableOrganizationRecognize(true);
        List<Term> seg = nShortSegment.seg(content);
        return seg;
    }

    /**
     * CRF分词
     * 
     * @param content
     * @return
     */
    public List<Term> getCRFSegment(String content) {
        Segment segment = new CRFSegment();
        segment.enablePartOfSpeechTagging(true);
        List<Term> seg = segment.seg(content);
        return seg;
    }

    public String paraseContent(String content) throws MalformedURLException, IOException {
        List<Term> nShortSegment = getNShortSegment(content);
        List<String> workList = new ArrayList<String>();
        for (Term term : nShortSegment) {
            if (term.nature != null) {
                String word = term.word;
                workList.add(word);
            }
        }
        Pattern p = Pattern.compile("\\d{1,}");

        for (String work : workList) {
            if (work.equals("公交")) {
                workList.remove(work);
                for (String number : workList) {
                    Matcher m = p.matcher(number);
                    if (m.find()) {
                        String busInfo = WebCash.getBusInfo(number);
                        return busInfo;
                    }

                }
            }
        }
        return null;

    }

    public static void main(String[] args) throws MalformedURLException, IOException {
        String content = "92路公交车";
        PraseContent praseContent = new PraseContent();
       /* List<Term> crfSegment = praseContent.getCRFSegment(content);
        System.out.println(crfSegment);*/
        String paraseContent = praseContent.paraseContent(content);
        System.out.println(paraseContent);
    }

}
