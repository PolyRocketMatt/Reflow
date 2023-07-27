package com.github.polyrocketmatt.restruct.utils.decompilation;

import org.jd.core.v1.ClassFileToJavaSourceDecompiler;
import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.loader.LoaderException;
import org.jd.core.v1.api.printer.Printer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class JDecompiler {

    private final Loader jdLoader;
    private final Printer jdPrinter;
    private final String decompiledSource;

    public JDecompiler(String pathToSource) {
        this.jdLoader = getLoader(pathToSource);
        this.jdPrinter = new Printer() {
            private static final String TAB = "  ";
            private static final String NEWLINE = "\n";

            private int indentationCount = 0;
            private final StringBuilder sb = new StringBuilder();

            @Override
            public String toString() {
                return sb.toString();
            }

            @Override
            public void start(int maxLineNumber, int majorVersion, int minorVersion) {
            }

            @Override
            public void end() {
            }

            @Override
            public void printText(String text) {
                sb.append(text);
            }

            @Override
            public void printNumericConstant(String constant) {
                sb.append(constant);
            }

            @Override
            public void printStringConstant(String constant, String ownerInternalName) {
                sb.append(constant);
            }

            @Override
            public void printKeyword(String keyword) {
                sb.append(keyword);
            }

            @Override
            public void printDeclaration(int type, String internalTypeName, String name, String descriptor) {
                sb.append(name);
            }

            @Override
            public void printReference(int type, String internalTypeName, String name, String descriptor, String ownerInternalName) {
                sb.append(name);
            }

            @Override
            public void indent() {
                this.indentationCount++;
            }

            @Override
            public void unindent() {
                this.indentationCount--;
            }

            @Override
            public void startLine(int lineNumber) {
                sb.append(TAB.repeat(Math.max(0, indentationCount)));
            }

            @Override
            public void endLine() {
                sb.append(NEWLINE);
            }

            @Override
            public void extraLine(int count) {
                while (count-- > 0) sb.append(NEWLINE);
            }

            @Override
            public void startMarker(int type) {

            }

            @Override
            public void endMarker(int type) {
            }
        };

        ClassFileToJavaSourceDecompiler decompiler = new ClassFileToJavaSourceDecompiler();

        try {
            decompiler.decompile(jdLoader, jdPrinter, pathToSource);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        this.decompiledSource = jdPrinter.toString();
    }

    public Loader getLoader() {
        return jdLoader;
    }

    public Printer getPrinter() {
        return jdPrinter;
    }

    public String getDecompiledSource() {
        return decompiledSource;
    }

    public static Loader getLoader(String path) {
        return new Loader() {
            @Override
            public byte[] load(String internalName) throws LoaderException {
                try (InputStream is = new FileInputStream(path)) {
                    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                        byte[] buffer = new byte[1024];
                        int read = is.read(buffer);

                        while (read > 0) {
                            out.write(buffer, 0, read);
                            read = is.read(buffer);
                        }

                        return out.toByteArray();
                    } catch (IOException ex) {
                        throw new LoaderException(ex);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                return null;
            }

            @Override
            public boolean canLoad(String internalName) {
                return new File(internalName).exists();
            }
        };
    }

}
