package com.telcobright.util.db;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Converter utility to transform Spring Boot entity classes to Quarkus-compatible classes
 * by removing Spring-specific annotations while preserving specified annotations.
 */
public class SpringToQuarkusEntityConverter {

    private static final Logger logger = LoggerFactory.getLogger(SpringToQuarkusEntityConverter.class);

    // Spring-specific imports to remove
    private static final List<String> SPRING_IMPORT_PATTERNS = Arrays.asList(
            "org\\.springframework\\..*",
            "org\\.springframework\\.boot\\..*",
            "org\\.springframework\\.data\\..*",
            "org\\.springframework\\.web\\..*",
            "org\\.springframework\\.context\\..*",
            "org\\.springframework\\.beans\\..*",
            "org\\.springframework\\.stereotype\\..*"
    );

    // Spring-specific annotations to remove (if not in preserve list)
    private static final List<String> SPRING_ANNOTATIONS = Arrays.asList(
            "Repository", "Service", "Component", "Controller", "RestController",
            "Configuration", "SpringBootApplication", "Autowired", "Value", "Bean",
            "RequestMapping", "GetMapping", "PostMapping", "PutMapping", "DeleteMapping", "PatchMapping",
            "PathVariable", "RequestParam", "RequestBody", "ResponseBody", "RequestHeader",
            "Transactional", "EnableAutoConfiguration", "ComponentScan", "Scope",
            "Qualifier", "Primary", "Lazy", "Profile", "Conditional", "CrossOrigin",
            "ResponseStatus", "ExceptionHandler", "ControllerAdvice", "RestControllerAdvice",
            "ModelAttribute", "SessionAttributes", "CookieValue", "MatrixVariable",
            "RequestPart", "Validated", "Valid", "Async", "EventListener",
            "EnableScheduling", "Scheduled", "EnableAsync", "EnableCaching", "Cacheable",
            "CacheEvict", "CachePut", "EnableTransactionManagement", "EnableJpaRepositories"
    );

    /**
     * Convert Spring entity files to Quarkus-compatible files
     *
     * @param src                   Source directory containing Spring entities
     * @param dst                   Destination directory for converted Quarkus entities
     * @param annotationsToPreserve List of annotation names to preserve (e.g., "Entity", "Table", "Id")
     *                              If empty, all annotations will be removed
     */
    public static void convert(String src, String dst, List<String> annotationsToPreserve) {
        try {
            logger.info("Starting conversion from {} to {}", src, dst);
            logger.info("Annotations to preserve: {}", annotationsToPreserve.isEmpty() ? "NONE" : annotationsToPreserve);

            Path sourcePath = Paths.get(src);
            Path destPath = Paths.get(dst);

            // Validate source directory
            if (!Files.exists(sourcePath) || !Files.isDirectory(sourcePath)) {
                throw new IllegalArgumentException("Source directory does not exist or is not a directory: " + src);
            }

            // Create destination directory if it doesn't exist
            if (!Files.exists(destPath)) {
                Files.createDirectories(destPath);
                logger.info("Created destination directory: {}", dst);
            }

            // Clean destination directory
            if (Files.exists(destPath)) {
                FileUtils.cleanDirectory(destPath.toFile());
                logger.info("Cleaned destination directory");
            }

            // Process all Java files recursively
            Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toString().endsWith(".java")) {
                        processJavaFile(file, sourcePath, destPath, annotationsToPreserve);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

            logger.info("Conversion completed successfully");

        } catch (Exception e) {
            logger.error("Error during conversion", e);
            throw new RuntimeException("Conversion failed: " + e.getMessage(), e);
        }
    }

    /**
     * Process a single Java file
     */
    private static void processJavaFile(Path sourceFile, Path sourceRoot, Path destRoot,
                                         List<String> annotationsToPreserve) throws IOException {
        // Calculate relative path and destination file
        Path relativePath = sourceRoot.relativize(sourceFile);
        Path destFile = destRoot.resolve(relativePath);

        // Create parent directories if needed
        Files.createDirectories(destFile.getParent());

        logger.debug("Processing file: {}", sourceFile);

        // Read and process the file
        List<String> lines = Files.readAllLines(sourceFile, StandardCharsets.UTF_8);
        List<String> processedLines = processLines(lines, annotationsToPreserve);

        // Write processed content to destination
        Files.write(destFile, processedLines, StandardCharsets.UTF_8);
        logger.debug("Written file: {}", destFile);
    }

