import java.util.*;
import java.lang.Math.*;
//promotion, en passant

class Board {
    private Piece.Color turnPlayerColor;
    private int[] whiteKingPosition;
    private int[] blackKingPosition;
    private Piece[][] boardLayout;
    private Piece[][] previousBoardLayout;

    Board() {
        turnPlayerColor = Piece.Color.WHITE;
        whiteKingPosition = new int[]{7, 4};
        blackKingPosition = new int[]{0, 4};
        boardLayout = new Piece[8][8];
        previousBoardLayout = new Piece[8][8];
    }

    private Board(Piece.Color turnPlayerColor, int[] whiteKingPosition, int[] blackKingPosition,
                  Piece[][] previousBoardLayout) {
        this.turnPlayerColor = turnPlayerColor;
        this.whiteKingPosition = whiteKingPosition;
        this.blackKingPosition = blackKingPosition;
        this.previousBoardLayout = previousBoardLayout;
        boardLayout = new Piece[8][8];
    }

    private Map<int[], List<int[]>> getAllPiecesPossibleMoves() {
        Map<int[], List<int[]>> mapOfPieceMoves = new HashMap<>();

        for (int i = 0; i < boardLayout.length; i++) {
            for (int j = 0; j < boardLayout[i].length; j++) {
                if (boardLayout[i][j] != null && boardLayout[i][j].getColor() == turnPlayerColor) {
                    Piece currentPiece = boardLayout[i][j];
                    List<int[]> possibleMoves = currentPiece.getPieceType().getPossibleMoves(i, j, this, false);
                    mapOfPieceMoves.put(new int[]{i, j}, possibleMoves);
                }
            }
        }
        return mapOfPieceMoves;
    }

    private Map<int[], List<Board>> getAllPossibleBoardStates() {
        Map<int[], List<Board>> mapOfBoardStates = new HashMap<>();

        for (int i = 0; i < boardLayout.length; i++) {
            for (int j = 0; j < boardLayout[i].length; j++) {
                if (boardLayout[i][j] != null && boardLayout[i][j].getColor() == turnPlayerColor) {
                    int[] piecePosition = new int[]{i, j};
                    List<Board> possibleBoards = new ArrayList<>();
                    Piece currentPiece = boardLayout[i][j];

                    if (currentPiece.getPieceType() == Piece.PieceType.PAWN_UNMOVED) {
                        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n\n\n");
                        printBoardLayout();
                        System.out.println();
                        printPreviousBoardLayout();
                    }
                    List<int[]> possibleMoves = currentPiece.getPieceType().getPossibleMoves(i, j, this, false);
                    for (int[] move : possibleMoves) {
                        possibleBoards.add(movePieceOnNewBoard(i, j, move[0], move[1], turnPlayerColor));
                    }
                    mapOfBoardStates.put(piecePosition, possibleBoards);
                }
            }
        }
        return mapOfBoardStates;
    }

    private Map<int[], List<int[]>> getAllPiecesLegalMoves() {
        Map<int[], List<int[]>> mapOfPieceMoves = getAllPiecesPossibleMoves();
        Map<int[], List<int[]>> mapOfLegalMoves = new HashMap<>();

        for (Map.Entry<int[], List<int[]>> entry : mapOfPieceMoves.entrySet()) {
            int currentRowOfPiece = entry.getKey()[0];
            int currentColumnOfPiece = entry.getKey()[1];

            List<int[]> possibleMovesWithoutCheck = new ArrayList<>();
            List<int[]> possibleMoves = entry.getValue();

            for (int[] currentMove : possibleMoves) {
                int targetRow = currentMove[0];
                int targetColumn = currentMove[1];

                Board newBoard = movePieceOnNewBoard(currentRowOfPiece, currentColumnOfPiece, targetRow, targetColumn,
                        turnPlayerColor);
                if (!newBoard.isKingInCheck()) {
                    possibleMovesWithoutCheck.add(currentMove);
                }
            }
            if (!possibleMovesWithoutCheck.isEmpty()) {
                mapOfLegalMoves.put(new int[]{currentRowOfPiece, currentColumnOfPiece}, possibleMovesWithoutCheck);
            }
        }
        return mapOfLegalMoves;
    }

