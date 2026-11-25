package com.holub.life;

import java.io.*;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import com.holub.io.Files;
import com.holub.ui.MenuSite;

/**
 * The Universe is a mediator that sits between the Swing
 * event model and the Life classes. It is also a singleton,
 * accessed via Universe.instance(). It handles all
 * Swing events and translates them into requests to the
 * LifeBoard, which is the model of the game. The Universe
 * itself only draws the board and handles the mouse.
 *
 * @include /etc/license.txt
 */

public class Universe extends JPanel
{	private static	final Universe 	theInstance = new Universe();

	/** The size of the smallest "atomic" cell---a Resident object---in
	 *  pixels at 100% zoom.
	 */
	private static final int  DEFAULT_CELL_SIZE = 8;

	private final LifeBoard board = new LifeBoard();
	private final ZoomModel zoomModel = new ZoomModel();

	private LifePattern selectedPattern = null;
	private DrawingMode drawingMode = DrawingMode.CLICK;
	private final PatternStampTool stampTool = new PatternStampTool(4);

	private Universe()
	{
		setBackground(NeonTheme.BACKGROUND);
		setOpaque(true);
		updateCanvasSize();

		// The view observes the model and repaints itself
		// whenever the board changes.
		board.addGameStateListener((generation, aliveCells) -> repaint());
		zoomModel.addZoomListener(zoom -> updateCanvasSize());

		MouseAdapter mouseHandler = new MouseAdapter()
		{
			private Point lastDragPoint = null;

			@Override
			public void mousePressed(MouseEvent e)
			{	if (!SwingUtilities.isLeftMouseButton(e))
					return;

				stampTool.start();
				lastDragPoint = e.getPoint();

				Point cell = toCell(e.getPoint());
				if (cell == null)
					return;

				if (selectedPattern != null) {
					board.placePattern(selectedPattern, cell.y, cell.x);
					stampTool.shouldStamp(cell.y, cell.x);
				} else if (drawingMode == DrawingMode.CLICK) {
					board.toggle(cell.y, cell.x);
				} else {
					board.setAlive(cell.y, cell.x,
							drawingMode == DrawingMode.DRAW);
				}
			}

			@Override
			public void mouseDragged(MouseEvent e)
			{	if (!SwingUtilities.isLeftMouseButton(e))
					return;

				if (lastDragPoint == null)
					lastDragPoint = e.getPoint();

				// Fill in the cells between two mouse events so a
				// fast drag does not leave gaps.
				Point from = lastDragPoint;
				Point to = e.getPoint();
				int steps = Math.max(Math.abs(to.x - from.x),
									 Math.abs(to.y - from.y));

				for (int i = 0; i <= steps; i++) {
					int x = from.x;
					int y = from.y;
					if (steps > 0) {
						x += (to.x - from.x) * i / steps;
						y += (to.y - from.y) * i / steps;
					}

					Point cell = toCell(new Point(x, y));
					if (cell != null)
						dragOver(cell);
				}

				lastDragPoint = to;
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{	lastDragPoint = null;
			}
		};

		addMouseListener(mouseHandler);
		addMouseMotionListener(mouseHandler);

		MenuSite.addLine(this, "Patterns", "Normal Click",
				e -> selectedPattern = null);
		MenuSite.addLine(this, "Patterns", "Rotate Selected",
				e -> {
					if (selectedPattern != null)
						selectedPattern = selectedPattern.rotateClockwise();
				});
		MenuSite.addLine(this, "Patterns", "Flip Selected",
				e -> {
					if (selectedPattern != null)
						selectedPattern = selectedPattern.flipHorizontal();
				});

		// One menu item per prototype. Patterns registered later
		// (for example imported from a file) get an item too.
		PatternRegistry patterns = PatternRegistry.getDefault();
		for (String name : patterns.getNames())
			addPatternMenuItem(name);
		patterns.addListener(pattern -> addPatternMenuItem(pattern.getName()));

		MenuSite.addLine( this, "Grid", "Clear",
				e -> {
					Clock.instance().stop();
					board.clear();
				});

		MenuSite.addLine( this, "Grid", "Load",  e -> doLoad()  );
		MenuSite.addLine( this, "Grid", "Store", e -> doStore() );
		MenuSite.addLine( this, "Grid", "Exit",  e -> System.exit(0) );

		Clock.instance().addClockListener( () -> board.tick() );
	}

	private void addPatternMenuItem(String name)
	{	MenuSite.addLine(this, "Patterns", name,
				e -> selectedPattern = PatternRegistry.getDefault().create(name));
	}

	public LifePattern getSelectedPattern()
	{	return selectedPattern;
	}

	/** Singleton Accessor. */
	public static Universe instance()
	{	return theInstance;
	}

	public LifeBoard getBoard()
	{	return board;
	}

	public ZoomModel getZoomModel()
	{	return zoomModel;
	}

	/** Convert a pixel position on the panel to a board position.
	 *  Returns null when the point is outside the board.
	 *  The returned Point has x = column and y = row.
	 */
	private Point toCell(Point pixel)
	{	if (getWidth() <= 0 || getHeight() <= 0)
			return null;

		int size = board.getSize();
		int column = pixel.x * size / getWidth();
		int row = pixel.y * size / getHeight();

		if (pixel.x < 0 || pixel.y < 0 || row >= size || column >= size)
			return null;

		return new Point(column, row);
	}

	private void dragOver(Point cell)
	{	if (selectedPattern != null) {
			if (drawingMode == DrawingMode.DRAW
					&& stampTool.shouldStamp(cell.y, cell.x))
				board.placePattern(selectedPattern, cell.y, cell.x);
		} else if (drawingMode != DrawingMode.CLICK) {
			board.setAlive(cell.y, cell.x, drawingMode == DrawingMode.DRAW);
		}
	}

	public void placePattern(LifePattern pattern, int startRow, int startColumn)
	{	board.placePattern(pattern, startRow, startColumn);
	}

	public void setDrawingMode(DrawingMode mode) {
		if (mode == null) {
			throw new IllegalArgumentException("Drawing mode cannot be null");
		}

		this.drawingMode = mode;

		if (mode == DrawingMode.ERASE) {
			selectedPattern = null;
		}
	}

	public void setPatternSpacing(int spacing) {
		stampTool.setSpacing(spacing);
	}

	public int getAliveCellCount() {
		return board.getAliveCellCount();
	}

	public void addGameStateListener(GameStateListener listener) {
		board.addGameStateListener(listener);
	}

	// ---- zoom ------------------------------------------------------

	public double getZoomLevel()		{ return zoomModel.getZoomLevel(); }
	public void   setZoomLevel(double z){ zoomModel.setZoomLevel(z);	   }
	public void   zoomIn()				{ zoomModel.zoomIn();			   }
	public void   zoomOut()				{ zoomModel.zoomOut();			   }
	public void   resetZoom()			{ zoomModel.resetZoom();		   }

	private void updateCanvasSize()
	{	int size = (int) Math.round(
				board.getSize() * DEFAULT_CELL_SIZE * zoomModel.getZoomLevel());

		Dimension newSize = new Dimension(size, size);
		setPreferredSize(newSize);
		setMinimumSize(newSize);
		setMaximumSize(newSize);

		revalidate();
		repaint();
	}

	// ---- load / store ----------------------------------------------

	private void doLoad()
	{	try
		{	File file = Files.userSelected(".", ".life", "Life File", "Load");
			Clock.instance().stop();

			try (FileInputStream in = new FileInputStream(file)) {
				board.load(in);
			}
		}
		catch( IOException theException )
		{	JOptionPane.showMessageDialog( null, "Read Failed!",
					"The Game of Life", JOptionPane.ERROR_MESSAGE);
			repaint();
		}
	}

	private void doStore()
	{	try
		{	File file = Files.userSelected(".", ".life", "Life File", "Write");
			Clock.instance().stop();

			try (FileOutputStream out = new FileOutputStream(file)) {
				board.store(out);
			}
		}
		catch( IOException theException )
		{	JOptionPane.showMessageDialog( null, "Write Failed!",
					"The Game of Life", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		board.redraw(g, new Rectangle(0, 0, getWidth(), getHeight()));
	}
}
