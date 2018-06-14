import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

class ChessGame {
    private static final int[] invalidMoveSign = new int[] {-1, -1};

    private Board gameBoard = new Board();
    private Scanner playerInput = new Scanner(System.in);
    private boolean hasResigned = false;

    private ChessGame() {
        gameBoard.initializeBoardLayout();
    }

    private static void printGameStartMessage() {
        System.out.println("Welcome to Basic Chess!");
        System.out.println("Each turn, type two numbers to select a chess piece by row and column, like so: '4 5'.");
        System.out.println("Then type two numbers to select its destination square, or type 'cancel' to select a " +
                        "different piece.");
        System.out.println("If you select a chess piece that you do not control, or you try to make an illegal " +
                "move, you will be prompted to type in a new selection.");
        System.out.println("When selecting your piece to move, you may type 'quit' to concede the game.");
        System.out.println("Press enter to begin.");
    }

    private void printGameBoard() {
        gameBoard.printBoardLayout();
        System.out.println(gameBoard.getTurnPlayerColor() + " to move.");
    }

    private void printGameEndMessage() {
        Piece.Color opponentColor = gameBoard.getOppositeTurnPlayerColor();
        if (gameBoard.isCheckmate()) {
            System.out.println(gameBoard.getTurnPlayerColor() + " has no legal moves.");
            System.out.println("Checkmate.");
            System.out.println(opponentColor + " wins by checkmate.");
        } else if (gameBoard.isStalemate()) {
            System.out.println(gameBoard.getTurnPlayerColor() + " has no legal moves.");
            System.out.println("Stalemate.");
            System.out.println("The game ends in a draw.");
        } else if (hasResigned) {
            System.out.println(opponentColor + " wins by resignation.");
        }
    }

    private void playGame() {
        gameplayLoop:
        while (!isGameFinished()) {
            printGameBoard();

            while (true) {
                int[] pieceCoordinates = selectPieceToMove();
                if (Arrays.equals(pieceCoordinates, invalidMoveSign)) {
                    break gameplayLoop;
                }
                System.out.println("Please type the coordinates of your target square.");
                int[] targetCoordinates = selectTargetSquare(pieceCoordinates[0], pieceCoordinates[1]);
                if (Arrays.equals(targetCoordinates, invalidMoveSign)) {
                    printGameBoard();
                    continue;
                }
                gameBoard = gameBoard.movePieceOnNewBoard(pieceCoordinates[0], pieceCoordinates[1], targetCoordinates[0],
                        targetCoordinates[1], gameBoard.getTurnPlayerColor());
                break;
            }
            gameBoard.setTurnPlayerColor(gameBoard.getOppositeTurnPlayerColor());
        }
        printGameEndMessage();
    }

    private int[] selectPieceToMove() {
        while (true) {
            String[] moveInput = getPlayerMoveInput();
            if ("quit".equals(moveInput[0].toLowerCase())) {
                hasResigned = true;
                break;
            } else if (moveInput.length != 2) {
                System.out.println("\t\tPlease input two integers, or else 'quit' to concede.");
                continue;
            }

            int[] playerMove = validatePlayerIntInput(moveInput[0], moveInput[1]);
            if (Arrays.equals(validatePlayerIntInput(moveInput[0], moveInput[1]), invalidMoveSign) ||
                    !playerControlsPieceToMove(playerMove[0], playerMove[1])) {
                continue;
            }
            return new int[]{playerMove[0], playerMove[1]};
        }
        return invalidMoveSign;
    }

    private int[] selectTargetSquare(int movingPieceRow, int movingPieceColumn) {
        while (true) {
            String[] moveInput = getPlayerMoveInput();
            if ("cancel".equals(moveInput[0].toLowerCase())) {
                return invalidMoveSign;
            } else if (moveInput.length != 2) {
                System.out.println("\t\tPlease input two integers, or else 'cancel' to choose another piece.");
                continue;
            }

            int[] playerMove = validatePlayerIntInput(moveInput[0], moveInput[1]);
            if (Arrays.equals(validatePlayerIntInput(moveInput[0], moveInput[1]), invalidMoveSign) ||
                    !targetSquareIsLegalMove(movingPieceRow, movingPieceColumn, playerMove)) {
                continue;
            }
            return new int[]{playerMove[0], playerMove[1]};
        }
    }

    private String[] getPlayerMoveInput() {
        return playerInput.nextLine().split(" ");
    }

    private static int[] validatePlayerIntInput(String rowInput, String columnInput) {
        int rowIndex;
        int columnIndex;
        try {
            rowIndex = Integer.parseInt(rowInput);
            columnIndex = Integer.parseInt(columnInput);

            if (rowIndex < 0 || rowIndex >= Board.MAX_ROWS || columnIndex < 0 || columnIndex >= Board.MAX_COLUMNS) {
                System.out.println("Please ensure that your first integer is between 0 and " + Board.MAX_ROWS +
                        ", and that your second integer is between 0 and " + Board.MAX_COLUMNS + ".");
                return new int[]{-1, -1};
            }
        } catch (NumberFormatException e) {
            System.out.println("Please input two integers.");
            return new int[]{-1, -1};
        }
        return new int[]{rowIndex, columnIndex};
    }

    private boolean playerControlsPieceToMove(int rowIndex, int columnIndex) {
        Piece[][] boardLayout = gameBoard.getBoardLayout();
        if (boardLayout[rowIndex][columnIndex] == null) {
            System.out.println("There is no piece on square " + rowIndex + "," + columnIndex + ".");
            return false;
        } else if (boardLayout[rowIndex][columnIndex].getColor() != gameBoard.getTurnPlayerColor()) {
            System.out.println("That is not one of your pieces.");
            return false;
        }
        return true;
    }

    private boolean targetSquareIsLegalMove(int currentRow, int currentColumn, int[] targetCoordinates) {
        List<int[]> allLegalMoves = gameBoard.getPieceLegalMoves(currentRow, currentColumn);
        for (int[] currentMove : allLegalMoves) {
            if (Arrays.equals(targetCoordinates, currentMove)) {
                return true;
            }
        }
        System.out.println("That is not a legal move for that piece.");
        return false;
    }

    private boolean isGameFinished() {
        return (hasResigned || gameBoard.isCheckmate() || gameBoard.isStalemate());
    }

    public static void main(String[] args) {
        ChessGame game = new ChessGame();
        printGameStartMessage();
        game.playerInput.nextLine();
        game.playGame();
    }
}
