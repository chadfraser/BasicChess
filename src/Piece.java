import java.util.ArrayList;
import java.util.List;


class Piece {
    private Color color;
    private PieceType pieceType;

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
        PAWN("P") {
            @Override
            public List<int[]> getPossibleMoves(int row, int column, Board currentBoard,
                                                boolean isCurrentlyTestingCheck) {
                assert (row > 0 && row < Board.MAX_ROWS - 1) : "Pawns cannot start the turn on the first or last row.";

                Piece[][] boardLayout = currentBoard.getBoardLayout();
                List<int[]> possibleMoves = new ArrayList<>();
                int targetRow;

                // White pawns move up the board (along the row array in the negative direction) whereas black pawns
                // move down the board (along the row array in the positive direction)
                if (currentBoard.getTurnPlayerColor() == Color.WHITE) {
                    targetRow = row - 1;
                } else {
                    targetRow = row + 1;
                }

                if (boardLayout[targetRow][column] == null) {
                    possibleMoves.add(new int[]{targetRow, column});
                }
                if (canCaptureEnPassant(row, column - 1, currentBoard) ||
                        (column > 0 && boardLayout[targetRow][column - 1] != null &&
                        boardLayout[targetRow][column - 1].color != currentBoard.getTurnPlayerColor())) {
                    possibleMoves.add(new int[]{targetRow, column - 1});
                }
                if (canCaptureEnPassant(row, column + 1, currentBoard) ||
                        (column < Board.MAX_COLUMNS - 1 && boardLayout[targetRow][column + 1] != null &&
                                boardLayout[targetRow][column + 1].color != currentBoard.getTurnPlayerColor())) {
                    possibleMoves.add(new int[]{targetRow, column + 1});
                }
                return possibleMoves;
            }

            boolean canCaptureEnPassant(int currentRow, int targetColumn, Board currentBoard) {
                int enPassantRow;
                boolean canCaptureEnPassant;

                if (currentBoard.getTurnPlayerColor() == Piece.Color.WHITE) {
                    enPassantRow = currentRow - 2;
                } else {
                    enPassantRow = currentRow + 2;
                }

                try {
                    canCaptureEnPassant = (targetColumn >= 0 && targetColumn < Board.MAX_COLUMNS &&
                            checkPreviousBoardForEnPassantCapture(currentRow, targetColumn, enPassantRow,
                                    currentBoard.getPreviousBoard()) &&
                            checkCurrentBoardForEnPassantCapture(currentRow, targetColumn, enPassantRow, currentBoard));
                } catch (ArrayIndexOutOfBoundsException e) {
                    return false;
                }
                return canCaptureEnPassant;
            }

            private boolean checkPreviousBoardForEnPassantCapture(int currentRow, int targetColumn, int enPassantRow,
                                                                  Board previousBoard) {
                Piece[][] previousBoardLayout = previousBoard.getBoardLayout();
                return (previousBoardLayout[enPassantRow][targetColumn] != null &&
                        previousBoardLayout[enPassantRow][targetColumn].getPieceType() == Piece.PieceType.PAWN_UNMOVED &&
                        (previousBoardLayout[currentRow][targetColumn] == null ||
                            previousBoardLayout[currentRow][targetColumn].getColor() == previousBoard.getTurnPlayerColor()));
            }

            private boolean checkCurrentBoardForEnPassantCapture(int currentRow, int targetColumn, int enPassantRow,
                                                                 Board currentBoard) {
                Piece[][] boardLayout = currentBoard.getBoardLayout();
                return (boardLayout[enPassantRow][targetColumn] == null &&
                        boardLayout[currentRow][targetColumn] != null &&
                        boardLayout[currentRow][targetColumn].getPieceType() == Piece.PieceType.PAWN &&
                        boardLayout[currentRow][targetColumn].getColor() != currentBoard.getTurnPlayerColor());
            }
        },


