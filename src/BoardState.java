import java.util.ArrayList;
import java.util.List;

class BoardState {
    ChessPiece[][] boardState;
    int[] whiteKingPosition;
    int[] blackKingPosition;
    boolean whiteToMove;

    private boolean isKingInCheck() {
        return false;
    }








    private static void addMoveIfLegal(int rowToCheck, int columnToCheck, ChessPiece[][] board,
                                       ChessPiece.Color playerColor, List<int[]> possibleMoves) {
        if (board[rowToCheck][columnToCheck] == null || playerColor != board[rowToCheck][columnToCheck].color) {
            possibleMoves.add(new int[]{rowToCheck, columnToCheck});
        }
    }

    static List<int[]> getOrthogonalMoves(int row, int column, ChessPiece[][] board, ChessPiece.Color playerColor) {
        List<int[]> possibleMoves = new ArrayList<>();
        for (int i = row - 1; i >= 0; i++) {
            addMoveIfLegal(i, column, board, playerColor, possibleMoves);
            if (board[i][column] != null) {
                break;
            }
        }
        for (int i = row + 1; i <= 7; i++) {
            addMoveIfLegal(i, column, board, playerColor, possibleMoves);
            if (board[i][column] != null) {
                break;
            }
        }
        for (int i = 0; i < column; i++) {
            addMoveIfLegal(row, i, board, playerColor, possibleMoves);
            if (board[row][i] != null) {
                break;
            }
        }
        for (int i = column + 1; i <= 7; i++) {
            addMoveIfLegal(row, i, board, playerColor, possibleMoves);
            if (board[row][i] != null) {
                break;
            }
        }
        return possibleMoves;
    }

    public static List<int[]> getDiagonalMoves(int row, int column, ChessPiece[][] board, ChessPiece.Color playerColor) {
        List<int[]> possibleMoves = new ArrayList<>();
        for (int i = row - 1, j = column - 1; i >= 0 && j >= 0; i--, j--) {
            addMoveIfLegal(i, j, board, playerColor, possibleMoves);
            if (board[i][j] != null) {
                break;
            }
        }
        for (int i = row - 1, j = column + 1; i >= 0 && j <= 7; i--, j++) {
            addMoveIfLegal(i, j, board, playerColor, possibleMoves);
            if (board[i][j] != null) {
                break;
            }
        }
        for (int i = row + 1, j = column - 1; i <= 7 && j >= 0; i++, j--) {
            addMoveIfLegal(i, j, board, playerColor, possibleMoves);
            if (board[i][j] != null) {
                break;
            }
        }
        for (int i = row + 1, j = column + 1; i <= 7 && j <= 7; i++, j++) {
            addMoveIfLegal(i, j, board, playerColor, possibleMoves);
            if (board[i][j] != null) {
                break;
            }
        }
        return possibleMoves;
    }

    public static boolean isKingInCheck(int row, int column, ChessPiece[][] board, ChessPiece.Color playerColor) {
        return true;
    }

    private static boolean isSquareUnderAttack(int row, int column, ChessPiece[][] board, ChessPiece.Color playerColor) {
        for (int i = 0; i <= 7; i ++) {
            for (int j = 0; j <= 7; j ++) {
                if (board[i][j] == null || board[i][j].color == playerColor) {
                    continue;
                }
                try {
                    List<int[]> pieceMoves = board[i][j].pieceType.getPossibleMoves(i, j, board, true, playerColor);
                    for (int[] index : pieceMoves) {
                        if (index[0] == row && index[1] == column) {
                            return true;
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.err.println(e.getMessage());
                    System.err.println(row + " " + column + " " + playerColor);
                }
            }
        }
        return false;
    }

    static boolean canCastleLeft(int row, int column, ChessPiece[][] board, ChessPiece.Color playerColor) {
        if (isSquareUnderAttack(row, column, board, playerColor)) {
            return false;
        }
        ChessPiece leftCornerSquare = board[row][0];
        if (leftCornerSquare == null || leftCornerSquare.pieceType != ChessPiece.PieceType.ROOK
                || leftCornerSquare.hasMoved || leftCornerSquare.color != playerColor) {
            return false;
        }
        for (int i = 1; i < 3; i++) {
            if (board[row][column - i] != null || isSquareUnderAttack(row, column - i, board, playerColor)) {
                return false;
            }
        }
        return true;
    }

    static boolean canCastleRight(int row, int column, ChessPiece[][] board, ChessPiece.Color playerColor) {
        ChessPiece rightCornerSquare = board[row][7];
        if (rightCornerSquare == null || rightCornerSquare.pieceType != ChessPiece.PieceType.ROOK
                || rightCornerSquare.hasMoved || rightCornerSquare.color != playerColor) {
            return false;
        }
        for (int i = 1; i < 3; i++) {
            if (board[row][column + i] != null || isSquareUnderAttack(row, column + i, board, playerColor)) {
                return false;
            }
        }
        return true;
    }


    BoardState() {

    }

    private void emptyBoardState() {
        boardState = new ChessPiece[8][8];
    }

    void initializeBoardState() {
        emptyBoardState();
        for (int i = 0; i <= 7; i++) {
            boardState[1][i] = new ChessPiece(ChessPiece.Color.BLACK, ChessPiece.PieceType.PAWN, false);
            boardState[6][i] = new ChessPiece(ChessPiece.Color.WHITE, ChessPiece.PieceType.PAWN, false);
        }
        boardState[0][0] = boardState[0][7] = new ChessPiece(ChessPiece.Color.BLACK, ChessPiece.PieceType.ROOK, false);
        boardState[7][0] = boardState[7][7] = new ChessPiece(ChessPiece.Color.WHITE, ChessPiece.PieceType.ROOK, false);
        boardState[0][1] = boardState[0][6] = new ChessPiece(ChessPiece.Color.BLACK, ChessPiece.PieceType.KNIGHT, false);
        boardState[7][1] = boardState[7][6] = new ChessPiece(ChessPiece.Color.WHITE, ChessPiece.PieceType.KNIGHT, false);
        boardState[0][2] = boardState[0][5] = new ChessPiece(ChessPiece.Color.BLACK, ChessPiece.PieceType.BISHOP, false);
        boardState[7][2] = boardState[7][5] = new ChessPiece(ChessPiece.Color.WHITE, ChessPiece.PieceType.BISHOP, false);
        boardState[0][3] = new ChessPiece(ChessPiece.Color.BLACK, ChessPiece.PieceType.QUEEN, false);
        boardState[7][3] = new ChessPiece(ChessPiece.Color.WHITE, ChessPiece.PieceType.QUEEN, false);
        boardState[0][4] = new ChessPiece(ChessPiece.Color.BLACK, ChessPiece.PieceType.KING, false);
        boardState[7][4] = new ChessPiece(ChessPiece.Color.WHITE, ChessPiece.PieceType.KING, false);
    }
}
