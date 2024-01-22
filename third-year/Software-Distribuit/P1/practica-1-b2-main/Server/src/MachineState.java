public enum MachineState {
    HELLO(1), READY(2), PLAY(3), ADMIT(4), ACTION(5), RESULT(6), ERROR(8), END(0);

    private final int opcode;

    MachineState(int opcode) {
        this.opcode = opcode;
    }

    public int getOpcode() {
        return opcode;
    }
}
