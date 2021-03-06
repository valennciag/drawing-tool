package com.drawing.command.receiver;

import com.drawing.board.Board;
import com.drawing.board.Line;
import com.drawing.board.Point;
import com.drawing.board.dao.BoardDAO;
import com.drawing.board.dao.BoardDAOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This is an abstract representation of a receiver in the Command pattern.
 * This class keep common logic to be used across receivers
 */
public abstract class AbstractReceiver {

    protected BoardDAO boardDAO;

    /**
     * Computes the set of points that makes a line in a canvas
     * @param line the line to draw
     * @return a List representing the points that compounds a line
     * @throws ReceiverException if the line is out of canvas
     */
    public List<Point<Integer, Integer>> computePath(Line line) throws ReceiverException {

        List<Point<Integer, Integer>> path = new ArrayList<>();

        if(Line.HORIZONTAL.equals(line.getDirection())){
            for (int i = line.getFrom().getX(); i < line.getTo().getX(); i++) {
                path.add(new Point<>(i,line.getTo().getY()));
            }
            path.add(line.getTo());
        } else if(Line.VERTICAL.equals(line.getDirection())){
            for (int j = line.getFrom().getY(); j < line.getTo().getY(); j++) {
                path.add(new Point<>(line.getTo().getX(),j));
            }
            path.add(line.getTo());
        } else {
            throw new ReceiverException("Line is not properly set.");
        }

        return path;
    }

    /**
     * Defines if a line is on the canvas perimeter
     * @param line the line to evaluate
     * @return true is the line is on the canvas perimeter
     * @throws ReceiverException if there is a problem accessing the board
     */
    public boolean isLineOnCanvas(Line line) throws ReceiverException{
        try {
            Optional<Board> oBoard = this.boardDAO.load();
            boolean isOnCanvas = false;
            if(oBoard.isPresent()){
                Board board = oBoard.get();
                if(this.isPointOnCanvas(line.getFrom()) && this.isPointOnCanvas(line.getTo())){
                    isOnCanvas = true;
                }
            }
            return isOnCanvas;
        } catch (BoardDAOException e) {
            System.out.println(e.getMessage());
            throw new ReceiverException("Cannot define if line on canvas. ");
        }
    }

    /**
     * Defines if a point is on the canvas perimeter
     * @param point the point to evaluate
     * @return true if the point is on canvas perimeter
     * @throws ReceiverException
     */
    public boolean isPointOnCanvas(Point<Integer, Integer> point) throws ReceiverException {
        try {
            Optional<Board> oBoard = this.boardDAO.load();
            boolean isOnCanvas = false;
            if(oBoard.isPresent()){
                Board board = oBoard.get();
                if(point.getX() > 0
                        && point.getX() < board.getWidth()-1
                        && point.getY() > 0
                        && point.getY() < board.getHeight()-1){
                    isOnCanvas = true;
                }
            }
            return isOnCanvas;
        } catch (BoardDAOException e) {
            System.out.println(e.getMessage());
            throw new ReceiverException("Cannot define if point on canvas. ");
        }
    }

    /**
     * Fills a grid with an specific character. This is an implementation of the flood fill
     * recursive algorithm
     * @param x x axis coordinate
     * @param y y axis coordinate
     * @param grid the grid to fill
     * @param filler the character to fill the grid with
     */
    protected void fillGrid(int x, int y, String[][] grid, String filler){
        if (x < 1) return;
        if (y < 1) return;
        if (x >= grid.length-1) return;
        if (y >= grid[x].length-1) return;

        if (filler.equals(grid[x][y])) return;
        if ("x".equals(grid[x][y])) return;

        grid[x][y] = filler;

        fillGrid(x - 1, y, grid, filler);
        fillGrid(x + 1, y, grid, filler);
        fillGrid(x, y - 1, grid, filler);
        fillGrid(x, y + 1, grid, filler);
    }
}
