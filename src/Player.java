import java.awt.*;
import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {
        Map<Point, FieldType> map = new HashMap<>();

        ArrayList<BomberMan> players = new ArrayList<>();

        ArrayList<Bomb> bombs = new ArrayList<>();

        ArrayList<Item> items = new ArrayList<Item>();

        Scanner in = new Scanner(System.in);
        int width = in.nextInt();
        int height = in.nextInt();
        int myId = in.nextInt();

        // game loop
        while (true) {
            for (int i = 0; i < height; i++) {
                String row = in.next();
                for (int j = 0; j < row.length(); j++) {
                    if(row.charAt(j) == '0'){
                        map.put(new Point(j, i), FieldType.Box);
                    }
                    else if(row.charAt(j) == 'x'){
                        map.put(new Point(j, i), FieldType.Wall);
                    }
                    else{
                        map.put(new Point(j, i), FieldType.Emtpy);
                    }
                    //map.put(new Point(j, i), row.charAt(j) == '.' ? FieldType.Emtpy : FieldType.Box);
                }
            }
            int entities = in.nextInt();
            players.clear();
            bombs.clear();
            Point currentPosition = new Point (0,0);
            for (int i = 0; i < entities; i++) {
                int entityType = in.nextInt();
                int owner = in.nextInt();
                int x = in.nextInt();
                int y = in.nextInt();
                int param1 = in.nextInt();
                int param2 = in.nextInt();

                if (entityType == 0) {
                    //Player
                    players.add(new BomberMan(x, y, owner, param1));
                    if(owner == myId){
                        currentPosition = new Point(x,y);
                    }
                } else if(entityType == 1) {
                    //Bomb
                    bombs.add(new Bomb(x, y, param1, owner, param2));
                }
                else{
                    items.add(new Item(x,y, param1));
                }
            }

            Point pointToPlace = null;
            int score = Integer.MIN_VALUE;
            System.err.println(currentPosition);
            for(Point p : map.keySet()){
                if(map.get(p).equals(FieldType.Emtpy)){
                    int currentScore = willDestroyNBoxs(new Bomb(p.x, p.y,8,0 , 3), map) * (int) Math.round(100d / distanceToCoordinate(p, currentPosition));
                    if(currentScore > score){
                        pointToPlace = p;
                        score = currentScore;
                        //System.err.println(pointToPlace);
                    }
                }
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            System.out.println("BOMB "+pointToPlace.x+" "+pointToPlace.y);
        }
    }

    public static int willDestroyNBoxs(Bomb bomb, Map<Point, FieldType> map) {
        int destroyedBoxes = 0;
        for (int i = bomb.getX() - bomb.getExplosionrange(); i < bomb.getX() + bomb.getExplosionrange(); i++) {
            Point p = new Point(i, bomb.getY());
            if (map.containsKey(p) && map.get(p).equals(FieldType.Box)) {
                destroyedBoxes++;
            }
        }
        for (int i = bomb.getY() - bomb.getExplosionrange(); i < bomb.getY() + bomb.getExplosionrange(); i++) {
            Point p = new Point(bomb.getX(), i);
            if (map.containsKey(p) && map.get(p).equals(FieldType.Box)) {
                destroyedBoxes++;
            }
        }
        return destroyedBoxes;
    }

    private static int distanceToCoordinate (Point point1, Point point2) {
        int xWert = Math.abs(point1.x) - Math.abs(point2.x);
        int yWert = Math.abs(point1.y) - Math.abs(point2.y);
        return xWert + yWert;
    }
}

class BomberMan {
    int x;
    int y;
    int id;
    int bombsInInventory;

    public BomberMan(int x, int y, int id, int bombsInInventory) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.bombsInInventory = bombsInInventory;
    }
}

class Bomb {
    private int x;
    private int y;
    private int timer;
    private int owner;
    private int explosionrange;

    public Bomb(int x, int y, int timer, int owner, int explosionRange) {
        this.x = x;
        this.y = y;
        this.timer = timer;
        this.owner = owner;
        this.explosionrange = explosionRange;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getTimer() {
        return timer;
    }

    public int getOwner() {
        return owner;
    }

    public int getExplosionrange() {
        return explosionrange;
    }
}

class Item{
    private int x;
    private int y;
    private int itemType;

    public Item(int x, int y, int itemType) {
        this.x = x;
        this.y = y;
        this.itemType = itemType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getItemType() {
        return itemType;
    }
}

class FloodFill {

    public Integer numberOfFreeFields(Map<Point, Integer> map, Point player) {
        Map<Point, Integer> mapClone = new HashMap<>();
        mapClone.putAll(map);
        return floodfill(mapClone, -1, -2, player, 0);
    }

    private int floodfill(Map<Point, Integer> map, int colorToReplace, int colorToPaint, Point p, int replaces) {
        if (map != null && map.containsKey(p)) {
            int currentColor = map.get(p);
            if (currentColor == colorToReplace) {
                map.put(p, colorToPaint);
                replaces++;
                replaces = floodfill(map, colorToReplace, colorToPaint, new Point(p.x + 1, p.y), replaces);
                replaces = floodfill(map, colorToReplace, colorToPaint, new Point(p.x - 1, p.y), replaces);
                replaces = floodfill(map, colorToReplace, colorToPaint, new Point(p.x, p.y + 1), replaces);
                replaces = floodfill(map, colorToReplace, colorToPaint, new Point(p.x, p.y - 1), replaces);
            }
        }
        return replaces;
    }

    public boolean containsEnemyPoint(Map<Point, Integer> map, Point player, Point enemy) {
        Map<Point, Integer> mapClone = new HashMap<>();
        mapClone.putAll(map);
        mapClone.put(player, -1);
        return getMap(mapClone, -1, -2, player, enemy, false);
    }


    private boolean getMap(Map<Point, Integer> map, int colorToReplace, int colorToPaint, Point p, Point enemy, boolean hasEnemy) {
        if (hasEnemy)
            return true;
        if (map != null && map.containsKey(p)) {
            if (p.equals(enemy)) return true;
            int currentColor = map.get(p);
            if (currentColor == colorToReplace) {
                map.put(p, colorToPaint);
                if (getMap(map, colorToReplace, colorToPaint, new Point(p.x + 1, p.y), enemy, hasEnemy))
                    hasEnemy = true;
                if (getMap(map, colorToReplace, colorToPaint, new Point(p.x - 1, p.y), enemy, hasEnemy))
                    hasEnemy = true;
                if (getMap(map, colorToReplace, colorToPaint, new Point(p.x, p.y + 1), enemy, hasEnemy))
                    hasEnemy = true;
                if (getMap(map, colorToReplace, colorToPaint, new Point(p.x, p.y - 1), enemy, hasEnemy))
                    hasEnemy = true;
            }
        }
        return hasEnemy;
    }
}


enum FieldType {
    Emtpy,
    Box,
    Wall
}