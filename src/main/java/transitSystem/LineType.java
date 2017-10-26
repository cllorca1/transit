package transitSystem;

public enum LineType {

    RURAL_BUS, TOWN_BUS, CITY_BUS, SBAHN, TRAM, UBAHN, REGIOBAHN;

    public static LineType convertFromCode(int code){
        switch (code){
            case 1:
                return RURAL_BUS;

            case 2:
                return TOWN_BUS;

            case 3:
                return CITY_BUS;

            case 4:
                return SBAHN;

            case 5:
                return TRAM;

            case 6:
                return UBAHN;

            case 7:
                return REGIOBAHN;

            default:
                return null;


        }
    }

}
