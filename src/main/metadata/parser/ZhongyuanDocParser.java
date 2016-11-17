package main.metadata.parser;

import main.metadata.metadata.LianJiaHouse;
import main.monitor.URLPool;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.MyConstants;
import util.net.NetUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ZhongyuanDocParser implements BaseParser {

    @Override
    public boolean canDuplicate() {
        return true;
    }

    public List<Document> getDocsViaURLS(List<String> URLS) throws Exception {
        List<Document> docs = new ArrayList<Document>();
        for (String URL : URLS) {
            Document doc = Jsoup.parse(NetUtils.httpGet(URL));
            docs.add(doc);
        }
        return docs;
    }

    public List<LianJiaHouse> getHouseList(Document doc) {
        List<LianJiaHouse> list = new ArrayList<LianJiaHouse>();
        Elements lis = doc.select(".house-listBox>.house-item");
        for (Element li : lis) {
            try {
                String houseURL = li.select(".house-title>.cBlueB").get(0).absUrl("href");
                list.add(getDetail(houseURL));
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }

        detectNextPage(doc);

        return list;
    }

    public void detectNextPage(Document doc) {
        Elements select = doc.select(".pagerbar>.fsm.fb");
        for (Element element : select) {
            if (">".equals(element.text())){
                URLPool.getInstance().pushURL(element.absUrl("href"));
                break;
            }
        }

    }

    public LianJiaHouse getDetail(String detailUrl) {
        try {
            TimeUnit.MILLISECONDS.sleep(100L);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        Document detailDocument = null;
        try {
            detailDocument = Jsoup.parse(NetUtils.httpGet(detailUrl));
            if (detailDocument.select(".roombase-price>.cRed").size() == 0) {
                detailDocument = Jsoup.parse(NetUtils.httpGet(detailUrl));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        String houseId = StringUtils.substringBefore(StringUtils.substringAfterLast(detailUrl, "/"), ".");
        String houseTitle = detailDocument.select(".roominfor>dd>h5.f18").text();
        String houseLocation = "";//区域
        String houseRoom = detailDocument.select(".roombase-price>.f000").get(0).text();
        String listingData = detailDocument.select(".labeltag>.f999").text();
        Elements transactions = detailDocument.select(".hbase_txt>.cell");
        String houseHeight = "";
        String direction = "";// 南北
        String houseType = "";// 精装
        String year = "";
        for (Element transaction : transactions) {
            if (StringUtils.contains(transaction.text(), "楼层")) {
                houseHeight = StringUtils.substringAfter(transaction.text(), "楼层：");
            }else if (StringUtils.contains(transaction.text(), "朝向")) {
                direction = StringUtils.substringAfter(transaction.text(), "朝向：");
            }else if (StringUtils.contains(transaction.text(), "装修")) {
                houseType = StringUtils.substringAfter(transaction.text(), "装修：");
            }else if (StringUtils.contains(transaction.text(), "小区地址")) {
                houseLocation = StringUtils.substringAfter(transaction.text(), "小区地址：");
            }else if (StringUtils.contains(transaction.text(), "年代")) {
                year = StringUtils.substringAfter(transaction.text(), "年代：");
                year = StringUtils.substringBefore(year, "年");
            }
        }
        String price = detailDocument.select(".roombase-price>.cRed").get(0).text();
        Elements select1 = detailDocument.select(".saleavgprice");
        select1.select("a").remove();
        String pricePerSquare = select1.text();

        String communityName = detailDocument.select(".txt_r.f666>.cBlue").text();
        //String communityId = StringUtils.substringAfterLast(StringUtils.removeEnd(communityInfo.attr("href"), "/"), "/");
        String areaInfo = StringUtils.substringBefore(detailDocument.select(".roombase-price>.f000").get(1).text(),"平");// 面积

        System.out.println("-------" + communityName + "," + areaInfo + "," + price + "," + year);
        Elements elements = detailDocument.select(".citeTxt>.f999");
        String houseCode = "";//房源编码
        if (elements!=null){
            for (Element element : elements) {
                if (element.text().contains("房源编码")){
                    houseCode = element.text().replace("房源编码：","").trim();
                    break;
                }
            }
        }
        String seeTimes = detailDocument.select(".second.f999>.f333").get(0).text();
        seeTimes = StringUtils.substringBefore(areaInfo, "次");
        LianJiaHouse house = new LianJiaHouse();
        house.setHouseId(houseId);
        house.setHouseTitle(houseTitle);
        house.setHouseLocation(communityName);
        //house.setCommunityId(communityId);
        house.setHouseRoom(houseRoom);
        house.setHouseArea(areaInfo);
        house.setHouseDirection(direction);
        house.setHousePrice(price);
        house.setPricePerSquare(pricePerSquare);
        // house.setDown(isDown);
        house.setHouseURL(detailUrl);
        house.setRegionURL(houseCode);
        house.setHouseType(houseType);
        house.setHouseHeight(houseHeight);
        house.setHouseBuildType(houseLocation);
        house.setHouseBuildYear(year);
        house.setSeeTimes(getInt(seeTimes));
        house.setListingDate(listingData);
        house.setCity(MyConstants.CURRENT_CITY);
        return house;
    }

    private static int getInt(String str) {
        try {
            return Integer.parseInt(str);
        }
        catch (Exception e) {
            return 0;
        }
    }
}
