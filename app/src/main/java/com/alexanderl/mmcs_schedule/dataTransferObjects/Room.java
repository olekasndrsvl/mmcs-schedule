package com.alexanderl.mmcs_schedule.dataTransferObjects;

public class Room {
    private int roomNumber;
    private String roomName;
    public Room(int num, String romename)
    {
        roomNumber=num;
        roomName=romename;
    }
    public Room(int num)
    {
        roomNumber=num;
        roomName=null;
    }
    public String getRoomName() {
        return roomName;
    }

    public int getRoomNumber() {
        return roomNumber;
    }
    public String toString()
    {
        return "Ауд. "+roomNumber;
    }
}
