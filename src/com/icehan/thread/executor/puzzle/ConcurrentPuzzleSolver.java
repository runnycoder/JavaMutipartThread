package com.icehan.thread.executor.puzzle;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

public class ConcurrentPuzzleSolver<P,M> {
    private final Puzzle<P,M> puzzle;
    private final ExecutorService executorService;
    private final ConcurrentMap<P,Boolean> seen;
    final ValueLatch<Node<P,M>> solution = new ValueLatch<Node<P,M>>();

    public ConcurrentPuzzleSolver(Puzzle<P, M> puzzle, ExecutorService executorService, ConcurrentMap<P, Boolean> seen) {
        this.puzzle = puzzle;
        this.executorService = executorService;
        this.seen = seen;
    }

    public List<M> solve() throws InterruptedException {
        try {
            P p = puzzle.initialPosition();
            executorService.execute(newTask(p, null, null));
            Node<P, M> soleNode = solution.getValue();
            return (soleNode==null)?null:soleNode.asMoveList();
        } finally {
            executorService.shutdown();
        }

    }

    protected Runnable newTask(P p,M m, Node<P,M> n){
        return new SolveTask(p, m, n);
    }

    class SolveTask extends Node<P,M> implements Runnable{
        public SolveTask(P pos, M move, Node<P, M> prev) {
            super(pos, move, prev);
        }

        @Override
        public void run() {
            //如果已经找到解决方案或者结点已被访问过
            if(solution.isSet()||seen.putIfAbsent(pos, true)!=null){
                return;
            }
            if (puzzle.isGoal(pos)){
                solution.setValue(this);
            }else{
                for(M m:puzzle.legalMoves(pos)){
                    executorService.execute(newTask(puzzle.move(pos, m), m, this));
                }
            }

        }
    }
}
