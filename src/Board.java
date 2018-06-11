import java.util.*;

class Board {
    Piece[][] boardLayout;
    Piece.Color turnPlayerColor;
    int[] whiteKingPosition;
    int[] blackKingPosition;

    Board() {
        turnPlayerColor = Piece.Color.WHITE;
        whiteKingPosition = new int[]{7, 4};
        blackKingPosition = new int[]{0, 4};
        boardLayout = new Piece[8][8];
    }

    Board(Piece.Color turnPlayerColor, int[] whiteKingPosition, int[] blackKingPosition) {
        this.turnPlayerColor = turnPlayerColor;
        this.whiteKingPosition = whiteKingPosition;
        this.blackKingPosition = blackKingPosition;
        boardLayout = new Piece[8][8];
    }

    Map<int[], List<int[]>> getAllPiecesPossibleMoves() {
        Piece currentPiece;
        Map<int[], List<int[]>> mapOfPieceMoves = new HashMap<>();

        for (int i = 0; i < boardLayout.length; i++) {
            for (int j = 0; j < boardLayout[i].length; j++) {
                if (boardLayout[i][j] != null && boardLayout[i][j].color == turnPlayerColor) {
                    currentPiece = boardLayout[i][j];
                    List<int[]> possibleMoves = currentPiece.pieceType.getPossibleMoves(i, j, this,
                            currentPiece.hasMoved);
                    mapOfPieceMoves.put(new int[]{i, j}, possibleMoves);
                }
            }
        }
        return mapOfPieceMoves;
    }

