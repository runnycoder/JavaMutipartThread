package com.icehan.thread.executor.puzzle;

import java.util.Set;

/**
 * 定义一个谜题 谜题包含一个初始位置和一个目标位置
 * P代表位置类 M代表移动类
 * 判断初始位置到目标位置是否存在合法的路径
 */
public interface Puzzle<P,M> {
    P initialPosition();
    boolean isGoal(P position);
    Set<M> legalMoves(P position);
    P move(P position,M move);
}
