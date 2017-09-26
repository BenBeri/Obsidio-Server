package com.benberi.cadesim.server.model.cade.map;

import com.benberi.cadesim.server.CadeServer;

import java.io.*;

public enum MapType {

    DEFAULT("default.txt");

    private String name;

    MapType(String name) {
        this.name = name;
    }

    public int[][] load() {
        int[][] map = new int[BlockadeMap.MAP_WIDTH][BlockadeMap.MAP_HEIGHT];

       // File file = new File(CadeServer.class.getResource("resources/maps/default.txt").getPath());
        File file = new File("maps/default.txt");

        int x = 0;
        int y = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                for (String tile : split) {
                    map[x][y] = Integer.parseInt(tile);
                    x++;
                }
                x = 0;
                y++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int[][] finalMap = new int[BlockadeMap.MAP_WIDTH][BlockadeMap.MAP_HEIGHT];

        int x1 = 0;
        int y1 = 0;

        for (int i = 0; i < map.length; i++) {
            for (int j = map[i].length - 1; j > -1; j--) {
                finalMap[x1][y1] = map[i][j];
                y1++;
            }
            y1 = 0;
            x1++;
        }

        return finalMap;
    }
}
