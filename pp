#!/bin/bash

# Portfolio Performance CLI Launcher Script
# This script runs the Portfolio Performance command-line interface using a fat jar

set -e

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Default Java executable
JAVA_CMD="${JAVA_HOME:-$(dirname $(dirname $(readlink -f $(which java))))}/bin/java"
if [[ ! -x "$JAVA_CMD" ]]; then
    JAVA_CMD="java"
fi

# Check Java version (require Java 17+)
check_java_version() {
    local java_version
    if java_version=$("$JAVA_CMD" -version 2>&1 | head -1 | awk -F '"' '{print $2}'); then
        local major_version=$(echo "$java_version" | cut -d'.' -f1)
        if [[ "$major_version" -lt 17 ]]; then
            echo "Error: Java 17 or higher is required. Found Java $java_version" >&2
            echo "Please set JAVA_HOME to point to a Java 17+ installation." >&2
            return 1
        fi
    else
        echo "Warning: Could not determine Java version" >&2
    fi
}

# Fat jar location
FAT_JAR="$SCRIPT_DIR/name.abuchen.portfolio.cli/target/portfolio-cli.jar"

# Check if fat jar exists
if [[ ! -f "$FAT_JAR" ]]; then
    echo "Error: CLI fat jar not found at $FAT_JAR" >&2
    echo "" >&2
    echo "Please build the CLI fat jar first:" >&2
    echo "" >&2
    echo "  cd $SCRIPT_DIR/name.abuchen.portfolio.cli" >&2
    echo "  mvn clean compile package -DskipTests" >&2
    echo "" >&2
    echo "Note: Java 17+ is required for building and running this application." >&2
    exit 1
fi

# Set JVM options
JVM_OPTS="-Xmx1G"
if [[ -n "$PP_JVM_OPTS" ]]; then
    JVM_OPTS="$PP_JVM_OPTS"
fi

# Enable debug mode if requested
if [[ "$PP_DEBUG" == "true" ]]; then
    echo "Debug: Java command: $JAVA_CMD" >&2
    echo "Debug: JVM options: $JVM_OPTS" >&2
    echo "Debug: Fat jar: $FAT_JAR" >&2
    echo "Debug: Arguments: $@" >&2
fi

# Show help if no arguments provided
if [[ $# -eq 0 ]]; then
    echo "Portfolio Performance CLI"
    echo ""
    echo "Usage: pp --file <portfolio.xml> [options] <command> [command-options]"
    echo ""
    echo "Examples:"
    echo "  pp --file my-portfolio.xml accounts list"
    echo "  pp --file my-portfolio.xml transactions list --from 2024-01-01"
    echo "  pp --file my-portfolio.xml --format json accounts list"
    echo ""
    echo "Environment variables:"
    echo "  PP_JVM_OPTS    - Additional JVM options (default: -Xmx1G)"
    echo "  PP_DEBUG       - Set to 'true' to enable debug output"
    echo "  JAVA_HOME      - Java installation directory (Java 17+ required)"
    echo ""
    echo "For detailed help, run: pp --help"
    echo ""
    if [[ ! -f "$FAT_JAR" ]]; then
        echo "Note: CLI fat jar not found. Build it with:" >&2
        echo "  cd $SCRIPT_DIR/name.abuchen.portfolio.cli && mvn clean package -DskipTests" >&2
        echo ""
    fi
    exit 1
fi

# Check Java version
check_java_version

# Run the CLI application using the fat jar
exec "$JAVA_CMD" $JVM_OPTS -jar "$FAT_JAR" "$@"