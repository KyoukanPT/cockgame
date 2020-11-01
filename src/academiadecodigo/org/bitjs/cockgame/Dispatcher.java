package academiadecodigo.org.bitjs.cockgame;

import org.academiadecodigo.bootcamp.Prompt;
import org.academiadecodigo.bootcamp.scanners.string.StringInputScanner;

import java.io.*;
import java.net.Socket;

public class Dispatcher extends Thread {

    private Socket clientSocket;
    private Prompt terminal;
    private Dispatcher opponent;

    private BufferedWriter clientWriter;
    private StringInputScanner input;
    private int player;

    public Dispatcher(Socket clientSocket) {
        this.clientSocket = clientSocket;
        input = new StringInputScanner();
        try {
            clientWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            terminal = new Prompt(clientSocket.getInputStream(), new PrintStream(clientSocket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            clientWriter.write("Welcome to the fabulous Cock Game.\n" + "--------------------------------\n");
            clientWriter.flush();

            nextPlay();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void nextPlay(){
            while (clientSocket.isConnected()) {
                if (Thread.currentThread().isAlive()){
                    input.setMessage("Choose a position: \n");
                    int test = Integer.parseInt(terminal.getUserInput(input));
                    Server.checkLogic((test), this.player, this);
                } else {
                    try {
                        this.clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    this.interrupt();
                    break;
                    /*try {
                        Thread.currentThread().wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                }
            }
    }

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public void setOpponent(Dispatcher opponent){
        this.opponent = opponent;
    }

    public Prompt getTerminal() {
        return this.terminal;
    }

    public Socket getClientSocket(){
        return this.clientSocket;
    }

    public StringInputScanner getInput() {
        return this.input;
    }

    public BufferedWriter getClientWriter() {
        return this.clientWriter;
    }

   public Dispatcher getOpponent(){
        return this.opponent;
   }

}
