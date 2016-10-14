package main.metadata.metadata;

public class LianJiaHouse {
	private String houseId;
	private String houseTitle;
	private String houseLocation;
	private String communityId;
	private String houseRoom;
	private String houseArea;
	private String houseDirection;
	private String housePrice;
	private String pricePerSquare;
	private String houseURL;
	private String regionURL;
	private boolean isDown;
	private String houseType;
	private String houseHeight;
	private String houseBuildYear;
	private String houseBuildType;
	private String listingDate;
	private int status;
	private int seeTimes;
	private int seeTimesLastWeek;
	private String city;

	public String getCommunityId() {
		return communityId;
	}

	public void setCommunityId(String communityId) {
		this.communityId = communityId;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getListingDate() {
		return listingDate;
	}

	public void setListingDate(String listingDate) {
		this.listingDate = listingDate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getSeeTimes() {
		return seeTimes;
	}

	public void setSeeTimes(int seeTimes) {
		this.seeTimes = seeTimes;
	}

	public int getSeeTimesLastWeek() {
		return seeTimesLastWeek;
	}

	public void setSeeTimesLastWeek(int seeTimesLastWeek) {
		this.seeTimesLastWeek = seeTimesLastWeek;
	}

	public String getHouseType() {
		return houseType;
	}
	public void setHouseType(String houseType) {
		this.houseType = houseType;
	}
	public String getHouseHeight() {
		return houseHeight;
	}
	public void setHouseHeight(String houseHeight) {
		this.houseHeight = houseHeight;
	}
	public String getHouseBuildYear() {
		return houseBuildYear;
	}
	public void setHouseBuildYear(String houseBuildYear) {
		this.houseBuildYear = houseBuildYear;
	}
	public String getHouseBuildType() {
		return houseBuildType;
	}
	public void setHouseBuildType(String houseBuildType) {
		this.houseBuildType = houseBuildType;
	}
	public String getHouseURL() {
		return houseURL;
	}
	public void setHouseURL(String houseURL) {
		this.houseURL = houseURL;
	}
	public String getRegionURL() {
		return regionURL;
	}
	public void setRegionURL(String regionURL) {
		this.regionURL = regionURL;
	}
	public boolean isDown() {
		return isDown;
	}
	public void setDown(boolean isDown) {
		this.isDown = isDown;
	}
	public String getHousePrice() {
		return housePrice;
	}
	public void setHousePrice(String housePrice) {
		this.housePrice = housePrice;
	}
	public String getPricePerSquare() {
		return pricePerSquare.replace(" 元/m²", "");
	}
	public void setPricePerSquare(String pricePerSquare) {
		this.pricePerSquare = pricePerSquare;
	}
	public String getHouseId() {
		return houseId;
	}
	public void setHouseId(String houseId) {
		this.houseId = houseId;
	}
	public String getHouseTitle() {
		return houseTitle;
	}
	public void setHouseTitle(String houseTitle) {
		this.houseTitle = houseTitle;
	}
	public String getHouseLocation() {
		return houseLocation;
	}
	public void setHouseLocation(String houseLocation) {
		this.houseLocation = houseLocation;
	}
	public String getHouseRoom() {
		return houseRoom;
	}
	public void setHouseRoom(String houseRoom) {
		this.houseRoom = houseRoom;
	}
	public String getHouseArea() {
		return houseArea.replace("平米", "");
	}
	public void setHouseArea(String houseArea) {
		this.houseArea = houseArea;
	}
	public String getHouseDirection() {
		return houseDirection;
	}
	public void setHouseDirection(String houseDirection) {
		this.houseDirection = houseDirection;
	}
	
	
}
