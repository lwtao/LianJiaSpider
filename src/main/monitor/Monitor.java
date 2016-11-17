package main.monitor;

import main.metadata.metadata.LianJiaHouse;
import main.metadata.parser.LianJiaDocParser;
import main.metadata.parser.LianJiaRecordParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import util.MyConstants;
import util.database.LianJiaDataHelper;
import util.net.NetUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Monitor {

	public static void main(String[] args) throws Exception {
		

		List<String> locations = new ArrayList<String>();
//		locations.add("chaoyang");
//		locations.add("haidian");
//		locations.add("fengtai");
//		locations.add("shijingshan");
		LianJiaDataHelper dh = new LianJiaDataHelper();
		
		List<String> directions = new ArrayList<String>();
//		directions.add(LianJiaParams.roomDirectionKey_SN);

//		List<String> URLS = LianJiaURLParser.genURL(locations, 0, 500, -1,
//				-1, LianJiaParams.roomCountKey_THREE, null, directions, false,
//				false, false);
		LianJiaDocParser lianJiaDocParser;
		if (MyConstants.CURRENT_CITY.equals("cs")){
		/*	URLPool.getInstance().batchPush(LianJiaURLParser.genURL(locations, 40, 150, 50,
					160, LianJiaParams.roomCountKey_THREE, LianJiaParams.roomAgeKey_TEN, directions, false,
					false, true));
			URLPool.getInstance().batchPush(LianJiaURLParser.genURL(locations, 40, 150, 50,
					160, LianJiaParams.roomCountKey_TWO, LianJiaParams.roomAgeKey_TEN, directions, false,
					false, true));
			URLPool.getInstance().batchPush(LianJiaURLParser.genURL(locations, 40, 150, 50,
					160, LianJiaParams.roomCountKey_FOUR, LianJiaParams.roomAgeKey_TEN, directions, false,
					false, true));*/
			URLPool.getInstance().pushURL("http://cs.lianjia.com/ershoufang/pg1y2l3l4a3a4a5a6p3p4p5/");
			lianJiaDocParser = new LianJiaDocParser();
		}else if (MyConstants.CURRENT_CITY.equals("sz")) {
			/*URLPool.getInstance().batchPush(LianJiaURLParser.genURL(locations, 250, 400, 50,
					120, LianJiaParams.roomCountKey_THREE, LianJiaParams.roomAgeKey_TEN, directions, false,
					false, false));
			URLPool.getInstance().batchPush(LianJiaURLParser.genURL(locations, 250, 400, 50,
					120, LianJiaParams.roomCountKey_TWO, LianJiaParams.roomAgeKey_TEN, directions, false,
					false, false));
			URLPool.getInstance().batchPush(LianJiaURLParser.genURL(locations, 250, 400, 50,
					120, LianJiaParams.roomCountKey_THREE, LianJiaParams.roomAgeKey_TEN2TWENTY, directions, false,
					false, false));
			URLPool.getInstance().batchPush(LianJiaURLParser.genURL(locations, 250, 400, 50,
					120, LianJiaParams.roomCountKey_TWO, LianJiaParams.roomAgeKey_TEN2TWENTY, tions, false,
					false, false));*/
			URLPool.getInstance().pushURL("http://sz.lianjia.com/ershoufang/sf1hu1de2de1pg1y4l2l3a3a4p3p4/");//68PG
			lianJiaDocParser = new LianJiaDocParser();
		} else {
			URLPool.getInstance().pushURL("http://sz.lianjia.com/chengjiao/sf1de2de1y4pg1l2l3ba60ea100bp300ep450/");
			lianJiaDocParser = new LianJiaRecordParser();
		}


		//URLPool.getInstance().batchPush(LianJiaURLParser.genURL(locations, 250, 400, 50,
		//		120, LianJiaParams.roomCountKey_THREE, LianJiaParams.roomAgeKey_TEN, directions, false,
		//		false, false));
		//URLPool.getInstance().batchPush(LianJiaURLParser.genURL(locations, 250, 400, 50,
		//		120, LianJiaParams.roomCountKey_TWO, LianJiaParams.roomAgeKey_TEN, directions, false,
		//		false, false));
		//URLPool.getInstance().batchPush(LianJiaURLParser.genURL(locations, 250, 400, 50,
		//		120, LianJiaParams.roomCountKey_THREE, LianJiaParams.roomAgeKey_TEN2TWENTY, directions, false,
		//		false, false));
		//URLPool.getInstance().batchPush(LianJiaURLParser.genURL(locations, 250, 400, 50,
		//		120, LianJiaParams.roomCountKey_TWO, LianJiaParams.roomAgeKey_TEN2TWENTY, directions, false,
		//		false, false));
		/*URLPool.getInstance().batchPush(LianJiaURLParser.genURL(locations, 40, 150, 50,
				160, LianJiaParams.roomCountKey_FOUR, LianJiaParams.roomAgeKey_TEN, directions, false,
				false, true));*/
		while(URLPool.getInstance().hasNext()){
			String URL = URLPool.getInstance().popURL();
			try{
				System.out.println("--------------URL------------------------------");
				System.out.println(URL);
				System.out.println("--------------HouseList--------------------------");
				String content = NetUtils.httpGet(URL);
				Document doc = Jsoup.parse(content);
				URI uri = new URI(URL);
				doc.setBaseUri(uri.getScheme()+ "://"+ uri.getHost());
				List<LianJiaHouse> list = lianJiaDocParser.getHouseList(doc);
				if (list== null || list.size()==0){
					content = NetUtils.httpGet(URL);
					doc = Jsoup.parse(content);
					doc.setBaseUri(uri.getScheme()+ "://"+ uri.getHost());
					list = lianJiaDocParser.getHouseList(doc);
				}
				for(LianJiaHouse house : list){
					String s = house.getHouseTitle() + "\t" + house.getHouseLocation() + "\t" + house.getHousePrice() + "\t" + house.getPricePerSquare() + "\t" + "\t降价:" + house.isDown();
					System.out.println(s);
				}
				
				dh.batchSaveHouse(list);
				
				System.out.println("\t抓取結束");
				
				
			} catch(Exception e){
				if (!lianJiaDocParser.canDuplicate){
					break;
				}
				URLPool.getInstance().pushURL(URL);
				e.printStackTrace();
			}
		}

	}

}
