public enum Error {
    ERROR1(1,"CARÀCTER NO RECONEGUT"), ERROR2(2,"MISSATGE DESCONEGUT"), ERROR3(3,"MISSATGE FORA DE PROTOCOL"),
    ERROR4(4,"INICI DE SESSIÓ INCORRECTE"), ERROR5(5,"PARAULA DESCONEGUDA"),
    ERROR6(6,"MISSATGE MAL FORMAT"), ERROR99(99,"ERROR DESCONEGUT");

    private final int errorCode;
    private final String message;

    Error(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }
    public String getMessage(){return message;}

}


