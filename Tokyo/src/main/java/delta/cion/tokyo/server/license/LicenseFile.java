package delta.cion.tokyo.server.license;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class LicenseFile {

	private static boolean writed = false;

	private static final Logger LOGGER = LoggerFactory.getLogger(LicenseFile.class);
	private static final File LICENSE_FILE = new File("LICENSE");

	private static final String LICENSE = """
		Fantasy Axe Studio | Project Violette Public License
		Copyright (C) 2026 Timur Levchenko <delta.cion.fe@gmail.com>
		Version 1 | Date: 13 April 2026

		TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION

		0. The software is provided "As is", without warranty of any kind, express or implied, including but not limited to the warranties of merchantability, fitness for a particular purpose and noninfringement.

		1. In no event shall the authors or copyright holders be liable for any claim, damages or other liability, whether in an action of contract, tort or otherwise, arising from, out of or in connection with the software or the use or other dealings in the software.

		2. Everyone is permitted to copy and distribute verbatim or modified
		copies of this license document, and changing it is allowed as long
		as the name is changed.

		3. Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted.
		""";

	public LicenseFile() {
		if (!writed) writed = true;
		else return;

		if (LICENSE_FILE.exists() && isFull()) return;
		else setLicense();
	}

	private void setLicense() {
		try (Writer writer = new FileWriter(LICENSE_FILE)) {
			if (!LICENSE_FILE.exists()) {
				if (LICENSE_FILE.createNewFile()) LOGGER.debug("License file created");
				else LOGGER.debug("Cannot create License file");}
			writer.write(LICENSE);
		} catch (IOException ignored) {}
	}

	private boolean isFull() {
		try (Reader reader = new FileReader(LICENSE_FILE)) {
			if (reader.toString().isEmpty()) return false;
			return reader.toString().equals(LICENSE);
		} catch (IOException e) {
			return false;
		}
	}

}
