import java.util.ArrayList;
import java.util.List;


class Piece {
    Color color;
    PieceType pieceType;
    boolean hasMoved;

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
            public List<int[]> getPossibleMoves(int row, int column, Board currentBoard, boolean thisPieceHasMoved) {
                assert (row > 0 && row < 7) : "Pawns cannot start the turn on the first or last row.";

                Piece[][] boardLayout = currentBoard.boardLayout;
                List<int[]> possibleMoves = new ArrayList<>();
                int rowToMoveTo;
                int rowToMoveToEnPassant;

                if (currentBoard.turnPlayerColor == Color.WHITE) {
                    rowToMoveTo = row - 1;
                    rowToMoveToEnPassant = row - 2;
                } else {
                    rowToMoveTo = row + 1;
                    rowToMoveToEnPassant = row + 2;
                }

                if (boardLayout[rowToMoveTo][column] == null) {
                    possibleMoves.add(new int[]{rowToMoveTo, column});
                    if (!thisPieceHasMoved && boardLayout[rowToMoveToEnPassant][column] == null) {
                        possibleMoves.add(new int[]{rowToMoveToEnPassant, column});
                    }
                }
                if (column > 0 && boardLayout[rowToMoveTo][column - 1] != null &&
                        boardLayout[rowToMoveTo][column - 1].color != currentBoard.turnPlayerColor) {
                    possibleMoves.add(new int[]{rowToMoveTo, column - 1});
                }
                if (column < 7 && boardLayout[rowToMoveTo][column + 1] != null &&
                        boardLayout[rowToMoveTo][column + 1].color != currentBoard.turnPlayerColor) {
                    possibleMoves.add(new int[]{rowToMoveTo, column + 1});
                }
                return possibleMoves;
            }
        },

        ROOK("Rook") {
            @Override
            public List<int[]> getPossibleMoves(int row, int column, Board currentBoard, boolean thisPieceHasMoved) {
                return currentBoard.getOrthogonalMoves(row, column);
            }
        },

        KNIGHT("Knight") {
            @Override
            public List<int[]> getPossibleMoves(int row, int column, Board currentBoard, boolean thisPieceHasMoved) {
                Piece[][] boardLayout = currentBoard.boardLayout;
                List<int[]> possibleMoves = new ArrayList<>();
                int[][] allMoves = {{row - 1, column - 2}, {row - 1, column + 2},
                        {row - 2, column - 1}, {row - 2, column + 1},
                        {row + 1, column - 2}, {row + 1, column + 2},
                        {row + 2, column - 1}, {row + 2, column + 1}};

                for (int[] i : allMoves) {
                    if ((0 <= i[0] && i[0] <= 7) && (0 <= i[1] && i[1] <= 7) &&
                            (boardLayout[i[0]][i[1]] == null ||
                                    boardLayout[i[0]][i[1]].color != currentBoard.turnPlayerColor)) {
                        possibleMoves.add(i);
                    }
                }
                return possibleMoves;
            }
        },

        BISHOP("Bishop") {
            @Override
            public List<int[]> getPossibleMoves(int row, int column, Board currentBoard, boolean thisPieceHasMoved) {
                return currentBoard.getDiagonalMoves(row, column);
            }
        },

        QUEEN("Queen") {
            @Override
            public List<int[]> getPossibleMoves(int row, int column, Board currentBoard, boolean thisPieceHasMoved) {
                List<int[]> possibleMoves = currentBoard.getDiagonalMoves(row, column);
                possibleMoves.addAll(currentBoard.getOrthogonalMoves(row, column));
                return possibleMoves;
            }
        },

        KING("King") {
            @Override
            public List<int[]> getPossibleMoves(int row, int column, Board currentBoard, boolean thisPieceHasMoved) {
                assert ((currentBoard.turnPlayerColor == Color.WHITE &&
                        currentBoard.whiteKingPosition == new int[]{row,column}) ||
                        (currentBoard.turnPlayerColor == Color.BLACK &&
                                currentBoard.blackKingPosition == new int[]{row,column})) :
                        "The king's coordinates have been corrupted.";

                Piece[][] boardLayout = currentBoard.boardLayout;
                List<int[]> possibleMoves = new ArrayList<>();
                int[][] allMoves = {{row - 1, column - 1}, {row - 1, column},
                        {row - 1, column + 1}, {row, column - 1},
                        {row, column + 1}, {row + 1, column - 1},
                        {row + 1, column}, {row + 1, column + 1}};

                for (int[] i : allMoves) {
                    if ((0 <= i[0] && i[0] <= 7) && (0 <= i[1] && i[1] <= 7) &&
                            (boardLayout[i[0]][i[1]] == null ||
                                    boardLayout[i[0]][i[1]].color != currentBoard.turnPlayerColor)) {
                        possibleMoves.add(i);
                    }
                }

//                if (currentBoard.canCastleLeft()) {
//                    possibleMoves.add(new int[]{row, column - 2});
//                }
//                if (currentBoard.canCastleRight()) {
//                    possibleMoves.add(new int[]{row, column + 2});
//                }

                return possibleMoves;
            }
        };

        public abstract List<int[]> getPossibleMoves(int row, int column, Board currentBoard,
                                                     boolean thisPieceHasMoved);

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

}