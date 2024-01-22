import java.io.IOException;
import Util.Util;

public class GameHandler extends Thread{

    /*
    TO DO
    Protocol dynamics from Server.
    Methods: run(), init(), play().
     */

    private GameProtocol gameProtocol;
    private Server server;
    private int bulletsServer = 0;
    private Util util;

    //Constructor con parámetros Utils utils y hacemos los news de clientpProtocol y client, y client set utils.

    public GameHandler(Util util) {
        gameProtocol = new GameProtocol(util);
        server = new Server();
        this.util = util;
    }


    public void run() {
        String name;
        int opcode;
        int id1 = 0;
        int id2 = 0;
        String clientAction = null;
        boolean game = true;






        try {
            while (game) {
                opcode = util.readByte();
                switch (opcode) {
                    case 1:
                        id1 = gameProtocol.receiveHello();
                        gameProtocol.sendReady(id1);
                        break;

                    case 3:
                        id2 = gameProtocol.receivePlay();
                        gameProtocol.sendAdmit(id1,id2);
                        break;
                    case 5:
                        clientAction = gameProtocol.receiveAction();
                        gameProtocol.sendResult(clientAction);
                        break;


                }



            }
        }catch (IOException error){
            System.out.println("El client s'ha desconnectat");;
        }

    }
}
