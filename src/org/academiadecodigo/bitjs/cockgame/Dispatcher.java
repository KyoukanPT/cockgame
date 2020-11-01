package org.academiadecodigo.bitjs.cockgame;

import org.academiadecodigo.bootcamp.Prompt;
import org.academiadecodigo.bootcamp.scanners.string.StringInputScanner;

import java.io.*;
import java.net.Socket;

public class Dispatcher extends Thread {

    private final Socket clientSocket;
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
        while (clientSocket.isBound()) {
            nextPlay();
        }
    }

    public void nextPlay() {
        if (Server.getWinner() == null) {
            input.setMessage("");
            Server.checkLogic(Integer.parseInt(terminal.getUserInput(input)), this.player, this);
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

    public Socket getClientSocket(){
        return this.clientSocket;
    }

    public BufferedWriter getClientWriter() {
        return this.clientWriter;
    }

   public Dispatcher getOpponent(){
        return this.opponent;
   }

   public Prompt getTerminal(){
        return this.terminal;
   }

   public StringInputScanner getInput(){
        return this.input;
   }

}
