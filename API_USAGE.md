# PandyNickColor API Usage Guide

This document provides a comprehensive guide on how to use the `PandyNickColorAPI` to manage player nickname colors in the TAB list for Minecraft servers running the `PandyNickColor` plugin. The API allows developers to set, reset, check, and remove nickname colors programmatically.

## Prerequisites

- **PandyNickColor Plugin**: Ensure the `PandyNickColor` plugin is installed on your server. You can download it from the [releases page](https://github.com/RazerAtheriez/PandyNickColor/releases).
- **Server Requirements**:
  - Paper 1.21.4 or newer.
  - TAB plugin 5.2.0 or newer ([GitHub NEZNAMY/TAB](https://github.com/NEZNAMY/TAB)).
- **Development Environment**:
  - A Java development environment (e.g., IntelliJ IDEA, Eclipse).
  - Maven for dependency management.

## Adding the Dependency

To use the `PandyNickColorAPI` in your project, add the following dependency to your `pom.xml` using JitPack:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.RazerAtheriez</groupId>
        <artifactId>PandyNickColor</artifactId>
        <version>v1.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
