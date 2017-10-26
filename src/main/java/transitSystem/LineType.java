package transitSystem;

public enum LineType {

    RURAL_BUS, TOWN_BUS, CITY_BUS, SBAHN, TRAM, UBAHN, REGIOBAHN;

    public static LineType convertFromCode(int code){
        switch (code){
            case 1:
                return RURAL_BUS;

            case 2:
                return RURAL_BUS;

            case 3:
                return RURAL_BUS;

            case 4:
                return RURAL_BUS;

            case 5:
                return RURAL_BUS;

            case 6:
                return RURAL_BUS;

            case 7:
                return RURAL_BUS;

            default:
                return null;


        }
    }

}
