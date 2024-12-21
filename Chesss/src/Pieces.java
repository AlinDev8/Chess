import java.util.*;

/*
1) Class Constructors
2) Collection Copying
3) Getters & Setters
4) Piece Related Methods
5) Board Distribution Related Methods
6) Game Logic Methods
7) Piece Movement Methods
8) Overridden Methods
*/

/**
 * The Pieces class is used to represent the board that is being played with.
 * It contains the position of all pieces, alongside all the methods that handle game logic,
 * such as checking when a game ends or handling the movement of a piece.
 */

public class Pieces {

    /**
     * pieces is a HashMap which uses a Coordinate-Piece key-value pair,
     * with the Coordinate representing the current position of the Piece
     *
     * previousPieces is a HashMap which uses a Coordinate-Piece key-value pair, used of a representation of the previous board
     * isCapture is a boolean value, used to determine whether a given move has led to a capture
     * isGUIGame is a boolean value, used to determine whether the current game being played is via a GUIBoard or a TBIBoard
     *
     * gameProgress is an ArrayList, containg HashMaps with Coordinate-Piece key-value pairs,
     * used to store the board's development as a game is played.
     * This is used to determine whether there is a draw by threefold repetition
     */

    private HashMap<Coordinate, Piece> pieces;
    private HashMap<Coordinate, Piece> previousPieces;
    private boolean isCapture;
    private boolean isGUIGame;
    private ArrayList<HashMap<Coordinate,Piece>> gameProgress = new ArrayList<>();

    //________________________________________________Class Constructors________________________________________________

    public Pieces() {
        pieces = Boards.getChessBoard();
        previousPieces = copyHashMap(pieces);
        gameProgress.add(copyHashMap(pieces));
        updatePotentials(); // when we instantiate Pieces, we immediately calculte the potential moves to begin the game
    }

    public Pieces(HashMap<Coordinate, Piece> newBoard) {
        pieces = newBoard;
        previousPieces = copyHashMap(pieces);
        gameProgress.add(copyHashMap(pieces));
        updatePotentials();
    }

    public Pieces (Pieces original) { // we create a copy constructor to help calculate potential moves
        this.pieces = copyHashMap(original.getPieces());
        this.previousPieces = original.previousPieces;
        this.isCapture = original.isCapture;
        this.isGUIGame = original.isGUIGame;
        this.gameProgress = copyArrayHash(original.getGameProgress());
    }

    //________________________________________________Collection Copying________________________________________________

    private HashMap<Coordinate, Piece> copyHashMap (HashMap<Coordinate, Piece> original) { // creates a copy of a pieces HashMap

        HashMap<Coordinate, Piece> copyMap = new HashMap<>();
        for (Coordinate key : original.keySet()) {
            Coordinate newKey = new Coordinate(key);
            Piece newPiece = original.get(key).makeCopy();
            copyMap.put(newKey,newPiece);
        }

        return copyMap;
    }

    private ArrayList<HashMap<Coordinate,Piece>> copyArrayHash (ArrayList<HashMap<Coordinate,Piece>> original) { // creates a copy of a gameProgress ArrayList
        ArrayList<HashMap<Coordinate,Piece>> copyList = new ArrayList<>();
        for (HashMap<Coordinate,Piece> game : original) {
            copyList.add(copyHashMap(game));
        }

        return copyList;
    }

    //________________________________________________Getters & Setters________________________________________________

    public HashMap<Coordinate, Piece> getPieces() {
        return pieces;
    }

    public void setPieces(HashMap<Coordinate,Piece> pieces) {this.pieces = pieces;}

    public boolean getIsCapture() {
        return isCapture;
    }

    public void setIsCapture(boolean captureStatus) {
        this.isCapture = captureStatus;
    }

    public HashMap<Coordinate, Piece> getPreviousPieces() {
        return previousPieces;
    }

    public void setPreviousPieces(HashMap<Coordinate, Piece> previousPieces) {
        this.previousPieces = copyHashMap(previousPieces);
    }

    public ArrayList<HashMap<Coordinate, Piece>> getGameProgress() {
        return gameProgress;
    }

    public void setGUIGame (boolean GUIStatus) {
        isGUIGame = GUIStatus;
    }

    //________________________________________________Piece Related Methods________________________________________________

    /**
     * Adds a Coordinate-Piece key-value pair to the pieces HashMap (used when handling a piece movement)
     * @param coordinate the destination coordinate for a given move
     * @param piece the piece making the move
     */

    public void addPiece(Coordinate coordinate, Piece piece) {
        pieces.put(coordinate,piece);
    }

    /**
     * Finds a given piece within the current instance of Pieces
     * @param piece the piece that is looked for
     * @return the Coordinate that a given piece occupies. If the piece isn't in the board, returns the emptyCoordinate
     * @throws NullPointerException if the piece provided isn't instantiated
     */

