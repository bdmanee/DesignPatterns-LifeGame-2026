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
		getContentPane().add( Universe.instance(), BorderLayout.CENTER); //{=life.java.install}

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
		pack();
		setVisible( true );
	}
}
