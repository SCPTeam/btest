package it.unige.cseclab.stim;

import java.io.IOException;
import java.util.Map;

import com.android.chimpchat.adb.AdbBackend;
import com.android.chimpchat.core.IChimpDevice;
import com.android.monkeyrunner.recorder.actions.Action;
import com.lagodiuk.ga.Fitness;
import it.unige.cseclab.log.Log;

public class TestRunner implements Fitness<TestChromosome, Double> {
	
	static AdbBackend ab = null;
	String pkg;
	Map<String,Double> Env;
	
	public TestRunner(String pkg, Map<String, Double> env) {
		super();
		this.pkg = pkg;
		Env = env;
	}

	public static double run(String appkg, TestChromosome T, Map<String,Double> E) {

		Log.log("Running Test " + T.toString());
		if(ab == null)
			 ab = new AdbBackend();

		Process P = null;
        try {
            P = Runtime.getRuntime().exec("adb shell monkey -p " + appkg + " -c android.intent.category.LAUNCHER 1");
            P.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

	    IChimpDevice device = ab.waitForConnection();
	    // adb", "logcat", "GACALL:V", "*:S
        try {
            P = Runtime.getRuntime().exec("adb logcat GACALL:V *:S");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // pb.redirectErrorStream(true);
	    
	    LogReader reader = new LogReader(P, E);
	    reader.start();
	    
	    for(Action a : T.aVector()) {
	    	try {
				a.execute(device);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(e);
				System.out.println("Action: " + a.serialize() +"\n");
			}
	    }
	    
	    reader.end();
	    reader.interrupt();
	    
	    device.dispose();
	    
	    // adb shell pm clear com.my.app.package
        try {
            P = Runtime.getRuntime().exec("adb shell pm clear " + appkg);
            P.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        double score = reader.getScore(T);
        Log.log("Score: " + score);

	    return score;
	}

	@Override
	public Double calculate(TestChromosome chromosome) {
		return run(pkg, chromosome, Env);
	}

}
