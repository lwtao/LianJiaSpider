package main.metadata.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import main.metadata.metadata.LianJiaHouse;
import main.metadata.metadata.LianJiaParams;
import main.monitor.URLPool;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.DateUtils;
import util.MyConstants;
import util.net.NetUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LianJiaDocParser implements BaseParser {

    public List<Document> getDocsViaURLS(List<String> URLS) throws Exception {
        List<Document> docs = new ArrayList<Document>();
        for (String URL : URLS) {
            Document doc = Jsoup.parse(NetUtils.httpGet(URL));
            docs.add(doc);
        }
        return docs;
    }

    @Override
    public boolean canDuplicate() {
        return true;
    }

    public List<LianJiaHouse> getHouseList(Document doc) {
        List<LianJiaHouse> list = new ArrayList<LianJiaHouse>();
        Elements lis = doc.select("ul.sellListContent> li");
        for (Element li : lis) {
            try {
                //LianJiaHouse house = new LianJiaHouse();
                // String houseId = li.attr("data-id");
                // if(houseId == null || houseId.isEmpty())
                // continue;
                // String houseTitle = li.select("div.info-panel > h2 > a").attr("title");
                String houseURL = li.select("a.img").get(0).absUrl("href");
                /*
                 * String regionURL = LianJiaParams.BaseURL +
                 * li.select("div.info-panel > div.col-1 > div.where > a").attr("href"); String houseLocation =
                 * li.select("div.info-panel > div.col-1 > div.where > a > span").text(); String houseRoom =
                 * li.select("div.info-panel > div.col-1 > div.where > span.zone > span").text(); String houseArea =
                 * li.select("div.info-panel > div.col-1 > div.where > span.meters").text(); String spanText =
                 * li.select("div.info-panel > div.col-1 > div.where > span").text(); String[] spanTexts =
                 * spanText.split(" "); String houseDirection = spanTexts[spanTexts.length -1]; String housePrice =
                 * li.select("div.info-panel > div.col-3 > div.price > span.num").text(); String pricePerSquare =
                 * li.select("div.info-panel > div.col-3 > div.price-pre").text(); Elements img =
                 * li.select("div.info-panel > div.col-3 > div.price > img");
                 * 
                 * String[] otherInfos =
                 * li.select("div.info-panel > div.col-1 > div.other > div.con").text().split("/"); String houseType =
                 * otherInfos[0]; String houseHeight = otherInfos[1]; String[] houseOtherStrs =
                 * otherInfos[2].split("年建"); String houseBuildYear = houseOtherStrs[0]; String houseBuildType =
                 * houseOtherStrs[1];
                 * 
                 * boolean isDown = img.size() >0;
                 * 
                 * house.setHouseId(houseId); house.setHouseTitle(houseTitle); house.setHouseLocation(houseLocation);
                 * house.setHouseRoom(houseRoom); house.setHouseArea(houseArea);
                 * house.setHouseDirection(houseDirection); house.setHousePrice(housePrice);
                 * house.setPricePerSquare(pricePerSquare); house.setDown(isDown); house.setHouseURL(houseURL);
                 * house.setRegionURL(regionURL); house.setHouseType(houseType); house.setHouseHeight(houseHeight);
                 * house.setHouseBuildType(houseBuildType); house.setHouseBuildYear(houseBuildYear);
                 */

                list.add(getDetail(houseURL,li));
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }

        detectNextPage(doc);

        return list;
    }

    public void detectNextPage(Document doc) {
        Elements pageLinks = doc.select("div.page-box>div.house-lst-page-box");
        String pageDataStr = pageLinks.attr("page-data");
        String[] pageDatas = pageDataStr.split(",");
        if (pageDatas.length == 2) {
            String totalPage = pageDatas[0].split(":")[1];
            String hrefTemplet = pageLinks.get(0).absUrl("page-url");
            String currentPage = pageDatas[1].split(":")[1].substring(0, pageDatas[1].split(":")[1].length() - 1);
            if (Integer.parseInt(currentPage) <= 100 && !currentPage.equals(totalPage)) {
                String nextPage = String.valueOf((Integer.valueOf(currentPage) + 1));
                String nextURL = hrefTemplet.replace("{page}", nextPage);
                if (StringUtils.isBlank(nextURL)){
                    System.out.println("---------------currentPage:"+currentPage);
                }
                // System.out.println("URL ADD:"+nextURL);
                URLPool.getInstance().pushURL(nextURL);
            }
        }

    }

    public LianJiaHouse getDetail(String detailUrl,Element li) {
        try {
            TimeUnit.MILLISECONDS.sleep(100L);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*Document detailDocument = null;
        try {
            detailDocument = Jsoup.parse(NetUtils.httpGet(detailUrl));
            if (detailDocument.select(".price>.total").size() == 0) {
                detailDocument = Jsoup.parse(NetUtils.httpGet(detailUrl));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        String houseId = StringUtils.substringBefore(StringUtils.substringAfterLast(detailUrl, "/"), ".");
        String houseTitle = detailDocument.select(".title>h1.main").text();
        String houseLocation = detailDocument.select(".areaName>span.info").text();
        String houseRoom = detailDocument.select(".houseInfo>.room>.mainInfo").text();
        Elements transactions = detailDocument.select(".introContent>.transaction>.content>ul>li");
        String listingData = "";
        for (Element transaction : transactions) {
            if (StringUtils.contains(transaction.text(), "挂牌时间")) {
                listingData = StringUtils.substringAfter(transaction.text(), "挂牌时间");
                break;
            }
        }
        String houseHeight = detailDocument.select(".houseInfo>.room>.subInfo").text();
        String price = detailDocument.select(".price>.total").get(0).text();
        String pricePerSquare = detailDocument.select(".unitPriceValue").get(0).text();
        String houseType = detailDocument.select(".type>.subInfo").get(0).text();// 精装
        String direction = detailDocument.select(".type>.mainInfo").get(0).text();// 南北
        Element communityInfo = detailDocument.select(".aroundInfo>.communityName>a.info").get(0);
        String communityName = communityInfo.text();
        String communityId = StringUtils.substringAfterLast(StringUtils.removeEnd(communityInfo.attr("href"), "/"), "/");
        String areaInfo = detailDocument.select(".houseInfo>.area>.mainInfo").get(0).text();
        areaInfo = StringUtils.substringBefore(areaInfo, "平米");// 面积
        String year = detailDocument.select(".houseInfo>.area>.subInfo").get(0).text();
        year = StringUtils.substringBefore(year, "年");
        // System.out.println("-------"+ price);
        System.out.println("-------" + communityName + "," + areaInfo + "," + price + "," + year);
        Elements elements = detailDocument.select(".transaction .content ul>li");
        String houseCode = "";//房源编码
        if (elements!=null){
            for (Element element : elements) {
                if (element.text().contains("房源编码")){
                    houseCode = element.text().replace("房源编码","");
                    break;
                }
            }
        }
        LianJiaHouse house = new LianJiaHouse();
        house.setHouseId(houseId);
        house.setHouseTitle(houseTitle);
        house.setHouseLocation(communityName);
        house.setCommunityId(communityId);
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
        //setSeeRecord(house);
        house.setListingDate(listingData);
        house.setCity(MyConstants.CURRENT_CITY);
        house.setCreateDate(DateUtils.dateToString(new Date(), DateUtils.yyyyMMdd));*/
        LianJiaHouse house = new LianJiaHouse();
        String houseId = StringUtils.substringBefore(StringUtils.substringAfterLast(detailUrl, "/"), ".");
        house.setHouseId(houseId);
        String houseTitle = li.select(".title>a").text();
        house.setHouseTitle(houseTitle);
        // 松泉山庄 | 2室2厅 | 71.04平米 | 南 北 | 精装 | 无电梯
        String houseInfo = li.select(".houseInfo").text();
        String[] houseInfos = houseInfo.split("\\|");
        String communityName = houseInfos[0];
        house.setHouseLocation(StringUtils.trim(communityName));
        String houseRoom = houseInfos[1];
        house.setHouseRoom(StringUtils.trim(houseRoom));
        String areaInfo = houseInfos[2];
        house.setHouseArea(StringUtils.trim(areaInfo));
        String direction = houseInfos[3];
        house.setHouseDirection(StringUtils.trim(direction));
        String houseType = houseInfos[4];
        house.setHouseType(StringUtils.trim(houseType));
        String price = li.select(".totalPrice>span").text();
        house.setHousePrice(price);
        String pricePerSquare = li.select(".unitPrice>span").text().replace("单价","").replace("元/平米","");
        house.setPricePerSquare(pricePerSquare);
        house.setHouseURL(detailUrl);
        String houseCode = "";
        house.setRegionURL(houseCode);

        String positionInfo = li.select(".positionInfo").text();
        String houseHeight = positionInfo.substring(0,3);
        house.setHouseHeight(houseHeight);
        String houseLocation = li.select(".positionInfo>a").text();
        house.setHouseBuildType(houseLocation);
        int yearIndex = positionInfo.lastIndexOf("年");
        house.setHouseBuildYear(positionInfo.substring(yearIndex-4,yearIndex));
        //setSeeRecord(house);
        String listingData = "";
        house.setListingDate(listingData);
        house.setCity(MyConstants.CURRENT_CITY);
        house.setCreateDate(DateUtils.dateToString(new Date(), DateUtils.yyyyMMdd));
        return house;
    }

    public void setSeeRecord(LianJiaHouse lianJiaHouse) {
        try {
            String json = NetUtils.httpGet(LianJiaParams.seeRecordUrl.replace("communityId",
                                                                              lianJiaHouse.getCommunityId())
                                                                     .replace("houseId", lianJiaHouse.getHouseId()));
            JSONObject seeRecord = JSON.parseObject(json).getJSONObject("data").getJSONObject("seeRecord");
            int thisWeek = seeRecord.getIntValue("thisWeek");
            int totalCnt = seeRecord.getIntValue("totalCnt");
            lianJiaHouse.setSeeTimesLastWeek(thisWeek);
            lianJiaHouse.setSeeTimes(totalCnt);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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
