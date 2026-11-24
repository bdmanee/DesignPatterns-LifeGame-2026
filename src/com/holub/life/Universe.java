package com.holub.life;

import java.io.*;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import com.holub.io.Files;
import com.holub.ui.MenuSite;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.holub.life.Cell;
import com.holub.life.Storable;
import com.holub.life.Clock;
import com.holub.life.Neighborhood;
import com.holub.life.Resident;

/**
 * The Universe is a mediator that sits between the Swing
 * event model and the Life classes. It is also a singleton,
 * accessed via Universe.instance(). It handles all
 * Swing events and translates them into requests to the
 * outermost Neighborhood. It also creates the Composite
 * Neighborhood.
 *
 * @include /etc/license.txt
 */

public class Universe extends JPanel
{	private 		final Cell  	outermostCell;
	private static	final Universe 	theInstance = new Universe();

	/** The default height and width of a Neighborhood in cells.
	 *  If it's too big, you'll run too slowly because
	 *  you have to update the entire block as a unit, so there's more
	 *  to do. If it's too small, you have too many blocks to check.
	 *  I've found that 8 is a good compromise.
	 */
	private static final int  DEFAULT_GRID_SIZE = 8;

	/** The size of the smallest "atomic" cell---a Resident object.
	 *  This size is extrinsic to a Resident (It's passed into the
	 *  Resident's "draw yourself" method.
	 */
	private static final int  DEFAULT_CELL_SIZE = 8;

	// The constructor is private so that the universe can be created
	// only by an outer-class method [Neighborhood.createUniverse()].
	private LifePattern selectedPattern = null;
	private DrawingMode drawingMode = DrawingMode.CLICK;
	private final PatternStampTool stampTool = new PatternStampTool(4);
	private final List<GameStateListener> gameStateListeners =
			new CopyOnWriteArrayList<>();
	private int generationCount = 0;

	private final ZoomModel zoomModel = new ZoomModel();

	public ZoomModel getZoomModel()		{ return zoomModel; }
	public double getZoomLevel()		{ return zoomModel.getZoomLevel(); }
	public void   setZoomLevel(double z){ zoomModel.setZoomLevel(z);	   }
	public void   zoomIn()				{ zoomModel.zoomIn();			   }
	public void   zoomOut()				{ zoomModel.zoomOut();			   }
	public void   resetZoom()			{ zoomModel.resetZoom();		   }

	private void updateCanvasSize()
	{	int size = (int) Math.round(outermostCell.widthInCells()
				* DEFAULT_CELL_SIZE * zoomModel.getZoomLevel());

		Dimension newSize = new Dimension(size, size);
		setPreferredSize(newSize);
		setMinimumSize(newSize);
		setMaximumSize(newSize);

		revalidate();
		repaint();
	}


