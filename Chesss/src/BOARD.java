

public enum BOARD {

    FIRST_FILE('a'),
    LAST_FILE('h'),
    FIRST_RANK(1),
    LAST_RANK(8);

    private char fileVal;
    private int rankVal;

    BOARD(char file) {
        fileVal = file;
    }

    BOARD(int rank) {
        rankVal = rank;
    }

    public char getFileVal() {
        return fileVal;
    }

    public int getRankVal() {
        return rankVal;
    }
}