        PAWN_UNMOVED("p") {
            @Override
            public List<int[]> getPossibleMoves(int row, int column, Board currentBoard,
                                                boolean isCurrentlyTestingCheck) {
                Piece[][] boardLayout = currentBoard.getBoardLayout();
                int enPassantTargetRow;

                List<int[]> possibleMoves = PAWN.getPossibleMoves(row, column, currentBoard, isCurrentlyTestingCheck);
                if (currentBoard.getTurnPlayerColor() == Color.WHITE) {
                    enPassantTargetRow = row - 2;
                } else {
                    enPassantTargetRow = row + 2;
                }

                if (boardLayout[enPassantTargetRow][column] == null) {
                    possibleMoves.add(new int[]{enPassantTargetRow, column});
                }
                return possibleMoves;
            }
        },

        ROOK("R") {
            @Override
            public List<int[]> getPossibleMoves(int row, int column, Board currentBoard,
                                                boolean isCurrentlyTestingCheck) {
                return currentBoard.getOrthogonalMoves(row, column);
            }
        },

        ROOK_UNMOVED("R") {
            @Override
            public List<int[]> getPossibleMoves(int row, int column, Board currentBoard,
                                                boolean isCurrentlyTestingCheck) {
                return ROOK.getPossibleMoves(row, column, currentBoard, isCurrentlyTestingCheck);
            }
        },

        KNIGHT("N") {
            @Override
            public List<int[]> getPossibleMoves(int row, int column, Board currentBoard,
                                                boolean isCurrentlyTestingCheck) {
                Piece[][] boardLayout = currentBoard.getBoardLayout();
                List<int[]> possibleMoves = new ArrayList<>();
                int[][] allMoves = {{row - 1, column - 2}, {row - 1, column + 2},
                        {row - 2, column - 1}, {row - 2, column + 1},
                        {row + 1, column - 2}, {row + 1, column + 2},
                        {row + 2, column - 1}, {row + 2, column + 1}};

                for (int[] i : allMoves) {
                    if ((0 <= i[0] && i[0] < Board.MAX_ROWS) && (0 <= i[1] && i[1] < Board.MAX_COLUMNS) &&
                            (boardLayout[i[0]][i[1]] == null ||
                                    boardLayout[i[0]][i[1]].color != currentBoard.getTurnPlayerColor())) {
                        possibleMoves.add(i);
                    }
                }
                return possibleMoves;
            }
        },

        BISHOP("B") {
            @Override
            public List<int[]> getPossibleMoves(int row, int column, Board currentBoard,
                                                boolean isCurrentlyTestingCheck) {
                return currentBoard.getDiagonalMoves(row, column);
            }
        },

        QUEEN("Q") {
            @Override
            public List<int[]> getPossibleMoves(int row, int column, Board currentBoard,
                                                boolean isCurrentlyTestingCheck) {
                List<int[]> possibleMoves = currentBoard.getDiagonalMoves(row, column);
                possibleMoves.addAll(currentBoard.getOrthogonalMoves(row, column));
                return possibleMoves;
            }
        },

        KING("K") {
            @Override
            public List<int[]> getPossibleMoves(int row, int column, Board currentBoard,
                                                boolean isCurrentlyTestingCheck) {
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
                    if ((0 <= i[0] && i[0] < Board.MAX_ROWS) && (0 <= i[1] && i[1] < Board.MAX_COLUMNS) &&
                            (boardLayout[i[0]][i[1]] == null ||
                                    boardLayout[i[0]][i[1]].color != currentBoard.getTurnPlayerColor())) {
                        possibleMoves.add(i);
                    }
                }
                return possibleMoves;
            }
        },

        KING_UNMOVED("K") {
            @Override
            public List<int[]> getPossibleMoves(int row, int column, Board currentBoard,
                                                boolean isCurrentlyTestingCheck) {
                List<int[]> possibleMoves = KING.getPossibleMoves(row, column, currentBoard, isCurrentlyTestingCheck);

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
                                                     boolean isCurrentlyTestingCheck);

        private String chessNotationSymbol;
        PieceType(String chessNotationSymbol) {
            this.chessNotationSymbol = chessNotationSymbol;
        }

        @Override
        public String toString() {
            return chessNotationSymbol;
        }
    }

    Piece(Color color, PieceType piece) {
        this.color = color;
        this.pieceType = piece;
    }

    @Override
    public String toString() {
        return "[" + color.toString().charAt(0) + pieceType + "]";
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
}