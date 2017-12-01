import java.math.BigDecimal;
import java.util.ArrayList;

public class Room {

	public String RoomCode;
	public String RoomName;
	public int Beds;
	public String bedType;
	public int maxOcc;
	public BigDecimal basePrice;
	public String decor;
	public float popluarity;
	public ArrayList<BigDecimal> mr = new ArrayList<BigDecimal>();
	public BigDecimal totalRev;

	public Room(String roomCode, String roomName, int beds, String bedType, int maxOcc, BigDecimal basePrice,
			String decor) {
		super();
		RoomCode = roomCode;
		RoomName = roomName;
		Beds = beds;
		this.bedType = bedType;
		this.maxOcc = maxOcc;
		this.basePrice = basePrice;
		this.decor = decor;
	}

	public float getPopluarity() {
		return popluarity;
	}

	public void setPopluarity(float popluarity) {
		this.popluarity = popluarity;
	}

	public String getRoomCode() {
		return RoomCode;
	}

	public void setRoomCode(String roomCode) {
		RoomCode = roomCode;
	}

	public String getRoomName() {
		return RoomName;
	}

	public void setRoomName(String roomName) {
		RoomName = roomName;
	}

	public int getBeds() {
		return Beds;
	}

	public void setBeds(int beds) {
		Beds = beds;
	}

	public String getBedType() {
		return bedType;
	}

	public void setBedType(String bedType) {
		this.bedType = bedType;
	}

	public int getMaxOcc() {
		return maxOcc;
	}

	public void setMaxOcc(int maxOcc) {
		this.maxOcc = maxOcc;
	}

	public BigDecimal getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(BigDecimal basePrice) {
		this.basePrice = basePrice;
	}

	public String getDecor() {
		return decor;
	}

	public void setDecor(String decor) {
		this.decor = decor;
	}

	public float getTotalRev() {
		Float total = (float) 0;
		for (BigDecimal mrData : mr) {
			total += mrData.floatValue();
		}
		return total;
	}

}
