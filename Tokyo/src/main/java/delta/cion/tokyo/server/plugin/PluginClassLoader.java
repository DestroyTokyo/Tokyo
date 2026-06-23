package delta.cion.tokyo.server.plugin;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public class PluginClassLoader extends URLClassLoader {

	private final ClassLoader libraryLoader;

	public PluginClassLoader(URL[] urls, ClassLoader parent, ClassLoader libraryLoader) {
		super(urls, parent);
		this.libraryLoader = libraryLoader;
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		synchronized (getClassLoadingLock(name)) {
			Class<?> c = findLoadedClass(name);
			if (c != null) return resolveClass(c, resolve);

			if (libraryLoader != null) {
				try {
					c = libraryLoader.loadClass(name);
					if (c != null) return resolveClass(c, resolve);
				} catch (ClassNotFoundException ignored) {}
			}

			try {
				c = findClass(name);
				if (c != null) return resolveClass(c, resolve);
			} catch (ClassNotFoundException ignored) {}
			return super.loadClass(name, resolve);
		}
	}

	private Class<?> resolveClass(Class<?> c, boolean resolve) {
		if (resolve) resolveClass(c);
		return c;
	}

	@Override
	public void close() throws IOException {
		super.close();
	}
}
