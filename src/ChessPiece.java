import java.util.ArrayList;
import java.util.List;


class ChessPiece {
    Color color;
    PieceType pieceType ;
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
            public List<int[]> getPossibleMoves(int row, int column, ChessPiece[][] board, boolean hasMoved,
                                         Color playerColor) {
                List<int[]> possibleMoves = new ArrayList<>();
                if (playerColor == Color.WHITE) {
                    if (board[row - 1][column] == null) {
                        possibleMoves.add(new int[]{row - 1, column});
                        if (!hasMoved && board[row - 2][column] == null) {
                            possibleMoves.add(new int[]{row - 2, column});
                        }
                    }
                    if (column > 0 && board[row - 1][column - 1] != null &&
                            board[row - 1][column - 1].color == Color.BLACK) {
                        possibleMoves.add(new int[]{row - 1, column - 1});
                    }
                    if (column < 7 && board[row - 1][column + 1] != null &&
                            board[row - 1][column + 1].color == Color.BLACK) {
                        possibleMoves.add(new int[]{row - 1, column + 1});
                    }
                } else {
                    if (board[row + 1][column] == null) {
                        possibleMoves.add(new int[]{row + 1, column});
                        if (!hasMoved && board[row + 2][column] == null) {
                            possibleMoves.add(new int[]{row + 2, column});
                        }
                    }
                    if (column > 0 && board[row - 1][column - 1] != null &&
                            board[row - 1][column - 1].color == Color.WHITE) {
                        possibleMoves.add(new int[]{row + 1, column - 1});
                    }
                    if (column < 7  && board[row - 1][column + 1] != null &&
                            board[row - 1][column + 1].color == Color.WHITE) {
                        possibleMoves.add(new int[]{row + 1, column + 1});
                    }
                }
                return possibleMoves;
            }
        },

        ROOK("Rook") {
            @Override
            public List<int[]> getPossibleMoves(int row, int column, ChessPiece[][] board, boolean hasMoved,
                                         Color playerColor) {
                return BoardState.getOrthogonalMoves(row, column, board, playerColor);
            }
        },

        KNIGHT("Knight") {
            @Override
            public List<int[]> getPossibleMoves(int row, int column, ChessPiece[][] board, boolean hasMoved,
                                         Color playerColor) {
                List<int[]> possibleMoves = new ArrayList<>();
                int[][] allMoves = {{row - 1, column - 2}, {row - 1, column + 2},
                        {row - 2, column - 1}, {row - 2, column + 1},
                        {row + 1, column - 2}, {row + 1, column + 2},
                        {row + 2, column - 1}, {row + 2, column + 1}};

                for (int[] i : allMoves) {
                    if ((0 <= i[0] && i[0] <= 7) && (0 <= i[1] && i[1] <= 7) &&
                            (board[i[0]][i[1]] == null || board[i[0]][i[1]].color != playerColor)) {
                        possibleMoves.add(i);
                    }
                }
                return possibleMoves;
            }
        },

        BISHOP("Bishop") {
            @Override
            public List<int[]> getPossibleMoves(int row, int column, ChessPiece[][] board, boolean hasMoved,
                                         Color playerColor) {
                return BoardState.getDiagonalMoves(row, column, board, playerColor);
            }
        },

        QUEEN("Queen") {
            @Override
            public List<int[]> getPossibleMoves(int row, int column, ChessPiece[][] board, boolean hasMoved,
                                         Color playerColor) {
                List<int[]> possibleMoves = BoardState.getDiagonalMoves(row, column, board, playerColor);
                possibleMoves.addAll(BoardState.getOrthogonalMoves(row, column, board, playerColor));
                return possibleMoves;
            }
        },

        KING("King") {
            @Override
            public List<int[]> getPossibleMoves(int row, int column, ChessPiece[][] board, boolean hasMoved,
                                         Color playerColor) {
                List<int[]> possibleMoves = new ArrayList<>();
                int[][] allMoves = {{row - 1, column - 1}, {row - 1, column},
                        {row - 1, column + 1}, {row, column - 1},
                        {row, column + 1}, {row + 1, column - 1},
                        {row + 1, column}, {row + 1, column + 1}};

                for (int[] i : allMoves) {
                    if ((0 <= i[0] && i[0] <= 7) && (0 <= i[1] && i[1] <= 7) &&
                            (board[i[0]][i[1]] == null || board[i[0]][i[1]].color != playerColor)) {
                        possibleMoves.add(i);
                    }
                }

                if (!hasMoved && BoardState.canCastleLeft(row, column, board, playerColor)) {
                    possibleMoves.add(new int[]{row, column - 2});
                }
                if (!hasMoved && BoardState.canCastleRight(row, column, board, playerColor)) {
                    possibleMoves.add(new int[]{row, column + 2});
                }

                return possibleMoves;
            }
        };

        public abstract List<int[]> getPossibleMoves(int row, int column, ChessPiece[][] board, boolean hasMoved,
                                                     Color playerColor);

        private String chessNotationSymbol;
        PieceType(String chessNotationSymbol) {
            this.chessNotationSymbol = chessNotationSymbol;
        }

        @Override
        public String toString() {
            return chessNotationSymbol;
        }
    }

    ChessPiece(Color color, PieceType piece, boolean hasMoved) {
        this.color = color;
        this.pieceType = piece;
        this.hasMoved = hasMoved;
    }

    @Override
    public String toString() {
        return color + " " + pieceType;
    }
}
