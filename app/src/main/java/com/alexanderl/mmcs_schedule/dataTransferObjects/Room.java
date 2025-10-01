package com.alexanderl.mmcs_schedule.dataTransferObjects;

public class Room {
    private int roomNumber;
    public Room(int num)
    {
        roomNumber=num;
    }

    public int getRoomNumber() {
        return roomNumber;
    }
    public String toString()
    {
        return "Ауд. "+roomNumber;
    }
}
