void jar(String... args) { run("jar", args); }
void javac(String... args) { run("javac", args); }
void javadoc(String... args) { run("javadoc", args); }
void javap(String... args) { run("javap", args); }
void jdeps(String... args) { run("jdeps", args); }
void jlink(String... args) { run("jlink", args); }
void jmod(String... args) { run("jmod", args); }
void jpackage(String... args) { run("jpackage", args); }

void javap(Class<?> type) throws Exception {
    var loader = type.getClassLoader();
    if ("jdk.jshell".equals(loader.getClass().getModule().getName())) {
        var temp = java.nio.file.Files.createTempFile("TOOLING-", ".class");
        try {
            var name = type.getName().replace('.', '/') + ".class";
            try (var in = loader.getResourceAsStream(name);
                 var out = java.nio.file.Files.newOutputStream(temp)) {
                if (in == null) throw new AssertionError("Resource not found: " + name);
                in.transferTo(out);
            }
            run("javap", "-c", "-v", "-s", temp.toString());
            return;
        } finally {
            java.nio.file.Files.delete(temp);
        }
    }
    run("javap", "-c", "-v", "-s", type.getCanonicalName());
}

void run(String name, String... args) {
    var tool = java.util.spi.ToolProvider.findFirst(name);
    if (tool.isEmpty()) throw new RuntimeException("No such tool found: " + name);
    var code = tool.get().run(System.out, System.err, args);
    if (code == 0) return;
    System.err.println(name + " returned non-zero exit code: " + code);
}
void tools() {
  java.util.ServiceLoader.load(java.util.spi.ToolProvider.class).stream()
      .map(java.util.ServiceLoader.Provider::get)
      .map(java.util.spi.ToolProvider::name)
      .sorted()
      .forEach(System.out::println);
}
