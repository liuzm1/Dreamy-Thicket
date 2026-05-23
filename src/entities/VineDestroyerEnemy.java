package entities;



import core.GameEngine;

import maps.CollisionCheck;

import maps.MapManager;



import java.util.ArrayDeque;

import java.util.Arrays;

import java.util.Comparator;

import java.util.Queue;



/**

 * 第三关敌人：寻找并消除最近的玩家藤蔓（相邻格删除，不踩藤蔓）。

 * 移动时绕开石头/藤蔓及其他敌人；Enemy1 不主动让路，本怪自行寻路避让。

 */

public class VineDestroyerEnemy extends AnimatedSpriteEnemy {



    private static final int VINE_MOVE_SPEED = 70;

    private static final int GRID_COUNT = 16;

    private static final int[][] DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};



    private final MapManager mapManager;

    private Enemy[] peerEnemies = new Enemy[0];



    public VineDestroyerEnemy(GameEngine engine, CollisionCheck collisionCheck,

                              MapManager mapManager, int startCol, int startRow) {

        super(collisionCheck, engine, "resource/sprites/entities/Enemy3.png");

        this.mapManager = mapManager;

        moveSpeed = VINE_MOVE_SPEED;

        reset(startCol, startRow);

    }



    public void setPeerEnemies(Enemy[] peerEnemies) {

        this.peerEnemies = peerEnemies != null ? peerEnemies : new Enemy[0];

    }



    @Override

    public void update(double dt) {

        if (isOnCooldown()) {

            updateCooldown(dt);

            return;

        }



        if (!isMoving) {

            if (!tryDestroyAdjacentVine()) {

                int[] nearestVine = findNearestVine();

                if (nearestVine != null) {

                    tryMoveTowardVine(nearestVine[0], nearestVine[1]);

                }

            }

        }



        updateWalkAnimation(dt);

        updateSmoothMovement(dt);

    }



    /** 相邻格有藤蔓则删除（不踏入藤蔓格） */

    private boolean tryDestroyAdjacentVine() {

        for (int[] d : DIRS) {

            int vc = col + d[0];

            int vr = row + d[1];

            if (collisionCheck.isVine(vc, vr)) {

                mapManager.setTile(vc, vr, 0);

                return true;

            }

        }

        return false;

    }



    private int[] findNearestVine() {

        int bestDist = Integer.MAX_VALUE;

        int[] best = null;



        for (int r = 0; r < GRID_COUNT; r++) {

            for (int c = 0; c < GRID_COUNT; c++) {

                if (mapManager.getTile(c, r) != 5) continue;

                int dist = Math.abs(c - col) + Math.abs(r - row);

                if (dist < bestDist) {

                    bestDist = dist;

                    best = new int[]{c, r};

                }

            }

        }

        return best;

    }



    private void tryMoveTowardVine(int vineCol, int vineRow) {

        int[] step = findNextStepBfsAdjacentTo(vineCol, vineRow);

        if (step == null) {

            step = pickMoveToward(vineCol, vineRow);

        }

        if (step != null) {

            setSpriteRowFromDelta(step[0], step[1]);

            tryMoveTo(col + step[0], row + step[1]);

        }

    }



    /** BFS 走到离目标藤蔓曼哈顿距离为 1 的格子 */

    private int[] findNextStepBfsAdjacentTo(int vineCol, int vineRow) {

        if (Math.abs(col - vineCol) + Math.abs(row - vineRow) <= 1) {

            return null;

        }



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



            if (Math.abs(c - vineCol) + Math.abs(r - vineRow) == 1) {

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

                if (!canMoveTo(nc, nr)) continue;



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



    /** BFS 失败时，选一步最接近目标藤蔓的可走方向 */

    private int[] pickMoveToward(int targetCol, int targetRow) {

        int[][] options = Arrays.copyOf(DIRS, DIRS.length);

        Arrays.sort(options, Comparator.comparingInt(d ->

                Math.abs(targetCol - (col + d[0])) + Math.abs(targetRow - (row + d[1]))));



        for (int[] d : options) {

            if (canMoveTo(col + d[0], row + d[1])) {

                return d;

            }

        }

        return null;

    }



    private boolean canMoveTo(int nextCol, int nextRow) {

        if (collisionCheck.isSolid(nextCol, nextRow)) return false;

        if (nextCol == col && nextRow == row) return false;

        if (peerEnemies != null) {
            for (Enemy peer : peerEnemies) {
                if (peer == null || peer == this) continue;
                // 第三关追踪怪不挡藤蔓怪寻路，避免互相卡住导致 Enemy3 完全不动
                if (peer instanceof ChaseEnemy) continue;
                if (peer.occupiesOrHeadingTo(nextCol, nextRow)) return false;
            }
        }
        return true;

    }

}


