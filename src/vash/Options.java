/*
 * Copyright 2011, Zettabyte Storage LLC
 * 
 * This file is part of Vash.
 * 
 * Vash is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Vash is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with Vash.  If not, see <http://www.gnu.org/licenses/>.
 */
package vash;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Encapsulate command line options and option processing.
 */
public class Options {
	/*80col //////////////////////////////////////////////////////////////////////////////// */
	private static final String HELP_TEXT = "" +
			"Vash -- the visual hash function\n\n" +
			"Usage: java -jar Vash.jar [options]\n" +
			"\n" +
			"Help:\n" +
			"  --help                Show this help text\n" +
			"  -V,--version          Show the Vash version\n" +
			"  --known-algorithms    Show the options you can pass to --algorithm\n" +
			"\n" +
			"Required Options:\n" +
			"  -a,--algorithm\n" +
			"              [String]  Select the image generator algorithm to use\n" +
			"  -d,--data   [String]  The data to hash, as a string\n" +
			"  -f,--file   [String]  The data, read from a file (use - to read from stdin)\n" +
			"  -s,--salt   [String]  Provide a salt to the hashing algorithm.  This value\n" + 
			"                        will be normalized to the correct length for the\n" +
			"                        specified algorithm, by appending 0's\n" +
			"  -S,--salt-file\n" +
			"              [String]  Provide the salt with bytes from a file.  Only as many\n" +
			"                        bytes will be read as the selected algorithm requires\n" +
			"                        (use - to read from stdin)\n" +
			"\n" +
			"Output Options:\n" +
			"  -w,--width  [Integer] The output image width\n" +
			"  -h,--height [Integer] The output image height\n" +
			"  -o,--output [String]  The filename to write to (use - to write to stdout)\n";
	
	public static final String[] KNOWN_ALGORITHMS = {
		"1", "1-fast", "1.1"
	};
	public static final String[] PUBLIC_ALGORITHMS = {
		"1.1"
	};
	
	// tree arguments
	private String algorithm;
	private InputStream data;
	private byte[] salt;

	// output arguments
	private String output = "output.png";
	private int width = 128;
	private int height = 128;

	// animation parameters
	private AnimationMode animationMode = AnimationMode.WRAP;
	private double duration = 15.0; // in seconds
	private double period = 15.0; // in seconds
	private double frameRate = 30.0; // in fps