	private Universe()
	{	// Create the nested Cells that comprise the "universe." A bug
		// in the current implementation causes the program to fail
		// miserably if the overall size of the grid is too big to fit
		// on the screen.

		outermostCell = new Neighborhood(
				32,
				new Neighborhood(DEFAULT_GRID_SIZE, new Resident())
		);

		setBackground(NeonTheme.BACKGROUND);
		updateCanvasSize();
		zoomModel.addZoomListener(zoom -> updateCanvasSize());

		setOpaque		( true			 );


		addMouseListener(
				new MouseAdapter() {

					@Override
					public void mousePressed(MouseEvent e) {
						if (!SwingUtilities.isLeftMouseButton(e)) {
							return;
						}

						stampTool.start();

						Rectangle bounds = getBounds();

						bounds.x = 0;
						bounds.y = 0;


						if (selectedPattern == null) {
							if (drawingMode == DrawingMode.CLICK) {
								outermostCell.userClicked(e.getPoint(), bounds);
							} else {
								drawCellAt(e.getPoint());
							}
						} else {

							if (bounds.width <= 0 || bounds.height <= 0) {
								return;
							}

							int boardSize = outermostCell.widthInCells();


							int column = e.getX() * boardSize / bounds.width;
							int row = e.getY() * boardSize / bounds.height;

							if (row >= 0 && column >= 0
									&& row < boardSize && column < boardSize) {

								placePattern(selectedPattern, row, column);

								if (drawingMode == DrawingMode.DRAW) {
									stampTool.shouldStamp(row, column);
								}
							}

						}
						notifyGameStateChanged();
						repaint();
					}
				}
		);


		addMouseMotionListener(new MouseMotionAdapter() {

			private Point lastDragPoint = null;

			@Override
			public void mouseMoved(MouseEvent e) {
				lastDragPoint = null;
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (!SwingUtilities.isLeftMouseButton(e)) {
					return;
				}

				if (drawingMode == DrawingMode.DRAW
						&& selectedPattern != null) {

					int boardSize = outermostCell.widthInCells();

					if (getWidth() <= 0 || getHeight() <= 0) {
						return;
					}

					Point currentPoint = e.getPoint();

					if (lastDragPoint == null) {
						lastDragPoint = currentPoint;
					}

					int dx = currentPoint.x - lastDragPoint.x;
					int dy = currentPoint.y - lastDragPoint.y;
					int steps = Math.max(Math.abs(dx), Math.abs(dy));

					for (int i = 0; i <= steps; i++) {
						int x = lastDragPoint.x;
						int y = lastDragPoint.y;

						if (steps > 0) {
							x += dx * i / steps;
							y += dy * i / steps;
						}

						if (x < 0 || y < 0
								|| x >= getWidth() || y >= getHeight()) {
							continue;
						}

						int column = x * boardSize / getWidth();
						int row = y * boardSize / getHeight();

						if (stampTool.shouldStamp(row, column)) {
							placePattern(selectedPattern, row, column);
						}
					}

					lastDragPoint = currentPoint;
					notifyGameStateChanged();
					repaint();

				} else if (drawingMode != DrawingMode.CLICK) {
					drawCellAt(e.getPoint());
				}
			}
		});



		MenuSite.addLine(this, "Patterns", "Normal Click",
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						selectedPattern = null;
					}
				}
		);

		MenuSite.addLine(this, "Patterns", "Glider",
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						selectedPattern = PatternFactory.createPattern("glider");
					}
				}
		);

		MenuSite.addLine(this, "Patterns", "Blinker",
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						selectedPattern = PatternFactory.createPattern("blinker");
					}
				}
		);

		MenuSite.addLine(this, "Patterns", "Block",
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						selectedPattern = PatternFactory.createPattern("block");
					}
				}
		);

		MenuSite.addLine( this, "Grid", "Clear",
			new ActionListener()
			{	public void actionPerformed(ActionEvent e)
			{
				Clock.instance().stop();
				outermostCell.clear();
				generationCount = 0;

				notifyGameStateChanged();
				repaint();
			}
			}
		);

		MenuSite.addLine			// {=Universe.load.setup}
		(	this, "Grid", "Load",
			new ActionListener()
			{	public void actionPerformed(ActionEvent e)
				{	doLoad();
				}
			}
		);

		MenuSite.addLine
		(	this, "Grid", "Store",
			new ActionListener()
			{	public void actionPerformed(ActionEvent e)
				{	doStore();
				}
			}
		);

		MenuSite.addLine
		(	this, "Grid", "Exit",
			new ActionListener()
			{	public void actionPerformed(ActionEvent e)
		        {	System.exit(0);
		        }
			}
		);

		Clock.instance().addClockListener(
				new Clock.Listener() {
					public void tick() {
						generationCount++;


						if (outermostCell.figureNextState(
								Cell.DUMMY, Cell.DUMMY, Cell.DUMMY, Cell.DUMMY,
								Cell.DUMMY, Cell.DUMMY, Cell.DUMMY, Cell.DUMMY
						)) {
							if (outermostCell.transition()) {
								refreshNow();
							}
						}

						notifyGameStateChanged();
					}
				}
		);
	}

	public void placePattern(LifePattern pattern, int startRow, int startColumn) {
		if (pattern == null) {
			throw new IllegalArgumentException("Pattern cannot be null");
		}

		boolean[][] cells = pattern.getCells();

		for (int row = 0; row < cells.length; row++) {
			for (int column = 0; column < cells[row].length; column++) {
				if (cells[row][column]) {
					outermostCell.setAliveAt(
							startRow + row, startColumn + column, true);
				}
			}
		}

		repaint();
	}

	/** Singleton Accessor. The Universe object itself is manufactured
	 *  in Neighborhood.createUniverse()
	 */

	public static Universe instance()
	{	return theInstance;
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

	private void drawCellAt(Point point) {
		if (drawingMode == DrawingMode.CLICK) {
			return;
		}

		if (getWidth() <= 0 || getHeight() <= 0) {
			return;
		}

		int boardSize = outermostCell.widthInCells();
		int row = point.y * boardSize / getHeight();
		int column = point.x * boardSize / getWidth();

		boolean alive = drawingMode == DrawingMode.DRAW;

		outermostCell.setAliveAt(row, column, alive);

		notifyGameStateChanged();
		repaint();
	}

	public void addGameStateListener(GameStateListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException("Listener cannot be null");
		}

		gameStateListeners.add(listener);
	}
	public int getAliveCellCount() {
		AliveCellCounter counter = new AliveCellCounter();
		outermostCell.accept(counter, 0, 0);
		return counter.getCount();
	}

	private void notifyGameStateChanged() {
		int generation = generationCount;
		int aliveCells = getAliveCellCount();

		SwingUtilities.invokeLater(() -> {
			for (GameStateListener listener : gameStateListeners) {
				listener.onGameStateChanged(generation, aliveCells);
			}
		});
	}


	private void doLoad() {
		try {
			FileInputStream in = new FileInputStream(
					Files.userSelected(".", ".life", "Life File", "Load")
			);

			Clock.instance().stop();
			outermostCell.clear();

			Storable memento = outermostCell.createMemento();
			memento.load(in);
			outermostCell.transfer(memento, new Point(0, 0), Cell.LOAD);

			in.close();

			generationCount = 0;

			notifyGameStateChanged();
			repaint();

		} catch (IOException theException) {
			JOptionPane.showMessageDialog(
					null,
					"Read Failed!",
					"The Game of Life",
					JOptionPane.ERROR_MESSAGE
			);

			// Refresh board even if loading fails
			notifyGameStateChanged();
			repaint();
		}
	}

	private void doStore()
	{	try
		{
			FileOutputStream out = new FileOutputStream(
				  Files.userSelected(".",".life","Life File","Write"));

			Clock.instance().stop();		// stop the game

			Storable memento = outermostCell.createMemento();
			outermostCell.transfer( memento, new Point(0,0), Cell.STORE );
			memento.flush(out);

			out.close();
		}
		catch( IOException theException )
		{	JOptionPane.showMessageDialog( null, "Write Failed!",
					"The Game of Life", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Rectangle panelBounds = new Rectangle(
				0, 0, getWidth(), getHeight()
		);

		outermostCell.redraw(g, panelBounds, true);
	}

	/** Request a Swing repaint after the game state changes. */
	private void refreshNow() {
		repaint();
	}

}