    public Coordinate findPiece(Piece piece) {

        Objects.requireNonNull(piece, "Provide an existing piece. It can't be null.");

        for (Coordinate key : pieces.keySet()) {
            if (pieces.get(key).equals(piece))
                return key;
        }
        System.err.println(piece.getName().toFullString() +" not found.");
        return Coordinate.emptyCoordinate;
    }

    /**
     * Finds a given king within the current instance of Pieces
     * @param colour the colour of the king that is being looked for
     * @return the Coordinate that the King occupies. If the King isn't in the board, returns the emptyCoordinate
     */

    public Coordinate findKing(COLOUR colour) {
        for (Coordinate key : pieces.keySet()) {
            Piece potentialKing = pieces.get(key);
            if (potentialKing.getName().equals(ID.KING) && (potentialKing.getColour() == colour))
                return key;
        }
        String pieceNotInBoard = "King not found. Assuming it isn't in board. Empty coordinate provided.";
        System.err.println(pieceNotInBoard);
        return Coordinate.emptyCoordinate;
    }

    /**
     * Returns a piece within the current instance of Pieces, given a Coordinate
     * @param coordinate the coordinate that is being looked for within the board
     * @return the Piece occupying the given coordinate. If there is no piece at the coordinate, returns the emptyPiece
     * @throws NullPointerException if the Coordinate provided isn't instantiated
     */

    public Piece getPiece(Coordinate coordinate) {

        Objects.requireNonNull(coordinate, "Provide an existing coordinate. It can't be null.");

        for (Coordinate key : pieces.keySet()) {
            if (key.equals(coordinate))
                return pieces.get(key);
        }
        System.err.println("There is no piece in this coordinate. Empty piece provided.");
        return Piece.emptyPiece;
    }

    //________________________________________________Board Distribution Related Methods________________________________________________

    /**
     * Provides a HashMap containing the coordinates and pieces of a given colour
     * @param colour the colour of the pieces of interest
     * @return a HashMap of the form of pieces, containing only those pieces of a given colour
     */

    public HashMap<Coordinate, Piece> getColourPieces(COLOUR colour) {
        HashMap<Coordinate,Piece> colours = new HashMap<>();
        for (Coordinate key : pieces.keySet()) {
            Piece piece = pieces.get(key);
            if (piece.getColour() == colour)
                colours.put(key,piece);
        }
        return colours;
    }

    /**
     * Provides a HashSet of all the potential moves of the pieces of a given colour
     * @param colour the colour of the pieces of interest
     * @return a HashSet of Coordinates, containing all the coordinates that pieces of a given colour can go to
     */

    public HashSet<Coordinate> allColouredPotentials (COLOUR colour) {
        HashSet<Coordinate> allMoves = new HashSet<>();
        HashMap<Coordinate, Piece> allColoured = getColourPieces(colour);
        for (Piece piece : allColoured.values()){
            allMoves.addAll(piece.getPotentialMoves());
        }
        return allMoves;
    }

    /**
     * Provides a HashSet of all the raw moves of the pieces of a given colour
     * @param colour the colour of the pieces of interest
     * @return a HashSet of Coordinates, containing all the raw coordinates that pieces of a given colour can go to
     */

    public HashSet<Coordinate> allColouredRaws (COLOUR colour) {
        HashSet<Coordinate> allMoves = new HashSet<>();
        HashMap<Coordinate, Piece> allColoured = getColourPieces(colour);
        for (Piece piece : allColoured.values()){
            allMoves.addAll(piece.getRawMoves(this));
        }
        return allMoves;
    }

    /**
     * For all pawns in the current board, updates the previous coordinate that they occupied after a move is made.
     * This is used to validate en passant captures.
     */

    public void updatePreviousMovePawns () {
        for (Piece potentialPawn : pieces.values()){
            if (potentialPawn.getName() == ID.PAWN) {
                Pawn pawn = (Pawn) potentialPawn;
                pawn.setPreviousCoordinate(pawn.getCoords());
            }
        }
    }

    /**
     * For a given piece, determines whether there is a piece in the same file of the same type (i.e same ID)
     * @param piece the piece that is being considered
     * @return true if and only if there is a piece, of the same colour, of the same type in the same file as the argument piece
     * within the given board
     */

    public boolean pieceInSameFile (Piece piece) {

        if (piece.getName() == ID.KING)
            return false;

        HashMap <Coordinate, Piece> coloured = getColourPieces(piece.getColour());
        for (Piece value : coloured.values()) {
            if (value.getName() == piece.getName() && value.getFile() == piece.getFile() && !value.equals(piece))
                return true;
        }
        return false;
    }

