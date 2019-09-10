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
                    else if(row.charAt(j) == 'X'){
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
                        System.err.println("MOVED TO "+x+":"+y);
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

            FloodFill f = new FloodFill();
            Map<Point, FieldType> mapCopy = new HashMap<>(map);
            ArrayList<Point> reachablePoints = f.getConnectedPoints(mapCopy, FieldType.Emtpy, currentPosition);

            reachablePoints.remove(currentPosition);

            boolean ownBomb = false;
            for(Bomb b : bombs){
                Iterator<Point> p = reachablePoints.iterator();
                while(p.hasNext()){
                    Point point = p.next();
                    if(b.isInRange(point))
                        p.remove();
                    if(b.getOwner() == myId)
                        ownBomb = true;
                }

            }

            System.err.println(reachablePoints);

            Point pointToPlace = null;
            double score = 0;
            for(Point p : reachablePoints) {
                double currentScore = calculateScore(map, items, bombs, currentPosition, p);
                if(currentScore > score){
                    pointToPlace = p;
                    score = currentScore;
                }
            }


            if(pointToPlace == null || ownBomb){
                Point finalCurrentPosition = currentPosition;
                Point p = reachablePoints.stream().min(Comparator.comparingInt(a -> distanceToCoordinate(finalCurrentPosition, a))).orElseGet(() -> new Point(0,0));
                p = reachablePoints.get(0);
                System.err.println("MOVING TO "+p);
                System.out.println("MOVE "+(int) p.x+" "+(int) p.y);
            }
            else {
                System.err.println("BOMBING "+pointToPlace);
                System.out.println("BOMB " + pointToPlace.x + " " + pointToPlace.y);
            }

        }
    }

    public static double calculateScore(Map<Point, FieldType> map, ArrayList<Item> items, ArrayList<Bomb> bombs, Point player, Point pointToCalculate){
        for(Bomb b : bombs){
            //TODO If in explosionRange
            if(b.isInRange(player))
                return  -1;
        }
        //Todo also calculate other bombs --> will we destroy first?
        int destroyingBoxes = willDestroyNBoxs(new Bomb(pointToCalculate.x,pointToCalculate.y,8, 0,3), map);
        int distance = distanceToCoordinate(player, pointToCalculate);
        //TODO get a score for the path based on the number of items collectable
        int pathValueBasedOnItems = 1;
        for(Item item : items){
            if(item.getX() == pointToCalculate.getX() && item.getY() == pointToCalculate.getY()){
                pathValueBasedOnItems = 10;
            }
        }
        //TODO do we need more bombs?

        return destroyingBoxes * pathValueBasedOnItems + (10000d / distance);
    }

    public static int willDestroyNBoxs(Bomb bomb, Map<Point, FieldType> map) {
        int destroyedBoxes = 0;
        for (int i = bomb.getX() - bomb.getExplosionrange(); i <= bomb.getX() + bomb.getExplosionrange(); i++) {
            Point p = new Point(i, bomb.getY());
            if (map.containsKey(p) && map.get(p).equals(FieldType.Box)) {
                destroyedBoxes++;
            }
        }
        for (int i = bomb.getY() - bomb.getExplosionrange(); i <= bomb.getY() + bomb.getExplosionrange(); i++) {
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


    public boolean isInRange(Point p){
        for(int i = -explosionrange; i <= explosionrange; i++){
            if(p.equals(new Point(x + i, y))) {
                return true;
            }
            if(p.equals(new Point(x, y + i))) {
                return true;
            }
        }
        return false;
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

    public ArrayList<Point> getConnectedPoints(Map<Point, FieldType> map, FieldType fieldType, Point player){
        Map<Point, FieldType> clone = new HashMap<>(map);
        Map<Point, FieldType> m = f(map,fieldType,player);
        ArrayList<Point> points = new ArrayList<>();
        for(Point p : m.keySet()){
            if(m.get(p) == null && clone.get(p).equals(fieldType)){
                if(!points.contains(p))
                    points.add(p);
            }
        }
        return points;
    }

    private Map<Point, FieldType> f(Map<Point, FieldType> map, FieldType fieldType, Point p){
        if(map.containsKey(p) && map.get(p) != null && map.get(p).equals(fieldType)){
            map.put(p, null);
            map = f(map, fieldType, new Point(p.x + 1, p.y));
            map = f(map, fieldType, new Point(p.x - 1, p.y));
            map = f(map, fieldType, new Point(p.x, p.y + 1));
            map = f(map, fieldType, new Point(p.x, p.y - 1));
        }
        return map;
    }
}


enum FieldType {
    Emtpy,
    Box,
    Wall
}
