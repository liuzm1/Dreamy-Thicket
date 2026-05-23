package entities;

import core.GameEngine;
import maps.CollisionCheck;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Queue;

/**
 * 第二关敌人：追踪最近玩家。
 * 单人追 player1；双人追距离更近的那位。
 * 若与巡逻怪(Enemy1)冲突，则让 Enemy1 先走。
 */
public class ChaseEnemy extends AnimatedSpriteEnemy {

    private static final int CHASE_MOVE_SPEED = 60;
    private static final int GRID_COUNT = 16;
    private static final int[][] DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    private Enemy[] peerEnemies = new Enemy[0];

    public ChaseEnemy(GameEngine engine, CollisionCheck collisionCheck,
                      int startCol, int startRow) {
        super(collisionCheck, engine, "resource/sprites/entities/Enemy2.png");
        moveSpeed = CHASE_MOVE_SPEED;
        reset(startCol, startRow);
    }

    public void setPeerEnemies(Enemy[] peerEnemies) {
        this.peerEnemies = peerEnemies != null ? peerEnemies : new Enemy[0];
    }

    public void update(double dt, Player[] players) {
        if (isOnCooldown()) {
            updateCooldown(dt);
            return;
        }
        if (!isMoving) {
            Player target = pickClosestPlayer(players);
            if (target != null) {
                tryChaseStep(target, players);
            }
        }
        updateWalkAnimation(dt);
        updateSmoothMovement(dt);
    }

    @Override
    public void update(double dt) {
        update(dt, new Player[0]);
    }

    private Player pickClosestPlayer(Player[] players) {
        Player closest = null;
        int bestDist = Integer.MAX_VALUE;
        if (players == null) return null;

        for (Player p : players) {
            if (p == null) continue;
            int dist = Math.abs(p.col - col) + Math.abs(p.row - row);
            if (dist < bestDist) {
                bestDist = dist;
                closest = p;
            }
        }
        return closest;
    }

    private void tryChaseStep(Player target, Player[] players) {
        int[] step = findNextStepBfs(target.col, target.row, target, players);
        if (step == null) {
            step = pickMoveToward(target.col, target.row, target, players);
        }
        if (step != null) {
            setSpriteRowFromDelta(step[0], step[1]);
            tryMoveTo(col + step[0], row + step[1]);
        }
    }

    /** BFS 找通往目标的最短路径上的第一步（可绕石头/藤蔓） */
    private int[] findNextStepBfs(int targetCol, int targetRow, Player target, Player[] players) {
        if (col == targetCol && row == targetRow) return null;

        boolean[][] visited = new boolean[GRID_COUNT][GRID_COUNT];
        int[][] firstDx = new int[GRID_COUNT][GRID_COUNT];
        int[][] firstDy = new int[GRID_COUNT][GRID_COUNT];

        Queue<int[]> queue = new ArrayDeque<>();
        queue.add(new int[]{col, row});
        visited[col][row] = true;

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int c = cur[0];
            int r = cur[1];

            if (c == targetCol && r == targetRow) {
                int dx = firstDx[c][r];
                int dy = firstDy[c][r];
                if (dx == 0 && dy == 0) return null;
                return new int[]{dx, dy};
            }

            for (int[] d : DIRS) {
                int nc = c + d[0];
                int nr = r + d[1];
                if (nc < 0 || nc >= GRID_COUNT || nr < 0 || nr >= GRID_COUNT) continue;
                if (visited[nc][nr]) continue;
                if (!canMoveTo(nc, nr, target, players)) continue;

                visited[nc][nr] = true;
                if (c == col && r == row) {
                    firstDx[nc][nr] = d[0];
                    firstDy[nc][nr] = d[1];
                } else {
                    firstDx[nc][nr] = firstDx[c][r];
                    firstDy[nc][nr] = firstDy[c][r];
                }
                queue.add(new int[]{nc, nr});
            }
        }
        return null;
    }

    /** BFS 失败时（被藤蔓/Enemy1 暂时挡住），选一步最接近玩家的可走方向 */
    private int[] pickMoveToward(int targetCol, int targetRow, Player target, Player[] players) {
        int[][] options = Arrays.copyOf(DIRS, DIRS.length);
        Arrays.sort(options, Comparator.comparingInt(d ->
                Math.abs(targetCol - (col + d[0])) + Math.abs(targetRow - (row + d[1]))));

        for (int[] d : options) {
            if (canMoveTo(col + d[0], row + d[1], target, players)) {
                return d;
            }
        }
        return null;
    }

    private boolean canMoveTo(int nextCol, int nextRow, Player target, Player[] players) {
        if (collisionCheck.isSolid(nextCol, nextRow)) return false;
        if (Enemy.isBlockedByPeer(this, peerEnemies, nextCol, nextRow)) return false;

        if (players != null) {
            for (Player p : players) {
                if (p == null || p == target) continue;
                if (p.col == nextCol && p.row == nextRow) return false;
            }
        }
        return true;
    }
}
