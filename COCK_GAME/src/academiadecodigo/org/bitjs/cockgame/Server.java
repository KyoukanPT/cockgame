package academiadecodigo.org.bitjs.cockgame;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ExecutorService clientHandler;
    private Boolean gameStart = false;
    private static LinkedList<Dispatcher> playerPool;
    private int connections;
    private static String play;
    private static String[] board = new String[9];
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
    private static final String RED_BACKGROUND = "\033[41m";
    private static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";

    public Server(){
        try {
            playerPool = new LinkedList<>();
            serverSocket = new ServerSocket(8080);
            clientHandler = Executors.newFixedThreadPool(2);
            for (int i = 0; i < 9; i++) {
                board[i] = String.valueOf(i + 1);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            while (!gameStart) {
                System.out.println("Waiting for players connections");
                clientSocket = serverSocket.accept();
                System.out.println("Player connection: " + clientSocket);
                Dispatcher player = new Dispatcher(clientSocket);
                connections++;
                player.setPlayer(connections);
                playerPool.offer(player);
                if (connections == 2){
                    gameStart = true;
                }
                clientHandler.submit(player);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void checkLogic(int play, int playerNum) {
        BufferedWriter actualPlayer = playerPool.get(playerNum).getClientWriter();
        String winner = null;

        try {
            if (!(playerNum > 0 && playerNum <= 9)) {
                actualPlayer.write("Invalid input; re-enter slot number:");
                actualPlayer.flush();
            }
        if (playerPool.get(playerNum).getPlayer() == 2){
            board[play - 1] = "X";

        } else {
            System.out.println("aqui");
            board[play - 1] = "O";
        }
        printBoard();
        checkWinner();
       /*winner = checkWinner();
        if (board[numInput - 1].equals(String.valueOf(numInput))) {
            board[numInput - 1] = play;
            if (play.equals("X")) {
                play = "O";
            } else {
                play = "X";
            }
            printBoard();
            winner = checkWinner();
        } else {
            actualPlayer.write("Slot already taken; re-enter slot number:");
            actualPlayer.flush();
        }
        if (winner.equalsIgnoreCase("draw")) {
            actualPlayer.write("It's a draw! Thanks for playing.");
        } else {
            actualPlayer.write("Congratulations! " + winner + "'s have won! Thanks for playing.");
        }
        actualPlayer.flush();*/
        } catch (InputMismatchException | IOException e) {
            e.printStackTrace();
        }
    }

    public static String checkWinner() {
        for (int i = 0; i < 8; i++) {
            String line = null;
            switch (i) {
                case 0:
                    line = board[0] + board[1] + board[2]; //3 horizontal top
                    break;
                case 1:
                    line = board[3] + board[4] + board[5]; //3 horizontal middle
                    break;
                case 2:
                    line = board[6] + board[7] + board[8]; //3 horizontal bottom
                    break;
                case 3:
                    line = board[0] + board[3] + board[6]; //3 diagonal left to right
                    break;
                case 4:
                    line = board[1] + board[4] + board[7]; //3 vertical middle
                    break;
                case 5:
                    line = board[2] + board[5] + board[8]; //3 vertical right
                    break;
                case 6:
                    line = board[0] + board[4] + board[8]; //3 vertical left
                    break;
                case 7:
                    line = board[2] + board[4] + board[6]; //3 diagonal right to left
                    break;
            }
            if (line.equals("XXX")) {
                return "X";
            } else if (line.equals("OOO")) {
                return "O";
            }
        }

        for (int i = 0; i < 9; i++) {
            if (Arrays.asList(board).contains(String.valueOf(i + 1))) {
                break;
            } else if (i == 8) return "draw";
        }

        System.out.print(play + "'s turn; enter a slot number to place " + play + " in:");
        return null;
    }

    public static void printBoard() {
        StringBuilder boardPainter = new StringBuilder();

        boardPainter.append(ANSI_BLUE_BACKGROUND+ANSI_BLACK + "/---|---|---\\\n"+ANSI_RESET);
        boardPainter.append(ANSI_BLUE_BACKGROUND + ANSI_BLACK + "| "+ board[0] + " | " + board[1] + " | " + board[2] + " |\n"+ANSI_RESET);
        boardPainter.append(ANSI_BLUE_BACKGROUND+ ANSI_BLACK+ "|---"+ "|---|"+ "---|\n"+ANSI_RESET);
        boardPainter.append(ANSI_WHITE_BACKGROUND+ ANSI_BLACK+"| " + board[3] + " | " + board[4] + " | " + board[5] + " |\n"+ANSI_RESET);
        boardPainter.append(ANSI_WHITE_BACKGROUND+ ANSI_BLACK+"|---"+ "|---|"+ "---|\n"+ANSI_RESET);
        boardPainter.append(RED_BACKGROUND+ ANSI_BLACK+"| " + board[6] + " | " + board[7] + " | " + board[8] + " |\n"+ANSI_RESET);
        boardPainter.append(RED_BACKGROUND+ ANSI_BLACK+ "/---|---|---\\\n"+ANSI_RESET);
        try {
            for (Dispatcher players : playerPool) {
                players.getClientWriter().write(boardPainter.toString());
                players.getClientWriter().flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Boolean getGameStart() {
        return gameStart;
    }
}
