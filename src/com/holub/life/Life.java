package com.holub.life;

import java.awt.*;
import javax.swing.*;
import com.holub.ui.MenuSite;

/*******************************************************************
 * An implemenation of Conway's Game of Life.
 *
 * @include /etc/license.txt
 */

public final class Life extends JFrame
{	
	private static JComponent universe;

	public static void main( String[] arguments )
	{	new Life();
	}

	private Life()
	{	super( "The Game of Life. "
					+"(c)2003 Allen I. Holub <http://www.holub.com>");

		// Must establish the MenuSite very early in case
		// a subcomponent puts menus on it.
		MenuSite.establish( this );		//{=life.java.establish}

		setDefaultCloseOperation	( EXIT_ON_CLOSE 		);
		getContentPane().setLayout	( new BorderLayout()	);

		JScrollPane scrollPane = new JScrollPane(
				Universe.instance(),
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
		);

		scrollPane.getViewport().setBackground(NeonTheme.BACKGROUND);
		scrollPane.setBackground(NeonTheme.BACKGROUND);

		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(16);

		JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
		JScrollBar horizontalBar = scrollPane.getHorizontalScrollBar();

		verticalBar.setBackground(NeonTheme.PANEL);
		horizontalBar.setBackground(NeonTheme.PANEL);

		verticalBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				thumbColor = NeonTheme.PURPLE;
				trackColor = NeonTheme.BACKGROUND;
			}
		});

		horizontalBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				thumbColor = NeonTheme.PURPLE;
				trackColor = NeonTheme.BACKGROUND;
			}
		});

		scrollPane.setPreferredSize(new Dimension(850, 650));
		new ZoomController(Universe.instance(), scrollPane);

		JPanel boardPanel = new JPanel(new BorderLayout());
		boardPanel.add(new ZoomPanel(Universe.instance().getZoomModel()),
					   BorderLayout.NORTH);
		boardPanel.add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(boardPanel, BorderLayout.CENTER);

		GameStatusPanel statusPanel = new GameStatusPanel();

		Universe.instance().addGameStateListener(statusPanel);

		getContentPane().add(statusPanel, BorderLayout.NORTH);
		getContentPane().add(new GameControlPanel(), BorderLayout.SOUTH);

		JMenu rulesMenu = new JMenu("Rules");

		JRadioButtonMenuItem conwayItem =
				new JRadioButtonMenuItem("Conway", true);

		JRadioButtonMenuItem highLifeItem =
				new JRadioButtonMenuItem("HighLife");

		ButtonGroup ruleGroup = new ButtonGroup();
		ruleGroup.add(conwayItem);
		ruleGroup.add(highLifeItem);

		conwayItem.addActionListener(e -> {
			RuleManager.setRule(new ConwayRule());
			setTitle("Game of Life - Conway");
		});

		highLifeItem.addActionListener(e -> {
			RuleManager.setRule(new HighLifeRule());
			setTitle("Game of Life - HighLife");
		});

		rulesMenu.add(conwayItem);
		rulesMenu.add(highLifeItem);

		getJMenuBar().add(rulesMenu);

// Apply neon styling to all menus
		NeonTheme.applyMenuTheme(getJMenuBar());

		getContentPane().setBackground(NeonTheme.BACKGROUND);

		pack();
		setVisible(true);


	}
}
