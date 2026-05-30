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
package core;

import entities.*;
import maps.CollisionCheck;
import java.util.ArrayList;

public class EnemyManager {
    private final GameInstance game;

    // ====================== Enemy arrays ======================
    private PatrolEnemy[] patrolEnemies;
    private ChaseEnemy[] chaseEnemies;          // Array: level 2 can spawn 2 chasers
    private VineDestroyerEnemy[] vineDestroyers; // Array: level 3 can spawn 2 vine destroyers

    public EnemyManager(GameInstance game) {
        this.game = game;
    }

    public void setupEnemiesForLevel(int levelNum, CollisionCheck collisionCheck) {
        // Clear enemies from previous level
        patrolEnemies = null;
        chaseEnemies = null;
        vineDestroyers = null;

        // Spawn positions from level notes (col, row)
        if (levelNum == 1) {
            // Level 1: 2 horizontal patrol enemies
            patrolEnemies = new PatrolEnemy[]{
                    new PatrolEnemy(game, collisionCheck, 2, 4, 1),   // Enemy A (2, 5)
                    new PatrolEnemy(game, collisionCheck, 13, 10, -1) // Enemy B (13, 10)
            };
        } else if (levelNum == 2) {
            // Level 2: 2 chase enemies + 1 patrol
            chaseEnemies = new ChaseEnemy[]{
                    new ChaseEnemy(game, collisionCheck, 13, 2),      // Chaser A (2, 13)
                    new ChaseEnemy(game, collisionCheck, 2, 13)      // Chaser B (13, 13)
            };
        } else if (levelNum == 3) {
            // Level 3: 1 chase enemy + 2 vine destroyers
            chaseEnemies = new ChaseEnemy[]{
                    new ChaseEnemy(game, collisionCheck, 7, 8),    // Chaser A (13, 13)
            };
            vineDestroyers = new VineDestroyerEnemy[]{
                    new VineDestroyerEnemy(game, collisionCheck, game.getMapManager(), 8, 8) // Vine destroyer B (13, 2)
                     // Vine destroyer C (13, 13)
            };
        }
    }

    public Enemy[] getAllEnemies() {
        ArrayList<Enemy> list = new ArrayList<>();
        if (patrolEnemies != null) {
            for (PatrolEnemy enemy : patrolEnemies) {
                if (enemy != null) list.add(enemy);
            }
        }
        if (chaseEnemies != null) {
            for (ChaseEnemy enemy : chaseEnemies) {
                if (enemy != null) list.add(enemy);
            }
        }
        if (vineDestroyers != null) {
            for (VineDestroyerEnemy enemy : vineDestroyers) {
                if (enemy != null) list.add(enemy);
            }
        }
        return list.toArray(new Enemy[0]);
    }

    private void syncEnemyPeers() {
        Enemy[] all = getAllEnemies();
        if (patrolEnemies != null) {
            for (PatrolEnemy enemy : patrolEnemies) {
                if (enemy != null) enemy.setPeerEnemies(all);
            }
        }
        if (chaseEnemies != null) {
            for (ChaseEnemy enemy : chaseEnemies) {
                if (enemy != null) enemy.setPeerEnemies(all);
            }
        }
        if (vineDestroyers != null) {
            for (VineDestroyerEnemy enemy : vineDestroyers) {
                if (enemy != null) enemy.setPeerEnemies(all);
            }
        }
    }

    public void update(double dt, Player[] activePlayers) {
        syncEnemyPeers();
        if (patrolEnemies != null) {
            for (PatrolEnemy enemy : patrolEnemies) {
                if (enemy != null) enemy.update(dt);
            }
        }
        if (chaseEnemies != null) {
            for (ChaseEnemy enemy : chaseEnemies) {
                if (enemy != null) enemy.update(dt, activePlayers);
            }
        }
        if (vineDestroyers != null) {
            for (VineDestroyerEnemy enemy : vineDestroyers) {
                if (enemy != null) enemy.update(dt);
            }
        }
    }

    public void draw() {
        if (patrolEnemies != null) {
            for (PatrolEnemy enemy : patrolEnemies) {
                if (enemy != null) enemy.draw(game);
            }
        }
        if (chaseEnemies != null) {
            for (ChaseEnemy enemy : chaseEnemies) {
                if (enemy != null) enemy.draw(game);
            }
        }
        if (vineDestroyers != null) {
            for (VineDestroyerEnemy enemy : vineDestroyers) {
                if (enemy != null) enemy.draw(game);
            }
        }
    }

    public void checkCollisions(Player[] activePlayers) {
        for (Player player : activePlayers) {
            if (player == null) continue;
            if (!game.isTwoPlayer && !player.isAlive()) continue;
            if (game.isTwoPlayer && game.getSharedLives() <= 0) continue;

            // 1. Patrol enemy collisions
            if (patrolEnemies != null) {
                for (PatrolEnemy enemy : patrolEnemies) {
                    if (enemy != null && !enemy.isOnCooldown() && isSameGrid(enemy, player)) {
                        game.onPlayerHitEnemy(player, enemy);
                        return;
                    }
                }
            }
            // 2. Chase enemy collisions
            if (chaseEnemies != null) {
                for (ChaseEnemy enemy : chaseEnemies) {
                    if (enemy != null && !enemy.isOnCooldown() && isSameGrid(enemy, player)) {
                        game.onPlayerHitEnemy(player, enemy);
                        return;
                    }
                }
            }
            // 3. Vine destroyer collisions
            if (vineDestroyers != null) {
                for (VineDestroyerEnemy enemy : vineDestroyers) {
                    if (enemy != null && !enemy.isOnCooldown() && isSameGrid(enemy, player)) {
                        game.onPlayerHitEnemy(player, enemy);
                        return;
                    }
                }
            }
        }
    }

    private boolean isSameGrid(Enemy enemy, Player player) {
        return enemy.col == player.col && enemy.row == player.row;
    }
}