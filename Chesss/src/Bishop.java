import javax.swing.*;
import java.util.ArrayList;



public class Bishop extends Piece{

    private ImageIcon icon;

    //________________________________________________Class Constructors________________________________________________

    public Bishop(COLOUR colour, Coordinate OGcoord) {
        super(ID.BISHOP, colour, OGcoord);
        if (getColour() == COLOUR.B)
            icon = new ImageIcon("BBishop.png");
        else if (getColour() == COLOUR.W)
            icon = new ImageIcon("WBishop.png");
    }

    public Bishop(Bishop original) {
        super(original);
    }

    //________________________________________________Overridden Methods________________________________________________

    @Override
    public Bishop makeCopy() {
        return new Bishop(this);
    }


    @Override
    public ArrayList<Coordinate> getRawMoves(Pieces pieces) {
        ArrayList<Coordinate> frontRDig = Move.frontRDigFree(pieces, this,dimension);
        ArrayList<Coordinate> backRDig = Move.backRDigFree(pieces, this, dimension);
        ArrayList<Coordinate> backLDig = Move.backLDigFree(pieces, this,dimension);
        ArrayList<Coordinate> frontLDig = Move.frontLDigFree(pieces, this, dimension);

        frontRDig.addAll(backRDig);
        backLDig.addAll(frontLDig);
        frontRDig.addAll(backLDig);

        return frontRDig;
    }

    @Override
    public ImageIcon getImageIcon() {
        return icon;
    }




}
