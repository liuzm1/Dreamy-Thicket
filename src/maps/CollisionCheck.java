package maps;

public class CollisionCheck {
    MapManager mapManager;
    public CollisionCheck(MapManager mapManager) {
        this.mapManager = mapManager;
    }

    /*
      检测某个坐标是否为障碍物，判断这个格子能不能走，能走false,不能走true
     */
    public boolean isSolid(int col, int row){
        //边界检测
        if(col < 0 || col >= mapManager.GRID_COUNT || row < 0 || row >= mapManager.GRID_COUNT)
            return true;

        //获取地图数据进行判断
        int tileType = mapManager.getMapData()[row][col];

        // 这里的 1 是石头，5 是藤蔓（藤蔓也挡路）
        return tileType == 1 || tileType == 5;
    }

    /*
     * 专门检测藤蔓的逻辑（藤蔓消除功能用）
     * 判断这个格子是不是藤蔓，是藤蔓 true,不是藤蔓 false
     */
    public boolean isVine(int col, int row){
        // 先检查是否越界，没越界再看是不是 5
        if(col < 0 || col >= mapManager.GRID_COUNT || row < 0 || row >= mapManager.GRID_COUNT)
            return false;

        return mapManager.getMapData()[row][col] == 5;
    }

}
