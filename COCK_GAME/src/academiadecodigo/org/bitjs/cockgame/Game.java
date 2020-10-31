package academiadecodigo.org.bitjs.cockgame;

import org.academiadecodigo.bootcamp.Prompt;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;


public class Game {

        private Scanner in;
        private String[] board;
        private String play;
        private Prompt prompt;

        public void init (){
            in = new Scanner(System.in);
            board = new String[9];
            play = "X";
            String winner = null;
            populateEmptyBoard();

            System.out.println("Welcome to the fabulous Cock Game.");
            System.out.println("--------------------------------");
            printBoard();
            System.out.print("X's will play first. Enter a slot number to place X in: ");

            while (winner == null) {
                int numInput;
                try {
                    numInput = in.nextInt();
                    if (!(numInput > 0 && numInput <= 9)) {
                        System.out.println("Invalid input; re-enter slot number:");
                        continue;
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input; re-enter slot number:");
                    continue;
                }
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
                    System.out.println("Slot already taken; re-enter slot number:");
                    continue;
                }
            }
            if (winner.equalsIgnoreCase("draw")) {
                System.out.println("It's a draw! Thanks for playing.");
            } else {
                System.out.println("Congratulations! " + winner + "'s have won! Thanks for playing.");
            }
        }

        public String checkWinner() {
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

        public void printBoard() {
            System.out.println("/---|---|---\\");
            System.out.println("| " + board[0] + " | " + board[1] + " | " + board[2] + " |");
            System.out.println("|---"+ "|---|"+ "---|");
            System.out.println("| " + board[3] + " | " + board[4] + " | " + board[5] + " |");
            System.out.println("|---"+ "|---|"+ "---|");
            System.out.println("| " + board[6] + " | " + board[7] + " | " + board[8] + " |");
            System.out.println("/---|---|---\\");
        }

        //Board position numbers for visual help
        public void populateEmptyBoard() {
            for (int i = 0; i < 9; i++) {
                board[i] = String.valueOf(i + 1);
            }
        }
    }


