import javax.swing.*;
import java.util.*;

/*
1) Class Constructors
2) Getters & Setters
3) Castling Validation Methods
4) Overridden Methods
*/

public class King extends Piece{

    // the coordinate to which the king goes after castling King side
    private Coordinate castleCoordKingK;
    // the coordinate to whcih the king goes after castling Queen side
    private Coordinate castleCoordKingQ;
    // the coordinate through which the king goes when castling King side
    private Coordinate transitionCoordKingK;
    // the coordinate through which the king goes when castling Queen side
    private Coordinate transitionCoordKingQ;
    // the rook on the King side
    private Rook rookKing;
    // the rook on the Queen side
    private Rook rookQueen;
    private ImageIcon icon;

    //________________________________________________Class Constructors________________________________________________

    public King(COLOUR colour, Coordinate OGcoord) {
        super(ID.KING, colour, OGcoord);
        if (getColour() == COLOUR.B)
            icon = new ImageIcon("BKing.png");
        else if (getColour() == COLOUR.W)
            icon = new ImageIcon("WKing.png");
    }

    public King(King original) {
        super(original);
    }

    //________________________________________________Getters & Setters________________________________________________

    public Coordinate getCastleCoordKingK() {
        return castleCoordKingK;
    }

    public Coordinate getCastleCoordKingQ() {
        return castleCoordKingQ;
    }

    public Coordinate getTransitionCoordKingK() {
        return transitionCoordKingK;
    }

    public Coordinate getTransitionCoordKingQ() {
        return transitionCoordKingQ;
    }

    public Rook getRookKing() {
        return rookKing;
    }

    public Rook getRookQueen() {
        return rookQueen;
    }

    //________________________________________________Castling Validation Methods________________________________________________

    /**
     * Определяет, может ли король поставить шах на сторону короля (короткая рокировка)
     * @param pieces фигуры на доске, на которой ведется игра
     * @return значение true тогда и только тогда, когда:
     * король не находится под контролем
     * на стороне короля находится ладья (h1 у белых, h8 у черных)
     * между королем и ладьей нет фигур
     * ни ладья, ни король не сдвинулись с места
     * Если король может делать замки, то координата, по которой король отправляется в замок, добавляется в castleCoordKingk.
     * Он устанавливает соответствующую координату для ладьи на стороне короля.
     * Это также сохраняет координаты, по которым проходит король при рокировке.
     * Это используется в "removeOwnCheck", чтобы убедиться, что рокировка возможна
     * ("Никто не может выполнить рокировку вне, через или в шахматном порядке").
     */


    public boolean canCastleKing (Pieces pieces) {

        if (pieces.isCheck(getColour()))
            return false;

        HashMap<Coordinate, Piece> colouredPieces = pieces.getColourPieces(getColour());

        for (Piece value : colouredPieces.values()) {
            if (value.getName() == ID.ROOK && value.getFile() == BOARD.LAST_FILE.getFileVal())
                rookKing = (Rook) value;
        }

        int distanceRookKing = 2;
        ArrayList<Coordinate> castleCoords;

        if (getColour() == COLOUR.B)
            castleCoords = Move.leftFree(pieces, this, dimension);
        else
            castleCoords = Move.rightFree(pieces, this, dimension);

        boolean isSpace = castleCoords.size() == distanceRookKing;

        boolean canCastle = rookKing != null &&
                !rookKing.getHasMoved() &&
                !getHasMoved() &&
                isSpace;

        if (canCastle) {
            castleCoordKingK = castleCoords.get(1);
            transitionCoordKingK = castleCoords.get(0);
            rookKing.setCastleCoordRook(castleCoords.get(0));
            return true;
        }
        return false;
    }

    /**
     * Определяет, может ли король взять под свой контроль ферзя (длительная рокировка)
     * @param pieces фигуры на игровом поле
     * @return значение true тогда и только тогда, когда:
     * король не находится под контролем
     * на стороне ферзя находится ладья (a1 у белых, a8 у черных)
     * между королем и ладьей нет фигур
     * ни ладья, ни король не сдвинулись с места
     * Если король может сделать рокировку, то координата, по которой король переходит в касл, добавляется в castleCoordKingQ
     * Она устанавливает соответствующую координату для ладьи на стороне ферзя
     * Также сохраняется координата, по которой король переходит при рокировке.
     * Это используется в "removeOwnCheck", чтобы убедиться, что рокировка возможна
     * ("Никто не может выполнить рокировку вне, через или в шахматном порядке").
     */


    public boolean canCastleQueen (Pieces pieces) {

        if (pieces.isCheck(getColour()))
            return false;

        HashMap<Coordinate,Piece> colouredPieces = pieces.getColourPieces(getColour());

        for (Piece value : colouredPieces.values()) {
            if (value.getName() == ID.ROOK && value.getFile() == BOARD.FIRST_FILE.getFileVal())
                rookQueen = (Rook) value;
        }

        int distanceRookQueen = 3;
        ArrayList<Coordinate> castleCoords;

        if (getColour() == COLOUR.W) {
            castleCoords = Move.leftFree(pieces, this, dimension);
        }
        else {
            castleCoords = Move.rightFree(pieces, this, dimension);
        }

        boolean isSpace = castleCoords.size() == distanceRookQueen;


        boolean canCastle = rookQueen != null &&
                !rookQueen.getHasMoved() &&
                !getHasMoved() &&
                isSpace;

        if (canCastle) {
            castleCoordKingQ = castleCoords.get(1);
            transitionCoordKingQ = castleCoords.get(0);
            rookQueen.setCastleCoordRook(castleCoords.get(0));
            return true;
        }
        return false;
    }

    //________________________________________________Overridden Methods________________________________________________

    @Override
    public King makeCopy() {
        return new King(this);
    }

    /**
     * Создает список массивов, содержащий все исходные ходы, доступные королю на данной доске
     * @parm pieces игровую доску на фигуры
     * @return список массивов, содержащий все координаты, полученные из класса ходов.
     * (все диагонали, все вертикали и все горизонтали, используя 1 в качестве ограничения).
     * Также добавляет координаты, если возможна рокировка.
     */


    @Override
    public ArrayList<Coordinate> getRawMoves(Pieces pieces) {
        //get and add all "raw" moves (reachable from position)
        ArrayList<Coordinate> front = Move.frontFree(pieces,this,single);
        ArrayList<Coordinate> right = Move.rightFree(pieces,this,single);
        ArrayList<Coordinate> back = Move.backFree(pieces,this,single);
        ArrayList<Coordinate> left = Move.leftFree(pieces,this,single);
        ArrayList<Coordinate> frontRDig = Move.frontRDigFree(pieces, this,single);
        ArrayList<Coordinate> backRDig = Move.backRDigFree(pieces, this, single);
        ArrayList<Coordinate> backLDig = Move.backLDigFree(pieces, this,single);
        ArrayList<Coordinate> frontLDig = Move.frontLDigFree(pieces, this, single);

        front.addAll(right);
        back.addAll(left);
        front.addAll(back);

        frontRDig.addAll(backRDig);
        backLDig.addAll(frontLDig);
        frontRDig.addAll(backLDig);

        front.addAll(frontRDig);

        // if can castle, add coordinates
        if (canCastleKing(pieces))
            front.add(castleCoordKingK);
        if (canCastleQueen(pieces))
            front.add(castleCoordKingQ);


        return front;
    }

    @Override
    public ImageIcon getImageIcon() {
        return icon;
    }


}