    /**
     * Process lines of a Java file to remove Spring-specific code
     */
    private static List<String> processLines(List<String> lines, List<String> annotationsToPreserve) {
        List<String> result = new ArrayList<>();
        Set<String> preserveSet = new HashSet<>(annotationsToPreserve);

        for (String line : lines) {
            String processedLine = line;

            // Skip Spring import lines
            if (isSpringImport(line)) {
                logger.trace("Removing Spring import: {}", line.trim());
                continue;
            }

            // Process annotations
            if (containsAnnotation(line)) {
                processedLine = processAnnotations(line, preserveSet);
                if (processedLine.trim().isEmpty()) {
                    continue;
                }
            }

            result.add(processedLine);
        }

        // Clean up multiple empty lines
        return cleanupEmptyLines(result);
    }

    /**
     * Check if a line is a Spring import
     */
    private static boolean isSpringImport(String line) {
        String trimmed = line.trim();
        if (!trimmed.startsWith("import ")) {
            return false;
        }

        for (String pattern : SPRING_IMPORT_PATTERNS) {
            if (Pattern.compile("import\\s+" + pattern).matcher(trimmed).find()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a line contains an annotation
     */
    private static boolean containsAnnotation(String line) {
        return line.trim().startsWith("@") || line.contains("@");
    }

    /**
     * Process annotations in a line
     */
    private static String processAnnotations(String line, Set<String> preserveSet) {
        String result = line;

        // If we should preserve all annotations (empty preserve list means remove all)
        if (preserveSet.isEmpty()) {
            // Remove all annotations
            for (String annotation : SPRING_ANNOTATIONS) {
                result = removeAnnotation(result, annotation);
            }
        } else {
            // Remove only Spring annotations that are not in the preserve list
            for (String annotation : SPRING_ANNOTATIONS) {
                if (!preserveSet.contains(annotation)) {
                    result = removeAnnotation(result, annotation);
                }
            }
        }

        return result;
    }

    /**
     * Remove a specific annotation from a line
     */
    private static String removeAnnotation(String line, String annotation) {
        // Handle annotations with and without parameters
        String pattern1 = "@" + annotation + "\\s*\\([^)]*\\)";  // With parameters
        String pattern2 = "@" + annotation + "(?![a-zA-Z])";      // Without parameters

        String result = line.replaceAll(pattern1, "");
        result = result.replaceAll(pattern2, "");

        // Clean up if the line only contained the annotation
        if (result.trim().isEmpty() || result.trim().equals("")) {
            return "";
        }

        return result;
    }

    /**
     * Clean up multiple consecutive empty lines
     */
    private static List<String> cleanupEmptyLines(List<String> lines) {
        List<String> result = new ArrayList<>();
        boolean lastWasEmpty = false;

        for (String line : lines) {
            boolean isEmpty = line.trim().isEmpty();

            if (isEmpty && lastWasEmpty) {
                continue; // Skip consecutive empty lines
            }

            result.add(line);
            lastWasEmpty = isEmpty;
        }

        return result;
    }

    /**
     * Main method for command-line execution
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: SpringToQuarkusEntityConverter <source_dir> <dest_dir> [annotations_to_preserve]");
            System.err.println("Example: SpringToQuarkusEntityConverter /src/entities /dest/entities Entity,Table,Id,Column");
            System.exit(1);
        }

        String sourceDir = args[0];
        String destDir = args[1];
        List<String> annotationsToPreserve = new ArrayList<>();

        if (args.length > 2 && !args[2].isEmpty()) {
            // Parse comma-separated list of annotations to preserve
            annotationsToPreserve = Arrays.stream(args[2].split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        logger.info("=================================================");
        logger.info("Spring to Quarkus Entity Converter");
        logger.info("=================================================");
        logger.info("Source directory: {}", sourceDir);
        logger.info("Destination directory: {}", destDir);
        logger.info("Annotations to preserve: {}", annotationsToPreserve);

        try {
            convert(sourceDir, destDir, annotationsToPreserve);
            logger.info("✓ Conversion completed successfully!");
        } catch (Exception e) {
            logger.error("✗ Conversion failed: {}", e.getMessage());
            System.exit(1);
        }
    }
}