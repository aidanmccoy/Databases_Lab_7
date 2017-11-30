import java.math.BigDecimal;
import java.sql.Date;

public class Reservation {

	public int CODE;
	public String Room;
	public Date CheckIn;
	public Date CheckOut;
	public BigDecimal Rate;
	public String LastName;
	public String FirstName;
	public int Adults;
	public int Kids;

	public Reservation(int cODE, String room, Date checkIn, Date checkOut, BigDecimal rate, String lastName,
			String firstName, int adults, int kids) {
		super();

		CODE = cODE;
		Room = room;
		CheckIn = checkIn;
		CheckOut = checkOut;
		Rate = rate;
		LastName = lastName;
		FirstName = firstName;
		Adults = adults;
		Kids = kids;
	}

	public int getCODE() {
		return CODE;
	}

	public void setCODE(int cODE) {
		CODE = cODE;
	}

	public String getRoom() {
		return Room;
	}

	public void setRoom(String room) {
		Room = room;
	}

	public Date getCheckIn() {
		return CheckIn;
	}

	public void setCheckIn(Date checkIn) {
		CheckIn = checkIn;
	}

	public Date getCheckOut() {
		return CheckOut;
	}

	public void setCheckOut(Date checkOut) {
		CheckOut = checkOut;
	}

	public BigDecimal getRate() {
		return Rate;
	}

	public void setRate(BigDecimal rate) {
		Rate = rate;
	}

	public String getLastName() {
		return LastName;
	}

	public void setLastName(String lastName) {
		LastName = lastName;
	}

	public String getFirstName() {
		return FirstName;
	}

	public void setFirstName(String firstName) {
		FirstName = firstName;
	}

	public int getAdults() {
		return Adults;
	}

	public void setAdults(int adults) {
		Adults = adults;
	}

	public int getKids() {
		return Kids;
	}

	public void setKids(int kids) {
		Kids = kids;
	}

}

