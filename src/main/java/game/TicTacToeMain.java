package game;

import gameclasses.*;
import parsers.*;

import java.util.*;

public class TicTacToeMain {

    public final static int SIZE = 3;
    public final static int SIZE_FIELD = 9;
    public final static char EMPTY = '-';
    public final static char CROSS = 'X';
    public final static char ZERO = '0';
    public static Scanner scanner = new Scanner(System.in);
    public static Logger log = new Logger();
    public static HashMap<String, Integer> players = new HashMap<>();
    public static ArrayList<String> steps = new ArrayList<>();
    public static ArrayList<Player> playerJson = new ArrayList<>();
    public static String[] playerWinner;

    public static void main(String[] args) {

        System.out.println("Начинаем игру!");

        System.out.println("Введите ваше имя для хода крестиком: ");
        String player1 = scanner.nextLine();
        players.put(player1, 0);
        Player playerJSON1 = new Player("1", player1, "X");
        playerJson.add(playerJSON1);

        System.out.println("Введите ваше имя для хода ноликом: ");
        String player2 = scanner.nextLine();
        players.put(player2, 0);
        Player playerJSON2 = new Player("2", player2, "0");
        playerJson.add(playerJSON2);

        //заполняем лист шагов первоначальными значениями
        fillOfList();

        playStart(player1, player2, players);
        playAgain(player1, player2, players);

        //парсеры XML
        Writer writerXML = new XMLWriter(player1, player2, steps, playerWinner);
        writerXML.write();
        Reader readerXML = new XMLReader();
        readerXML.read();

        Game game = new Game(steps);
        GameResult gameResult = new GameResult(playerWinner);
        Gameplay gameplay = new Gameplay(playerJson, game, gameResult);
        JsonModel model = new JsonModel(gameplay);

        //парсеры Json
        Writer writerJson = new JsonWriter(model);
        writerJson.write();
        Reader readerJson = new JsonReader();
        readerJson.read();
    }


    public static void playStart(String player1, String player2, HashMap<String, Integer> players) {
        int count = 0;
        char[][] field = new char[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                field[i][j] = EMPTY;
            }
        }

        //делаем флаг , чтобы понять кто сейчас ходит
        boolean isCrossTurn = true;
        while (true) {
            System.out.println("Ход " + (isCrossTurn ? "крестиком " : "ноликом ") + ",введите 2 цифры поля(например 12)");
            printField(field);
            String input = scanner.nextLine(); //32
            steps.set(count, input);
            String[] parts = input.split("");
            int x = Integer.parseInt(parts[0]) - 1; // 3-1=2
            int y = Integer.parseInt(parts[1]) - 1; // 2-1=1
            if (field[x][y] != EMPTY) {
                System.out.println("Поле занято. Выберите другую ячейку");
                //повторяем попытку хода
                continue;
            }

            //совершаем ход
            field[x][y] = isCrossTurn ? CROSS : ZERO;
            count++;

            if (isDraw(field)) {
                System.out.println("Ничья!");
                playerWinner = new String[]{" не выиграл никто ", "дружба", "!"};
                break;
            }
            if (isWin(field, isCrossTurn ? CROSS : ZERO)) {
                System.out.println("Выиграл " + (isCrossTurn ? player1 : player2) + "!");

                if (isCrossTurn) {
                    players.put(player1, players.get(player1) + 1);
                    playerWinner = new String[]{"1", player1, "X"};
                } else {
                    players.put(player2, players.get(player2) + 1);
                    playerWinner = new String[]{"2", player2, "0"};
                }

                printField(field);
                //завершаем игру
                break;
            } else {
                isCrossTurn = !isCrossTurn;
            }
        }
        printRating(players);
    }

    public static void printRating(HashMap<String, Integer> players) {
        System.out.println("Игра окончена.Рейтинг: ");
        for (Map.Entry entry : players.entrySet()) {
            log.log("Игрок: " + entry.getKey() + " Счет: "
                    + entry.getValue());
        }
    }


    public static void playAgain(String player1, String player2, HashMap<String, Integer> players) {
        while (true) {
            System.out.println("Продолжить игру? Если да, нажмите Y. Если нет, нажимте N ");
            String choice = scanner.nextLine();
            if (choice.equals("Y")) {
                playStart(player1, player2, players);
            } else if (choice.equals("N")) {
                System.out.println("Пока.пока");
                break;
            }
        }
    }


    public static boolean isWin(char[][] field, char player) {
        if (field[0][0] == player && field[0][1] == player && field[0][2] == player)
            return true;
        if (field[1][0] == player && field[1][1] == player && field[1][2] == player)
            return true;
        if (field[2][0] == player && field[2][1] == player && field[2][2] == player)
            return true;

        if (field[0][0] == player && field[1][0] == player && field[2][0] == player)
            return true;
        if (field[0][1] == player && field[1][1] == player && field[2][1] == player)
            return true;
        if (field[0][2] == player && field[1][2] == player && field[2][2] == player)
            return true;

        if (field[0][0] == player && field[1][1] == player && field[2][2] == player)
            return true;
        if (field[2][0] == player && field[1][1] == player && field[0][2] == player)
            return true;

        return false;
    }

    public static void printField(char[][] field) {
        for (char[] row : field) {
            for (char cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    public static void fillOfList() {
        //заполняем лист шагов (для создания XML) первоначальными значениями
        for (int i = 0; i < SIZE_FIELD; i++) {
            steps.add("-");
        }
    }

    public static boolean isDraw(char[][] field) {
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 3; col++)
                if (field[row][col] == EMPTY)
                    return false;
        return true;
    }
}