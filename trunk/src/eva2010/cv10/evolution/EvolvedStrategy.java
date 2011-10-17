package eva2010.cv10.evolution;

import java.util.ArrayList;

import java.util.List;

import eva2010.cv9.Result;
import eva2010.cv9.Strategy;

public class EvolvedStrategy extends Strategy {

    // <editor-fold defaultstate="collapsed" desc="Fields">

    List<Result> results = new ArrayList<Result>();
    
    Move[] moves;

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructors">
	
    public EvolvedStrategy(Move[] moves) {
        this.moves = moves;
        reset();
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods">

    @Override
    public String getName() {
        return "Evolved strategy";
    }
	
    @Override
    public String authorName() {
        return "Charles Darwin";
    }

    @Override
    public Move nextMove() {
        Result[] lastMoves = results.subList(results.size() - 3, results.size()).toArray(new Result[0]);

        //oponent made an error (exception)
        if (lastMoves[0].getOponentsMove() == null) {
            return Move.COOPERATE;
        }
        if (lastMoves[1].getOponentsMove() == null) {
            return Move.COOPERATE;
        }
	if (lastMoves[2].getOponentsMove() == null) {
            return Move.COOPERATE;
        }
		
        //prevod na 6bitove cislo
        int index = 0;
        index += lastMoves[0].getMyMove().ordinal() << 5;
        index += lastMoves[0].getOponentsMove().ordinal() << 4;
        index += lastMoves[1].getMyMove().ordinal() << 3;
        index += lastMoves[1].getOponentsMove().ordinal() << 2;
        index += lastMoves[2].getMyMove().ordinal() << 1;
        index += lastMoves[2].getOponentsMove().ordinal();
		
        return moves[index];
    }

    @Override
    public void reward(Result res) {
        results.add(res);
    }

    @Override
    public void reset() {
        results.clear();
        Result result = new Result(Move.COOPERATE, Move.COOPERATE);
		
        //nez se odehraji prvni 3 tahy, tak budeme spolupracovat
        results.add(result);
        results.add(result);
        results.add(result);
    }

    // </editor-fold>
}
