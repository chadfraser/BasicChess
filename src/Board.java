import java.util.*;

//promotion

class Board {
    static final int MAX_ROWS = 8;
    static final int MAX_COLUMNS = 8;

    private Piece.Color turnPlayerColor;
    private int[] whiteKingPosition;
    private int[] blackKingPosition;
    private Piece[][] boardLayout;
    private Board previousBoard;

    Board() {
        turnPlayerColor = Piece.Color.WHITE;
        whiteKingPosition = new int[]{7, 4};
        blackKingPosition = new int[]{0, 4};
        boardLayout = new Piece[MAX_ROWS][MAX_COLUMNS];
        previousBoard = null;
    }

    private Board(Piece.Color turnPlayerColor, int[] whiteKingPosition, int[] blackKingPosition,
                  Board previousBoard) {
        this.turnPlayerColor = turnPlayerColor;
        this.whiteKingPosition = whiteKingPosition;
        this.blackKingPosition = blackKingPosition;
        this.previousBoard = previousBoard;
        boardLayout = new Piece[MAX_ROWS][MAX_COLUMNS];
    }

    private Map<int[], List<int[]>> getAllPiecesPossibleMoves() {
        Map<int[], List<int[]>> mapOfPieceMoves = new HashMap<>();

        for (int i = 0; i < MAX_ROWS; i++) {
            for (int j = 0; j < MAX_COLUMNS; j++) {
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

        for (int i = 0; i < MAX_ROWS; i++) {
            for (int j = 0; j < MAX_COLUMNS; j++) {
                if (boardLayout[i][j] != null && boardLayout[i][j].getColor() == turnPlayerColor) {
                    int[] piecePosition = new int[]{i, j};
                    List<Board> possibleBoards = new ArrayList<>();
                    Piece currentPiece = boardLayout[i][j];

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

    List<int[]> getPieceLegalMoves(int row, int column) {
        List<int[]> possibleMoves = boardLayout[row][column].getPieceType().getPossibleMoves(row, column, this, false);
        List<int[]> listOfLegalMoves = new ArrayList<>();

        for (int[] currentMove : possibleMoves) {
            int targetRow = currentMove[0];
            int targetColumn = currentMove[1];

            Board newBoard = movePieceOnNewBoard(row, column, targetRow, targetColumn, turnPlayerColor);
            if (!newBoard.isKingInCheck()) {
                listOfLegalMoves.add(currentMove);
            }
        }
        return listOfLegalMoves;
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

    boolean isCheckmate() {
        return (isKingInCheck() && areThereNoLegalMoves());
    }

    boolean isStalemate() {
        return (!isKingInCheck() && areThereNoLegalMoves());
    }

    private boolean isSquareUnderAttack(int row, int column) {
        turnPlayerColor = getOppositeTurnPlayerColor();
        for (int i = 0; i < MAX_ROWS; i++) {
            for (int j = 0; j < MAX_COLUMNS; j++) {
                if (boardLayout[i][j] == null || boardLayout[i][j].getColor() != turnPlayerColor) {
                    continue;
                }
                List<int[]> pieceMoves = boardLayout[i][j].getPieceType().getPossibleMoves(i, j, this, true);
                for (int[] currentMove : pieceMoves) {
                    if (currentMove[0] == row && currentMove[1] == column) {
                        turnPlayerColor = getOppositeTurnPlayerColor();
                        return true;
                    }
                }
            }
        }
        turnPlayerColor = getOppositeTurnPlayerColor();
        return false;
    }


    private void addMoveToListIfLegal(int targetRow, int targetColumn, List<int[]> possibleMoves) {
        if (targetRow < 0 || targetRow >= MAX_ROWS || targetColumn < 0 || targetColumn >= MAX_COLUMNS) {
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
            addMoveToListIfLegal(i, column, possibleMoves);
            if (boardLayout[i][column] != null) {
                break;
            }
        }
        for (int i = row + 1; i < MAX_ROWS; i++) {
            addMoveToListIfLegal(i, column, possibleMoves);
            if (boardLayout[i][column] != null) {
                break;
            }
        }
        for (int i = column - 1; i >= 0; i--) {
            addMoveToListIfLegal(row, i, possibleMoves);
            if (boardLayout[row][i] != null) {
                break;
            }
        }
        for (int i = column + 1; i < MAX_COLUMNS; i++) {
            addMoveToListIfLegal(row, i, possibleMoves);
            if (boardLayout[row][i] != null) {
                break;
            }
        }
        return possibleMoves;
    }

    List<int[]> getDiagonalMoves(int row, int column) {
        List<int[]> possibleMoves = new ArrayList<>();
        for (int i = row - 1, j = column - 1; i >= 0 && j >= 0; i--, j--) {
            addMoveToListIfLegal(i, j, possibleMoves);
            if (boardLayout[i][j] != null) {
                break;
            }
        }
        for (int i = row - 1, j = column + 1; i >= 0 && j < MAX_COLUMNS; i--, j++) {
            addMoveToListIfLegal(i, j, possibleMoves);
            if (boardLayout[i][j] != null) {
                break;
            }
        }
        for (int i = row + 1, j = column - 1; i < MAX_ROWS && j >= 0; i++, j--) {
            addMoveToListIfLegal(i, j, possibleMoves);
            if (boardLayout[i][j] != null) {
                break;
            }
        }
        for (int i = row + 1, j = column + 1; i < MAX_ROWS && j < MAX_COLUMNS; i++, j++) {
            addMoveToListIfLegal(i, j, possibleMoves);
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

        Piece rightCornerSquare = boardLayout[kingRow][MAX_COLUMNS - 1];
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

    Piece.Color getOppositeTurnPlayerColor() {
        if (turnPlayerColor == Piece.Color.WHITE) {
            return Piece.Color.BLACK;
        } else {
            return Piece.Color.WHITE;
        }
    }

    Board movePieceOnNewBoard(int currentRow, int currentColumn, int targetRow, int targetColumn,
                              Piece.Color newBoardColor) {
        Board newBoard = new Board(newBoardColor, whiteKingPosition, blackKingPosition, this);

        Piece.PieceType currentPieceType = checkAndAlterMovingPieceType(boardLayout[currentRow][currentColumn].getPieceType());
        Piece.Color currentPieceColor =  boardLayout[currentRow][currentColumn].getColor();

        for (int newBoardRow = 0; newBoardRow < MAX_ROWS; newBoardRow++) {
            for (int newBoardColumn = 0; newBoardColumn < MAX_COLUMNS; newBoardColumn++) {
                if (newBoardRow == targetRow && newBoardColumn == targetColumn) {
                    newBoard.boardLayout[newBoardRow][newBoardColumn] = new Piece(currentPieceColor, currentPieceType);
                } else if ((boardLayout[newBoardRow][newBoardColumn] == null) ||
                        (newBoardRow == currentRow && newBoardColumn == currentColumn)) {
                    newBoard.boardLayout[newBoardRow][newBoardColumn] = null;
                } else {
                    Piece thisSquarePiece = boardLayout[newBoardRow][newBoardColumn];
                    newBoard.boardLayout[newBoardRow][newBoardColumn] = new Piece(thisSquarePiece.getColor(),
                            thisSquarePiece.getPieceType());
                }
            }
        }
        newBoard.adjustBoardLayoutForUnusualSituations(currentRow, currentColumn, targetRow, targetColumn,
                currentPieceType);
        return newBoard;
    }

    private void adjustBoardLayoutForUnusualSituations(int currentRow, int currentColumn, int targetRow,
                                                       int targetColumn, Piece.PieceType currentPieceType) {
        if (currentPieceType == Piece.PieceType.KING) {
            checkAndUpdateKingPosition(targetRow, targetColumn);
            checkAndAdjustRookAfterCastling(currentRow, currentColumn, targetColumn);
        } else if (currentPieceType == Piece.PieceType.PAWN) {
            checkAndPromotePawn(targetRow, targetColumn);
            checkAndCaptureEnPassantPawn(currentRow, currentColumn, targetRow, targetColumn);
        }
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

    private void checkAndPromotePawn(int targetRow, int targetColumn) {
        if (targetRow == 0 || targetRow == MAX_ROWS - 1) {
            boardLayout[targetRow][targetColumn] = new Piece(turnPlayerColor, Piece.PieceType.QUEEN);
        }
    }

    private void checkAndCaptureEnPassantPawn(int currentRow, int currentColumn, int targetRow, int targetColumn) {
        if (previousBoard.boardLayout[targetRow][targetColumn] == null && Math.abs(targetColumn - currentColumn) == 1) {
            boardLayout[currentRow][targetColumn] = null;
        }
    }

    private void checkAndAdjustRookAfterCastling(int currentRow, int currentColumn, int targetColumn) {
        if (targetColumn - currentColumn == 2) {
            boardLayout[currentRow][currentColumn + 1] = new Piece(turnPlayerColor, Piece.PieceType.ROOK);
            boardLayout[currentRow][MAX_COLUMNS - 1] = null;
        } else if (targetColumn - currentColumn == -2) {
            boardLayout[currentRow][currentColumn - 1] = new Piece(turnPlayerColor, Piece.PieceType.ROOK);
            boardLayout[currentRow][0] = null;
        }
    }

    private void checkAndUpdateKingPosition(int row, int column) {
        if (turnPlayerColor == Piece.Color.WHITE) {
            whiteKingPosition = new int[]{row, column};
        } else {
            blackKingPosition = new int[]{row, column};
        }
    }

    private void emptyBoardLayout() {
        boardLayout = new Piece[MAX_ROWS][MAX_COLUMNS];
    }

    void initializeBoardLayout() {
        emptyBoardLayout();
        turnPlayerColor = Piece.Color.WHITE;
        previousBoard = new Board();

        for (int i = 0; i < MAX_ROWS; i++) {
            boardLayout[1][i] = new Piece(Piece.Color.BLACK, Piece.PieceType.PAWN_UNMOVED);
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

        for(int i = 0; i < MAX_ROWS; i++) {
            for (int j = 0; j < MAX_COLUMNS; j++) {
                System.arraycopy(boardLayout[i], 0, previousBoard.boardLayout[i], 0, 8);
            }
        }
    }

    Piece[][] getBoardLayout() {
        return boardLayout;
    }

    public void setBoardLayout(Piece[][] boardLayout) {
        this.boardLayout = boardLayout;
    }

    Board getPreviousBoard() {
        return previousBoard;
    }

    public void setPreviousBoard(Board previousBoard) {
        this.previousBoard = previousBoard;
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
        for (Piece[] currentRow : previousBoard.boardLayout) {
            for (Piece j : currentRow) {
                System.out.print(j + ", ");
            }
            System.out.println();
        }
    }
}
