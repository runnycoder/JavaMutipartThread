package com.icehan.thread.executor.puzzle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 单线程顺序的方式 深度搜索可达路径
 * @param <P>
 * @param <M>
 */
public class SequentialPuzzleSolver<P,M> {
    private final  Puzzle<P,M> puzzle;
    private final Set<P> seen = new HashSet<P>();

    public SequentialPuzzleSolver(Puzzle<P, M> puzzle) {
        this.puzzle = puzzle;
    }

    private List<M> search(Node<P,M> node){
        if(!seen.contains(node.pos)){
            seen.add(node.pos);
            if(puzzle.isGoal(node.pos)){
                return node.asMoveList();
            }
            for(M move: puzzle.legalMoves(node.pos)){
                P pos = puzzle.move(node.pos, node.move);
                Node<P, M> nextNode = new Node<>(pos, move, node);
                List<M> result = search(nextNode);
                if(result!=null){
                    return result;
                }
            }
        }
        return null;
    }

    public List<M> solve(){
        P pos = puzzle.initialPosition();
        return null;
    }
}
