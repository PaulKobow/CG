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

        Scanner in = new Scanner(System.in);
        int width = in.nextInt();
        int height = in.nextInt();
        int myId = in.nextInt();

        // game loop
        while (true) {
            for (int i = 0; i < height; i++) {
                String row = in.next();
                for (int j = 0; j < row.length(); j++) {
                    map.put(new Point(j, i), row.charAt(j) == '.' ? FieldType.Emtpy : FieldType.Box);
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
                } else {
                    //Bomb
                    bombs.add(new Bomb(x, y, param1, owner, param2));
                }
            }

            Point pointToPlace = null;
            int score = Integer.MIN_VALUE;
            for(Point p : map.keySet()){
                if(map.get(p).equals(FieldType.Emtpy)){
                    int currentScore = willDestroyNBoxs(new Bomb(p.x, p.y,8,0 , 3), map) * (int) p.distance(currentPosition);
                    if(currentScore > score){
                        pointToPlace = p;
                    }
                }
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            System.out.println("BOMB "+pointToPlace.x+" "+pointToPlace.y);
        }
    }

    public static int willDestroyNBoxs(Bomb bomb, Map<Point, FieldType> map){
        int destroyedBoxes = 0;
        for (int i = bomb.getX() - bomb.getExplosionrange(); i < bomb.getX() + bomb.getExplosionrange(); i++) {
            Point p = new Point(i, bomb.getY());
            if(map.containsKey(p) && map.get(p).equals(FieldType.Box)){
                destroyedBoxes++;
            }
        }
        for (int i = bomb.getY() - bomb.getExplosionrange(); i < bomb.getY() + bomb.getExplosionrange(); i++) {
            Point p = new Point(bomb.getX(), i);
            if(map.containsKey(p) && map.get(p).equals(FieldType.Box)){
                destroyedBoxes++;
            }
        }
        return destroyedBoxes;
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

enum FieldType {
    Emtpy,
    Box
}