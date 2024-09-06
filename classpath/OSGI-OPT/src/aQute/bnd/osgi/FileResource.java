package aQute.bnd.osgi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import aQute.lib.io.IO;
import aQute.lib.io.IOConstants;

public class FileResource implements Resource {
	private static final int		THRESHOLD	= IOConstants.PAGE_SIZE * 16;
	private static final ByteBuffer	CLOSED	= ByteBuffer.allocate(0);
	private ByteBuffer	buffer;
	private final Path				file;
	private String		extra;
	private boolean		deleteOnClose;
	private final long				lastModified;
	private final long				size;

	public FileResource(File file) throws IOException {
		this(file.toPath());
	}

	public FileResource(Path path) throws IOException {
		this(path, Files.readAttributes(path, BasicFileAttributes.class));
	}

	/* Used by Jar.buildFromDirectory */
	FileResource(Path path, BasicFileAttributes attrs) throws IOException {
		file = path.toAbsolutePath();
		lastModified = attrs.lastModifiedTime().toMillis();
		size = attrs.size();
	}

	/**
	 * Turn a resource into a file so that anything in the conversion is
	 * properly caught
	 * 
	 * @param r
	 * @throws Exception
	 */
	public FileResource(Resource r) throws Exception {
		file = Files.createTempFile("fileresource", ".resource");
		deleteOnClose(true);
		file.toFile().deleteOnExit();
		try (OutputStream out = IO.outputStream(file)) {
			r.write(out);
		}
		lastModified = r.lastModified();
		size = Files.size(file);
	}

	@Override
	public ByteBuffer buffer() throws Exception {
		if (buffer != null) {
			return buffer.duplicate();
		}
		if (IO.isWindows() && (size > THRESHOLD)) {
			return null;
		}
		return (buffer = IO.read(file)).duplicate();
	}

	public InputStream openInputStream() throws Exception {
		if (buffer != null) {
			return IO.stream(buffer());
		} else {
			return IO.stream(file);
		}
	}

	@Override
	public String toString() {
		return file.toString();
	}

	public void write(OutputStream out) throws Exception {
		if (buffer != null) {
			IO.copy(buffer(), out);
		} else {
			IO.copy(file, out);
		}
	}

	public long lastModified() {
		return lastModified;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public long size() {
		return size;
	}

	public void close() throws IOException {
		/*
		 * Allow original buffer to be garbage collected and prevent it being
		 * remapped for this FileResouce.
		 */
		buffer = CLOSED;
		if (deleteOnClose)
			IO.delete(file);
	}

	public void deleteOnClose(boolean b) {
		deleteOnClose = b;
	}

	public File getFile() {
		return file.toFile();
	}
}
