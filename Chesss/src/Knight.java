import javax.swing.*;
import java.util.ArrayList;

/*
1) Class Constructors
2) Overridden Methods
*/

public class Knight extends Piece{

    private ImageIcon icon;

    //________________________________________________Class Constructors________________________________________________

    public Knight(COLOUR colour, Coordinate OGcoord) {
        super(ID.KNIGHT, colour, OGcoord);
        if (getColour() == COLOUR.B)
            icon = new ImageIcon("BKnight.png");
        else if (getColour() == COLOUR.W)
            icon = new ImageIcon("WKnight.png");
    }

    public Knight(Knight original) {
        super(original);
    }

    //________________________________________________Overridden Methods________________________________________________

    @Override
    public Knight makeCopy() {
        return new Knight(this);
    }

    /**
     * Выводит список массивов, содержащий все исходные ходы, доступные коню на данной доске
     * @param pieces  разбивает доску на фигуры, на которых ведется игра
     * @return список массивов, содержащий все координаты, полученные из класса Move (все ходы коня).
     */

    @Override
    public ArrayList<Coordinate> getRawMoves(Pieces pieces) {
        ArrayList<Coordinate> front = Move.frontKnight(pieces,this);
        ArrayList<Coordinate> right = Move.backKnight(pieces,this);
        ArrayList<Coordinate> back = Move.rightKnight(pieces,this);
        ArrayList<Coordinate> left = Move.leftKnight(pieces,this);

        front.addAll(right);
        back.addAll(left);
        front.addAll(back);

        return front;
    }

    @Override
    public ImageIcon getImageIcon() {
        return icon;
    }

}