    Map<int[], List<int[]>> getAllPiecesLegalMoves() {
        Map<int[], List<int[]>> mapOfLegalMoves = new HashMap<>();
        Map<int[], List<int[]>> mapOfPieceMoves = getAllPiecesPossibleMoves();
        for (Map.Entry<int[], List<int[]>> entry : mapOfPieceMoves.entrySet()) {
            int[] currentCoordinates = entry.getKey();
            int currentRowOfPiece = currentCoordinates[0];
            int currentColumnOfPiece = currentCoordinates[1];
            Piece currentPiece = boardLayout[currentCoordinates[0]][currentCoordinates[1]];
            List<int[]> possibleMovesWithoutCheck = new ArrayList<>();
            List<int[]> possibleMoves = entry.getValue();
//            System.out.println(currentPiece);
//
//            System.out.print(currentRowOfPiece + "," + currentColumnOfPiece + ": ");
//            for (int[] currentMove : possibleMoves) {
//                System.out.print("(" + currentMove[0] + "," + currentMove[1] + "),  ");
//            }
//            System.out.println();
            for (int[] currentMove : possibleMoves) {
                int rowToMoveTo = currentMove[0];
                int columnToMoveTo = currentMove[1];

                Board newBoard = new Board(getOppositeTurnPlayerColor(turnPlayerColor), whiteKingPosition,
                        blackKingPosition);
                if (currentPiece.pieceType == Piece.PieceType.KING) {
                    if (turnPlayerColor == Piece.Color.WHITE) {
                        newBoard.whiteKingPosition = currentMove;
                    } else {
                        newBoard.blackKingPosition = currentMove;
                    }
                }

                for (int newBoardRow = 0; newBoardRow < 8; newBoardRow++) {
                    for (int newBoardColumn = 0; newBoardColumn < 8; newBoardColumn++) {
                        if (newBoardRow == rowToMoveTo && newBoardColumn == columnToMoveTo) {
                            newBoard.boardLayout[newBoardRow][newBoardColumn] = boardLayout[currentRowOfPiece][currentColumnOfPiece];
                        } else if (newBoardRow == currentRowOfPiece && newBoardColumn == currentColumnOfPiece) {
                            newBoard.boardLayout[newBoardRow][newBoardColumn] = null;
                        } else {
                            newBoard.boardLayout[newBoardRow][newBoardColumn] = boardLayout[newBoardRow][newBoardColumn];
                        }
                    }
                }
                newBoard.turnPlayerColor = turnPlayerColor;
                if (!newBoard.isKingInCheck()) {
                    possibleMovesWithoutCheck.add(currentMove);

                    for (Piece[] i : newBoard.boardLayout) {
                        for (Piece j : i) {
                            System.out.print(j);
                            System.out.print(", ");
                        }
                        System.out.println();
                    }
                }
            }
            mapOfLegalMoves.put(new int[]{currentRowOfPiece, currentColumnOfPiece}, possibleMovesWithoutCheck);
        }
        return mapOfLegalMoves;
    }

//        for (int i = 0; i < boardLayout.length; i++) {
//            for (int j = 0; j < boardLayout[i].length; j++) {
//                if (boardLayout[i][j] != null && boardLayout[i][j].color == turnPlayerColor) {
//                    currentPiece = boardLayout[i][j];
//                    List<int[]> possibleMoves = currentPiece.pieceType.getPossibleMoves(i, j, this,
//                            currentPiece.hasMoved);
//                    for (int[] move : possibleMoves) {
//                        rowToMove = move[0];
//                        columnToMove = move[1];
//                        newBoard = new Board();
//                        if (turnPlayerColor == Piece.Color.BLACK) {
//                            newBoard.turnPlayerColor = Piece.Color.WHITE;
//                        } else {
//                            newBoard.turnPlayerColor = Piece.Color.BLACK;
//                        }
//
////                        System.out.println("");
//                        for (int iSecond = 0; iSecond < newBoard.boardLayout.length; iSecond++) {
////                            System.out.println("*");
//                            for (int jSecond = 0; jSecond < newBoard.boardLayout[i].length; jSecond++) {
//                                if (iSecond == rowToMove && jSecond == columnToMove) {
//                                    newBoard.boardLayout[iSecond][jSecond] = boardLayout[i][j];
//                                } else if (iSecond == i || jSecond == j) {
//                                    newBoard.boardLayout[iSecond][jSecond] = null;
//                                } else {
//                                    newBoard.boardLayout[iSecond][iSecond] = boardLayout[i][j];
//                                }
//                            }
//                        }
//                        if (!newBoard.isKingInCheck()) {
//                            possibleMovesWithoutCheck.add(move);
//                        }
//                    }
//                    mapOfPieceMoves.put(new int[]{i, j}, possibleMovesWithoutCheck);
//                }
//            }
//        }
//        System.out.println(mapOfPieceMoves.size());
//        return mapOfPieceMoves;
//    }

    private boolean isKingInCheck() {
        int[] kingPosition;
        Piece currentPiece;

        if (turnPlayerColor == Piece.Color.WHITE) {
            kingPosition = whiteKingPosition;
        } else {
            kingPosition = blackKingPosition;
        }
        turnPlayerColor = getOppositeTurnPlayerColor(turnPlayerColor);

        for (int i = 0; i < boardLayout.length; i++) {
            for (int j = 0; j < boardLayout[i].length; j++) {
                if (boardLayout[i][j] != null && boardLayout[i][j].color == turnPlayerColor) {
                    currentPiece = boardLayout[i][j];
                    List<int[]> possibleMoves = currentPiece.pieceType.getPossibleMoves(i, j, this,
                            currentPiece.hasMoved);

                    for (int[] currentMove : possibleMoves) {
                        if (Arrays.equals(kingPosition, currentMove)) {
                            turnPlayerColor = getOppositeTurnPlayerColor(turnPlayerColor);
                            return true;
                        }
                    }
                }
            }
        }
        turnPlayerColor = getOppositeTurnPlayerColor(turnPlayerColor);
        return false;
    }

    private boolean isCheckmate() {
        int kingRow;
        int kingColumn;
        Piece playerKing;

        if (!isKingInCheck()) {
            return false;
        }

        Map<int[], List<int[]>> allLegalPossibleMoves = getAllPiecesLegalMoves();
        return allLegalPossibleMoves.isEmpty();
    }

