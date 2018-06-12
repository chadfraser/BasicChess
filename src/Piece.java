import java.util.ArrayList;
import java.util.List;


class Piece {
    private Color color;
    private PieceType pieceType;
    private boolean hasMoved;

    public enum Color {
        WHITE("White"), BLACK("Black");

        private String titleCaseColor;
        Color(String titleCaseColor) {
            this.titleCaseColor = titleCaseColor;
        }

        @Override
        public String toString() {
            return titleCaseColor;
        }
    }

    public enum PieceType {
        PAWN("Pawn") {
            @Override
            public List<int[]> getPossibleMoves(int row, int column, Board currentBoard, boolean thisPieceHasMoved,
                                                boolean isCurrentlyTestingCheck) {
                assert (row > 0 && row < 7) : "Pawns cannot start the turn on the first or last row.";

                Piece[][] boardLayout = currentBoard.getBoardLayout();
                List<int[]> possibleMoves = new ArrayList<>();
                int targetRow;
                int enPassantTargetRow;

                // White pawns move up the board (along the row array in the negative direction) whereas black pawns
                // move down the board (along the row array in the positive direction)
                if (currentBoard.getTurnPlayerColor() == Color.WHITE) {
                    targetRow = row - 1;
                    enPassantTargetRow = row - 2;
                } else {
                    targetRow = row + 1;
                    enPassantTargetRow = row + 2;
                }

                if (boardLayout[targetRow][column] == null) {
                    possibleMoves.add(new int[]{targetRow, column});
                    if (!thisPieceHasMoved && boardLayout[enPassantTargetRow][column] == null) {
                        possibleMoves.add(new int[]{enPassantTargetRow, column});
                    }
                }
                if (column > 0 && boardLayout[targetRow][column - 1] != null &&
                        boardLayout[targetRow][column - 1].color != currentBoard.getTurnPlayerColor()) {
                    possibleMoves.add(new int[]{targetRow, column - 1});
                }
                if (column < 7 && boardLayout[targetRow][column + 1] != null &&
                        boardLayout[targetRow][column + 1].color != currentBoard.getTurnPlayerColor()) {
                    possibleMoves.add(new int[]{targetRow, column + 1});
                }
                return possibleMoves;
            }
        },

        ROOK("Rook") {
            @Override
            public List<int[]> getPossibleMoves(int row, int column, Board currentBoard, boolean thisPieceHasMoved,
                                                boolean isCurrentlyTestingCheck) {
                return currentBoard.getOrthogonalMoves(row, column);
            }
        },

        KNIGHT("Knight") {
            @Override
            public List<int[]> getPossibleMoves(int row, int column, Board currentBoard, boolean thisPieceHasMoved,
                                                boolean isCurrentlyTestingCheck)  {
                Piece[][] boardLayout = currentBoard.getBoardLayout();
                List<int[]> possibleMoves = new ArrayList<>();
                int[][] allMoves = {{row - 1, column - 2}, {row - 1, column + 2},
                        {row - 2, column - 1}, {row - 2, column + 1},
                        {row + 1, column - 2}, {row + 1, column + 2},
                        {row + 2, column - 1}, {row + 2, column + 1}};

                for (int[] i : allMoves) {
                    if ((0 <= i[0] && i[0] <= 7) && (0 <= i[1] && i[1] <= 7) &&
                            (boardLayout[i[0]][i[1]] == null ||
                                    boardLayout[i[0]][i[1]].color != currentBoard.getTurnPlayerColor())) {
                        possibleMoves.add(i);
                    }
                }
                return possibleMoves;
            }
        },

        BISHOP("Bishop") {
            @Override
            public List<int[]> getPossibleMoves(int row, int column, Board currentBoard, boolean thisPieceHasMoved,
                                                boolean isCurrentlyTestingCheck) {
                return currentBoard.getDiagonalMoves(row, column);
            }
        },

        QUEEN("Queen") {
            @Override
            public List<int[]> getPossibleMoves(int row, int column, Board currentBoard, boolean thisPieceHasMoved,
                                                boolean isCurrentlyTestingCheck)  {
                List<int[]> possibleMoves = currentBoard.getDiagonalMoves(row, column);
                possibleMoves.addAll(currentBoard.getOrthogonalMoves(row, column));
                return possibleMoves;
            }
        },

        KING("King") {
            @Override
            public List<int[]> getPossibleMoves(int row, int column, Board currentBoard, boolean thisPieceHasMoved,
                                                boolean isCurrentlyTestingCheck)  {
                assert ((currentBoard.getTurnPlayerColor() == Color.WHITE &&
                        currentBoard.getWhiteKingPosition() == new int[]{row,column}) ||
                        (currentBoard.getTurnPlayerColor() == Color.BLACK &&
                                currentBoard.getBlackKingPosition() == new int[]{row,column})) :
                        "The king's coordinates have been corrupted.";

                Piece[][] boardLayout = currentBoard.getBoardLayout();
                List<int[]> possibleMoves = new ArrayList<>();
                int[][] allMoves = {{row - 1, column - 1}, {row - 1, column},
                        {row - 1, column + 1}, {row, column - 1},
                        {row, column + 1}, {row + 1, column - 1},
                        {row + 1, column}, {row + 1, column + 1}};

                for (int[] i : allMoves) {
                    if ((0 <= i[0] && i[0] <= 7) && (0 <= i[1] && i[1] <= 7) &&
                            (boardLayout[i[0]][i[1]] == null ||
                                    boardLayout[i[0]][i[1]].color != currentBoard.getTurnPlayerColor())) {
                        possibleMoves.add(i);
                    }
                }
                // To prevent infinite recursion, we do not check if the king can castle when testing if any of the
                // opponent's moves intersect with that of the turn player's king
                if (!isCurrentlyTestingCheck) {
                    if (currentBoard.canCastleLeft()) {
                        possibleMoves.add(new int[]{row, column - 2});
                    }
                    if (currentBoard.canCastleRight()) {
                        possibleMoves.add(new int[]{row, column + 2});
                    }
                }
                return possibleMoves;
            }
        };

        public abstract List<int[]> getPossibleMoves(int row, int column, Board currentBoard,
                                                     boolean thisPieceHasMoved, boolean isCurrentlyTestingCheck);

        private String chessNotationSymbol;
        PieceType(String chessNotationSymbol) {
            this.chessNotationSymbol = chessNotationSymbol;
        }

        @Override
        public String toString() {
            return chessNotationSymbol;
        }
    }

    Piece(Color color, PieceType piece, boolean hasMoved) {
        this.color = color;
        this.pieceType = piece;
        this.hasMoved = hasMoved;
    }

    @Override
    public String toString() {
        return color + " " + pieceType;
    }


    Color getColor() {
        return color;
    }

    void setColor(Color color) {
        this.color = color;
    }

    PieceType getPieceType() {
        return pieceType;
    }

    void setPieceType(PieceType pieceType) {
        this.pieceType = pieceType;
    }

    boolean getHasMoved() {
        return hasMoved;
    }

    void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
}