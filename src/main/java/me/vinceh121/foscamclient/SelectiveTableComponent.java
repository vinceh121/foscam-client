package me.vinceh121.foscamclient;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

public class SelectiveTableComponent extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private BufferedImage image;
	private int rows, columns, squareSize = 10;
	private List<Point> selectedPoints = new ArrayList<Point>();
	private String[] columnNames, rowNames;

	private int maxStringWidth;

	private int maxStringHeight;

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image != null)
			g.drawImage(image, 0, 0, getWidth(), getHeight(), this);

		maxStringWidth = 0;
		maxStringHeight = g.getFontMetrics().getHeight();

		g.setColor(Color.BLACK);

		if (rowNames != null) {
			for (int y = maxStringHeight; y < rowNames.length * squareSize; y += squareSize) {
				String s = rowNames[y / squareSize];
				g.drawString(s, 0, y + maxStringHeight);
				int width = g.getFontMetrics().stringWidth(s);
				if (width > maxStringWidth)
					maxStringWidth = width;
			}
		}

		if (columnNames != null) {
			for (int x = maxStringWidth; x < columnNames.length * squareSize; x += squareSize) {
				g.drawString(columnNames[x / squareSize], x, maxStringHeight);
			}
		}

		// Draw boxes
		g.setColor(Color.BLACK);
		for (int x = maxStringWidth; x <= columns * squareSize; x += squareSize) {
			for (int y = maxStringHeight; y <= rows * squareSize; y += squareSize) {
				g.drawRect(x, y, squareSize, squareSize);
			}
		}

		// Draw selected points
		g.setColor(new Color(0, 0, 255, 126));
		for (int x = maxStringWidth; x <= columns * squareSize; x += squareSize) {
			for (int y = maxStringHeight; y <= rows * squareSize; y += squareSize) {
				if (isPointSelected(x / squareSize, y / squareSize)) {
					g.fillRect(x, y, squareSize, squareSize);
				}
			}
		}

	}

	public SelectiveTableComponent() {
		super();
		addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				togglePoint(e.getX() / squareSize, e.getY() / squareSize);
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				togglePoint(e.getX() / squareSize, e.getY() / squareSize);
			}
		});
	}

	public void togglePoint(int x, int y) {
		if (x < 0 || y < 0) {
			return;
		}
		int pos = getPointPosition(x, y);
		if (pos == -1) {
			selectedPoints.add(new Point(x, y));
		} else {
			selectedPoints.remove(pos);
		}

		repaint();
	}

	public int getPointPosition(int x, int y) {
		for (int i = 0; i <= selectedPoints.size(); i++) {
			try {
				Point p = selectedPoints.get(i);
				if (p.getX() == x && p.getY() == y) {
					return i;
				}
			} catch (IndexOutOfBoundsException e) {
				return -1;
			}
		}
		return -1;
	}

	public boolean isPointSelected(int x, int y) {
		for (Point p : selectedPoints) {
			if (p.getX() == x && p.getY() == y) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the image
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(BufferedImage image) {
		this.image = image;
	}

	/**
	 * @return the collumns
	 */
	public int getCollumns() {
		return columns;
	}

	/**
	 * @param collumns the collumns to set
	 */
	public void setCollumns(int collumns) {
		this.columns = collumns;
	}

	/**
	 * @return the rows
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * @param rows the rows to set
	 */
	public void setRows(int rows) {
		this.rows = rows;
	}

	/**
	 * @return the squareSize
	 */
	public int getSquareSize() {
		return squareSize;
	}

	/**
	 * @param squareSize the squareSize to set
	 */
	public void setSquareSize(int squareSize) {
		this.squareSize = squareSize;
	}

	/**
	 * @return the selectedPoints
	 */
	public List<Point> getSelectedPoints() {
		return selectedPoints;
	}

	/**
	 * @return the columnNames
	 */
	public String[] getColumnNames() {
		return columnNames;
	}

	/**
	 * @param columnNames the columnNames to set
	 */
	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}

	/**
	 * @return the rowNames
	 */
	public String[] getRowNames() {
		return rowNames;
	}

	/**
	 * @param rowNames the rowNames to set
	 */
	public void setRowNames(String[] rowNames) {
		this.rowNames = rowNames;
	}

}
