package application;

public class Room {

    private String roomId;
    private int capacity;
    private boolean isLab; 

    public Room(String roomId, int capacity, boolean isLab) {
        this.roomId = roomId;
        this.capacity = capacity;
        this.isLab = isLab;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isLab() {
        return isLab;
    }

    public void setLab(boolean isLab) {
        this.isLab = isLab;
    }

    @Override
    public String toString() {
        return "Room " + roomId + " (Cap: " + capacity + ", Lab: " + isLab + ")";
    }
}