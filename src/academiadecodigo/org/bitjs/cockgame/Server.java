package academiadecodigo.org.bitjs.cockgame;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.LinkedList;

public class Server {
    private ServerSocket serverSocket;
    private Boolean gameStart = false;
    private static LinkedList<Dispatcher> playerPool;
    private int connections;
    private final static String[] board = new String[9];

    private static Dispatcher currentPlayer;

    private final static String PLAYER_X = "X";
    private final static String PLAYER_O = "O";

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    private static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
    private static final String RED_BACKGROUND = "\033[41m";
    private static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";


    public Server() {
        try {
            playerPool = new LinkedList<>();
            serverSocket = new ServerSocket(8080);

            for (int i = 0; i < 9; i++) {
                board[i] = String.valueOf(i + 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void start() {
        try {
            while (!gameStart) {
                System.out.println("Waiting for players connections");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Player connection: " + clientSocket);
                Dispatcher player = new Dispatcher(clientSocket);
                player.getClientWriter().write("WELCOME TO THE FABULOUS COCK GAME!");
                player.getClientWriter().write(printCock());
                player.getClientWriter().flush();
                connections++;
                player.setPlayer(connections);
                playerPool.offer(player);
                currentPlayer = playerPool.get(0);
                if (connections == 2) {
                    gameStart = true;
                    playerPool.get(0).setOpponent(playerPool.get(1));
                    playerPool.get(1).setOpponent(playerPool.get(0));
                    playerPool.get(0).start();
                    playerPool.get(1).start();
                    printBoard();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void checkLogic(int play, int playerNum, Dispatcher playerMove) {
            String winner;
                try {
                    if (!(play > 0 && play <= 9)) {
                        playerPool.get(playerNum).getClientWriter().write("Invalid input; re-enter slot number:");
                        playerPool.get(playerNum).getClientWriter().flush();
                    } else if (currentPlayer == playerMove) {
                        if (currentPlayer.getPlayer() == 1) {
                            board[play - 1] = "X";
                        } else {
                            board[play - 1] = "O";
                        }

                        printBoard();
                        winner = checkWinner();
                        if (winner != null) {
                            for (Dispatcher player : playerPool) {
                                player.getClientWriter().write(winner);
                                player.getClientWriter().flush();
                            }
                            playerPool.get(0).getClientSocket().close();
                            playerPool.get(1).getClientSocket().close();
                            playerPool.get(0).interrupt();
                            playerPool.get(1).interrupt();
                        }
                    }
                    currentPlayer = playerMove.getOpponent();
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
                return PLAYER_X + " is the winner!";
            } else if (line.equals("OOO")) {
                return PLAYER_O + " is the winner!";
            }
        }

        for (int i = 0; i < 9; i++) {
            if (Arrays.asList(board).contains(String.valueOf(i + 1))) {
                break;
            } else if (i == 8) return "draw";
        }

        for (Dispatcher players : playerPool) {
            try {
                players.getClientWriter().write(players.getPlayer() + "'s turn; enter a slot number to play\n");
                players.getClientWriter().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String printCock(){
        StringBuilder cock = new StringBuilder();
        return cock.toString();
    }

    public static void printBoard() {
        StringBuilder boardPainter = new StringBuilder();


            boardPainter.append(ANSI_BLUE_BACKGROUND + ANSI_BLACK + "/---|---|---\\\n" + ANSI_BLACK_BACKGROUND);
            boardPainter.append(ANSI_BLUE_BACKGROUND + ANSI_BLACK + "| " + board[0] + " | " + board[1] + " | " + board[2] + " |\n" + ANSI_BLACK_BACKGROUND);
            boardPainter.append(ANSI_BLUE_BACKGROUND + ANSI_BLACK + "|---" + "|---|" + "---|\n" + ANSI_BLACK_BACKGROUND);
            boardPainter.append(ANSI_WHITE_BACKGROUND + ANSI_BLACK + "| " + board[3] + " | " + board[4] + " | " + board[5] + " |\n" + ANSI_BLACK_BACKGROUND);
            boardPainter.append(ANSI_WHITE_BACKGROUND + ANSI_BLACK + "|---" + "|---|" + "---|\n" + ANSI_BLACK_BACKGROUND);
            boardPainter.append(RED_BACKGROUND + ANSI_BLACK + "| " + board[6] + " | " + board[7] + " | " + board[8] + " |\n" + ANSI_BLACK_BACKGROUND);
            boardPainter.append(RED_BACKGROUND + ANSI_BLACK + "/---|---|---\\\n" + ANSI_BLACK_BACKGROUND + ANSI_RESET);

            try {
                for (Dispatcher players : playerPool) {
                    players.getClientWriter().write("\033[H\033[2J");
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