    /**
     * For a given piece, determines whether there is a piece in the same rank of the same type (i.e same ID)
     * @param piece the piece that is being considered
     * @return true if and only if there is a piece, of the same colour, of the same type in the same rank as the argument piece
     * within the given board
     */

    public boolean pieceInSameRank (Piece piece) {

        if (piece.getName() == ID.KING)
            return false;

        HashMap <Coordinate, Piece> coloured = getColourPieces(piece.getColour());
        for (Piece value : coloured.values()) {
            if (value.getName() == piece.getName() && value.getRank() == piece.getRank() && !value.equals(piece))
                return true;
        }
        return false;
    }



    public boolean pieceToSameCoordinate (Coordinate coordinate, Piece piece) {
        assert piece.getPotentialMoves().contains(coordinate);

        if (piece.getName() == ID.KING)
            return false;

        HashMap <Coordinate, Piece> coloured = getColourPieces(piece.getColour());
        for (Piece value : coloured.values()) {
            if (value.getName() == piece.getName() && value.getPotentialMoves().contains(coordinate) && !value.equals(piece))
                return true;
        }
        return false;
    }

    //________________________________________________Game Logic Methods________________________________________________



    public boolean isCheck(COLOUR colour) { //check against the given colour
        Coordinate kingPosition = findKing(colour);

        if (kingPosition.equals(Coordinate.emptyCoordinate))
            throw new IllegalArgumentException("There is no king in the board. This is an illegal game!");

        HashSet<Coordinate> dangerMoves = allColouredPotentials(COLOUR.not(colour));
        return (dangerMoves.contains(kingPosition));
    }



    public boolean isMate(COLOUR colour) {
        HashSet<Coordinate> allMoves = allColouredPotentials(colour);
        return isCheck(colour) && (allMoves.size() == 0);
    }

    /**
     * Определяет, будет ли на доске ничья.
     * @return значение true, если:
     * на доске всего 2 короля
     * всего два короля и слон/ конь
     * есть только два короля и два слона на противоположных сторонах, но одного цвета диагонали (слоны могут передвигаться только по черным или белым клеткам)
     * допускается троекратное повторение (одна и та же позиция повторяется 3 раза в любое время во время игры).
     * Это выясняется путем циклического просмотра gameProgress и проверки наличия 3 одинаковых хэш-карт.
     */


    public boolean isDraw() {

        int n = gameProgress.size();

        boolean twoKings = !findKing(COLOUR.B).equals(Coordinate.emptyCoordinate) && !findKing(COLOUR.W).equals(Coordinate.emptyCoordinate);

        if (getPieces().size() == 2) // only 2 kings
            return twoKings;
        else if (getPieces().size() == 3) { // 2 kings and a bishop/knight
            int counter = 0;
            for (Piece piece : this.getPieces().values()) {
                if (piece.getName() == ID.BISHOP || piece.getName() == ID.KNIGHT)
                    counter++;
            }
            return twoKings && counter == 1;
        }
        else if (getPieces().size() == 4) { // 2 kings and 2 bishops with same diagonal colour
            int counterB = 0;
            Bishop bishopB = null;
            int counterW = 0;
            Bishop bishopW = null;
            for (Piece piece : this.getPieces().values()) {
                if (piece.getName() == ID.BISHOP) {
                    if (piece.getColour() == COLOUR.B) {
                        bishopB = (Bishop) piece;
                        counterB++;
                    }
                    else {
                        bishopW = (Bishop) piece;
                        counterW++;
                    }
                }
            }

            boolean sameColourBishops = counterB == 1 &&
                                        counterW == 1 &&
                                        bishopB.getOGcoord().getFile() != bishopW.getOGcoord().getFile();

            return twoKings && sameColourBishops;
        }
        else if (n >= 3){ // threefold repetition
            for (HashMap<Coordinate, Piece> currentGame : gameProgress) {
                int counter = 0;
                for (HashMap<Coordinate, Piece> checkGame : gameProgress) {
                    if (currentGame.equals(checkGame)) {
                        counter++;
                    }
                }
                if (counter == 3)
                    return true;
            }

        }

        return false;

    }

    /**
     * Определяет, возникла ли на доске патовая ситуация.
     * @param colour - цвет хода, в котором был сделан возможный патовый ход
     * @return значение true, если на доске нет шаха, но фигуры противоположного цвета не имеют потенциальных ходов
     */


    public boolean isStalemate(COLOUR colour) {
        HashSet<Coordinate> allMoves = allColouredPotentials(COLOUR.not(colour));
        return allMoves.size() == 0 && !isCheck(COLOUR.not(colour));

    }

    //________________________________________________Piece Movement Methods________________________________________________

