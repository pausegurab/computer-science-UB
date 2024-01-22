import java.io.IOException;
import java.util.Scanner;
import Util.Util;

public class GameClient {

    /*
    TO DO.
    Class that encapsulates the game's logic. Sequence of states following the established protocol .
     */

    private ClientProtocol clientProtocol;
    private Client client;
    private int bulletsClient = 0;
    private Scanner sc;
    private Util util;
    private boolean game;
    private int id;


    //Constructor con parámetros Utils utils y hacemos los news de clientpProtocol y client, y client set utils.

    public GameClient(Util util) throws IOException {
        clientProtocol = new ClientProtocol(util);
        client = new Client();
        sc = new Scanner(System.in);
        this.util = util;
        game = true;
        init();

    }
    public void init() throws IOException {
        clientProtocol.sendHello();
    }

    //creem mètode de màquina d'estats play().

    public void play() throws IOException {
        // id Random

        String result;
        int opcode;


        State state = clientProtocol.getState();




        while (game) {
            opcode = util.readByte();

            switch (opcode) {

                case 2:
                    id = clientProtocol.receiveReady();
                    clientProtocol.sendPlay(id);
                    break;

                case 4:
                    clientProtocol.receiveAdmit();
                    clientProtocol.sendAction();

                    break;


                case 6:
                    result = clientProtocol.receiveResult();
                    if (result.equals("ENDS0") || result.equals("ENDS1")) {
                        //clientProtocol.setState(State.END);
                        if(result.equals("ENDS0")){
                            System.out.println("Has perdut la partida, ha guanyat el Server");
                        }else if(result.equals("ENDS1")){
                            System.out.println("Has guanyat la partida");
                        }
                        System.out.println("La partida ha acabat. Vols fer-ne una altra?(SI/NO)");
                        String resume = sc.next();

                        if(resume.equalsIgnoreCase("SI")){
                            clientProtocol.sendPlay(id);
                        }else if(resume.equalsIgnoreCase("NO")){

                           game = false;

                        }else{
                            System.out.println("Resposta no vàlida");
                            game = false;
                        }
                    }else{
                        clientProtocol.sendAction();
                    }
                    break;
                case 8:
                    int errorCode = clientProtocol.receiveError();
                    if(errorCode==3){
                        clientProtocol.sendAction();
                    }
                    else if(errorCode==5){
                        clientProtocol.sendAction();
                    }else{
                        game = false;
                    }
                    break;

            }


        }
    }




}
