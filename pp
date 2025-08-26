#!/bin/bash

# Portfolio Performance CLI Launcher Script
# This script runs the Portfolio Performance command-line interface

set -e

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Default Java executable
JAVA_CMD="${JAVA_HOME:-$(dirname $(dirname $(readlink -f $(which java))))}/bin/java"
if [[ ! -x "$JAVA_CMD" ]]; then
    JAVA_CMD="java"
fi

# Check Java version (require Java 21+)
check_java_version() {
    local java_version
    if java_version=$("$JAVA_CMD" -version 2>&1 | head -1 | awk -F '"' '{print $2}'); then
        local major_version=$(echo "$java_version" | cut -d'.' -f1)
        if [[ "$major_version" -lt 21 ]]; then
            echo "Error: Java 21 or higher is required. Found Java $java_version" >&2
            echo "Please set JAVA_HOME to point to a Java 21+ installation." >&2
            return 1
        fi
    else
        echo "Warning: Could not determine Java version" >&2
    fi
}

# Main class to run
MAIN_CLASS="name.abuchen.portfolio.cli.commands.PortfolioCLI"

# Function to check if a directory exists and add to classpath
add_to_classpath() {
    local path="$1"
    if [[ -d "$path" ]]; then
        if [[ -z "$CLASSPATH" ]]; then
            CLASSPATH="$path"
        else
            CLASSPATH="$CLASSPATH:$path"
        fi
    fi
}

# Function to add JAR files from directory to classpath
add_jars_to_classpath() {
    local dir="$1"
    if [[ -d "$dir" ]]; then
        for jar in "$dir"/*.jar; do
            if [[ -f "$jar" ]]; then
                if [[ -z "$CLASSPATH" ]]; then
                    CLASSPATH="$jar"
                else
                    CLASSPATH="$CLASSPATH:$jar"
                fi
            fi
        done
    fi
}

# Build classpath
CLASSPATH=""

# Add compiled classes from target directories (preferred)
add_to_classpath "$SCRIPT_DIR/name.abuchen.portfolio.cli/target/classes"
add_to_classpath "$SCRIPT_DIR/name.abuchen.portfolio/target/classes"

# Add dependency JARs if they exist
add_jars_to_classpath "$SCRIPT_DIR/name.abuchen.portfolio.cli/target/dependency"
add_jars_to_classpath "$SCRIPT_DIR/name.abuchen.portfolio/target/dependency"

# Check if we have compiled classes
if [[ ! -d "$SCRIPT_DIR/name.abuchen.portfolio.cli/target/classes" ]]; then
    echo "Error: Project not built. Please build the project first:" >&2
    echo "" >&2
    echo "  cd $SCRIPT_DIR" >&2
    echo "  mvn -f portfolio-app/pom.xml clean compile -Plocal-dev" >&2
    echo "" >&2
    echo "Note: Java 21 is required for building and running this application." >&2
    exit 1
fi

# Set JVM options
JVM_OPTS="-Xmx1G --enable-native-access=ALL-UNNAMED"
if [[ -n "$PP_JVM_OPTS" ]]; then
    JVM_OPTS="$PP_JVM_OPTS"
fi

# Enable debug mode if requested
if [[ "$PP_DEBUG" == "true" ]]; then
    echo "Debug: Java command: $JAVA_CMD" >&2
    echo "Debug: JVM options: $JVM_OPTS" >&2
    echo "Debug: Classpath: $CLASSPATH" >&2
    echo "Debug: Main class: $MAIN_CLASS" >&2
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
    echo "  PP_JVM_OPTS    - Additional JVM options (default: -Xmx1G --enable-native-access=ALL-UNNAMED)"
    echo "  PP_DEBUG       - Set to 'true' to enable debug output"
    echo "  JAVA_HOME      - Java installation directory (Java 21+ required)"
    echo ""
    echo "For detailed help, run: pp --help"
    echo ""
    if [[ ! -d "$SCRIPT_DIR/name.abuchen.portfolio.cli/target/classes" ]]; then
        echo "Note: Project appears to not be built. Run the following to build:" >&2
        echo "  mvn -f portfolio-app/pom.xml clean compile -Plocal-dev" >&2
        echo ""
    fi
    exit 1
fi

# Check Java version
check_java_version

# Run the CLI application
exec "$JAVA_CMD" $JVM_OPTS -cp "$CLASSPATH" "$MAIN_CLASS" "$@"