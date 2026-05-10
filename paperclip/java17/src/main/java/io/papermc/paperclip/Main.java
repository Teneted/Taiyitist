package io.papermc.paperclip;

import java.lang.reflect.Method;

public final class Main {
    public static void main(String[] args) {
        if (getJavaVersion() < 25) {
            System.err.println("Minecraft 26.1 requires running the server with Java 25 or above. Download Java 25 (or above) from https://adoptium.net/");
            System.exit(1);
        }
        try {
            Class<?> paperclipClass = Class.forName("io.papermc.paperclip.Paperclip");
            Method mainMethod = paperclipClass.getMethod("main", new Class[] { String[].class });
            mainMethod.invoke(null, new Object[] { args });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int getJavaVersion() {
        String version = System.getProperty("java.specification.version");
        String[] parts = version.split("\\.");
        String errorMsg = "Could not determine version of the current JVM";
        if (parts.length == 0)
            throw new IllegalStateException("Could not determine version of the current JVM");
        if (parts[0].equals("1")) {
            if (parts.length < 2)
                throw new IllegalStateException("Could not determine version of the current JVM");
            return Integer.parseInt(parts[1]);
        }
        return Integer.parseInt(parts[0]);
    }
}
