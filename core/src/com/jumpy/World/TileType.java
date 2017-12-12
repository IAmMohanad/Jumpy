package com.jumpy.World;


import java.util.HashMap;

public enum TileType {

    NORMAL_BLOCK(1, true, "NORMAL_BLOCK"),
    BROWN_BLOCK(2, true, "BROWN_BLOCK"),
    BROWN_GRASS_BLOCK(3, true, "BROWN_GRASS_BLOCK"),
    BROWN_BROKEN_BLOCK(4, true, "BROWN_BROKEN_BLOCK"),
    DARK_BLOCK(5, true, "DARK_BLOCK"),
    GRASS_LEFT_CORNER(6, false, "grass_left_corner"),
    GRASS_MIDDLE(7, false, "grass_middle"),
    GRASS_RIGHT_CORNER(8, false, "grass_right_corner");

    private int id;
    private boolean collidable;
    private String name;
    private int damage;

    public static final int TILE_SIZE = 32;

    TileType(int id, boolean collidable, String name){
        this(id, collidable, name, 0);
    }

    TileType(int id, boolean collidable, String name, int damage){
        this.id = id;
        this.collidable = collidable;
        this.name = name;
        this.damage = damage;
    }

    public int getId() {
        return id;
    }

    public boolean isCollidable() {
        return collidable;
    }

    public String getName() {
        return name;
    }

    private static HashMap<Integer, TileType> tileMap;

    static{
        tileMap = new HashMap<Integer, TileType>();
        for(TileType tileType : TileType.values()){
            tileMap.put(tileType.getId(), tileType);
        }
    }

    public static TileType getTileTypeById(int id){
        return tileMap.get(id);
    }
}
