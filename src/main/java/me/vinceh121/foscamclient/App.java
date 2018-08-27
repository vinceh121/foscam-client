package me.vinceh121.foscamclient;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.plaf.synth.SynthLookAndFeel;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.w3c.dom.Element;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import java.awt.Toolkit;
import javax.swing.SwingConstants;
import javax.swing.JCheckBoxMenuItem;

public class App extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel fieldProductName;
	private JLabel fieldSerialN;
	private JLabel fieldMac;
	private JLabel fieldDeviceTime;
	private JLabel fieldFirmware;
	private JLabel fieldHardwareVersion;
	private JLabel fieldDeviceName;
	private static EmbeddedMediaPlayerComponent player;
	private JToggleButton tglbtnInfrared;
	private JToggleButton tglbtnAutoinfrared;
	private JToggleButton tglbtnRecord;
	private JToggleButton tglbtnMirror;
	private JToggleButton tglbtnFlip;
	private JCheckBoxMenuItem chckbxmntmEnableAlarm;
	private AlarmThread alarmThread = new AlarmThread();
	private MotionAlarmFrame motionFrame;
	private JMenuItem mntmScheduleMotion;

	private App() {
		setIconImage(
				Toolkit.getDefaultToolkit().getImage(App.class.getResource("/com/famfamfam/icons/silk/webcam.png")));
		setTitle("Foscam");
		setSize(560, 400);
		setLocationRelativeTo(null);

		CGIManager.getInstance().init();

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmConnect = new JMenuItem("Connect");
		mntmConnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ConnectionPopup p = new ConnectionPopup();
				p.setVisible(true);

				CGIManager.getInstance().setHost(p.getHost());
				CGIManager.getInstance().setPort(p.getPort());
				CGIManager.getInstance().setUsername(p.getUsername());
				CGIManager.getInstance().setPassword(p.getPassword());

				try {
					updateStats();

					String ir = CGIManager.getInstance().execCmd("getInfraLedConfig").getElementsByTagName("mode")
							.item(0).getTextContent();
					if (ir.equals("0")) {
						tglbtnAutoinfrared.setSelected(true);
					} else {
						tglbtnAutoinfrared.setSelected(false);
					}
				} catch (IOException | URISyntaxException | CGIException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, e1.getLocalizedMessage());
					return;
				}
				player.getMediaPlayer().playMedia(CGIManager.getInstance().getRtspMrl());
				mntmScheduleMotion.setEnabled(true);
				chckbxmntmEnableAlarm.setEnabled(true);
			}
		});
		mnFile.add(mntmConnect);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		chckbxmntmEnableAlarm = new JCheckBoxMenuItem("Enable alarm");
		chckbxmntmEnableAlarm.setEnabled(false);
		chckbxmntmEnableAlarm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (chckbxmntmEnableAlarm.isSelected()) {
					alarmThread.start();
				} else {
					alarmThread.interrupt();
				}
			}
		});
		chckbxmntmEnableAlarm.setSelected(false);
		mnFile.add(chckbxmntmEnableAlarm);

		mntmScheduleMotion = new JMenuItem("Schedule & Motion config.");
		mntmScheduleMotion.setEnabled(false);
		mntmScheduleMotion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				motionFrame = new MotionAlarmFrame();
				motionFrame.setVisible(true);
			}
		});
		mnFile.add(mntmScheduleMotion);
		mnFile.add(mntmExit);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		JPanel statusPanel = new JPanel();
		tabbedPane.addTab("Status", new ImageIcon(App.class.getResource("/com/famfamfam/icons/silk/information.png")),
				statusPanel, null);
		tabbedPane.setEnabledAt(0, true);
		GridBagLayout gbl_statusPanel = new GridBagLayout();
		gbl_statusPanel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_statusPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_statusPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_statusPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		statusPanel.setLayout(gbl_statusPanel);

		JLabel lblProductName = new JLabel("Product Name:");
		GridBagConstraints gbc_lblProductName = new GridBagConstraints();
		gbc_lblProductName.insets = new Insets(0, 0, 5, 5);
		gbc_lblProductName.gridx = 0;
		gbc_lblProductName.gridy = 0;
		statusPanel.add(lblProductName, gbc_lblProductName);

		fieldProductName = new JLabel("");
		GridBagConstraints gbc_fieldProductName = new GridBagConstraints();
		gbc_fieldProductName.insets = new Insets(0, 0, 5, 5);
		gbc_fieldProductName.gridx = 1;
		gbc_fieldProductName.gridy = 0;
		statusPanel.add(fieldProductName, gbc_fieldProductName);

		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.setIcon(new ImageIcon(App.class.getResource("/com/famfamfam/icons/silk/arrow_refresh.png")));
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					updateStats();
				} catch (IOException | URISyntaxException | CGIException e) {
					JOptionPane.showMessageDialog(null,
							"An error occured while refreshing. Make sure you are connected to a device.");
					e.printStackTrace();
				}
			}
		});
		GridBagConstraints gbc_btnRefresh = new GridBagConstraints();
		gbc_btnRefresh.insets = new Insets(0, 0, 5, 5);
		gbc_btnRefresh.gridx = 2;
		gbc_btnRefresh.gridy = 0;
		statusPanel.add(btnRefresh, gbc_btnRefresh);

		JLabel lblSerialN = new JLabel("Serial N°:");
		GridBagConstraints gbc_lblSerialN = new GridBagConstraints();
		gbc_lblSerialN.insets = new Insets(0, 0, 5, 5);
		gbc_lblSerialN.gridx = 0;
		gbc_lblSerialN.gridy = 1;
		statusPanel.add(lblSerialN, gbc_lblSerialN);

		fieldSerialN = new JLabel("");
		GridBagConstraints gbc_fieldSerialN = new GridBagConstraints();
		gbc_fieldSerialN.insets = new Insets(0, 0, 5, 5);
		gbc_fieldSerialN.gridx = 1;
		gbc_fieldSerialN.gridy = 1;
		statusPanel.add(fieldSerialN, gbc_fieldSerialN);

		JLabel lblDeviceName = new JLabel("Device Name:");
		GridBagConstraints gbc_lblDeviceName = new GridBagConstraints();
		gbc_lblDeviceName.insets = new Insets(0, 0, 5, 5);
		gbc_lblDeviceName.gridx = 0;
		gbc_lblDeviceName.gridy = 2;
		statusPanel.add(lblDeviceName, gbc_lblDeviceName);

		fieldDeviceName = new JLabel("");
		GridBagConstraints gbc_fieldDeviceName = new GridBagConstraints();
		gbc_fieldDeviceName.insets = new Insets(0, 0, 5, 5);
		gbc_fieldDeviceName.gridx = 1;
		gbc_fieldDeviceName.gridy = 2;
		statusPanel.add(fieldDeviceName, gbc_fieldDeviceName);

		JLabel lblMac = new JLabel("MAC:");
		GridBagConstraints gbc_lblMac = new GridBagConstraints();
		gbc_lblMac.insets = new Insets(0, 0, 5, 5);
		gbc_lblMac.gridx = 0;
		gbc_lblMac.gridy = 3;
		statusPanel.add(lblMac, gbc_lblMac);

		fieldMac = new JLabel("");
		GridBagConstraints gbc_fieldMac = new GridBagConstraints();
		gbc_fieldMac.insets = new Insets(0, 0, 5, 5);
		gbc_fieldMac.gridx = 1;
		gbc_fieldMac.gridy = 3;
		statusPanel.add(fieldMac, gbc_fieldMac);

		JLabel lblDeviceTime = new JLabel("Device Time:");
		GridBagConstraints gbc_lblDeviceTime = new GridBagConstraints();
		gbc_lblDeviceTime.insets = new Insets(0, 0, 5, 5);
		gbc_lblDeviceTime.gridx = 0;
		gbc_lblDeviceTime.gridy = 4;
		statusPanel.add(lblDeviceTime, gbc_lblDeviceTime);

		fieldDeviceTime = new JLabel("");
		GridBagConstraints gbc_fieldDeviceTime = new GridBagConstraints();
		gbc_fieldDeviceTime.insets = new Insets(0, 0, 5, 5);
		gbc_fieldDeviceTime.gridx = 1;
		gbc_fieldDeviceTime.gridy = 4;
		statusPanel.add(fieldDeviceTime, gbc_fieldDeviceTime);

		JLabel lblFirmwareVersion = new JLabel("Firmware Version:");
		GridBagConstraints gbc_lblFirmwareVersion = new GridBagConstraints();
		gbc_lblFirmwareVersion.insets = new Insets(0, 0, 5, 5);
		gbc_lblFirmwareVersion.gridx = 0;
		gbc_lblFirmwareVersion.gridy = 5;
		statusPanel.add(lblFirmwareVersion, gbc_lblFirmwareVersion);

		fieldFirmware = new JLabel("");
		GridBagConstraints gbc_fieldFirmware = new GridBagConstraints();
		gbc_fieldFirmware.insets = new Insets(0, 0, 5, 5);
		gbc_fieldFirmware.gridx = 1;
		gbc_fieldFirmware.gridy = 5;
		statusPanel.add(fieldFirmware, gbc_fieldFirmware);

		JLabel lblHadwareVersion = new JLabel("Hadware Version:");
		GridBagConstraints gbc_lblHadwareVersion = new GridBagConstraints();
		gbc_lblHadwareVersion.insets = new Insets(0, 0, 0, 5);
		gbc_lblHadwareVersion.gridx = 0;
		gbc_lblHadwareVersion.gridy = 6;
		statusPanel.add(lblHadwareVersion, gbc_lblHadwareVersion);

		fieldHardwareVersion = new JLabel("");
		GridBagConstraints gbc_fieldHardwareVersion = new GridBagConstraints();
		gbc_fieldHardwareVersion.insets = new Insets(0, 0, 0, 5);
		gbc_fieldHardwareVersion.gridx = 1;
		gbc_fieldHardwareVersion.gridy = 6;
		statusPanel.add(fieldHardwareVersion, gbc_fieldHardwareVersion);

		JPanel viewPanel = new JPanel();
		tabbedPane.addTab("View", new ImageIcon(App.class.getResource("/com/famfamfam/icons/silk/webcam.png")),
				viewPanel, null);
		viewPanel.setLayout(new BorderLayout(0, 0));

		player = new EmbeddedMediaPlayerComponent();
		player.getVideoSurface().addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == 's') {
					player.getMediaPlayer().saveSnapshot();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});
		viewPanel.add(player);

		JToolBar toolBar = new JToolBar();
		viewPanel.add(toolBar, BorderLayout.SOUTH);

		tglbtnInfrared = new JToggleButton("Infrared");
		tglbtnInfrared.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (tglbtnAutoinfrared.isSelected()) {
					JOptionPane.showMessageDialog(null,
							"You can't set the infrared light state when auto-ifrared is enabled.");
				}
				try {
					if (tglbtnInfrared.isSelected()) {
						CGIManager.getInstance().execCmd("openInfraLed");
					} else {
						CGIManager.getInstance().execCmd("closeInfraLed");
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,
							"Failed to open/close infrared. Are you connected to a device?\nError message: "
									+ e.getMessage());
					e.printStackTrace();
				}
			}
		});
		toolBar.add(tglbtnInfrared);

		tglbtnAutoinfrared = new JToggleButton("Auto-Infrared");
		tglbtnAutoinfrared.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (tglbtnAutoinfrared.isSelected()) {
						CGIManager.getInstance().execCmd("setInfraLedConfig", new NameValuePair() {

							@Override
							public String getValue() {
								return "0";
							}

							@Override
							public String getName() {
								return "mode";
							}
						});
					} else {
						CGIManager.getInstance().execCmd("setInfraLedConfig", new NameValuePair() {

							@Override
							public String getValue() {
								return "1";
							}

							@Override
							public String getName() {
								return "mode";
							}
						});
					}
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		});
		toolBar.add(tglbtnAutoinfrared);

		JButton btnSnap = new JButton("Snap");
		btnSnap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!player.getMediaPlayer().saveSnapshot()) {
					JOptionPane.showMessageDialog(null, "Could not save snapshot");
				}
			}
		});
		btnSnap.setIcon(new ImageIcon(App.class.getResource("/com/famfamfam/icons/silk/picture_save.png")));
		toolBar.add(btnSnap);

		tglbtnRecord = new JToggleButton("Record");
		tglbtnRecord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (tglbtnRecord.isSelected()) {
					boolean b = player.getMediaPlayer().playMedia(CGIManager.getInstance().getRtspMrl(),
							":sout=#transcode{vcodec=mp4v,vb=4096,scale=1,acodec=mpga,ab=128,channels=2,samplerate=44100}:duplicate{dst=file{dst="
									+ JOptionPane.showInputDialog("File name (*.mp4)") + "},dst=display}");
					if (!b) {
						JOptionPane.showConfirmDialog(null, "Error starting the recording.");
					}
				} else {
					player.getMediaPlayer().stop();
					player.getMediaPlayer().playMedia(CGIManager.getInstance().getRtspMrl());
				}
			}
		});
		tglbtnRecord.setIcon(new ImageIcon(App.class.getResource("/com/famfamfam/icons/silk/page_white_camera.png")));
		toolBar.add(tglbtnRecord);

		tglbtnMirror = new JToggleButton("Mirror");
		tglbtnMirror.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					CGIManager.getInstance().execCmd("mirrorVideo", new NameValuePair() {

						@Override
						public String getValue() {
							return tglbtnMirror.isSelected() ? "1" : "0";
						}

						@Override
						public String getName() {
							return "isMirror";
						}
					});
				} catch (IOException | URISyntaxException | CGIException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Failed to mirror video.");
				}

			}
		});
		tglbtnMirror
				.setIcon(new ImageIcon(App.class.getResource("/com/famfamfam/icons/silk/shape_flip_horizontal.png")));
		toolBar.add(tglbtnMirror);

		tglbtnFlip = new JToggleButton("Flip");
		tglbtnFlip.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					CGIManager.getInstance().execCmd("flipVideo", new NameValuePair() {

						@Override
						public String getValue() {
							return tglbtnFlip.isSelected() ? "1" : "0";
						}

						@Override
						public String getName() {
							return "isFlip";
						}
					});
				} catch (IOException | URISyntaxException | CGIException exc) {
					exc.printStackTrace();
					JOptionPane.showMessageDialog(null, "Failed to mirror video.");
				}
			}
		});
		tglbtnFlip.setIcon(new ImageIcon(App.class.getResource("/com/famfamfam/icons/silk/shape_flip_vertical.png")));
		toolBar.add(tglbtnFlip);

		JToolBar toolBarZoom = new JToolBar();
		toolBarZoom.setOrientation(SwingConstants.VERTICAL);
		viewPanel.add(toolBarZoom, BorderLayout.WEST);

		JButton btnZoomin = new JButton("");
		btnZoomin.setToolTipText("Zoom In");
		btnZoomin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					CGIManager.getInstance().execCmd("zoomIn");
				} catch (IOException | URISyntaxException | CGIException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Could not zoom in, does your device have a zoom lens?");
				}
			}
		});
		btnZoomin.setIcon(new ImageIcon(App.class.getResource("/com/famfamfam/icons/silk/zoom_in.png")));
		toolBarZoom.add(btnZoomin);

		JButton btnStopzoom = new JButton("");
		btnStopzoom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					CGIManager.getInstance().execCmd("zoomStop");
				} catch (IOException | URISyntaxException | CGIException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "Could not stop the zoom, does your device have a zoom lens?");
				}
			}
		});
		btnStopzoom.setToolTipText("Stop Zoom");
		btnStopzoom.setIcon(new ImageIcon(App.class.getResource("/com/famfamfam/icons/silk/stop.png")));
		toolBarZoom.add(btnStopzoom);

		JButton btnZoomout = new JButton("");
		btnZoomout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					CGIManager.getInstance().execCmd("zoomOut");
				} catch (IOException | URISyntaxException | CGIException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "Could not zoom out, does your device have a zoom lens?");
				}
			}
		});
		btnZoomout.setToolTipText("Zoom Out");
		btnZoomout.setIcon(new ImageIcon(App.class.getResource("/com/famfamfam/icons/silk/zoom_out.png")));
		toolBarZoom.add(btnZoomout);

	}

	public static EmbeddedMediaPlayerComponent getMediaPlayerComp() {
		return player;
	}

	class AlarmThread extends Thread {
		@Override
		public void run() {

			for (;;) {
				try {
					sleep(5000);
					Element e = CGIManager.getInstance().execCmd("getDevState");
					if (e.getElementsByTagName("motionDetectAlarm").item(0).getTextContent().equals("2")) {
						JOptionPane.showMessageDialog(null, "Alarm detected");
					}
				} catch (InterruptedException intE) {
					System.out.println("Alarm thread stopped!");
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null,
							"An error occured on the alarm thread: " + e.getLocalizedMessage());
				}
			}
		}
	}

	private void updateStats() throws ClientProtocolException, IOException, URISyntaxException, CGIException {
		Element e = CGIManager.getInstance().execCmd("getDevInfo");

		fieldProductName.setText(e.getElementsByTagName("productName").item(0).getTextContent());
		fieldFirmware.setText(e.getElementsByTagName("firmwareVer").item(0).getTextContent());
		fieldHardwareVersion.setText(e.getElementsByTagName("hardwareVer").item(0).getTextContent());
		fieldSerialN.setText(e.getElementsByTagName("serialNo").item(0).getTextContent());
		fieldDeviceName.setText(e.getElementsByTagName("devName").item(0).getTextContent());
		fieldMac.setText(e.getElementsByTagName("mac").item(0).getTextContent());

		Calendar c = new Calendar.Builder().build();
		try {
			c.setTimeZone(TimeZone.getTimeZone(ZoneId.of(e.getElementsByTagName("timeZone").item(0).getTextContent())));
		} catch (Exception exc) {
			System.err.println("Could not parse time zone");
		}
		c.set(Integer.parseInt(e.getElementsByTagName("year").item(0).getTextContent()),
				Integer.parseInt(e.getElementsByTagName("mon").item(0).getTextContent()),
				Integer.parseInt(e.getElementsByTagName("day").item(0).getTextContent()),
				Integer.parseInt(e.getElementsByTagName("hour").item(0).getTextContent()),
				Integer.parseInt(e.getElementsByTagName("min").item(0).getTextContent()),
				Integer.parseInt(e.getElementsByTagName("sec").item(0).getTextContent()));
		fieldDeviceTime.setText(c.getTime().toString());
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		App app = new App();
		app.setVisible(true);
	}

}