    Map<int[], List<Board>> getAllLegalBoardStates() {
        Map<int[], List<Board>> mapOfBoardStates = getAllPossibleBoardStates();

        for (List<Board> currentBoardList : mapOfBoardStates.values()) {
            currentBoardList.removeIf(Board::isKingInCheck);
        }
        return mapOfBoardStates;
    }

    private boolean areThereNoLegalMoves() {
        Map<int[], List<int[]>> mapOfPieceMoves = getAllPiecesPossibleMoves();

        for (Map.Entry<int[], List<int[]>> entry : mapOfPieceMoves.entrySet()) {
            int currentRowOfPiece = entry.getKey()[0];
            int currentColumnOfPiece = entry.getKey()[1];

            List<int[]> possibleMoves = entry.getValue();

            for (int[] currentMove : possibleMoves) {
                int targetRow = currentMove[0];
                int targetColumn = currentMove[1];

                Board newBoard = movePieceOnNewBoard(currentRowOfPiece, currentColumnOfPiece, targetRow, targetColumn,
                        turnPlayerColor);
                if (!newBoard.isKingInCheck()) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isKingInCheck() {
        if (turnPlayerColor == Piece.Color.WHITE) {
            return isSquareUnderAttack(whiteKingPosition[0], whiteKingPosition[1]);
        } else {
            return isSquareUnderAttack(blackKingPosition[0], blackKingPosition[1]);
        }
    }

    private boolean isCheckmate() {
        return (isKingInCheck() && areThereNoLegalMoves());
    }

    private boolean isStalemate() {
        return (!isKingInCheck() && areThereNoLegalMoves());
    }

    private boolean isSquareUnderAttack(int row, int column) {
        turnPlayerColor = getOppositeTurnPlayerColor(turnPlayerColor);
        for (int i = 0; i <= 7; i++) {
            for (int j = 0; j <= 7; j++) {
                if (boardLayout[i][j] == null || boardLayout[i][j].getColor() != turnPlayerColor) {
                    continue;
                }
                List<int[]> pieceMoves = boardLayout[i][j].getPieceType().getPossibleMoves(i, j, this, true);
                for (int[] currentMove : pieceMoves) {
                    if (currentMove[0] == row && currentMove[1] == column) {
                        turnPlayerColor = getOppositeTurnPlayerColor(turnPlayerColor);
                        return true;
                    }
                }
            }
        }
        turnPlayerColor = getOppositeTurnPlayerColor(turnPlayerColor);
        return false;
    }


    private void addMoveIfLegal(int targetRow, int targetColumn, List<int[]> possibleMoves) {
        if (targetRow < 0 || targetRow > 7 || targetColumn < 0 || targetColumn > 7) {
            return;
        }
        if (boardLayout[targetRow][targetColumn] == null ||
                boardLayout[targetRow][targetColumn].getColor() != turnPlayerColor) {
            possibleMoves.add(new int[]{targetRow, targetColumn});
        }
    }

    List<int[]> getOrthogonalMoves(int row, int column) {
        List<int[]> possibleMoves = new ArrayList<>();
        for (int i = row - 1; i >= 0; i--) {
            addMoveIfLegal(i, column, possibleMoves);
            if (boardLayout[i][column] != null) {
                break;
            }
        }
        for (int i = row + 1; i <= 7; i++) {
            addMoveIfLegal(i, column, possibleMoves);
            if (boardLayout[i][column] != null) {
                break;
            }
        }
        for (int i = column - 1; i >= 0; i--) {
            addMoveIfLegal(row, i, possibleMoves);
            if (boardLayout[row][i] != null) {
                break;
            }
        }
        for (int i = column + 1; i <= 7; i++) {
            addMoveIfLegal(row, i, possibleMoves);
            if (boardLayout[row][i] != null) {
                break;
            }
        }
        return possibleMoves;
    }

    List<int[]> getDiagonalMoves(int row, int column) {
        List<int[]> possibleMoves = new ArrayList<>();
        for (int i = row - 1, j = column - 1; i >= 0 && j >= 0; i--, j--) {
            addMoveIfLegal(i, j, possibleMoves);
            if (boardLayout[i][j] != null) {
                break;
            }
        }
        for (int i = row - 1, j = column + 1; i >= 0 && j <= 7; i--, j++) {
            addMoveIfLegal(i, j, possibleMoves);
            if (boardLayout[i][j] != null) {
                break;
            }
        }
        for (int i = row + 1, j = column - 1; i <= 7 && j >= 0; i++, j--) {
            addMoveIfLegal(i, j, possibleMoves);
            if (boardLayout[i][j] != null) {
                break;
            }
        }
        for (int i = row + 1, j = column + 1; i <= 7 && j <= 7; i++, j++) {
            addMoveIfLegal(i, j, possibleMoves);
            if (boardLayout[i][j] != null) {
                break;
            }
        }
        return possibleMoves;
    }

    boolean canCastleLeft() {
        int kingRow;
        int kingColumn;
        Piece playerKing;

        if (turnPlayerColor == Piece.Color.WHITE) {
            kingRow = whiteKingPosition[0];
            kingColumn = whiteKingPosition[1];
        } else {
            kingRow = blackKingPosition[0];
            kingColumn = blackKingPosition[1];
        }

        playerKing = boardLayout[kingRow][kingColumn];
        if (playerKing.getPieceType() != Piece.PieceType.KING_UNMOVED || isKingInCheck()) {
            return false;
        }

        Piece leftCornerSquare = boardLayout[kingRow][0];
        if (leftCornerSquare == null || leftCornerSquare.getPieceType() != Piece.PieceType.ROOK_UNMOVED ||
                leftCornerSquare.getColor() != turnPlayerColor) {
            return false;
        }

        for (int i = 1; i < 3; i++) {
            if (boardLayout[kingRow][kingColumn - i] != null || isSquareUnderAttack(kingRow, kingColumn - i)) {
                return false;
            }
        }
        return true;
    }

    boolean canCastleRight() {
        int kingRow;
        int kingColumn;
        Piece playerKing;

        if (turnPlayerColor == Piece.Color.WHITE) {
            kingRow = whiteKingPosition[0];
            kingColumn = whiteKingPosition[1];
        } else {
            kingRow = blackKingPosition[0];
            kingColumn = blackKingPosition[1];
        }

        playerKing = boardLayout[kingRow][kingColumn];
        if (playerKing.getPieceType() != Piece.PieceType.KING_UNMOVED || isKingInCheck()) {
            return false;
        }

        Piece rightCornerSquare = boardLayout[kingRow][7];
        if (rightCornerSquare == null || rightCornerSquare.getPieceType() != Piece.PieceType.ROOK_UNMOVED||
                rightCornerSquare.getColor() != turnPlayerColor) {
            return false;
        }

        for (int i = 1; i < 3; i++) {
            if (boardLayout[kingRow][kingColumn + i] != null || isSquareUnderAttack(kingRow, kingColumn + i)) {
                return false;
            }
        }
        return true;
    }

    private static Piece.Color getOppositeTurnPlayerColor(Piece.Color currentTurnPlayerColor) {
        if (currentTurnPlayerColor == Piece.Color.WHITE) {
            return Piece.Color.BLACK;
        } else {
            return Piece.Color.WHITE;
        }
    }

    private Board movePieceOnNewBoard(int currentRow, int currentColumn, int targetRow, int targetColumn,
                                      Piece.Color newBoardColor) {
        Board newBoard = new Board(newBoardColor, whiteKingPosition, blackKingPosition, boardLayout);

        Piece.PieceType currentPieceType = checkAndAlterMovingPieceType(boardLayout[currentRow][currentColumn].getPieceType());
        Piece.Color currentPieceColor =  boardLayout[currentRow][currentColumn].getColor();

        newBoard.checkAndUpdateKingPosition(targetRow, targetColumn, currentPieceType);

        for (int newBoardRow = 0; newBoardRow <= 7; newBoardRow++) {
            for (int newBoardColumn = 0; newBoardColumn <= 7; newBoardColumn++) {
                if (newBoardRow == targetRow && newBoardColumn == targetColumn) {
                    newBoard.boardLayout[newBoardRow][newBoardColumn] = new Piece(currentPieceColor, currentPieceType);
                } else if ((boardLayout[newBoardRow][newBoardColumn] == null) ||
                        (newBoardRow == currentRow && newBoardColumn == currentColumn)) {
                    newBoard.boardLayout[newBoardRow][newBoardColumn] = null;
                } else {
                    Piece thisTilePiece = boardLayout[newBoardRow][newBoardColumn];
                    newBoard.boardLayout[newBoardRow][newBoardColumn] = new Piece(thisTilePiece.getColor(),
                            thisTilePiece.getPieceType());
                }
            }
        }
        newBoard.checkAndCaptureEnPassantPawn(currentRow, currentColumn, targetRow, targetColumn, currentPieceType);
        newBoard.checkAndAdjustRookAfterCastling(currentRow, currentColumn, targetColumn, currentPieceType);
        return newBoard;
    }

    private Piece.PieceType checkAndAlterMovingPieceType(Piece.PieceType movingPieceType) {
        if (movingPieceType == Piece.PieceType.PAWN_UNMOVED) {
            return Piece.PieceType.PAWN;
        } else if (movingPieceType == Piece.PieceType.ROOK_UNMOVED) {
            return Piece.PieceType.ROOK;
        } else if (movingPieceType == Piece.PieceType.KING_UNMOVED) {
            return Piece.PieceType.KING;
        }
        return movingPieceType;
    }

    private void checkAndCaptureEnPassantPawn(int currentRow, int currentColumn, int targetRow, int targetColumn,
                                           Piece.PieceType currentPieceType) {
        if (currentPieceType == Piece.PieceType.PAWN) {
            if (previousBoardLayout[targetRow][targetColumn] == null && Math.abs(targetColumn - currentColumn) == 1) {
                boardLayout[currentRow][targetColumn] = null;
            }
        }
    }

    private void checkAndAdjustRookAfterCastling(int currentRow, int currentColumn, int targetColumn,
                                                 Piece.PieceType currentPieceType) {
        if (currentPieceType == Piece.PieceType.KING) {
            if (targetColumn - currentColumn == 2) {
                boardLayout[currentRow][currentColumn + 1] = new Piece(turnPlayerColor, Piece.PieceType.ROOK);
                boardLayout[currentRow][7] = null;
            } else if (targetColumn - currentColumn == -2) {
                boardLayout[currentRow][currentColumn - 1] = new Piece(turnPlayerColor, Piece.PieceType.ROOK);
                boardLayout[currentRow][0] = null;
            }
        }
    }

    private void checkAndUpdateKingPosition(int row, int column, Piece.PieceType currentPieceType) {
        if (currentPieceType == Piece.PieceType.KING) {
            if (turnPlayerColor == Piece.Color.WHITE) {
                whiteKingPosition = new int[]{row, column};
            } else {
                blackKingPosition = new int[]{row, column};
            }
        }
    }

    boolean canCaptureEnPassant(int currentRow, int targetColumn) {
        int enPassantRow;
        boolean canCaptureEnPassant;

        if (turnPlayerColor == Piece.Color.WHITE) {
            enPassantRow = currentRow - 2;
        } else {
            enPassantRow = currentRow + 2;
        }

        try {
            canCaptureEnPassant = (targetColumn >= 0 && targetColumn <= 7 &&
                    checkPreviousBoardForEnPassantCapture(currentRow, targetColumn, enPassantRow) &&
                    checkCurrentBoardForEnPassantCapture(currentRow, targetColumn, enPassantRow));
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
        return canCaptureEnPassant;
    }

    private boolean checkPreviousBoardForEnPassantCapture(int currentRow, int targetColumn, int enPassantRow) {
        return (previousBoardLayout[enPassantRow][targetColumn] != null &&
                previousBoardLayout[enPassantRow][targetColumn].getPieceType() == Piece.PieceType.PAWN_UNMOVED &&
                (previousBoardLayout[currentRow][targetColumn] == null ||
                        previousBoardLayout[currentRow][targetColumn].getColor() == turnPlayerColor));
    }

    private boolean checkCurrentBoardForEnPassantCapture(int currentRow, int targetColumn, int enPassantRow) {
        return (boardLayout[enPassantRow][targetColumn] == null &&
                boardLayout[currentRow][targetColumn] != null &&
                boardLayout[currentRow][targetColumn].getPieceType() == Piece.PieceType.PAWN &&
                boardLayout[currentRow][targetColumn].getColor() != turnPlayerColor);
    }

    private void emptyBoardLayout() {
        boardLayout = new Piece[8][8];
    }

    void initializeBoardLayout() {
        emptyBoardLayout();
        turnPlayerColor = Piece.Color.WHITE;
        for (int i = 0; i <= 7; i++) {
            boardLayout[4][i] = new Piece(Piece.Color.BLACK, Piece.PieceType.PAWN_UNMOVED);
            boardLayout[6][i] = new Piece(Piece.Color.WHITE, Piece.PieceType.PAWN_UNMOVED);
        }
        boardLayout[0][0] = boardLayout[0][7] = new Piece(Piece.Color.BLACK, Piece.PieceType.ROOK_UNMOVED);
        boardLayout[7][0] = boardLayout[7][7] = new Piece(Piece.Color.WHITE, Piece.PieceType.ROOK_UNMOVED);
        boardLayout[0][1] = boardLayout[0][6] = new Piece(Piece.Color.BLACK, Piece.PieceType.KNIGHT);
        boardLayout[7][1] = boardLayout[7][6] = new Piece(Piece.Color.WHITE, Piece.PieceType.KNIGHT);
        boardLayout[0][2] = boardLayout[0][5] = new Piece(Piece.Color.BLACK, Piece.PieceType.BISHOP);
        boardLayout[7][2] = boardLayout[7][5] = new Piece(Piece.Color.WHITE, Piece.PieceType.BISHOP);
        boardLayout[0][3] = new Piece(Piece.Color.BLACK, Piece.PieceType.QUEEN);
        boardLayout[7][3] = new Piece(Piece.Color.WHITE, Piece.PieceType.QUEEN);
        boardLayout[0][4] = new Piece(Piece.Color.BLACK, Piece.PieceType.KING_UNMOVED);
        boardLayout[7][4] = new Piece(Piece.Color.WHITE, Piece.PieceType.KING_UNMOVED);

        for(int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.arraycopy(boardLayout[i], 0, previousBoardLayout[i], 0, 8);
            }
        }

        boardLayout[6][6] = new Piece(Piece.Color.BLACK, Piece.PieceType.PAWN);
        boardLayout[4][6] = null;
    }

    Piece[][] getBoardLayout() {
        return boardLayout;
    }

    public void setBoardLayout(Piece[][] boardLayout) {
        this.boardLayout = boardLayout;
    }

    Piece[][] getPreviousBoardLayout() {
        return previousBoardLayout;
    }

    public void setPreviousBoardLayout(Piece[][] previousBoardLayout) {
        this.boardLayout = previousBoardLayout;
    }

    Piece.Color getTurnPlayerColor() {
        return turnPlayerColor;
    }

    public void setTurnPlayerColor(Piece.Color turnPlayerColor) {
        this.turnPlayerColor = turnPlayerColor;
    }

    int[] getWhiteKingPosition() {
        return whiteKingPosition;
    }

    public void setWhiteKingPosition(int[] whiteKingPosition) {
        this.whiteKingPosition = whiteKingPosition;
    }

    int[] getBlackKingPosition() {
        return blackKingPosition;
    }

    public void setBlackKingPosition(int[] blackKingPosition) {
        this.blackKingPosition = blackKingPosition;
    }

    void printBoardLayout() {
        for (Piece[] currentRow : boardLayout) {
            for (Piece j : currentRow) {
                System.out.print(j + ", ");
            }
            System.out.println();
        }
    }

    void printPreviousBoardLayout() {
        for (Piece[] currentRow : previousBoardLayout) {
            for (Piece j : currentRow) {
                System.out.print(j + ", ");
            }
            System.out.println();
        }
    }
}
