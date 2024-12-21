import javax.swing.*;
import java.util.*;

/*
1) Class Constructors
2) Getters & Setters
3) Piece Movement Methods
4) toString() Methods
5) Overridden Methods
6) Abstract Methods
*/

/**
 * Класс фигур используется для представления общей шахматной фигуры. Он выступает в качестве суперкласса для 6 различных шахматных фигур.
 * Класс фигур также используется для определения всех ходов, которые данная фигура может сделать на данной доске.
 */


public abstract class Piece{

    /**
     * имя - это идентификатор фигуры. Определяет, является ли она пешкой, конем, слоном, ладьей, ферзем или королем.
     * colour - это ЦВЕТ фигуры. Определяет, черная она или белая.
     * coords - это текущая координата, которую фигура занимает на доске
     * OGcoord - это начальная координата, которую фигура занимает при создании доски
     * pieceID - это уникальная строка, связанная с каждой фигурой, которая создается при создании фигуры
     * potentialMoves - это все возможные координаты, по которым может перемещаться фигура на определенной доске
     * dimension - это "размер" доски, который по умолчанию равен 8
     * single - это первый ранг, который по умолчанию равен 1
     * hasMoved определяет, перемещалась ли фигура хотя бы один раз в пределах данной доски
     * Пустая фигура - это фигура по умолчанию, используемая в качестве заполнителя
     */

    private final ID name;
    private final COLOUR colour;
    private Coordinate coords;
    private final Coordinate OGcoord;
    private final String pieceID; //potentially remove
    private HashSet<Coordinate> potentialMoves = new HashSet<>();
    public int dimension = BOARD.LAST_RANK.getRankVal();
    public int single = BOARD.FIRST_RANK.getRankVal();
    private boolean hasMoved = false;
    public static Piece emptyPiece = new Rook(COLOUR.W,Coordinate.emptyCoordinate);

    //________________________________________________Class Constructors________________________________________________

    public Piece (ID name, COLOUR colour, Coordinate OGcoord) {

        Objects.requireNonNull(name, "The piece must be correctly identified with an ID.");
        Objects.requireNonNull(colour, "The piece must be either white or black.");
        Objects.requireNonNull(OGcoord, "The piece must have an origin coordinate to be correctly initiallised.");

        this.name = name;
        this.colour = colour;
        this.OGcoord = OGcoord;
        coords = OGcoord;
        pieceID = "*"+name.toString()+"*"+colour.toString()+"*"+OGcoord.getFile()+"*";
    }

    public Piece (Piece original) {
        Objects.requireNonNull(original,"You can't copy a null piece");
        this.name = original.name;
        this.colour = original.colour;
        this.OGcoord = new Coordinate(original.OGcoord);
        this.coords = new Coordinate(original.coords);
        this.pieceID = original.pieceID;
        this.potentialMoves = new HashSet<>();

        for (Coordinate coord : original.getPotentialMoves()) {
            this.potentialMoves.add(new Coordinate(coord));
        }

        this.dimension = original.dimension;
        this.single = original.single;
        this.hasMoved = original.hasMoved;
    }

    //________________________________________________Getters & Setters________________________________________________

    public Coordinate getCoords() {
        return coords;
    }

    public char getFile() {
        return getCoords().getFile();
    }

    public int getRank() {
        return getCoords().getRank();
    }

    public COLOUR getColour() {
        return colour;
    }

    public ID getName() {
        return name;
    }

    public Coordinate getOGcoord() {
        return OGcoord;
    }

    public String getPieceID() {
        return pieceID;
    }

    public void setCoords(Coordinate coords) {
        this.coords = coords;
    }

    public boolean getHasMoved() {return hasMoved;}

    public void setHasMoved() {hasMoved = true;}

    public void addMoves(ArrayList<Coordinate> someMoves) {
        potentialMoves.addAll(someMoves);
    }

    public void clearMoves() {
        potentialMoves.clear();
    }

    public HashSet<Coordinate> getPotentialMoves() {
        return potentialMoves;
    }

    //________________________________________________Piece Movement Methods________________________________________________

