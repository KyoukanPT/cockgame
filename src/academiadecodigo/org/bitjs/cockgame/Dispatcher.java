package academiadecodigo.org.bitjs.cockgame;

import org.academiadecodigo.bootcamp.Prompt;
import org.academiadecodigo.bootcamp.scanners.string.StringInputScanner;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Dispatcher implements Runnable {

    private Socket clientSocket;
    private Prompt terminal;

    private BufferedWriter clientWriter;
    private StringInputScanner input;
    private int player;

    private Lock gameLock = new ReentrantLock();

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
            Server.printBoard();
            input.setMessage("Choose a position: \n");
            int test = Integer.parseInt(terminal.getUserInput(input));
            Server.checkLogic((test), this.player);
            gameLock.lock();
        }
    }

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public Prompt getTerminal() {
        return this.terminal;
    }

    public StringInputScanner getInput() {
        return this.input;
    }

    public BufferedWriter getClientWriter() {
        return this.clientWriter;
    }

}