    private void addMoveIfLegal(int rowToCheck, int columnToCheck, List<int[]> possibleMoves) {
        if (rowToCheck < 0 || rowToCheck > 7 || columnToCheck < 0 || columnToCheck > 7) {
            return;
        }
        if (boardLayout[rowToCheck][columnToCheck] == null ||
                boardLayout[rowToCheck][columnToCheck].color != turnPlayerColor) {
            possibleMoves.add(new int[]{rowToCheck, columnToCheck});
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
        if (playerKing.hasMoved || isKingInCheck()) {
            return false;
        }

        Piece leftCornerSquare = boardLayout[kingRow][0];
        if (leftCornerSquare == null || leftCornerSquare.pieceType != Piece.PieceType.ROOK ||
                leftCornerSquare.hasMoved || leftCornerSquare.color != turnPlayerColor) {
            return false;
        }

        for (int i = 1; i < 3; i++) {
            if (boardLayout[kingRow][kingColumn - i] != null) { //|| isSquareUnderAttack(kingRow, kingColumn - i)) {
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
        if (playerKing.hasMoved || isKingInCheck()) {
            return false;
        }

        Piece rightCornerSquare = boardLayout[kingRow][7];
        if (rightCornerSquare == null || rightCornerSquare.pieceType != Piece.PieceType.ROOK ||
                rightCornerSquare.hasMoved || rightCornerSquare.color != turnPlayerColor) {
            return false;
        }

        for (int i = 1; i < 3; i++) {
            if (boardLayout[kingRow][kingColumn + i] != null) { //|| isSquareUnderAttack(kingRow, kingColumn - i)) {
                return false;
            }
        }
        return true;
    }


    static Piece.Color getOppositeTurnPlayerColor(Piece.Color currentTurnPlayerColor) {
        if (currentTurnPlayerColor == Piece.Color.WHITE) {
            return Piece.Color.BLACK;
        } else {
            return Piece.Color.WHITE;
        }
    }

    private void emptyBoardLayout() {
        boardLayout = new Piece[8][8];
    }

    void initializeBoardLayout() {
        emptyBoardLayout();
        turnPlayerColor = Piece.Color.WHITE;
        for (int i = 0; i <= 7; i++) {
            boardLayout[1][i] = new Piece(Piece.Color.BLACK, Piece.PieceType.PAWN, false);
            boardLayout[6][i] = new Piece(Piece.Color.WHITE, Piece.PieceType.PAWN, false);
        }
        boardLayout[0][0] = boardLayout[0][7] = new Piece(Piece.Color.BLACK, Piece.PieceType.ROOK, false);
        boardLayout[7][0] = boardLayout[7][7] = new Piece(Piece.Color.WHITE, Piece.PieceType.ROOK, false);
        boardLayout[0][1] = boardLayout[0][6] = new Piece(Piece.Color.BLACK, Piece.PieceType.KNIGHT, false);
        boardLayout[7][1] = boardLayout[7][6] = new Piece(Piece.Color.WHITE, Piece.PieceType.KNIGHT, false);
        boardLayout[0][2] = boardLayout[0][5] = new Piece(Piece.Color.BLACK, Piece.PieceType.BISHOP, false);
        boardLayout[7][2] = boardLayout[7][5] = new Piece(Piece.Color.WHITE, Piece.PieceType.BISHOP, false);
        boardLayout[0][3] = new Piece(Piece.Color.BLACK, Piece.PieceType.QUEEN, false);
        boardLayout[7][3] = new Piece(Piece.Color.WHITE, Piece.PieceType.QUEEN, false);
        boardLayout[0][4] = new Piece(Piece.Color.BLACK, Piece.PieceType.KING, false);
        boardLayout[7][4] = new Piece(Piece.Color.WHITE, Piece.PieceType.KING, false);
    }
}
