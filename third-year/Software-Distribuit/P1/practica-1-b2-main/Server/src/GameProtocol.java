import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import Util.Util;

public class GameProtocol {
    private Util util;
    private MachineState machineState;
    private int serverBullets;

    public GameProtocol(Util util) {
        this.util = util;
        this.serverBullets = 0;
    }

    public MachineState getState() {
        return machineState;
    }

    public int getServerBullets() {
        return serverBullets;
    }

    public void setServerBullets(int serverBullets) {
        this.serverBullets = serverBullets;
    }

    public void setState(MachineState machineState) {
        this.machineState = machineState;
    }

    public int receiveHello() throws IOException {
        try{



            int opcode = 1;
            int id = util.readInt();
            if(id < 10000 || id > 99999){
                sendError(4);
                String name = util.readStrings2Bytes0();
            }else{
                String name = util.readStrings2Bytes0();
                System.out.println("HELLO C-----"+opcode+" "+id+" "+name+"-----> S");

            }


            return id;
        }catch (IOException error){
            throw new RuntimeException(error.getMessage());
        }
    }

    public void sendReady(int id){
        try {


            int opcode = 2;
            util.writeByte(opcode);
            util.writeInt(id);
            System.out.println("READY S-----"+opcode+" "+id+"-----> C");
            util.flush();
        }catch (IOException error){
            throw new RuntimeException(error.getMessage());
        }
    }

    public int receivePlay() throws IOException {
        try{
            setServerBullets(0);
            int opcode = 3;
            int id = util.readInt();
            System.out.println("PLAY C-----"+opcode+" "+id+"-----> S");
            return id;
        }catch (IOException error){
            throw new RuntimeException(error.getMessage());
        }
    }

    public void sendAdmit(int id1, int id2){
        try {
            setServerBullets(0);
            byte b;
            int opcode = 4;
            util.writeByte(opcode);
            if(id1 == id2){
                b = 1;
            }else{
                b = 0;

            }
            util.writeByte(b);
            System.out.println("ADMIT S-----"+opcode+" "+b+"-----> C");
            if(b==0){
                sendError(4);
            }
            util.flush();

        }catch (IOException error){
            throw new RuntimeException(error.getMessage());
        }
    }

    public String receiveAction(){
        try{

            int opcode = 5;
            String movement = util.read10BytesString();
            if(movement.equalsIgnoreCase("ERROR")){
                sendError(3);
            }
            else if(!movement.equalsIgnoreCase("CHARG") && !movement.equalsIgnoreCase("SHOOT") && !movement.equalsIgnoreCase("BLOCK") || (movement.length()< 5)) {
                sendError(5);
            }


            System.out.println("ACTION C-----"+opcode+" "+movement+"-----> S");
            return movement;



        }catch (IOException error){
            throw new RuntimeException(error.getMessage());
        }
    }

    public void sendResult(String clientMovement){
        try {
            int opcode = 6;
            util.writeByte(opcode);
            String serverMovement;

            int numero = (int)(Math.random()*3+1);

            if (numero == 1){
                serverMovement = "BLOCK";
            }else if(numero == 2){
                serverMovement = "CHARG";
                this.setServerBullets(getServerBullets()+1);
            }else if(numero==3 && getServerBullets() > 0){
                serverMovement = "SHOOT";
                this.setServerBullets(getServerBullets()-1);;
            }else{
                numero = (int) (Math.random() * 2 + 1);
                if (numero == 1) {
                    serverMovement = "BLOCK";
                } else {
                    serverMovement = "CHARG";
                    this.setServerBullets(getServerBullets()+1);
                }
            }

            String result;

            if(clientMovement.equals("CHARG") && serverMovement.equals("SHOOT")){
                result = "ENDS0";
            }else if(serverMovement.equals("CHARG") && clientMovement.equals("SHOOT")) {
                result = "ENDS1";
            }else if(clientMovement.equals("BLOCK") && serverMovement.equals("CHARG")){
                result = "PLUS0";
            }else if(clientMovement.equals("CHARG") && serverMovement.equals("BLOCK")){
                result = "PLUS1";
            }else if(clientMovement.equals("CHARG") && serverMovement.equals("CHARG")){
                result = "PLUS2";
            }else if(clientMovement.equals("SHOOT") && serverMovement.equals("SHOOT")) {
                result = "DRAW0";
            }else if(clientMovement.equals("SHOOT") && serverMovement.equals("BLOCK")) {
                result = "SAFE0";
            }else if(clientMovement.equals("BLOCK") && serverMovement.equals("SHOOT")) {
                result = "SAFE1";
            }else if(clientMovement.equals("BLOCK") && serverMovement.equals("BLOCK")){
                result = "SAFE2";
            }else{
                sendError(2);
                result = "";
                setState(MachineState.ERROR);
                //Llancem error
            }

            util.write10BytesString(result);
            System.out.println("RESULT S-----"+opcode+" "+result+"-----> C");
            util.flush();




        }catch (IOException error){
            throw new RuntimeException(error.getMessage());
        }

    }
    public void sendError(int errorCode) throws IOException {
        System.out.println("Entro error");
        int opcode = 8;
        Error error = Error.ERROR99;
        switch(errorCode){
            case 1:
                error = Error.ERROR1;
                break;
            case 2:
                error = Error.ERROR2;
                break;
            case 3:
                error = Error.ERROR3;
                break;
            case 4:
                error = Error.ERROR4;
                break;
            case 5:
                error = Error.ERROR5;

                break;
            case 6:
                error = Error.ERROR6;
                break;
            case 99:
                error = Error.ERROR99;
                break;



        }
        try {
            util.writeByte(opcode);
            util.writeByte(error.getErrorCode());
            util.writeStrings2Bytes0(error.getMessage());
            util.flush();
        }catch (IOException err){
            throw new RuntimeException(err.getMessage());
        }
    }




}
