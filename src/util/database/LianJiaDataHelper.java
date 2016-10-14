package util.database;

import main.metadata.metadata.LianJiaHouse;
import util.DateUtils;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

public class LianJiaDataHelper {

	//static String tableName = "lianjia_table_sz";
	static String tableName = "lianjia_table";
	static String houseIdCol = "house_id";
	static String houseTitleCol = "house_title";
	static String houseLocationCol = "house_location";
	static String houseRoomCol = "house_room";
	static String houseAreaCol = "house_area";
	static String houseDirectionCol = "house_direction";
	static String housePriceCol = "house_price";
	static String pricePerSquareCol = "price_per_square";
	static String houseURLCol = "house_url";
	static String regionURLCol = "region_url";
	static String isDownCol = "is_down";
	static String createDateCol = "create_date";
	static String houseTypeCol = "house_type";
	static String houseHeightCol = "house_height";
	static String houseBuildYearCol = "house_build_year";
	static String houseBuildTypeCol = "house_build_type";

	Connection con = null;

	public boolean saveHouse(LianJiaHouse house) throws Exception {

		String insertSql = String
				.format("INSERT INTO %s ( house_id, house_title, house_location, house_room, house_area, house_direction, " +
								"house_price, price_per_square, house_url, region_url, is_down, create_date, house_type, " +
								"house_height , house_build_year,house_build_type,listing_date) " +
						"VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s','%s','%s', " +
								"'%s', '%s','%s','%s') on DEALLOCATE key UPDATE status=%s,see_times=%s,see_times_last_week=%s",
						tableName,
						house.getHouseId(),
						house.getHouseTitle(), house.getHouseLocation(),
						house.getHouseRoom(), house.getHouseArea(),
						house.getHouseDirection(), house.getHousePrice(),
						house.getPricePerSquare(), house.getHouseURL(),
						house.getRegionURL(), (house.isDown() ? 1 : 0),
						DateUtils.dateToString(new Date(), DateUtils.yyyyMMdd), house.getHouseType(), house.getHouseHeight(),
						house.getHouseBuildYear(), house.getHouseBuildType(),house.getListingDate(),house.getStatus(),
						house.getSeeTimes(),house.getSeeTimesLastWeek());
						
		try {
			con = MysqlPool.getInstance().getConnection();
			con.createStatement().execute(insertSql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (con != null)
				con.close();
		}

	}
	
	public boolean batchSaveHouse(List<LianJiaHouse> houses) throws Exception {
		try{
			con = MysqlPool.getInstance().getConnection();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} 
		
			for(LianJiaHouse house : houses){


				String insertSql = String
						.format("INSERT INTO %s ( house_id, house_title, house_location, house_room, house_area, house_direction, " +
										"house_price, price_per_square, house_url, region_url, is_down, create_date, house_type, " +
										"house_height , house_build_year,house_build_type,listing_date,city,status,see_times,see_times_last_week) " +
										"VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s','%s', '%s','%s','%s', " +
										"'%s', '%s','%s','%s', %s,%s,%s)  ON DUPLICATE KEY UPDATE  status=%s,see_times=%s,see_times_last_week=%s",
								tableName,
								house.getHouseId(),
								house.getHouseTitle(), house.getHouseLocation(),
								house.getHouseRoom(), house.getHouseArea(),
								house.getHouseDirection(), house.getHousePrice(),
								house.getPricePerSquare(), house.getHouseURL(),
								house.getRegionURL(), (house.isDown() ? 1 : 0),
								DateUtils.dateToString(new Date(), DateUtils.yyyyMMdd), house.getHouseType(), house.getHouseHeight(),
								house.getHouseBuildYear(), house.getHouseBuildType(),house.getListingDate(),house.getCity(),house.getStatus(),
								house.getSeeTimes(),house.getSeeTimesLastWeek(),house.getStatus(),
								house.getSeeTimes(),house.getSeeTimesLastWeek());
				try{
					con.createStatement().execute(insertSql);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if(con != null){
				con.close();
			}
			
			return true;
		
		
	}

	private void getHouseById(String houseId){
		String sql = "select * from lianjia_table where house_id='"+houseId+"'";
	}

	public boolean createTable() throws Exception {
		String dropTable = "DROP TABLE IF EXISTS `lianjia_table`";
		String createTable = "CREATE TABLE `lianjia_table` (  `id` bigint(20) NOT NULL AUTO_INCREMENT,  `house_id` text,  `house_title` text,  `house_location` text,  `house_room` text,  `house_area` text,  `house_direction` text,  `house_price` text,  `price_per_square` text,  `house_url` longtext,  `region_url` longtext,  `is_down` tinyint(2) DEFAULT NULL,  `create_date` text,  `house_type` text,  `house_height` text,  `house_build_year` text,  `house_build_type` text,  PRIMARY KEY (`id`)) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8";
		
		try {
			con = MysqlPool.getInstance().getConnection();
			con.createStatement().execute(dropTable);
			con.createStatement().execute(createTable);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (con != null)
				con.close();
		}
	}
	
	public static void main(String[] args) throws Exception{
		LianJiaDataHelper dh = new LianJiaDataHelper();
		dh.createTable();
	}
}
