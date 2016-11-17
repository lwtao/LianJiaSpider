package main.monitor;

import main.metadata.metadata.LianJiaHouse;
import main.metadata.parser.BaseParser;
import main.metadata.parser.LianJiaDocParser;
import main.metadata.parser.LianJiaRecordParser;
import main.metadata.parser.ZhongyuanDocParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import util.MyConstants;
import util.database.LianJiaDataHelper;
import util.net.NetUtils;

import java.net.URI;
import java.util.List;

public class Monitor {

	public static void main(String[] args) throws Exception {

		LianJiaDataHelper dh = new LianJiaDataHelper();

		BaseParser baseParser;
		if (MyConstants.CURRENT_CITY.equals("cs")){
			URLPool.getInstance().pushURL("http://cs.lianjia.com/ershoufang/pg1y2l3l4a3a4a5a6p3p4p5/");
			baseParser = new LianJiaDocParser();
		}else if (MyConstants.CURRENT_CITY.equals("sz")) {
			URLPool.getInstance().pushURL("http://sz.lianjia.com/ershoufang/sf1hu1de2de1pg1y4l2l3a3a4p3p4/");//68PG
			baseParser = new LianJiaDocParser();
		} else if (MyConstants.CURRENT_CITY.equals("zy")) {
			URLPool.getInstance().pushURL("http://sz.centanet.com/ershoufang/h3t1g1/?pmin=300&pmax=500&amin=70&amax=100");//1746
			baseParser = new ZhongyuanDocParser();
		} else {
			URLPool.getInstance().pushURL("http://sz.lianjia.com/chengjiao/sf1de2de1y4pg1l2l3ba60ea100bp300ep450/");
			baseParser = new LianJiaRecordParser();
		}
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
				List<LianJiaHouse> list = baseParser.getHouseList(doc);
				if (list== null || list.size()==0){
					content = NetUtils.httpGet(URL);
					doc = Jsoup.parse(content);
					doc.setBaseUri(uri.getScheme()+ "://"+ uri.getHost());
					list = baseParser.getHouseList(doc);
				}
				for(LianJiaHouse house : list){
					String s = house.getHouseTitle() + "\t" + house.getHouseLocation() + "\t" + house.getHousePrice() + "\t" + house.getPricePerSquare() + "\t" + "\t降价:" + house.isDown();
					System.out.println(s);
				}
				
				dh.batchSaveHouse(list);
				
				System.out.println("\t抓取結束");
				
				
			} catch(Exception e){
				if (!baseParser.canDuplicate()){
					break;
				}
				URLPool.getInstance().pushURL(URL);
				e.printStackTrace();
			}
		}

	}

}
