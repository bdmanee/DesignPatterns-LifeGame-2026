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

		Universe.instance().getBoard().addGameStateListener(statusPanel);

		getContentPane().add(statusPanel, BorderLayout.NORTH);

		StatisticsTracker statistics =
				new StatisticsTracker(Universe.instance().getBoard());
		getContentPane().add(new StatisticsPanel(statistics), BorderLayout.EAST);
		getContentPane().add(new GameControlPanel(), BorderLayout.SOUTH);

		JMenu rulesMenu = createRulesMenu();
		getJMenuBar().add(rulesMenu);

// Apply neon styling to all menus
		NeonTheme.applyMenuTheme(getJMenuBar());

		getContentPane().setBackground(NeonTheme.BACKGROUND);

		pack();
		setVisible(true);


	}

	/** Build the Rules menu from the RuleCatalog, plus a "Custom..."
	 *  item that reads a B/S rule typed by the user.
	 */
	private JMenu createRulesMenu()
	{	JMenu rulesMenu = new JMenu("Rules");
		ButtonGroup ruleGroup = new ButtonGroup();

		for (LifeRule rule : RuleCatalog.presets()) {
			rulesMenu.add(addRuleItem(rulesMenu, ruleGroup, rule,
					rule.getName().equals(RuleManager.getRule().getName())));
		}

		rulesMenu.addSeparator();

		JMenuItem customItem = new JMenuItem("Custom (B/S)...");
		customItem.addActionListener(e -> {
			String notation = JOptionPane.showInputDialog(this,
					"Enter a rule like B36/S23", "Custom Rule",
					JOptionPane.QUESTION_MESSAGE);

			if (notation == null || notation.trim().isEmpty())
				return;

			try {
				LifeRule rule = RuleBuilder.fromNotation(notation).build();
				// put it above the separator and the "Custom..." item
				rulesMenu.insert(addRuleItem(rulesMenu, ruleGroup, rule, true),
								 rulesMenu.getItemCount() - 2);
				useRule(rule);
				NeonTheme.applyMenuTheme(getJMenuBar());
			} catch (RuntimeException ex) {
				JOptionPane.showMessageDialog(this, ex.getMessage(),
						"Invalid Rule", JOptionPane.ERROR_MESSAGE);
			}
		});
		rulesMenu.add(customItem);

		return rulesMenu;
	}

	private JRadioButtonMenuItem addRuleItem(JMenu menu, ButtonGroup group,
											 LifeRule rule, boolean selected)
	{	JRadioButtonMenuItem item =
				new JRadioButtonMenuItem(rule.getName(), selected);
		item.addActionListener(e -> useRule(rule));
		group.add(item);
		return item;
	}

	private void useRule(LifeRule rule)
	{	RuleManager.setRule(rule);
		setTitle("Game of Life - " + rule.getName());
	}
}