	public InputStream getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = new ByteArrayInputStream(data);
	}
	public void setData(String filename) {
		if(filename.equals("-")) {
			this.data = System.in;
		} else {
			try {
				this.data = new FileInputStream(filename);
			} catch(FileNotFoundException e) {
				System.err.println(e.getLocalizedMessage());
				System.exit(1);
			}
		}
	}

	public byte[] getSalt() {
		return salt;
	}
	
	/**
	 * Normalize a salt value to be 64 bytes in length.
	 * @param algorithm
	 * @param salt
	 * @return
	 */
	public static byte[] normalizeSaltBytes(String algorithm, byte[] salt) {
		// get the algorithm's required salt size
		int size = Seed.getSaltSizeForAlgorithm(algorithm);
		if(size == 0) {
			System.err.format("The algorithm '%s' does not take a salt.%n", algorithm);
			System.exit(1);
		}

		byte[] s = new byte[size];
		int cnt = Math.min(s.length, salt.length);
		System.arraycopy(salt, 0, s, 0, cnt);
		return s;
	}

	/*
	 * Helper method to read the salt from a given file, or 
	 */
	public byte[] loadSaltFromFile(String algorithm, InputStream saltStream) {
		// get the algorithm's required salt size
		int size = Seed.getSaltSizeForAlgorithm(algorithm);
		if(size == 0) {
			System.err.format("The algorithm '%s' does not take a salt.%n", algorithm);
			System.exit(1);
		}

		byte[] buffer = new byte[size];
		int cnt = 0, offset = 0;
		try {
			while(cnt != -1 && offset < buffer.length) {
				cnt = saltStream.read(buffer, offset, buffer.length - offset);
				if(cnt != -1) offset += cnt;
			}
		} catch(IOException e) {
			System.err.format("Reading salt file failed: %s%n", e.getLocalizedMessage());
			System.exit(1);
		}
		return buffer;
	}

	public void setSalt(String algo, byte[] saltData) {
		this.salt = normalizeSaltBytes(algo, saltData);
	}
	public void setSalt(String algo, String filename) {
		InputStream fp = null;
		if(filename.equals("-")) {
			fp = System.in;
		} else {
			try {
				fp = new BufferedInputStream(new FileInputStream(filename));
			} catch(FileNotFoundException e) {
				System.err.println(e.getLocalizedMessage());
				System.exit(1);
			}
		}
		
		this.salt = loadSaltFromFile(algo, fp);
	}
	                                  
	
	public String getAlgorithm() {
		return algorithm;
	}
	public void setAlgorithm(String generator) {
		this.algorithm = generator;
	}

	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	
	public AnimationMode getAnimationMode() {
		return animationMode;
	}
	public void setAnimationMode(AnimationMode animationMode) {
		this.animationMode = animationMode;
	}
	
	public double getDuration() {
		return duration;
	}
	public void setDuration(double duration) {
		this.duration = duration;
	}
	
	public double getPeriod() {
		return period;
	}
	public void setPeriod(double period) {
		this.period = period;
	}

	public double getFrameRate() {
		return frameRate;
	}
	public void setFrameRate(double frameRate) {
		this.frameRate = frameRate;
	}

	public static void showKnownAlgorithms() {
		System.out.println("Known Algorithms:");
		for(String a : PUBLIC_ALGORITHMS) {
			System.out.format("\t%s%n", a);
		}
	}

	
	/**
	 * Construct an empty options set.  If you want to use this Options for anything, you
	 * will probably want to set at least set the seed and algorithm after creating it.
	 * An Options structure is not strictly needed, as the *Parameter classes are all
	 * initializable directly from the same values you would be plugging in here.
	 */
	public Options() {}

	
	/**
	 * Construct a new options collection from the main command line args.
	 * @param args Command line args.
	 */
	public Options(String[] args) throws IllegalArgumentException {
		if(args.length == 1 && args[0].equals("-h")) {
			System.out.println("Warning: -h is short of --height; use --help instead\n");
			System.out.println(HELP_TEXT);
			System.exit(0);
		}
		
		String[] _haveArg = {
				"--algorithm", "-a",
				"--data", "-d",
				"--file", "-f",
				"--salt", "-s",
				"--salt-file", "-S",
				"--output", "-o",
				"--width", "-w", 
				"--height", "-h",
				"--animate-mode", "-A",
				"--duration", "-D",
				"--period", "-P",
				"--frame-rate", "-R",
			};
		HashSet<String> haveArg = new HashSet<String>(_haveArg.length);
		for(String i : _haveArg) haveArg.add(i);
		
		// record when we find it, so we can set it at the end, 
		//	after we have the algorithm identifier
		byte[] saltBytes = null;
		String saltFilename = null;

		for(int i = 0; i < args.length; i++) {
			String arg = args[i];
			String opt = null;
			if(haveArg.contains(arg)) {
				i += 1;
				if(i == args.length) {
					throw new IllegalArgumentException("The option '" + arg.toString() + "' requires an argument.");
				}
				opt = args[i];
			}
			if(arg.equals("--help")) {
				System.out.println(HELP_TEXT);
				System.exit(0);
			} else if(arg.equals("--version") || arg.equals("-V")) {
				System.out.println(Vash.VERSION);
				System.exit(0);
			} else if(arg.equals("--known-algorithms")) {
				showKnownAlgorithms();
				System.exit(0);
			} else if(arg.equals("--algorithm") || arg.equals("-a")) {
				setAlgorithm(opt);
			} else if(arg.equals("--data") || arg.equals("-d")) {
				setData(opt.getBytes());
			} else if(arg.equals("--file") || arg.equals("-f")) {
				setData(opt);
			} else if(arg.equals("--salt") || arg.equals("-s")) {
				saltBytes = opt.getBytes();
			} else if(arg.equals("--salt-file") || arg.equals("-S")) {
				saltFilename = opt;
			} else if(arg.equals("--output") || arg.equals("-o")) {
				setOutput(opt);
			} else if(arg.equals("--width") || arg.equals("-w")) {
				setWidth(Integer.decode(opt));
			} else if(arg.equals("--height") || arg.equals("-h")) {
				setHeight(Integer.decode(opt));
			} else if(arg.equals("--animate-mode") || arg.equals("-A")) {
				setAnimationMode(AnimationMode.parseAnimationMode(opt));
			} else if(arg.equals("--duration") || arg.equals("-D")) {
				setDuration(Double.parseDouble(opt));
			} else if(arg.equals("--period") || arg.equals("-P")) {
				setPeriod(Double.parseDouble(opt));
			} else if(arg.equals("--frame-rate") || arg.equals("-R")) {
				setFrameRate(Double.parseDouble(opt));
			} else {
				throw new IllegalArgumentException(String.format("The option \"%s\" is not recognized.", arg));
			}
		}
		
		// ensure we got both a data and an algorithm parameter at some point
		if(getData() == null) {
			throw new IllegalArgumentException("The option -d or --data must be set.");
		}
		if(getAlgorithm() == null) {
			throw new IllegalArgumentException("The option -a or --algorithm must be set.");
		}
		if(Arrays.binarySearch(KNOWN_ALGORITHMS, getAlgorithm()) < 0) {
			throw new IllegalArgumentException("Unknown seed algorithm: \"" + algorithm + "\"");
		}

		// set the salt if we got one, now that we know we got an algorithm
		if(saltFilename != null && saltBytes != null) {
			throw new IllegalArgumentException("A salt file and salt literal must not both be specified.");
		}
		if(saltFilename != null) {
			setSalt(getAlgorithm(), saltFilename);
		} else if(saltBytes != null) {
			setSalt(getAlgorithm(), saltBytes);
		}
	}
}
