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
	private final List<GameStateListener> gameStateListeners =
			new CopyOnWriteArrayList<>();
	private int generationCount = 0;

	private Universe()
	{	// Create the nested Cells that comprise the "universe." A bug
		// in the current implementation causes the program to fail
		// miserably if the overall size of the grid is too big to fit
		// on the screen.

		outermostCell = new Neighborhood
						(	DEFAULT_GRID_SIZE,
							new Neighborhood
							(	DEFAULT_GRID_SIZE,
								new Resident()
							)
						);

		final Dimension PREFERRED_SIZE =
						new Dimension
						(  outermostCell.widthInCells() * DEFAULT_CELL_SIZE,
						   outermostCell.widthInCells() * DEFAULT_CELL_SIZE
						);

		addComponentListener
		(	new ComponentAdapter()
			{	public void componentResized(ComponentEvent e)
				{
					// Make sure that the cells fit evenly into the
					// total grid size so that each cell will be the
					// same size. For example, in a 64x64 grid, the
					// total size must be an even multiple of 63.

					Rectangle bounds = getBounds();
					bounds.height /= outermostCell.widthInCells();
					bounds.height *= outermostCell.widthInCells();
					bounds.width  =  bounds.height;
					setBounds( bounds );
				}
			}
		);

		setBackground	( Color.white	 );
		setPreferredSize( PREFERRED_SIZE );
		setMaximumSize	( PREFERRED_SIZE );
		setMinimumSize	( PREFERRED_SIZE );
		setOpaque		( true			 );


		addMouseListener(
				new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						Rectangle bounds = getBounds();
						bounds.x = 0;
						bounds.y = 0;

						if (selectedPattern == null) {
							outermostCell.userClicked(e.getPoint(), bounds);
						} else {
							if (bounds.width <= 0 || bounds.height <= 0) {
								return;
							}

							int boardSize = outermostCell.widthInCells();

							int column = e.getX() * boardSize / bounds.width;
							int row = e.getY() * boardSize / bounds.height;

							placePattern(selectedPattern, row, column);
						}
						notifyGameStateChanged();
						repaint();
					}
				}
		);


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

		int boardSize = outermostCell.widthInCells();
		Rectangle bounds = new Rectangle(
				0, 0, getWidth(), getHeight()
		);

		if (bounds.width <= 0 || bounds.height <= 0) {
			return;
		}

		for (int row = 0; row < cells.length; row++) {
			for (int column = 0; column < cells[row].length; column++) {

				if (!cells[row][column]) {
					continue;
				}

				int targetRow = startRow + row;
				int targetColumn = startColumn + column;

				if (targetRow < 0 || targetColumn < 0 ||
						targetRow >= boardSize ||
						targetColumn >= boardSize) {
					continue;
				}

				int x = (int) ((targetColumn + 0.5)
						* bounds.width / boardSize);

				int y = (int) ((targetRow + 0.5)
						* bounds.height / boardSize);

				((Neighborhood) outermostCell).setAliveAt(
						new Point(x, y), bounds
				);
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
	public void addGameStateListener(GameStateListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException("Listener cannot be null");
		}

		gameStateListeners.add(listener);
	}
	public int getAliveCellCount() {
		return ((Neighborhood) outermostCell).countAliveCells();
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

	/** Override paint to ask the outermost Neighborhood
	 *  (and any subcells) to draw themselves recursively.
	 *  All knowledge of screen size is also encapsulated.
	 *  (The size is passed into the outermost <code>Cell</code>.)
	 */

	public void paint(Graphics g)
	{
		Rectangle panelBounds = getBounds();
		Rectangle clipBounds  = g.getClipBounds();

		// The panel bounds is relative to the upper-left
		// corner of the screen. Pretend that it's at (0,0)
		panelBounds.x = 0;
		panelBounds.y = 0;
		outermostCell.redraw(g, panelBounds, true);		//{=Universe.redraw1}
	}

	/** Force a screen refresh by queing a request on
	 *  the Swing event queue. This is an example of the
	 *  Active Object pattern (not covered by the Gang of Four).
	 *  This method is called on every clock tick. Note that
	 *  the redraw() method on a given <code>Cell</code>
	 *  does nothing if the <code>Cell</code> doesn't
	 *  have to be refreshed.
	 */

	private void refreshNow()
	{	SwingUtilities.invokeLater
		(	new Runnable()
			{	public void run()
				{	Graphics g = getGraphics();
					if( g == null )		// Universe not displayable
						return;
					try
					{
						Rectangle panelBounds = getBounds();
						panelBounds.x = 0;
						panelBounds.y = 0;
						outermostCell.redraw(g, panelBounds, false); //{=Universe.redraw2}
					}
					finally
					{	g.dispose();
					}
				}
			}
		);
	}
}
