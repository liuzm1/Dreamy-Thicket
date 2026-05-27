package core;

import entities.*;
import maps.CollisionCheck;
import java.util.ArrayList;

public class EnemyManager {
    private final GameInstance game;

    // ====================== 敌人数组化升级 ======================
    private PatrolEnemy[] patrolEnemies;
    private ChaseEnemy[] chaseEnemies;          // 升级为数组，支持第二关同时出现2个追逐怪
    private VineDestroyerEnemy[] vineDestroyers; // 升级为数组，支持第三关同时出现2个拆墙怪

    public EnemyManager(GameInstance game) {
        this.game = game;
    }

    public void setupEnemiesForLevel(int levelNum, CollisionCheck collisionCheck) {
        // 每次切换关卡先清空上一关的敌人
        patrolEnemies = null;
        chaseEnemies = null;
        vineDestroyers = null;

        // 严格对照关卡备忘录的数组坐标 (col, row) 进行 new 对象
        if (levelNum == 1) {
            // 【第一关】2个横向平移怪
            patrolEnemies = new PatrolEnemy[]{
                    new PatrolEnemy(game, collisionCheck, 2, 4, 1),   // 敌人 A (2, 5)
                    new PatrolEnemy(game, collisionCheck, 13, 10, -1) // 敌人 B (13, 10)
            };
        } else if (levelNum == 2) {
            // 【第二关】2个自动追击怪 + 1个横向平移怪
            chaseEnemies = new ChaseEnemy[]{
                    new ChaseEnemy(game, collisionCheck, 13, 2),      // 追击怪 A (2, 13)
                    new ChaseEnemy(game, collisionCheck, 2, 13)      // 追击怪 B (13, 13)
            };
        } else if (levelNum == 3) {
            // 【第三关】1个自动追击怪 + 2个消除藤蔓怪
            chaseEnemies = new ChaseEnemy[]{
                    new ChaseEnemy(game, collisionCheck, 7, 8),    // 追击怪 A (13, 13)
            };
            vineDestroyers = new VineDestroyerEnemy[]{
                    new VineDestroyerEnemy(game, collisionCheck, game.getMapManager(), 8, 8) // 拆墙怪 B (13, 2)
                     // 拆墙怪 C (13, 13)
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

            // 1. 平移怪碰撞检测
            if (patrolEnemies != null) {
                for (PatrolEnemy enemy : patrolEnemies) {
                    if (enemy != null && !enemy.isOnCooldown() && isSameGrid(enemy, player)) {
                        game.onPlayerHitEnemy(player, enemy);
                        return;
                    }
                }
            }
            // 2. 多个追逐怪碰撞循环检测
            if (chaseEnemies != null) {
                for (ChaseEnemy enemy : chaseEnemies) {
                    if (enemy != null && !enemy.isOnCooldown() && isSameGrid(enemy, player)) {
                        game.onPlayerHitEnemy(player, enemy);
                        return;
                    }
                }
            }
            // 3. 多个拆墙怪碰撞循环检测
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