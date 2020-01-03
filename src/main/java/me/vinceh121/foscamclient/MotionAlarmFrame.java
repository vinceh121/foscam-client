package me.vinceh121.foscamclient;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class MotionAlarmFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTabbedPane tabbedPane;
	private SelectiveTableComponent tableSchedule;
	private SelectiveTableComponent tableMotion;

	/**
	 * Create the frame.
	 */
	public MotionAlarmFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		tableSchedule = new SelectiveTableComponent();
		tableSchedule.setRows(7);
		tableSchedule.setCollumns(47);
		tableSchedule.setColumnNames(generateTimeColumnName());
		tableSchedule.setSquareSize(getSquareSize());
		tableSchedule.setRowNames(
				new String[] { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" });
		tabbedPane.addTab("Schedule", null, tableSchedule, null);

		tableMotion = new SelectiveTableComponent();
		tableMotion.setRows(10);
		tableMotion.setCollumns(10);
		tableMotion.setSquareSize(20);
		tableMotion.setImage(App.getInstance().getMediaPlayerComp().mediaPlayer().snapshots().get());
		tabbedPane.addTab("Motion Alarm Configuration", null, tableMotion, null);

	}

	private String[] generateTimeColumnName() {
		List<String> l = new ArrayList<String>();
		Calendar c = new Calendar.Builder().setTimeOfDay(0, 0, 0).build();
		SimpleDateFormat df = new SimpleDateFormat("H:m");
		for (int i = 0; i <= 47; i++) {
			l.add(df.format(c.getTime()));
			c.add(Calendar.MINUTE, 30);
		}
		return l.toArray(new String[l.size()]);
	}

	private int getSquareSize() {
		JLabel lbl = new JLabel();
		FontMetrics f = lbl.getFontMetrics(lbl.getFont());
		int max = 0;
		for (String s : tableSchedule.getColumnNames()) {
			int i = f.stringWidth(s);
			if (i > 0) {
				max = i;
			}
		}
		return max;
	}


}
