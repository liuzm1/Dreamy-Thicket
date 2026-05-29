/**
 * ---------------------------------------------------------------------------
 * Massey University - 159.261 Games Programming
 * Assignment 2
 * ---------------------------------------------------------------------------
 * * [Dreamy Forest]
 * * Team Members:
 * - LIU ZIMO (ID:24009362)
 * - MIAO CHONG (ID: 24008986)
 * - SUN MINGYI (ID: 24009239)
 * - ZHOU XUAN (ID: 24009035)
 * ---------------------------------------------------------------------------
 **/
package entities;



import core.GameEngine;

import maps.CollisionCheck;

import maps.MapManager;



import java.util.ArrayDeque;

import java.util.Arrays;

import java.util.Comparator;

import java.util.Queue;



/**

 * Level 3 enemy: finds and destroys nearest player-placed vine (adjacent cell removal, does not step on vines).
 *
 * Routes around stone/vines/other enemies while moving; Enemy1 does not yield—this enemy pathfinds around them.

 */

public class VineDestroyerEnemy extends AnimatedSpriteEnemy {



    private static final int VINE_MOVE_SPEED = 25;

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
            // Split out if (!tryDestroyAdjacentVine()) so after attempting removal,
            // whether successful or not, search for a new target immediately if still idle
            tryDestroyAdjacentVine();

            // After clearing (or if none nearby), search the map for the nearest vine
            int[] nearestVine = findNearestVine();
            if (nearestVine != null) {
                tryMoveTowardVine(nearestVine[0], nearestVine[1]);
            }
        }

        updateWalkAnimation(dt);
        updateSmoothMovement(dt);
    }



    /** Remove vine on adjacent cell if present (does not step onto vine cell). */

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



    /** BFS to a cell with Manhattan distance 1 from target vine. */

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



    /** When BFS fails, pick walkable direction closest to target vine. */

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
                // Level 3 chase enemies do not block vine destroyer pathfinding (prevents Enemy3 getting stuck)
                if (peer instanceof ChaseEnemy) continue;
                if (peer.occupiesOrHeadingTo(nextCol, nextRow)) return false;
            }
        }
        return true;

    }

}


