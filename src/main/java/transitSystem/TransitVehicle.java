package transitSystem;

public class TransitVehicle {

    private LineType type;
    private String description;
    private int seatCapacity;
    private int standUpCapacity;
    private int length;

    public TransitVehicle(LineType type, String description, int seatCapacity, int standUpCapacity, int length) {
        this.type = type;
        this.description = description;
        this.seatCapacity = seatCapacity;
        this.standUpCapacity = standUpCapacity;
        this.length = length;
    }
}
