package util;

import java.io.File;
import java.net.URI;

/*********************************************************************************
 * AssetManager
 * 
 * An "asset" is any data file associated with our project that we need to use 
 * within our project. Good examples include textures, 3D marker models, and
 * descriptions of triangulations.
 * 
 * Assets need to be distributed with our application code in order for the 
 * application to run properly. Unfortunately, depending on how the application
 * is run (say, from the command line versus double-clicking), the appropriate
 * path specifying where assets reside on disk can be different. Obviously,
 * we don't want to hard code absolute paths for assets, or specify that the
 * user must have the application folder in a certain place on his/her disk.
 * 
 * Instead, the "AssetManager" class provides a way for us to look up assets.
 * Basically, this class allows our code to assume that the application was started
 * from within the application folder. 
 *********************************************************************************/
public class AssetManager {

	private static URI root;
	
	static{
		root = null;
		try{
			root = AssetManager.class.getProtectionDomain().getCodeSource().getLocation().toURI();
		} catch (Exception ee) {
			System.err.println("Error determining location of executable. Aborting.\n");
			System.exit(1);
		}	
	}

	public static String getAssetPath( String path ){
		return root.resolve("../Data/" + path).getPath();
	}
	
	public static File getAssetFile(String path) {
		File file = null;
		try {
			file = new File(AssetManager.getAssetPath(path));
		} catch (Exception ex) {
			System.err.println("Error: Could not locate file " + path + ".");
			System.exit(1);
		}

		return file;
	}
}
