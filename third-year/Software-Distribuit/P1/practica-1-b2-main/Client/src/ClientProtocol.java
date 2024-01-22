import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;
import Util.Util;

public class ClientProtocol {

    private Util util;
    private State state;
    public State getState() {
        return state;
    }
    private Scanner sc;
    private int clientBullets;

    public ClientProtocol(Util util) {
        this.state = State.HELLO;
        this.util = util;
        this.clientBullets = 0;
        sc = new Scanner(System.in);
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getClientBullets() {
        return clientBullets;
    }

    public void setClientBullets(int clientBullets) {
        this.clientBullets = clientBullets;
    }

    public void sendHello() throws IOException {
        try {

            double idDouble = 10000 + Math.random() * 90000;
            int id = (int) (idDouble);
            DataOutputStream dataOutputStream = util.getDataOutputStream();
            int opcode = 1;
            System.out.println("Com et dius?");
            String nom = sc.nextLine();
            util.writeByte(opcode);
            util.writeInt(id);
            util.writeStrings2Bytes0(nom);
            util.flush();


        }catch (IOException error){
            throw new RuntimeException(error.getMessage());
        }
    }

    public int receiveReady(){
        try{

            int id = util.readInt();
            return id;
        }catch (IOException error){
            throw new RuntimeException(error.getMessage());
        }
    }

    public void sendPlay(int id){
        try {
            setClientBullets(0);
            int opcode = 3;
            DataOutputStream dataOutputStream = util.getDataOutputStream();
            util.writeByte(opcode);
            util.writeInt(id);
            util.flush();
        }catch (IOException error){
            throw new RuntimeException(error.getMessage());
        }

    }

    public void receiveAdmit(){
        try{
            DataInputStream dataInputStream = util.getDataInputStream();
            Byte admes = util.readByte();
            if(admes == 0){
                throw new Exception("El client no ha estat acceptat");

            }else{
                System.out.println("Client acceptat");
            }


        }catch (IOException error){
            throw new RuntimeException(error.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String sendAction(){
        try {

            int opcode = 5;
            DataOutputStream dataOutputStream = util.getDataOutputStream();
            util.writeByte(opcode);
            System.out.println("Tria la teva pròxima acció: (SHOOT, CHARG, BLOCK)");
            String movement= sc.nextLine();
            int bulletsClient = getClientBullets();
            if(!fire(movement,bulletsClient)){
                // System.out.println("No pots disparar sense bales. Tria la teva pròxima acció: (SHOOT, CHARG, BLOCK)");
                movement = "ERROR";

            }
            if(movement.equalsIgnoreCase("CHARG")){
                setClientBullets(getClientBullets()+1);}
            else if(movement.equalsIgnoreCase("SHOOT")){
                setClientBullets(getClientBullets()-1);}




            util.write10BytesString(movement);
            util.flush();
            System.out.println("ACTION S-----"+opcode+" "+movement+"-----> C");

            return movement;
        }catch (IOException error){
            throw new RuntimeException(error.getMessage());
        }

    }public boolean fire(String move,int bullets){
        if (move.equalsIgnoreCase("SHOOT") && bullets == 0){
            return false;
        }else{
            return true;
        }
    }

    public String receiveResult(){
        try{


            int opcode = 6;
            String result = util.read10BytesString();
            System.out.println("RESULT S-----"+opcode+" "+result+"-----> C");

            return result;
        }catch (IOException error){
            throw new RuntimeException(error.getMessage());
        }
    }

    public int receiveError() throws IOException {
        int opcode = 8;
        int errorCode = util.readByte();
        String msg = util.readStrings2Bytes0();
        System.out.println(errorCode);
        System.out.println("ERROR C<-----"+opcode+" "+errorCode+" "+msg+"----- S");

        return errorCode;

    }







}