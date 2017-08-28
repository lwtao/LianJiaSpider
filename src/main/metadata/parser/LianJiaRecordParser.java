/******************************************************************************
 * Copyright (C) 2013 - 2016 ShenZhen oneplus Technology Co.,Ltd
 * All Rights Reserved.
 * 本软件为深圳万普拉斯科技有限公司开发研制。未经本公司正式书面同意，其他任何个人、团体不得使用、复制、修改或发布本软件 .
 *****************************************************************************/
package main.metadata.parser;

import main.metadata.metadata.LianJiaHouse;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.net.NetUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liuwentao
 * @history 2016/11/6 liuwentao 新建
 * @since 2016/11/6 21:27
 */
public class LianJiaRecordParser extends LianJiaDocParser implements BaseParser {

    @Override
    public boolean canDuplicate() {
        return true;
    }

    @Override
    public List<LianJiaHouse> getHouseList(Document doc) {
        List<LianJiaHouse> list = new ArrayList<LianJiaHouse>();
        Elements lis = doc.select("ul.listContent>li");
        for (Element li : lis) {
            try {
                String houseURL = li.select("a.img").get(0).absUrl("href");
                list.add(getDetail(houseURL,li));
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }

        detectNextPage(doc);

        return list;
    }

    @Override
    public LianJiaHouse getDetail(String detailUrl,Element li) {
       /* try {
            TimeUnit.MILLISECONDS.sleep(RandomUtils.nextInt(3000,10000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        Document detailDocument = null;
        try {
            detailDocument = Jsoup.parse(NetUtils.httpGet(detailUrl));
            if (detailDocument.select(".house-title>.wrapper>span").size() == 0) {
                detailDocument = Jsoup.parse(NetUtils.httpGet(detailUrl));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String houseId = StringUtils.substringBefore(StringUtils.substringAfterLast(detailUrl,"/"),".");
        String houseTitle = StringUtils.substringBefore(detailDocument.select("title").text(),"_");
        String dealDate = StringUtils.substringBefore(detailDocument.select(".house-title>.wrapper>span").text(),"链家成交").trim();
        String communityName = StringUtils.substringBetween(detailDocument.toString(),"resblockName:'","'");
        String houseLocation = detailDocument.select(".myAgent .name a").text();
        String text = detailDocument.select(".myAgent .name b").text();
        String[] strings = text.split("/");
        if (strings.length<3){
            System.out.println();
        }
        System.out.println("---------"+text);
        String houseRoom = strings[1];
        String square = strings[2];//面积
        String houseHeight = detailDocument.select(".houseInfo>.room>.subInfo").text();
        String price = detailDocument.select(".dealTotalPrice").get(0).text();
        String pricePerSquare = detailDocument.select(".info.fr>.price>b").get(0).text();
        //String area = detailDocument.select(".info.fr>.msg>.sp03>label").get(0).text();
        String year = StringUtils.substringBetween(detailDocument.select(".introContent li").text(),"建成年代"," ");
        //String houseType = detailDocument.select(".type>.subInfo").get(0).text();//精装
        //String direction = detailDocument.select(".type>.mainInfo").get(0).text();//南北


        LianJiaHouse house = new LianJiaHouse();
        house.setHouseId(houseId);
        house.setHouseTitle(houseTitle);
        house.setHouseLocation(communityName);
        house.setHouseRoom(houseRoom);
        house.setHouseArea(square);
        //house.setHouseDirection(direction);
        house.setHousePrice(price);
        house.setPricePerSquare(pricePerSquare);
//		house.setDown(isDown);
        house.setHouseURL(detailUrl);
//		house.setRegionURL(regionURL);
//        house.setHouseType(houseType);
        house.setHouseHeight(houseHeight);
        house.setHouseBuildType(houseLocation);
        house.setHouseBuildYear(year);

        //house.setListingDate(dealDate);
        house.setCreateDate(dealDate.replace(".","-"));
        house.setCity("xx");

        System.out.println(house);
        return house;
    }
}
