package be.pipo.cubixmc.bootstrap;

import static fr.theshark34.swinger.Swinger.getResource;
import static fr.theshark34.swinger.Swinger.getTransparentWhite;
import static fr.theshark34.swinger.Swinger.setResourcePath;

import java.io.File;
import java.io.IOException;

import fr.theshark34.openlauncherlib.bootstrap.Bootstrap;
import fr.theshark34.openlauncherlib.bootstrap.LauncherClasspath;
import fr.theshark34.openlauncherlib.bootstrap.LauncherInfos;
import fr.theshark34.openlauncherlib.util.ErrorUtil;
import fr.theshark34.openlauncherlib.util.GameDir;
import fr.theshark34.openlauncherlib.util.SplashScreen;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;
import fr.theshark34.supdate.application.integrated.FileDeleter;
import fr.theshark34.swinger.colored.SColoredBar;

public class MyBootstrap {
	
	private static SplashScreen splash;
	private static SColoredBar bar;
	private static Thread barThread;
	
	private static final LauncherInfos CMC_B_INFOS = new LauncherInfos("CubixMC - Launcher", "be.pipo.cubixmc.launcher.LauncherFrame");
	private static final File CMC_DIR = GameDir.createGameDir("cubixmc");
	private static final LauncherClasspath CMC_B_CP = new LauncherClasspath(new File(CMC_DIR, "Launcher/launcher.jar"), new File (CMC_DIR, "Launcher/Libs/"));

	private static ErrorUtil errorUtil = new ErrorUtil(new File (CMC_DIR, "Launcher/crashes/"));
	public static void main(String[] args) {
		setResourcePath("/be/pipo/cubixmc/bootstrap/resources");
		displaySplash();
		try {
		doUpdate();
		} catch (Exception e) {
			errorUtil.catchError(e, "Impossible de mettre a jour le launcher !");
			barThread.interrupt();
		}
		try {
		launchLauncher();
		} catch (IOException e) {
			errorUtil.catchError(e, "Impossible de lancer le launcher !");
		}
	}
	
	private static void displaySplash() {
		splash = new SplashScreen("CubixMC - Launcher", getResource("bg.png"));
		splash.setLayout(null);
		
		bar = new SColoredBar(getTransparentWhite(75), getTransparentWhite(125));
		bar.setBounds(0, 510, 379, 20);
		splash.add(bar);
		
		splash.setVisible(true);
	}
	
	private static void doUpdate() throws Exception {
		SUpdate su = new SUpdate("http://localhost/CubixMC-Launcher/Bootstrap", new File(CMC_DIR, "Launcher"));
		su.addApplication(new FileDeleter());
		
		barThread = new Thread() {
			@Override
			public void run() {
				while(!this.isInterrupted()) {
				bar.setValue((int) (BarAPI.getNumberOfTotalDownloadedBytes() / 1000));
				bar.setMaximum((int) (BarAPI.getNumberOfTotalBytesToDownload() / 1000));
				}
			}
		};
		barThread.start();
		
		su.start();
		barThread.interrupt();
	}
	
	private static void launchLauncher() throws IOException {
		Bootstrap bootstrap = new Bootstrap(CMC_B_CP, CMC_B_INFOS);
		Process p = bootstrap.launch();
		splash.setVisible(false);
		try {
		p.waitFor();
		} catch (InterruptedException e) {
			System.exit(0);
		}
	}
	
}
