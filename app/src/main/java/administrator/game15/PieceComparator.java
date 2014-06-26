package administrator.game15;

import java.util.Comparator;

/**
 * Created by administrator on 2014/06/27.
 */
public class PieceComparator implements Comparator<Piece> {

    public int compare(Piece a, Piece b)
    {
        if (a.posIdx > b.posIdx)        return 1;
        else if (a.posIdx == b.posIdx)  return 0;

        return -1;
    }
}
