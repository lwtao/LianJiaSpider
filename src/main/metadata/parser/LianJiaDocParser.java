package main.metadata.parser;

import main.metadata.metadata.LianJiaHouse;
import main.metadata.metadata.LianJiaParams;
import main.monitor.URLPool;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.net.NetUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LianJiaDocParser {
	
	private static String houseListSelector = "ul.listContent> li";
	
	public static List<Document> getDocsViaURLS(List<String> URLS) throws Exception{
		List<Document> docs = new ArrayList<Document>();
		for(String URL : URLS){
			Document doc = Jsoup.parse(NetUtils.httpGet(URL));
			docs.add(doc);
		}
		return docs;
	}
	
	public static List<LianJiaHouse> getHouseList(Document doc){
		List<LianJiaHouse> list = new ArrayList<LianJiaHouse>();
		Elements lis = doc.select(houseListSelector);
		for(Element li : lis){
			try{
				LianJiaHouse house = new LianJiaHouse();
//				String houseId = li.attr("data-id");
//				if(houseId == null || houseId.isEmpty())
//					continue;
//				String houseTitle = li.select("div.info-panel > h2 > a").attr("title");
				String houseURL = li.select("a.img").get(0).absUrl("href");
				/*String regionURL = LianJiaParams.BaseURL + li.select("div.info-panel > div.col-1 > div.where > a").attr("href");
				String houseLocation = li.select("div.info-panel > div.col-1 > div.where > a > span").text();
				String houseRoom = li.select("div.info-panel > div.col-1 > div.where > span.zone > span").text();
				String houseArea = li.select("div.info-panel > div.col-1 > div.where > span.meters").text();
				String spanText = li.select("div.info-panel > div.col-1 > div.where > span").text();
				String[] spanTexts = spanText.split(" ");
				String houseDirection = spanTexts[spanTexts.length -1];
				String housePrice = li.select("div.info-panel > div.col-3 > div.price > span.num").text();
				String pricePerSquare = li.select("div.info-panel > div.col-3 > div.price-pre").text();
				Elements img = li.select("div.info-panel > div.col-3 > div.price > img");
				
				String[] otherInfos = li.select("div.info-panel > div.col-1 > div.other > div.con").text().split("/");
				String houseType = otherInfos[0];
				String houseHeight = otherInfos[1];
				String[] houseOtherStrs = otherInfos[2].split("年建");
				String houseBuildYear = houseOtherStrs[0];
				String houseBuildType = houseOtherStrs[1];
				
				boolean isDown = img.size() >0;
				
				house.setHouseId(houseId);
				house.setHouseTitle(houseTitle);
				house.setHouseLocation(houseLocation);
				house.setHouseRoom(houseRoom);
				house.setHouseArea(houseArea);
				house.setHouseDirection(houseDirection);
				house.setHousePrice(housePrice);
				house.setPricePerSquare(pricePerSquare);
				house.setDown(isDown);
				house.setHouseURL(houseURL);
				house.setRegionURL(regionURL);
				house.setHouseType(houseType);
				house.setHouseHeight(houseHeight);
				house.setHouseBuildType(houseBuildType);
				house.setHouseBuildYear(houseBuildYear);*/


				list.add(getDetail(houseURL));
			} catch (Exception e){
				e.printStackTrace();
			}
			
		}
		
		detectNextPage(doc);
		
		return list;
	}
	
	
	public static void detectNextPage(Document doc){
		Elements pageLinks = doc.select("div.page-box, div.house-lst-page-box");
		String pageDataStr = pageLinks.attr("page-data");
		String[] pageDatas = pageDataStr.split(",");
		if(pageDatas.length == 2){
			String totalPage = pageDatas[0].split(":")[1];
			String hrefTemplet = pageLinks.attr("page-url");
			String currentPage = pageDatas[1].split(":")[1].substring(0,pageDatas[1].split(":")[1].length()-1);
			if(!currentPage.equals(totalPage)){
				String nextPage = String.valueOf((Integer.valueOf(currentPage)+1));
				String nextURL = LianJiaParams.BaseURL + hrefTemplet.replace("{page}", nextPage);
//				System.out.println("URL ADD:"+nextURL);
				URLPool.getInstance().pushURL(nextURL);
			}
		}
		
	}
	
	
	
	private static LianJiaHouse getDetail(String detailUrl){
		try {
			TimeUnit.MILLISECONDS.sleep(1000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Document detailDocument = null;
		try {
			detailDocument = Jsoup.parse(NetUtils.httpGet(detailUrl));
		} catch (Exception e) {
			e.printStackTrace();
		}
		String houseId = StringUtils.substringBefore(StringUtils.substringAfterLast(detailUrl,"/"),".");
		String houseTitle = detailDocument.select(".title>h1.main").text();
		String houseLocation = detailDocument.select(".areaName>span.info").text();
		String houseRoom = detailDocument.select(".houseInfo>.room>.mainInfo").text();
		int seeTimes = getInt(detailDocument.select("#record>.panel>.count").text());
		int seeTimesLastWeek = getInt(detailDocument.select("#record>.panel>.totalCount>span").text());
		String houseHeight = detailDocument.select(".houseInfo>.room>.subInfo").text();
		String price = detailDocument.select(".price>.total").get(0).text();
		String pricePerSquare = detailDocument.select(".unitPriceValue").get(0).text();
		String houseType = detailDocument.select(".type>.subInfo").get(0).text();//精装
		String direction = detailDocument.select(".type>.mainInfo").get(0).text();//南北
		String communityName = detailDocument.select(".aroundInfo>.communityName>a.info").get(0).text();
		String areaInfo = detailDocument.select(".houseInfo>.area>.mainInfo").get(0).text();
		areaInfo = StringUtils.substringBefore(areaInfo,"平米");//面积
		String year = detailDocument.select(".houseInfo>.area>.subInfo").get(0).text();
		year = StringUtils.substringBefore(year,"年");
//                System.out.println("-------"+ price);
		System.out.println("-------"+ communityName+","+areaInfo+","+price+","+year);
		LianJiaHouse house = new LianJiaHouse();
		house.setHouseId(houseId);
		house.setHouseTitle(houseTitle);
		house.setHouseLocation(communityName);
		house.setHouseRoom(houseRoom);
		house.setHouseArea(areaInfo);
		house.setHouseDirection(direction);
		house.setHousePrice(price);
		house.setPricePerSquare(pricePerSquare);
//		house.setDown(isDown);
		house.setHouseURL(detailUrl);
//		house.setRegionURL(regionURL);
		house.setHouseType(houseType);
		house.setHouseHeight(houseHeight);
		house.setHouseBuildType(houseLocation);
		house.setHouseBuildYear(year);
		house.setSeeTimes(seeTimes);
		house.setSeeTimesLastWeek(seeTimesLastWeek);

		return house;
	}

	private static int getInt(String str){
		try {
			return Integer.parseInt(str);
		}catch (Exception e){
			return 0;
		}
	}

}
