public enum Movements {
    BLOCK, CHARG, SHOOT;


    Movements(){}

    public Movements stringToMovement(String movement){
        return Movements.valueOf(movement);
    }






}
