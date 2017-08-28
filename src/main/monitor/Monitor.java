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
			//0314-24714
			//0109-2574 0117-2624 0207-2585  0210-2611 0213-2639 0220-2699 0224-2746 0228-2748 0308-2793 0314-2843
			//http://sz.lianjia.com/ershoufang/sf1hu1pg1y4l3a3a4p3p4/
			//0216-4358 0220-4457 0224-4541 0228-4555 0308-4605 0314-4646
			//http://sz.lianjia.com/ershoufang/hu1y4sf1l3l2a3a4p3p4/
			URLPool.getInstance().pushURL("http://sz.lianjia.com/ershoufang/luohuqu/co32sf1hu1y4l3l2a3a4p3p4/");
			URLPool.getInstance().pushURL("http://sz.lianjia.com/ershoufang/futianqu/co32sf1hu1y4l3l2a3a4p3p4/");
			URLPool.getInstance().pushURL("http://sz.lianjia.com/ershoufang/nanshanqu/co32sf1hu1y4l3l2a3a4p3p4/");
			URLPool.getInstance().pushURL("http://sz.lianjia.com/ershoufang/baoanqu/co32sf1hu1y4l3l2a3a4p3p4/");
			URLPool.getInstance().pushURL("http://sz.lianjia.com/ershoufang/longhuaqu/co32sf1hu1y4l3l2a3a4p3p4/");
			URLPool.getInstance().pushURL("http://sz.lianjia.com/ershoufang/longgangqu/co32sf1hu1y4l3l2a3a4p3p4/");
			baseParser = new LianJiaDocParser();
		} else if (MyConstants.CURRENT_CITY.equals("zy")) {
			URLPool.getInstance().pushURL("http://sz.centanet.com/ershoufang/h3t1u7/?pmin=300&pmax=500&amin=70&amax=100");
			URLPool.getInstance().pushURL("http://sz.centanet.com/ershoufang/h2t1u7/?pmin=300&pmax=500&amin=70&amax=100");
			baseParser = new ZhongyuanDocParser();
		} else {
			URLPool.getInstance().pushURL("http://sz.lianjia.com/chengjiao/pg1hu1/");
			//URLPool.getInstance().pushURL("http://sz.lianjia.com/chengjiao/luohuqu/pg11/");//罗湖
			//URLPool.getInstance().pushURL("http://sz.lianjia.com/chengjiao/futianqu/pg20/");//福田
			//URLPool.getInstance().pushURL("http://sz.lianjia.com/chengjiao/nanshanqu/pg14/");//南山
			//URLPool.getInstance().pushURL("http://sz.lianjia.com/chengjiao/yantianqu/pg3/");//盐田
			//URLPool.getInstance().pushURL("http://sz.lianjia.com/chengjiao/baoanqu/pg94/");//宝安
			//URLPool.getInstance().pushURL("http://sz.lianjia.com/chengjiao/longgangqu/pg99/");//龙岗
			//URLPool.getInstance().pushURL("http://sz.lianjia.com/chengjiao/longhuaqu/pg30/");//龙华
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