    /**
     * При наличии набора необработанных ходов, доступных для фигуры, он удаляет те ходы (координаты), которые привели бы к проверке,
     * и, следовательно, были бы недопустимыми ходами.
     * Он использует итератор для перебора всех необработанных ходов фигуры. Затем он создает временную доску (копируя текущую)
     * и перемещает фигуру на заданный исходный ход. Если это вызывает проверку, то координата удаляется из исходных ходов.
     * Полученный ArrayList будет использоваться для обновления возможных ходов фигуры.
     * @param pieces фигуры - набор фигур, содержащий информацию о текущем игровом поле
     * @return массив координат, содержащий все допустимые ходы, которые может сделать фигура на данном игровом поле
     */


    public ArrayList<Coordinate> removeOwnCheck(Pieces pieces) {

        King potentialKing = null;
        boolean removeKingCastle = false;
        boolean removeQueenCastle = false;

        ArrayList<Coordinate> potentials = getRawMoves(pieces);

        if (potentials.size() == 0)
            return potentials;

        Iterator<Coordinate> it = potentials.iterator();

        while (it.hasNext()) {
            Coordinate nextMove = it.next();
            Pieces p = new Pieces(pieces);
            p.pieceMove(nextMove, this.makeCopy());
            Coordinate kingPosition = p.findKing(getColour());
            HashSet<Coordinate> dangerMoves = p.allColouredRaws(COLOUR.not(getColour()));
            if (dangerMoves.contains(kingPosition))
                if (this.getName() == ID.KING) { // we need to remove the possibility of castling if the King can be put in check
                    potentialKing = (King) this;
                    if (nextMove.equals(potentialKing.getTransitionCoordKingK())) {
                        it.remove();
                        removeKingCastle = true;
                    }
                    else if (nextMove.equals(potentialKing.getTransitionCoordKingQ())) {
                        it.remove();
                        removeQueenCastle = true;
                    }
                    else
                        it.remove();
                }
                else
                    it.remove();
        }
        if (potentialKing != null) {
            if (removeKingCastle)
                potentials.remove(potentialKing.getCastleCoordKingK());
            if (removeQueenCastle)
                potentials.remove(potentialKing.getCastleCoordKingQ());
        }

        return potentials;
    }

    /**
     * Обновляет возможные ходы для фигуры
     * @param pieces - список фигур, содержащий информацию о текущем игровом поле.
     */

    public void updatePotentialMoves(Pieces pieces) {addMoves(removeOwnCheck(pieces));
    }

    /**
     * Определяет, соответствует ли координата допустимому ходу для текущего экземпляра фигуры. Проверяет, соответствует ли
     * указанная координата потенциальным ходам фигуры, и удостоверяется, что фигура, которая движется, имеет цвет
     * текущего хода игры.
     * @param destination - координата, представляющая цель хода
     * @param colour - цвет, соответствующий стороне, которая должна играть
     * @return  значение true, если это текущий ход фигуры и ход находится в пределах возможных ходов
     */

    public boolean isValidMove(Coordinate destination, COLOUR colour) {
        return getPotentialMoves().contains(destination) && getColour() == colour;
    }

    //________________________________________________toString() Methods________________________________________________

    @Override
    public String toString() { //standard PNG String
        return name.toString() + coords.toString();
    }

    public String toBoardString() { // for String in the TBIBoard
        if (name == ID.PAWN)
            return "p" + colour.toSmallString();
        else
            return name.toString() + colour.toSmallString();
    }

    public String toFancyString() { // for String that clarifies the piece's position
        return name.toFullString() + " is at " + coords.toString();
    }

    //________________________________________________Overridden Methods________________________________________________

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return name == piece.name &&
                colour == piece.colour &&
                OGcoord.equals(piece.OGcoord) &&
                pieceID.equals(piece.pieceID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, colour, OGcoord, pieceID);
    }


    //________________________________________________Abstract Methods________________________________________________

    /**
     * Содержит все исходные ходы (все ходы без учета возможных ошибок), возможные для данной фигуры на определенной доске
     * Исходные ходы рассчитываются индивидуально для каждой фигуры
     * @param pieces фигуры на игровом поле, на котором ведется игра
     * @return ArrayList, содержащий все исходные координаты хода.
     */


    public abstract ArrayList<Coordinate> getRawMoves(Pieces pieces);

    /**
     * @return значок изображения, связанный с определенным элементом. Он присваивается при создании экземпляра элемента.
     * Используется для назначения значков для гибочной панели
     */


    public abstract ImageIcon getImageIcon();

    /**
     * Создает точную копию экземпляра фигуры
     * Используется для создания копий хэш-карт и расчета возможных ходов
     * @return копию экземпляра фигуры
     */


    public abstract Piece makeCopy();
}
