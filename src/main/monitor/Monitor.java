package main.monitor;

import main.metadata.metadata.LianJiaHouse;
import main.metadata.parser.LianJiaDocParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import util.MyConstants;
import util.database.LianJiaDataHelper;
import util.net.NetUtils;

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
		}else {
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
			URLPool.getInstance().pushURL("http://sz.lianjia.com/ershoufang/hu1de2de1pg1co52y4l2l3a2a3a4p2p3p4/");
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


		List<Document> docs = new ArrayList<Document>();
		
		while(URLPool.getInstance().hasNext()){
			String URL = URLPool.getInstance().popURL();
			try{
				System.out.println("--------------URL------------------------------");
				System.out.println(URL);
				System.out.println("--------------HouseList--------------------------");
				String content = NetUtils.httpGet(URL);
				Document doc = Jsoup.parse(content);
				List<LianJiaHouse> list = LianJiaDocParser.getHouseList(doc);
				for(LianJiaHouse house : list){
					String s = house.getHouseTitle() + "\t" + house.getHouseLocation() + "\t" + house.getHousePrice() + "\t" + house.getPricePerSquare() + "\t" + "\t降价:" + house.isDown();
					System.out.println(s);
				}
				
				dh.batchSaveHouse(list);
				
				System.out.println("\t抓取結束");
				
				
			} catch(Exception e){
				URLPool.getInstance().pushURL(URL);
				e.printStackTrace();
			}
		}

	}

}
