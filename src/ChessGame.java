import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ChessGame {
    static boolean checkmate;
    static boolean stalemate;

    ChessGame() {
        Board gameBoard = new Board();

        gameBoard.initializeBoardLayout();

//        while (!checkmate && !stalemate) {
        gameBoard.printBoardLayout();

        Map<int[], List<Board>> allMoves = gameBoard.getAllLegalBoardStates();
        System.out.println("\n\n\n\n");
        for (Map.Entry<int[], List<Board>> entry : allMoves.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                System.out.println(entry.getKey()[0] + "," + entry.getKey()[1] + ":  ");

                for (Board currentMove : entry.getValue()) {
                    currentMove.printBoardLayout();
                    System.out.println();
                }
                System.out.println();
            }
        }
    }


//        for (int j = 0; j <= 7; j++) {
//            for (int i = 0; i <= 7; i++) {
//                try {
//                    Piece.PieceType currentPiece = gameBoard.boardLayout[j][i].pieceType;
//                    Piece.Color currentColor = gameBoard.boardLayout[j][i].color;
//                    System.out.print(gameBoard.boardLayout[j][i] + "'s moves  (" + j + "," + i + "):  ");
//                    List<int[]> moveArray = currentPiece.getPossibleMoves(j, i, gameBoard, false);
//                    for (int inn = 0; inn < moveArray.size(); inn++) {
//                        System.out.print(Arrays.toString(moveArray.get(inn)) + "  ");
//                    }
//                    System.out.println();
////                    System.out.println(Arrays.toString(currentPiece.getPossibleMoves(i, j, gameBoard.boardLayout, false,
////                            currentColor).toArray()) + "AAAAAAA");
//                    } catch (NullPointerException e) {
//
//                }
//            }
//        }

//        while (true) {
//            int index1 = Integer.parseInt(input.nextLine());
//            int index2 = Integer.parseInt(input.nextLine());
//            if (index2 == -1 || index2 == -1) {
//                break;
//            }
//            try {
//                Piece.PieceType currentPiece = gameBoard.boardLayout[index1][index2].pieceType;
//                Piece.Color currentColor = gameBoard.boardLayout[index1][index2].color;
//                System.out.print(gameBoard.boardLayout[index1][index2] + "'s moves:  ");
//                List<int[]> moveArray = currentPiece.getPossibleMoves(index1, index2, gameBoard, false);
//                for (int[] i : moveArray) {
//                    System.out.println(Arrays.toString(i));
//                }
//            } catch (NullPointerException e) {
//            }
//        }
////        break;
////        }
//    }

    public static void main(String[] args) {
        ChessGame game = new ChessGame();
    }
}
