CREATE TABLE IF NOT EXISTS lab7_rooms (
RoomCode char(5) PRIMARY KEY,
RoomName varchar(30),
Beds int(11),
bedType varchar(8),
maxOcc int(11),
basePrice DECIMAL(6,2),
decor varchar(20),
UNIQUE (RoomName)
);

CREATE TABLE IF NOT EXISTS lab7_reservations (
CODE int(11) PRIMARY KEY,
Room char(5) NOT NULL,
CheckIn date NOT NULL,
Checkout date NOT NULL,
Rate DECIMAL(6,2) NOT NULL,
LastName varchar(15) NOT NULL,
FirstName varchar(15) NOT NULL,
Adults int(11) NOT NULL,
Kids int(11) NOT NULL,
UNIQUE (Room, CheckIn),
FOREIGN KEY (Room) REFERENCES lab7_rooms (RoomCode)
);

INSERT INTO lab7_rooms SELECT * FROM INN.rooms;
INSERT INTO lab7_reservations SELECT CODE, Room,
DATE_ADD(CheckIn, INTERVAL 7 YEAR),
DATE_ADD(Checkout, INTERVAL 7 YEAR),
Rate, LastName, FirstName, Adults, Kids FROM INN.reservations;