    /**
     * При заданных координатах назначения и элементе, элемент перемещается в указанную координату.
     * Это делается путем добавления элемента и координаты в хэш-карту элементов.
     * Для элементов задаются новые координаты, для параметра "hasMoved" устанавливается значение true, а предыдущая координата элемента удаляется
     * в хэш-карте фигур.
     * @param coordinate конечную координату для данного хода
     * @param piece фигуру, которая совершает ход
     */

    public void pieceMove (Coordinate coordinate, Piece piece) {
        Coordinate pieceCoord = findPiece(piece);
        addPiece(coordinate, piece);
        piece.setCoords(coordinate);
        piece.setHasMoved();
        pieces.remove(pieceCoord);
    }

    /**
     * Учитывая координаты места назначения и фрагмент, соответствующим образом измените хэш-карту фрагментов.
     * Она устанавливает предыдущие фрагменты и определяет, был ли произведен захват.
     * После выполнения хода потенциальные ходы новых фигур пересчитываются,
     * и заданная хэш-карта фигур добавляется в программу игры.
     * Для всех фигур, кроме короля и пешки, используется пошаговый ход.
     * Для короля мы рассматриваем, возможна ли рокировка. Если да, то король и соответствующая ладья перемещаются.
     * Для пешки мы рассматриваем продвижение (выполнение запроса на продвижение) и последующий захват.
     * @param coordinate конечную координату для данного хода
     * @param piece- фигура, которая делает ход
     */


    public void makeMove (Coordinate coordinate, Piece piece) {

        if (piece.isValidMove(coordinate, piece.getColour())) {
            setPreviousPieces(this.getPieces());
            isCapture = Move.tileFull(this, coordinate) && Move.isNotTileColour(this,coordinate, piece.getColour());
            if (piece.getName() == ID.KING) {
                King castleKing = (King) piece;
                if (castleKing.canCastleQueen(this) && coordinate.equals(castleKing.getCastleCoordKingQ()) && !isCheck(castleKing.getColour())) {
                    assert !findPiece(castleKing.getRookQueen()).equals(Coordinate.emptyCoordinate);
                    pieceMove(coordinate,castleKing);
                    pieceMove(castleKing.getRookQueen().getCastleCoordRook(),castleKing.getRookQueen());
                }
                else if (castleKing.canCastleKing(this) && coordinate.equals(castleKing.getCastleCoordKingK()) && !isCheck(castleKing.getColour())) {
                    assert !findPiece(castleKing.getRookKing()).equals(Coordinate.emptyCoordinate);
                    pieceMove(coordinate,castleKing);
                    pieceMove(castleKing.getRookKing().getCastleCoordRook(),castleKing.getRookKing());
                }
                else {
                    pieceMove(coordinate, castleKing);
                }
            }
            else if (piece.getName() == ID.PAWN) {
                Pawn pawn = (Pawn) piece;

                updatePreviousMovePawns();
                if (Math.abs(coordinate.getRank() - pawn.getRank()) == 2)
                    pawn.setHasMovedTwo();

                if (pawn.canPromoteBlack(coordinate) || pawn.canPromoteWhite(coordinate)) {
                    Piece toPromote;

                    if (isGUIGame) {
                        pawn.GUIPromotionQuery(coordinate);
                        toPromote = pawn.getPromotedPiece();
                    }
                    else {
                        toPromote = pawn.promotionQuery(coordinate);
                    }
                    Coordinate pieceCoord = findPiece(piece);
                    addPiece(coordinate, toPromote);
                    pieces.remove(pieceCoord);
                }
                else if (pawn.getEnPassantLeft()) {
                    Coordinate left = Move.leftFree(this,pawn,1).get(0);
                    setIsCapture(true);
                    pieces.remove(left);
                    pieceMove(coordinate,pawn);
                }
                else if (pawn.getEnPassantRight()) {
                    Coordinate right = Move.rightFree(this,pawn,1).get(0);
                    setIsCapture(true);
                    pieces.remove(right);
                    pieceMove(coordinate,pawn);
                }
                else {
                    pieceMove(coordinate, pawn);
                }
            }
            else {
                pieceMove(coordinate, piece);
            }
        }
        else
            System.err.println(piece.getName().toFullString() + " to " + coordinate.toString() + " is an invalid move.");

        gameProgress.add(copyHashMap(pieces));
        updatePotentials();

    }



    public void updatePotentials() {

        for (Piece value : pieces.values()) {
            value.clearMoves();
            value.updatePotentialMoves(this);
        }
    }

    //________________________________________________Overridden Methods________________________________________________

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        pieces.forEach((coord, piece) -> str.append(piece.getPieceID())
                                .append(" is at ")
                                .append(coord.toString())
                                .append("\n"));

        return str.toString();
        }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pieces pieces1 = (Pieces) o;
        return Objects.equals(pieces, pieces1.pieces);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieces);
    }
}
